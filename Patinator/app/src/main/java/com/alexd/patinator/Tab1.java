package com.alexd.patinator;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

public class Tab1 extends Fragment {
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View view= inflater.inflate(R.layout.tab1,container,false);

        return view;
    }
    public void lanzarAcercaDe(View view) {
        Intent i = new Intent(getActivity(), AcercaDeActivity.class);
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

