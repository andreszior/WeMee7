package com.example.weMee7;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.example.weMee7.model.TESTpruebasDAO;
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

    }

    public void testDaniel (View view){
        new TESTpruebasDAO().borrarDatos();
    }
}