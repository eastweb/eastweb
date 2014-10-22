package edu.sdstate.eastweb.prototype;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.Callable;

public class UdpProtocol {

    public UdpProtocol()
    {

    }

    public static UdpProtocol Instance ()
    {
        if(instance == null) {
            instance = new UdpProtocol();
        }

        return instance;
    }
    private static UdpProtocol instance;

    public void Client(String FrameworkName, String Payload) throws Exception
    {
        @SuppressWarnings("resource")
        DatagramSocket clientSocket = new DatagramSocket();
        byte[] dataPackage = String.format(FrameworkName, Payload).getBytes();
        DatagramPacket sendPacket = new DatagramPacket(dataPackage, dataPackage.length, InetAddress.getByName("255.255.255.255"), 11000);

        clientSocket.setBroadcast(true);
        clientSocket.send(sendPacket);
    }

    // will be done on the scheduler side
    public void Server(String FrameworkName, String Payload) throws Exception
    {
        @SuppressWarnings("resource")
        DatagramSocket serverSocket = new DatagramSocket(11000);
        byte[] receiveData = new byte[1024];

        while(true)
        {
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            serverSocket.receive(receivePacket);
            String sentence = new String( receivePacket.getData());
            System.out.println("RECEIVED: " + sentence);

        }
    }
}
