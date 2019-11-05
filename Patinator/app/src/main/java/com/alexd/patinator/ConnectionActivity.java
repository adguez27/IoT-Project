package com.alexd.patinator;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ConnectionActivity extends AppCompatActivity {

    TextView textViewState, textViewRx, textViewDevice;
    EditText editTextDevice;

    private String patineteID;
    private int dstPort;
    private String dstAddress;


    UdpClientHandler udpClientHandler;
    UdpClientThread udpClientThread;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.connection);

        patineteID = ((Aplicacion) getApplication()).getPatineteID();
        dstPort = ((Aplicacion) getApplication()).getDstPort();
        dstAddress = ((Aplicacion) getApplication()).getDstAddress();

        udpClientHandler = new UdpClientHandler(this);

        udpClientThread = new UdpClientThread(dstAddress, dstPort, udpClientHandler);
        udpClientThread.start();

        textViewDevice = (TextView) findViewById(R.id.device);

        textViewState = (TextView)findViewById(R.id.state);
        textViewRx = (TextView)findViewById(R.id.received);

        textViewDevice.setText(patineteID);

    }

    private void updateState(String state){
        textViewState.setText(state);
    }

    private void updateRxMsg(String rxmsg){
        textViewRx.append(rxmsg + "\n");
    }

    private void clientEnd(){
        udpClientThread = null;
//        buttonConnect.setEnabled(true);

    }



    public static class UdpClientHandler extends Handler {
        public static final int UPDATE_STATE = 0;
        public static final int UPDATE_MSG = 1;
        public static final int UPDATE_END = 2;
        private ConnectionActivity parent;


        public UdpClientHandler(ConnectionActivity parent) {
            super();
            this.parent = parent;
        }

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what){
                case UPDATE_STATE:
//                    parent.updateState((String)msg.obj);
                    break;
                case UPDATE_MSG:
//                    parent.updateRxMsg((String)msg.obj);
                    break;
                case UPDATE_END:
                    parent.clientEnd();
                    break;
                default:
                    super.handleMessage(msg);
            }

        }
    }

}