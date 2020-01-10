package com.alexd.patinator;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

public class LoginActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 123;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        login();
    }
    public void login() {
        //inicio de sesion
        FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();

         //String nombre = usuario.getDisplayName();
        /*
        String correo = usuario.getEmail();
        String telefono = usuario.getPhoneNumber();
        Uri urlFoto = usuario.getPhotoUrl();
        String uid = usuario.getUid();
        String proveedor = usuario.getProviderId();
        */

        //Uri urlFoto = usuario.getPhotoUrl();

        if (usuario != null) {
            Toast.makeText(this, "Inicia sesión: " +
                    usuario.getDisplayName()+" - "+ usuario.getEmail(),Toast.LENGTH_LONG).show();
            Intent i = new Intent(this, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                    | Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            /*

            boolean emailVerified = usuario.isEmailVerified();

            if (emailVerified == false){
                usuario.sendEmailVerification();
            }

             */

            startActivity(i);
        } else {
            List<AuthUI.IdpConfig> providers = Arrays.asList(
                    new AuthUI.IdpConfig.EmailBuilder().build(),
                    new AuthUI.IdpConfig.PhoneBuilder().build(),
                    new AuthUI.IdpConfig.GoogleBuilder().build()
                    );
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setLogo(R.mipmap.ic_launcher)
                            .setTheme(R.style.ToolbarTheme)
                            .setAvailableProviders(providers)
                            .build(),
                    RC_SIGN_IN);
            //si no existe usuario
           /* startActivityForResult(AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setLogo(R.mipmap.ic_launcher)
                            .setAvailableProviders(
                                    Arrays.asList(
                                            new AuthUI.IdpConfig.EmailBuilder().build(),
                                            new AuthUI.IdpConfig.GoogleBuilder().build(),
                                            new AuthUI.IdpConfig.PhoneBuilder().build())
                            ).setAllowNewEmailAccounts(true)
                            .setIsSmartLockEnabled(false)
                            .build(),
                    RC_SIGN_IN);*/

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK) {
                login();
                finish();
            } else {

                if (response == null) {
                    Toast.makeText(this,"Cancelado",Toast.LENGTH_LONG).show();
                    return;
                }else if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                    Toast.makeText(this,"Sin conexión a Internet",
                            Toast.LENGTH_LONG).show();
                    return;
                } else if (response.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    Toast.makeText(this,"Error desconocido",
                            Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }
    }

}
