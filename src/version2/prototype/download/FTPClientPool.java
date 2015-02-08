package version2.prototype.download;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

public class FTPClientPool {
    private static final long CLIENT_POOL_CHECK_INTERVAL = 10000; // 10 seconds
    private static final long CLIENT_POOL_EXPIRE_TIME = 60000; // 60 seconds
    private static Map<String, Set<PoolEntry>> sClientsByHost = new HashMap<String, Set<PoolEntry>>();

    private static final class PoolEntry {
        private final FTPClient mClient;
        private long mLastReturnedTime;

        public PoolEntry(FTPClient client) {
            mClient = client;
            mLastReturnedTime = new Date().getTime();
        }

        public FTPClient getClient() {
            return mClient;
        }

        public long getLastReturnedTime() {
            return mLastReturnedTime;
        }
    }

    static {
        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    // Sleep for a while
                    try {
                        Thread.sleep(CLIENT_POOL_CHECK_INTERVAL);
                    } catch (InterruptedException e) {
                        System.err.println("[FtpClientPool] Warning: monitor thread was" +
                                " interrupted");
                        return;
                    }

                    // Check all client pool entries
                    final long expireTime = new Date().getTime() - CLIENT_POOL_EXPIRE_TIME;
                    synchronized (sClientsByHost) {
                        for (Map.Entry<String, Set<PoolEntry>> host : sClientsByHost.entrySet()) {
                            // Build a set of expired entries
                            Set<PoolEntry> expiredEntries = new HashSet<PoolEntry>();
                            for (PoolEntry entry : host.getValue()) {
                                if (entry.getLastReturnedTime() < expireTime) {
                                    try {
                                        entry.getClient().disconnect();
                                    } catch (IOException e) {
                                        // Disconnect quietly
                                    }
                                    expiredEntries.add(entry);
                                }
                            }

                            // Remove expired entries
                            host.getValue().removeAll(expiredEntries);
                        }
                    }
                }
            }
        });

        thread.setDaemon(true);
        thread.start();
    }

    private FTPClientPool() {
    }

    public static final FTPClient getFtpClient(String hostname, String un, String pw) throws IOException {
        final FTPClient client = new FTPClient();

        // Attempt to retrieve a connected client
        synchronized (sClientsByHost) {
            final Set<PoolEntry> clients = sClientsByHost.get(hostname);
            if (clients != null && !clients.isEmpty()) {
                final PoolEntry entry = clients.iterator().next();
                clients.remove(entry);
                return entry.getClient();
            }
        }

        // No connected clients were available in the pool -- make a new one
        System.out.println("[FtpClientPool] Opening a new FTP connection to " + hostname);

        client.connect(hostname);
        client.enterLocalPassiveMode(); // Setup for data transfers between client and server
        client.login(un, pw);
        client.setFileType(FTP.BINARY_FILE_TYPE);

        return client;
    }

    public static final void returnFtpClient(String hostname, FTPClient client) throws IOException {
        // Return the client to the pool
        synchronized (sClientsByHost) {
            Set<PoolEntry> clients = sClientsByHost.get(hostname);

            if (clients == null) {
                clients = new HashSet<PoolEntry>();
                sClientsByHost.put(hostname, clients);
            }

            clients.add(new PoolEntry(client));
        }
    }
}
