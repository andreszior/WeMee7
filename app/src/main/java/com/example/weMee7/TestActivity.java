package com.example.weMee7;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.weMee7.activities.MenuActivity;
import com.example.weMee7.activities.ReunionActivity;
import com.example.weMee7.view._SuperActivity;
import com.example.weMee7.view.usuario.UsuarioActivity;
import com.example.wemee7.R;

public class TestActivity extends _SuperActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
    }

    public void iniciarApp (View view){

    }

    //
    public void testAndres (View view){
        if(noHayConexion())
            return;

        Intent intent = new Intent(this, ReunionActivity.class);
        startActivity(intent);

    }

    public void testCarlos (View view){
        Intent intent = new Intent(this, MenuActivity.class);

        //Lanzar la nueva actividad
        startActivity(intent);

    }

    public void testDaniel (View view){
        if(noHayConexion())
            return;
        Intent intent = new Intent(this, UsuarioActivity.class);
        startActivity(intent);
    }
}