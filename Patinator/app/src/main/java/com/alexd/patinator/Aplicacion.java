package com.alexd.patinator;

import android.app.Application;
import android.util.Log;

public class Aplicacion extends Application {

    private String patineteID;

    private int dstPort;
    private String dstIPAddress;
    private String tipoPatinete;

    public String getPatineteID() {
        return patineteID;
    }

    public void setPatineteID(String patineteID) {
        this.patineteID = patineteID;

        String[] tokens = patineteID.split("-");

        this.tipoPatinete = tokens[0];
        this.dstPort = Integer.valueOf(tokens[1]);
        this.dstIPAddress = tokens[2];

        this.dstPort = 4445;
        this.dstIPAddress = "192.168.1.252";


        Log.i("Borja2", this.patineteID);
        Log.i("Borja2", this.tipoPatinete);
        Log.i("Borja2", String.valueOf(this.dstPort));
        Log.i("Borja2", this.dstIPAddress);

    }

    public int getDstPort() {
        return dstPort;
    }

    public void setDstPort(int dstPort) {
        this.dstPort = dstPort;
    }

    public String getDstIPAddress() {
        return dstIPAddress;
    }

    public void setDstIPAddress(String dstIPAddress) {
        this.dstIPAddress = dstIPAddress;
    }

    public String getTipoPatinete() {
        return tipoPatinete;
    }

    public void setTipoPatinete(String tipoPatinete) {
        this.tipoPatinete = tipoPatinete;
    }
}
