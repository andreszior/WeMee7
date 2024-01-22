package com.example.weMee7.activities;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.example.weMee7.model.dao.ReunionDAO;
import com.example.weMee7.model.entities.Reunion;
import com.example.weMee7.model.entities.Tarea;
import com.example.weMee7.view._SuperActivity;
import com.example.weMee7.view.reunion.InvitadosFragment;
import com.example.wemee7.R;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

//
public class ReunionActivity extends _SuperActivity {


    RecyclerView rcEvento;

    List<Tarea> tareas;

    Reunion reunion;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Fragment fragment = null;

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("meeting")) {
            String idReunion = intent.getStringExtra("id");
            reunion = (Reunion) intent.getParcelableExtra("meeting");
//            //cargarReunion(idReunion);
            reunion.setId(idReunion);
            fragment = ReunionFragment.newInstance(reunion);
        }

        //if (savedInstanceState == null) {
            colocarFragment(fragment);
        //}

        //colocarFragment(new InvitadosFragment());
    }

    //Se llama cuando se acaba el fragment de creacion de tareas, actualizar RC
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    //FUNCIONA
    private void cargarReunion(String idReunion) {
        new ReunionDAO().obtenerRegistroPorId(idReunion, resultado -> {
            reunion = (Reunion) resultado;
        });
    }
}