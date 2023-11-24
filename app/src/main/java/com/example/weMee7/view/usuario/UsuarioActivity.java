package com.example.weMee7.view.usuario;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.weMee7.model.entities.Usuario;
import com.example.wemee7.R;
import com.google.firebase.auth.FirebaseUser;

/**
 * Activity que gestiona los fragments
 * relacionados con el registro y validacion de usuarios,
 * perfil del usuario logueado
 * y lista de reuniones del usuario
 */
public class UsuarioActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario);

        //Llamar a la primera actividad
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fcvUsuario, AuthPhoneFragment.class, null)
                .commit();
    }
}