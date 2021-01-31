package com.jgangl.cat_o_matic;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class UDPSocket implements Runnable   {
    private volatile  int value;


    @Override
    public void run() {
    }


/*
    private String ipAddr;
    private int port;
    private DatagramSocket UDPSocket;
    private InetAddress address;

    private byte[] dataToSend;

    public UDPSocket(String addr, int port) {
        this.ipAddr = addr;
        this.port = port;
    }

    @Override
    public void run(){
        try {
            Initialize(InetAddress.getByName(address));

            byte[] data = Sdata.getBytes();
            //SendInstruction(data, port);




            DatagramPacket packet = new DatagramPacket(data, data.length, address, port);
            UDPSocket.send(packet);

            final int taille = 1024;
            final byte[] buffer = new byte[taille];

            //DatagramPacket packetreponse = null;
            DatagramPacket packetreponse = new DatagramPacket(buffer, buffer.length);

            UDPSocket.receive(packetreponse); //Seems to error here
            //textView.setText(new String(packetreponse.getData()));
            //textView.invalidate();
            //textView.requestLayout();
            //DisplayData(packetreponse);

        } catch (Exception e) {
            e.printStackTrace();
            //textView.setText("Hello");
        }
    }



    //  Initializes a socket with the parameters retrieved in the graphical interface for sending data
    public void Initialize(InetAddress address) {
        try {
            this.UDPSocket = new DatagramSocket();
            this.address = address;
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

*/
}
