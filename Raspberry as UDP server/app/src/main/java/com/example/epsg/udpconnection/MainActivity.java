package com.example.epsg.udpconnection;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManager;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Date;
import java.util.Enumeration;


public class MainActivity extends AppCompatActivity {


    private final static String TAG = MainActivity.class.getSimpleName();

    TextView infoIp, infoPort;
    TextView textViewState, textViewPrompt;

    static final int UdpServerPORT = 4445;
    UdpServerThread udpServerThread;

    GPIOController controller_BCM06;  // GPIO OUT
    GPIOController controller_BCM21;  // GPIO IN
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
/*
        infoIp = (TextView) findViewById(R.id.infoipText);
        infoPort = (TextView) findViewById(R.id.infoportText);
        textViewState = (TextView)findViewById(R.id.stateText);
        textViewPrompt = (TextView)findViewById(R.id.promptText);

        infoIp.setText(getIpAddress());
        Log.d("Borja", getIpAddress());
        infoPort.setText(String.valueOf(UdpServerPORT));
*/
        controller_BCM06 = new GPIOController("BCM6", Gpio.DIRECTION_OUT_INITIALLY_LOW);  // para GPIO
        controller_BCM21 = new GPIOController("BCM21", Gpio.DIRECTION_IN);  // para GPIO
    }

    /*
    @Override
    protected void onStart() {
        udpServerThread = new UdpServerThread(UdpServerPORT);
        udpServerThread.start();
        super.onStart();
    }

    @Override
    protected void onStop() {
        if(udpServerThread != null){
            udpServerThread.setRunning(false);
            udpServerThread = null;
        }

        super.onStop();
    }
    */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        controller_BCM06.destroy();  // GPIO
        controller_BCM21.destroy();  // GPIO


    }

    /*
    private void updateState(final String state){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textViewState.setText(state);
            }
        });
    }

    private void updatePrompt(final String prompt){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textViewPrompt.append(prompt);
            }
        });
    }

    private class UdpServerThread extends Thread{

        int serverPort;
        DatagramSocket socket;

        boolean running;

        public UdpServerThread(int serverPort) {
            super();
            this.serverPort = serverPort;
        }

        public void setRunning(boolean running){
            this.running = running;
        }

        @Override
        public void run() {

            running = true;

            try {
                updateState("Starting UDP Server");
                socket = new DatagramSocket(serverPort);

                updateState("UDP Server is running");
                Log.e(TAG, "UDP Server is running");

                while(running){
                    byte[] bufReceive = new byte[256];

                    // receive request
                    DatagramPacket packet = new DatagramPacket(bufReceive, bufReceive.length);
                    socket.receive(packet);     //this code block the program flow


                    //Borja
                    String message = new String(packet.getData());


                    // send the response to the client at "address" and "port"
                    InetAddress address = packet.getAddress();
                    int port = packet.getPort();

                    updatePrompt("Request from: " + address + ":   " + port + "   \n" + message + "\n");

                    String dString = new String(new Date().toString() + "\n" +
                            "Your address " + address.toString() + ":" + String.valueOf(port) +  "\n" +
                            "I have nothing to responde" + "\n");

                    byte[] bufSend = new byte[256];

                    bufSend = dString.getBytes();
                    packet = new DatagramPacket(bufSend, bufSend.length, address, port);
                    socket.send(packet);

                }

                Log.e(TAG, "UDP Server ended");

            } catch (SocketException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if(socket != null){
                    socket.close();
                    Log.e(TAG, "socket.close()");
                }
            }
        }
    }
    */
    private String getIpAddress() {
        String ip = "";
        try {
            Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface
                    .getNetworkInterfaces();
            while (enumNetworkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = enumNetworkInterfaces
                        .nextElement();
                Enumeration<InetAddress> enumInetAddress = networkInterface
                        .getInetAddresses();
                while (enumInetAddress.hasMoreElements()) {
                    InetAddress inetAddress = enumInetAddress.nextElement();

                    if (inetAddress.isSiteLocalAddress()) {
                        ip += "SiteLocalAddress: "
                                + inetAddress.getHostAddress() + "\n";
                    }

                }

            }

        } catch (SocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            ip += "Something Wrong! " + e.toString() + "\n";
        }

        return ip;
    }
    /*
    // this was created just for testing purposes
        public void onButton(View view) {
            controller_BCM06.setON();
        }

        public void offButton(View view) {
            controller_BCM06.setOFF();
        }

        public void switchButton(View view) {
            controller_BCM06.switchPin();
        }

    */
}
