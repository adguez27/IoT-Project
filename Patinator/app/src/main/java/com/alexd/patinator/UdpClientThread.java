package com.alexd.patinator;


import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;


public class UdpClientThread extends Thread {


    String dstAddress;
    int dstPort;
    private boolean running;
    ConnectionActivity.UdpClientHandler handler;

    DatagramSocket socket;
    InetAddress address;

    public UdpClientThread(String addr, int port, ConnectionActivity.UdpClientHandler handler) {
        super();
        dstAddress = addr;
        dstPort = port;
        this.handler = handler;
        Log.d("Borja ", "UdpClientThread constructor");
    }

    public void setRunning(boolean running){
        this.running = running;
    }

    private void sendState(String state){
        handler.sendMessage(
                Message.obtain(handler,
                        ConnectionActivity.UdpClientHandler.UPDATE_STATE, state));
    }

    @Override
    public void run() {
        sendState("connecting...");
        Log.d("Borja", "UdpClientThread Run Connecting");
        running = true;

        try {
            socket = new DatagramSocket();
            address = InetAddress.getByName(dstAddress);

            // send request
            byte[] bufSend = new byte[256];

            String str = "Hello This is the first message sent" + "\n";
            bufSend = str.getBytes();

            DatagramPacket packet =
                    new DatagramPacket(bufSend, bufSend.length, address, dstPort);
            socket.send(packet);

            sendState("connected");
            Log.d("Borja", "UdpClientThread Run Connected");

            // get response

            byte[] bufReceive = new byte[256];

            packet = new DatagramPacket(bufReceive, bufReceive.length);


            socket.receive(packet);
            String line = new String(packet.getData(), 0, packet.getLength());

            Log.d("Borja", line);


            handler.sendMessage(
                    Message.obtain(handler, ConnectionActivity.UdpClientHandler.UPDATE_MSG, line));

        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(socket != null){
                socket.close();
                handler.sendEmptyMessage(ConnectionActivity.UdpClientHandler.UPDATE_END);
            }
        }

    }
}

