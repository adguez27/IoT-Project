package com.alexd.patinator;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Tab3 extends Fragment {
@Override
public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        }

@Override
public View onCreateView(LayoutInflater inflater, ViewGroup container,
                         Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.tab3,container,false);

        Button cerrarSesion =(Button) view.findViewById(R.id.btn_cerrar_sesion);
        cerrarSesion.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                        AuthUI.getInstance().signOut(getActivity()) .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override public void onComplete(@NonNull Task<Void> task) {
                                        Intent i = new Intent(getActivity(),LoginActivity.class);
                                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                                                | Intent.FLAG_ACTIVITY_NEW_TASK
                                                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(i);
                                        getActivity().finish();
                                }
                        });
                }
        });

        FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();
        TextView nombre = view.findViewById(R.id.nombre);
        nombre.setText(usuario.getDisplayName().toString());
        TextView correo = view.findViewById(R.id.correo);
        correo.setText(usuario.getEmail());
        return view;

        }
}

