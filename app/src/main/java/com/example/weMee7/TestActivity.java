package com.example.weMee7;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.weMee7.activities.MenuActivity;
import com.example.weMee7.model.TESTpruebasDAO;
import com.example.weMee7.view.usuario.UsuarioActivity;
import com.example.weMee7.viewmodel.ValidarUsuario;
import com.example.wemee7.R;

public class TestActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
    }

    public void iniciarApp (View view){

    }

    public void testAndres (View view){


    }

    public void testCarlos (View view){
        Intent intent = new Intent(this, MenuActivity.class);

        //Lanzar la nueva actividad
        startActivity(intent);

    }

    public void testDaniel (View view){
        Intent intent = new Intent(this, UsuarioActivity.class);
        startActivity(intent);
    }
}