package com.alexd.patinator;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

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
        Button myButton =  view.findViewById(R.id.bMapa);
        myButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lanzarMaps(view);
            }
        });
        return view;
    }

    public void lanzarMaps (View view) {
        Intent i = new Intent(getActivity(), MapsActivity2.class);
        startActivity(i);
    }


}

