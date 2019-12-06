package com.example.epsg.udpconnection;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManager;

// import org.eclipse.paho.client.mqttv3.MqttClient;
// import org.eclipse.paho.client.mqttv3.MqttException;
//import org.eclipse.paho.client.mqttv3.MqttMessage;
//import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Date;
import java.util.Enumeration;

import static com.example.borjacarbo.comun.Mqtt.*;
// import static com.example.borja.comun.Mqtt.broker;
// import static com.example.borja.comun.Mqtt.clientId;


public class MainActivity extends AppCompatActivity implements MqttCallback {
    private final static String TAG = "Borja_" + MainActivity.class.getSimpleName();

    TextView infoIp, infoPort;
    TextView textViewState, textViewPrompt;

    static final int UdpServerPORT = 4445;
    UdpServerThread udpServerThread;

    GPIOController controller_BCM06;  // GPIO OUT
    GPIOController controller_BCM21;  // GPIO IN

    MqttClient client;  //MQTT

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Configuracion de la communicacion
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

        try {  // Para MQTT
            Log.i(TAG, "Conectando al broker " + broker);
            client = new MqttClient(broker, clientId, new MemoryPersistence());
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            connOpts.setKeepAliveInterval(60);
            connOpts.setWill(topicRoot + "patinete", "ShutDown".getBytes(),
                    qos, false);

            client.connect(connOpts);
        } catch (MqttException e) {
            Log.e(TAG, "Error al conectar.", e);
        }

        try {  // Para MQTT
            Log.i(TAG, "Suscribiendo a " + topicRoot + "patinete");
            client.subscribe(topicRoot + "patinete", qos);
            client.setCallback(this);
        } catch (MqttException e) {
            Log.e(TAG, "Error al suscribir.", e);
        }


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
        try {   // MQTT
            Log.i(TAG, "Desconectado");
            client.disconnect();
        } catch (MqttException e) {
            Log.e(TAG, "Error al desconectar.", e);
        }
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
    public void sendMQTTMessage(View view) {
        try {
            Log.i(TAG, "Publicando mensaje: " + "hola");
            MqttMessage message = new MqttMessage("hola".getBytes());
            message.setQos(qos);
            message.setRetained(false);
            client.publish(topicRoot + "patinator", message);
        } catch (MqttException e) {
            Log.e(TAG, "Error al publicar.", e);
        }
    }


    @Override
    public void connectionLost(Throwable cause) {
        Log.d(TAG, "Conexión perdida");
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        String payload = new String(message.getPayload());
        Log.d(TAG, "Recibiendo: " + topic + " -> " + payload);
        protocolHandler(payload);

    }


    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        Log.d(TAG, "Entrega completa");
    }



    private void sendACKMessage(String protocolMsg) {
        try {
            Log.i(TAG, "Publicando mensaje: " + topicRoot + "patinator " + protocolMsg);
            MqttMessage message = new MqttMessage(protocolMsg.getBytes());
            message.setQos(qos);
            message.setRetained(false);
            client.publish(topicRoot + "patinator", message);
        } catch (MqttException e) {
            Log.e(TAG, "Error al publicar.", e);
        }
    }

    private void protocolHandler(String message) {

        Log.d(TAG, "Recibido: " + "-> " + message);

        if (message.equals(new String("StartUp"))) {
            controller_BCM06.setON();
            sendACKMessage(message + "ACK");
        } else if (message.equals(new String("ShutDown"))) {
            controller_BCM06.setOFF();
            sendACKMessage(message + "ACK");
        } else if (message.equals(new String("Switch"))) {
            controller_BCM06.switchPin();
            sendACKMessage(message + "ACK");
        } else {
            Log.e(TAG, "Error: Message received not according protocol " + message);

        }

    }
}


