package com.alexd.patinator;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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


                return view;
        }
}


