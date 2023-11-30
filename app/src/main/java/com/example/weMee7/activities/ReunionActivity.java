package com.example.weMee7.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.example.weMee7.model.entities.Reunion;
import com.example.wemee7.R;

public class ReunionActivity extends AppCompatActivity {

    TextView detalles;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reunion_detalle);
        Reunion reunion = new Reunion();
        reunion.setNombre(getIntent().getStringExtra("nombre"));
        reunion.setDescripcion(getIntent().getStringExtra("descripcion"));
        reunion.setLugar(getIntent().getStringExtra("lugar"));
        //reunion.setFechaHora(TimeUtils.fechaHoraToTimestamp(getIntent().getStringExtra("fechaHora"), "00:00"));
        detalles = findViewById(R.id.detalles);
        detalles.setText(reunion.getNombre());
    }
}