package com.example.epsg.udpconnection;

import android.util.Log;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.GpioCallback;
import com.google.android.things.pio.PeripheralManager;

import java.io.IOException;

public class GPIOController {

    private static final String TAG = "Borja_GPIOController:";
    private final String pin_Name;
    private Gpio gpio;
    private Integer gpioStaus;
    private String function;
    private MainActivity mainActivity;
    PeripheralManager manager;


    public GPIOController(String pin_Name, Integer direction, MainActivity mainActivity, String function) {
        this.pin_Name = pin_Name;
        this.gpioStaus = Gpio.ACTIVE_HIGH;
        this.manager = PeripheralManager.getInstance();
        this.function = function;
        this.mainActivity = mainActivity;
        try {
            this.gpio = this.manager.openGpio(pin_Name);
//            this.gpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_HIGH);
            this.gpio.setDirection(direction);
            if (direction == Gpio.DIRECTION_IN) {
                this.gpio.registerGpioCallback(callback);
                this.gpio.setEdgeTriggerType(Gpio.EDGE_BOTH);
            }
        } catch (
                IOException e) {
            Log.e(TAG, "Error en el API PeripheralIO", e);
        }
    }

    private GpioCallback callback = new GpioCallback() {
        @Override
        public boolean onGpioEdge(Gpio gpio) {
            try {
                Log.e(TAG, "cambio botón " + Boolean.toString(gpio.getValue()) + " " + pin_Name);
                if (function.equals(new String("posicion")) && (gpio.getValue() == false) ){mainActivity.sendMQTTMessage2("tumbado");}
                else if (function.equals(new String("posicion")) && (gpio.getValue() == true)) {mainActivity.sendMQTTMessage2("depie");}
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true; // 5. devolvemos true para mantener callback activo
        }
    };



    public void switchPin() {
        Log.i(TAG, "at switch");

        try {
            if (gpioStaus == Gpio.ACTIVE_HIGH) {
                gpio.setValue(false);
                gpioStaus = Gpio.ACTIVE_LOW;
                Log.i(TAG, "LED OFF at switch " + pin_Name);
            } else {
                gpio.setValue(true);
                gpioStaus = Gpio.ACTIVE_HIGH;
                Log.i(TAG, "LED ON at switch "  + pin_Name);
            }
        } catch (IOException e) {
            Log.e(TAG, "Error en el API PeripheralIO", e);
        }
    }

    public void setON() {
        try {
            gpio.setValue(true);
            gpioStaus = Gpio.ACTIVE_HIGH;
            Log.i(TAG, "LED ON "  + pin_Name);
        } catch (IOException e) {
            Log.e(TAG, "Error en el API PeripheralIO at ON", e);
        }
    }

    public void setOFF() {
        try {
            gpio.setValue(false);
            gpioStaus = Gpio.ACTIVE_LOW;
            Log.i(TAG, "LED OFF "  + pin_Name);
        } catch (IOException e) {
            Log.e(TAG, "Error en el API PeripheralIO at OFF", e);
        }
    }


    public void destroy() {
        if (gpio != null) {  // 6. Cerramos recursos
            gpio.unregisterGpioCallback(callback);
            try {
                gpio.close();
            } catch (IOException e) {
                Log.e(TAG, "Error al cerrar gpio.", e);
            }
        }
    }
}
