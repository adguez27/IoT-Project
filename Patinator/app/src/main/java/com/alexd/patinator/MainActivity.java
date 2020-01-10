package com.alexd.patinator;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Layout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ViewUtils;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.FragmentTabHost;


import com.google.firebase.firestore.FirebaseFirestore;



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
    private NotificationManager notificationManager;
    static final String CANAL_ID = "mi_canal";
    static final int NOTIFICACION_ID = 1;

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
    public void lanzarChat(View view) {
        Intent i = new Intent(this, ChatActivity.class);
        startActivity(i);
    }
    public void lanzarAjustes(View view) {
        Intent i = new Intent(this, Preferencias.class);
        startActivity(i);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_about_us) {
            lanzarAcercaDe(null);
            return true;
        }
        if (id == R.id.Soporte) {
            lanzarChat(null);
            return true;
        }
        if (id == R.id.Ajustes) {
            lanzarAjustes(null);
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
                    notificationManager =(NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        NotificationChannel notificationChannel = new NotificationChannel(
                                CANAL_ID, "Mis Notificaciones",
                                NotificationManager.IMPORTANCE_DEFAULT);
                        notificationChannel.setDescription("Descripcion del canal");
                        notificationManager.createNotificationChannel(notificationChannel);
                    }
                    NotificationCompat.Builder notificacion =
                            new NotificationCompat.Builder(MainActivity.this, CANAL_ID)
                                    .setSmallIcon(R.mipmap.ic_launcher)
                                    .setContentTitle("Patinete en marcha")
                                    .setContentText("Recuerde bloquearlo al llegar a su destino.")
                                    .setWhen(System.currentTimeMillis() + 1000 * 60 * 60)
                                    .setLights(Color.RED, 3000, 1000);
                    PendingIntent intencionPendiente = PendingIntent.getActivity(
                            this, 0, new Intent(this, Tab2.class), 0);
                    notificacion.setContentIntent(intencionPendiente);
                    notificationManager.notify(NOTIFICACION_ID, notificacion.build());
                   final Button button =  findViewById(R.id.bSeleccionarPatinete);
                    button.setText("Bloquear");
                    Button b2 = findViewById(R.id.info);
                    b2.setVisibility(View.GONE);
                    final TextView text = findViewById(R.id.tab2txt);
                    text.setText("Pulsa el botón para bloquear el patinete");
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.Theme_AppCompat_Dialog_Alert);
                            builder.setCancelable(true);
                            builder.setTitle("Finalizar trayecto");
                            builder.setMessage("¿Está seguro que quiere bloquear el patinete?");
                            builder.setPositiveButton("Confirmar",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            block();
                                        }
                                    });
                            builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });

                          //  AlertDialog dialog = builder.create();
                            builder.show();


                        }
                    });
                    sendMessage("StartUp");      // per a MQTT
                    Log.d("Borja", "Sending StartUp");
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
        Toast.makeText(this, "Recibiendo: " + topic + "-> " + payload, Toast.LENGTH_LONG).show();
        Log.d(TAG, "Recibiendo: " + topic + "-> " + payload);
        protocolHandler(payload);

    }


    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {   // per a MQTT
        Log.d(TAG, "Entrega completa");
    }


    private void sendMessage(String protocolMsg) {   // per a MQTT
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

    public void block(){
        notificationManager.cancel(NOTIFICACION_ID);
        Button button =  findViewById(R.id.bSeleccionarPatinete);
        button.setText("Desbloquear");
        TextView text = findViewById(R.id.tab2txt);
        text.setText("Pulsa el botón para desbloquear el patinete");
        Button b2 = findViewById(R.id.info);
        b2.setVisibility(View.VISIBLE);
           sendMessage("ShutDown");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SeleccionPatinete(view);
            }
        });
    }
    private void sendACKMessage(String protocolMsg) {   // per a MQTT
        try {
            Log.i(TAG, "Publicando mensaje: " + protocolMsg);
            MqttMessage message = new MqttMessage(protocolMsg.getBytes());
            message.setQos(qos);
            message.setRetained(false);
            client.publish(topicRoot + "patinator", message);
        } catch (MqttException e) {
            Log.e(TAG, "Error al publicar.", e);
        }
    }

    private void protocolHandler(String message) {    // per a MQTT

        if (message.equals(new String("StartUpACK"))) {
            Toast.makeText(this, "Desbloqueao", Toast.LENGTH_LONG).show();
            Log.d(TAG, "Recibido: " + "-> " + message);
        } else if (message.equals(new String("ShutDownACK"))) {
            Toast.makeText(this, "Bloqueao", Toast.LENGTH_LONG).show();
            Log.d(TAG, "Recibido: " + "-> " + message);
        } else if (message.equals(new String("SwitchACK"))) {
            Log.d(TAG, "Recibido: " + "-> " + message);
        } else if (message.equals(new String("ShutDown"))) {
            Log.d(TAG, "Recibido: " + "-> " + message);
        } else {
            Toast.makeText(this, "Error: Message received not according protocol " + message, Toast.LENGTH_LONG).show();
                Log.e(TAG, "Error: Message received not according protocol " + message);

            }
        }
    }
