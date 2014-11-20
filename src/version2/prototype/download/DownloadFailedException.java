package version2.prototype.download;

@SuppressWarnings("serial")
public class DownloadFailedException extends Exception {

    public DownloadFailedException() {
        super();
    }

    public DownloadFailedException(String message) {
        super(message);
    }

}
