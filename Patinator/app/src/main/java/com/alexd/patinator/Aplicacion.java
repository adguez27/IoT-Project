package com.alexd.patinator;

import android.app.Application;
import android.util.Log;

public class Aplicacion extends Application {

    private String patineteID;

    private int dstPort;
    private String dstAddress;
    private String tipoPatinete;

    public String getPatineteID() {
        return patineteID;
    }

    public void setPatineteID(String patineteID) {
        this.patineteID = patineteID;

        String[] tokens = patineteID.split("-");

        this.tipoPatinete = tokens[0];
        this.dstPort = Integer.valueOf(tokens[1]);
        this.dstAddress = tokens[2];
        Log.i("Borja2", this.patineteID);
        Log.i("Borja2", String.valueOf(this.dstPort));
        Log.i("Borja2", this.dstAddress);

        Log.i("Borja2", this.tipoPatinete);
    }

    public int getDstPort() {
        return dstPort;
    }

    public void setDstPort(int dstPort) {
        this.dstPort = dstPort;
    }

    public String getDstAddress() {
        return dstAddress;
    }

    public void setDstAddress(String dstAddress) {
        this.dstAddress = dstAddress;
    }

    public String getTipoPatinete() {
        return tipoPatinete;
    }

    public void setTipoPatinete(String tipoPatinete) {
        this.tipoPatinete = tipoPatinete;
    }
}
