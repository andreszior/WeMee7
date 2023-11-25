package com.example.weMee7.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.weMee7.comun.ListAdapter;
import com.example.weMee7.model.entities.Reunion;
import com.example.wemee7.R;

import java.util.ArrayList;
import java.util.List;

public class MenuActivity extends AppCompatActivity {

    List<Reunion> reuniones;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        init();
    }

    public void init(){
    reuniones = new ArrayList<>();
    reuniones.add(new Reunion("1","Cumpleaños Carlos","lorem ipsum", "Bar Santianes,17", "04/02/2024 18:00"));
    reuniones.add(new Reunion("1","reunion2","lorem ipsum", "lugar2", "18.00"));
    reuniones.add(new Reunion("1","reunion3","lorem ipsum", "lugar3", "18.00"));

        ListAdapter listAdapter = new ListAdapter(reuniones, this);
        RecyclerView recyclerView = findViewById(R.id.listRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(listAdapter);
    }
}