package com.alexd.patinator;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTabHost;

public class MainActivity extends AppCompatActivity {

    private FragmentTabHost tabHost;

    private int dstSocketPort;
    private String tipoPatinete;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        tabHost = findViewById(android.R.id.tabhost);
        tabHost.setup(this,
                getSupportFragmentManager(),android.R.id.tabcontent);
        tabHost.addTab(tabHost.newTabSpec("tab1").setIndicator("",getDrawable(R.drawable.home) ),
                Tab1.class, null);

        tabHost.addTab(tabHost.newTabSpec("tab3").setIndicator("", getDrawable(R.drawable.qrcode)),
                Tab3.class,  null);
        tabHost.addTab(tabHost.newTabSpec("tab4").setIndicator("",getDrawable(R.drawable.profile)),
                Tab4.class,  null);
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
        startActivity(i);
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



}



