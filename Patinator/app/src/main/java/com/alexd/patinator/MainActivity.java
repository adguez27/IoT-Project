package com.alexd.patinator;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentTabHost;

import com.google.android.libraries.places.api.Places;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;    //MQTT
import org.eclipse.paho.client.mqttv3.MqttCallback;        //MQTT
import org.eclipse.paho.client.mqttv3.MqttClient;              //MQTT
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;   //MQTT
import org.eclipse.paho.client.mqttv3.MqttException;       //MQTT
import org.eclipse.paho.client.mqttv3.MqttMessage;         //MQTT
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;  //MQTT

import static com.example.borjacarbo.comun.Mqtt.broker;
import static com.example.borjacarbo.comun.Mqtt.clientId;
import static com.example.borjacarbo.comun.Mqtt.qos;
import static com.example.borjacarbo.comun.Mqtt.topicRoot;

// public class MainActivity extends AppCompatActivity {
public class MainActivity extends AppCompatActivity implements MqttCallback {  //MQTT

    private final static String TAG = "Borja_" + MainActivity.class.getSimpleName();


    private FragmentTabHost tabHost;

    private int dstSocketPort;
    private String tipoPatinete;

    MqttClient client;  //MQTT


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), "@string/google_api_key");
        }
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        tabHost = findViewById(android.R.id.tabhost);
        tabHost.setup(this,
                getSupportFragmentManager(), android.R.id.tabcontent);
        tabHost.addTab(tabHost.newTabSpec("tab1").setIndicator("",getDrawable(R.drawable.home) ),
                Tab1.class, null);

        tabHost.addTab(tabHost.newTabSpec("tab2").setIndicator("", getDrawable(R.drawable.qrcode)),
                Tab2.class,  null);
        tabHost.addTab(tabHost.newTabSpec("tab3").setIndicator("",getDrawable(R.drawable.profile)),
                Tab3.class,  null);


        try {  // Para MQTT
            Log.i(TAG, "Conectando al broker " + broker);
            client = new MqttClient(broker, clientId, new MemoryPersistence());
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            connOpts.setKeepAliveInterval(60);
            connOpts.setWill(topicRoot + "patinator", "ShutDown".getBytes(),
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
    //funcion para solicitar permisos
    public static void solicitarPermiso(final String permiso, String
            justificacion, final int requestCode, final Activity actividad) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(actividad,
                permiso)){
            new AlertDialog.Builder(actividad)
                    .setTitle("Solicitud de permiso")
                    .setMessage(justificacion)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            ActivityCompat.requestPermissions(actividad,
                                    new String[]{permiso}, requestCode);
                        }})
                    .show();
        } else {
            ActivityCompat.requestPermissions(actividad,
                    new String[]{permiso}, requestCode);
        }
    }
    //crea el menú

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

//lanza actividad del lector de qr
    public void SeleccionPatinete(View view) {
        Intent i = new Intent(this, SeleccionDePatineteActivity.class);

        startActivityForResult(i, 100);

    }

    public void lanzarAcercaDe(View view) {
        Intent i = new Intent(this, AcercaDeActivity.class);
        startActivity(i);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_about_us) {
            lanzarAcercaDe(null);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult (int requestCode, int resultCode, Intent data) {
        Log.d("Borja", "onActivityResult_main " + Integer.toString(requestCode));
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 100:
                Log.d("Borja", "onActivityResult_main " + Integer.toString(requestCode));

                if (resultCode == RESULT_OK) {
//                    Intent i = new Intent(this, ConnectionActivity.class);
//                    startActivityForResult(i, 200);

                    sendMessage("StartUp");      // per a MQTT
                }
                break;
            case 200:
                Log.d("Borja", "onActivityResult_main " + Integer.toString(requestCode));
                break;
            default:
                Log.d("Borja", "onActivityResult_main " + Integer.toString(requestCode));
        }

    }

    @Override
    protected void onDestroy() {   // per a MQTT
        try {   // MQTT
            Log.i(TAG, "Desconectado");
            client.disconnect();
        } catch (MqttException e) {
            Log.e(TAG, "Error al desconectar.", e);
        }
        super.onDestroy();
    }


    @Override
    public void connectionLost(Throwable cause) {  // per a MQTT
        Log.d(TAG, "Conexión perdida");
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {   // per a MQTT
        String payload = new String(message.getPayload());
        Log.d(TAG, "Recibiendo: " + topic + "-> " + payload);
        protocolHandler(payload);

    }


    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {   // per a MQTT
        Log.d(TAG, "Entrega completa");
    }


    private void sendMessage(String protocolMsg) {   // per a MQTT
        try {
            Log.i(TAG, "Publicando mensaje: " + topicRoot + "patinete " + protocolMsg);
            MqttMessage message = new MqttMessage(protocolMsg.getBytes());
            message.setQos(qos);
            message.setRetained(false);
            client.publish(topicRoot + "patinete", message);
        } catch (MqttException e) {
            Log.e(TAG, "Error al publicar.", e);
        }
    }

    private void sendACKMessage(String protocolMsg) {   // per a MQTT
        try {
            Log.i(TAG, "Publicando mensaje: " + protocolMsg);
            MqttMessage message = new MqttMessage(protocolMsg.getBytes());
            message.setQos(qos);
            message.setRetained(false);
            client.publish(topicRoot + "patinete", message);
        } catch (MqttException e) {
            Log.e(TAG, "Error al publicar.", e);
        }
    }

    private void protocolHandler(String message) {    // per a MQTT

        if (message.equals(new String("StartUpACK"))) {
            Log.d(TAG, "Recibido: " + "-> " + message);
        } else if (message.equals(new String("ShutDownACK"))) {
            Log.d(TAG, "Recibido: " + "-> " + message);
        } else if (message.equals(new String("SwitchACK"))) {
            Log.d(TAG, "Recibido: " + "-> " + message);
        } else if (message.equals(new String("ShutDown"))) {
            Log.d(TAG, "Recibido: " + "-> " + message);
        } else {
                Log.e(TAG, "Error: Message received not according protocol " + message);

            }
        }
    }

