package com.alexd.patinator;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class SeleccionDePatineteActivity extends AppCompatActivity {
    private CameraSource cameraSource;
    private SurfaceView cameraView;
    private final int MY_PERMISSIONS_REQUEST_CAMERA = 1;
    private String token = "";
    private String tokenanterior = "";
    private boolean connectionNotStarted = true;

    private TextView OutputString;
    public Intent intent;
    private View vista;

    private static final int SOLICITUD_PERMISO_CAMARA = 0;
    private static final int SOLICITUD_PERMISO_INTERNET = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selecciondepatinete);

        OutputString = findViewById(R.id.QR);

        cameraView = (SurfaceView) findViewById(R.id.camera_view);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            solicitarPermiso(Manifest.permission.CAMERA, "Sin el permiso para utilizar la camara para tomar el QR.",
                    SOLICITUD_PERMISO_CAMARA, this);
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            solicitarPermiso(Manifest.permission.INTERNET, "Sin el permiso para conectarse a Internet",
                    SOLICITUD_PERMISO_INTERNET, this);
        }
        initQR();
    }

    public static void solicitarPermiso(final String permiso, String justificacion, final int requestCode, final Activity actividad) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(actividad, permiso)){
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

    @Override public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == SOLICITUD_PERMISO_CAMARA) {
            if (grantResults.length== 1 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (requestCode == SOLICITUD_PERMISO_INTERNET) {
                    if (grantResults.length== 1 &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        //
                    } else {
                        Toast.makeText(this, "Sin el permiso, no puedo utilizar el patinete", Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                Toast.makeText(this, "Sin el permiso, no puedo tomar el QR", Toast.LENGTH_SHORT).show();
            }
        }
    }
    public void initQR() {

        // creo el detector qr
        BarcodeDetector barcodeDetector =
                new BarcodeDetector.Builder(this)
                        .setBarcodeFormats(Barcode.ALL_FORMATS)
                        .build();

        // creo la camara
        cameraSource = new CameraSource
                .Builder(this, barcodeDetector)
                .setRequestedPreviewSize(1600, 1024)
                .setAutoFocusEnabled(true) //you should add this feature
                .build();

        // listener de ciclo de vida de la camara
        cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {

                // verifico si el usuario dio los permisos para la camara
                if (ActivityCompat.checkSelfPermission(SeleccionDePatineteActivity.this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        // verificamos la version de ANdroid que sea al menos la M para mostrar
                        // el dialog de la solicitud de la camara
                        if (shouldShowRequestPermissionRationale(
                                Manifest.permission.CAMERA)) ;
                        requestPermissions(new String[]{Manifest.permission.CAMERA},
                                MY_PERMISSIONS_REQUEST_CAMERA);
                    }
                    return;
                } else {
                    try {
                        cameraSource.start(cameraView.getHolder());
                    } catch (IOException ie) {
                        Log.e("CAMERA SOURCE", ie.getMessage());
                    }
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });

        // preparo el detector de QR
        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
            }


            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();

                if (barcodes.size() > 0) {

                    // obtenemos el token
                    token = barcodes.valueAt(0).displayValue.toString();

                    // verificamos que el token anterior no se igual al actual
                    // esto es util para evitar multiples llamadas empleando el mismo token
                    if (!token.equals(tokenanterior)) {

                        // guardamos el ultimo token proceado
                        tokenanterior = token;
                        ((Aplicacion) getApplication()).setPatineteID(token);

                        Log.d("Borja1", "SeleccionDePatineteActivity " + token);
                        Log.d("Borja1", "SeleccionDePatineteActivity " + tokenanterior + "----");

                        OutputString.setText(token);

                    } else {

                        if (connectionNotStarted) {
                            ((Aplicacion) getApplication()).setPatineteID(token);

                            connectionNotStarted = !connectionNotStarted;

                            Log.d("Borja1", "SeleccionDePatineteActivity Initiating connections towards " + token);

                            intent = new Intent();

                            setResult(RESULT_OK, intent);
                            Log.d("Borja1", "SeleccionDePatineteActivity Initiating connections towards " + token);

                            finish();

                        }
                    }
                }
            }

        });

    }

    @Override
    public void onActivityResult (int requestCode, int resultCode, Intent data) {
        Log.d("Borja", "onActivityResult " + Integer.toString(requestCode));
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 100:
                Log.d("Borja", "onActivityResult " + Integer.toString(requestCode));

                if (resultCode == RESULT_OK) {
                    Intent i = new Intent(this, ConnectionActivity.class);
                    startActivityForResult(i, 200);
                }
                break;
            case 200:
                Log.d("Borja", "onActivityResult " + Integer.toString(requestCode));
                break;
            default:
                Log.d("Borja", "onActivityResult " + Integer.toString(requestCode));
        }

    }
}
