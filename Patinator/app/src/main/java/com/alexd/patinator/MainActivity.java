package com.alexd.patinator;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
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
        tabHost.addTab(tabHost.newTabSpec("tab1").setIndicator("Main Tab"),
                Tab1.class, null);
        tabHost.addTab(tabHost.newTabSpec("tab2").setIndicator("Tab 2"),
                Tab2.class, null);
        tabHost.addTab(tabHost.newTabSpec("tab3").setIndicator("Tab 3"),
                Tab3.class,  null);
        tabHost.addTab(tabHost.newTabSpec("tab4").setIndicator("Tab 4"),
                Tab4.class,  null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void SeleccionPatinete(View view) {
        Intent i = new Intent(this, SeleccionDePatineteActivity.class);
        startActivityForResult(i, 100);
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



