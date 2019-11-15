package com.alexd.patinator;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class Tab3 extends Fragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {

                super.onCreate(savedInstanceState);


        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {


                View view = inflater.inflate(R.layout.tab3, container, false);
                Button myButton =  view.findViewById(R.id.info);
                myButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                                lanzarInfo(view);
                        }
                });

                return view;
        }
        public void lanzarInfo (View view) {
                Intent i = new Intent(getActivity(), InfoActivity.class);
                startActivity(i);
        }
}


