package com.example.weMee7.activities;

        import static android.content.ContentValues.TAG;

        import androidx.annotation.NonNull;
        import androidx.constraintlayout.widget.ConstraintLayout;
        import androidx.fragment.app.Fragment;
        import androidx.fragment.app.FragmentManager;
        import androidx.fragment.app.FragmentTransaction;
        import androidx.recyclerview.widget.LinearLayoutManager;
        import androidx.recyclerview.widget.RecyclerView;

        import android.app.Dialog;
        import android.graphics.Color;
        import android.graphics.drawable.ColorDrawable;
        import android.os.Bundle;
        import android.util.Log;
        import android.view.Gravity;
        import android.view.View;
        import android.view.ViewGroup;
        import android.view.Window;
        import android.widget.ImageButton;
        import android.widget.ImageView;
        import android.widget.LinearLayout;
        import android.widget.TextView;

        import com.example.weMee7.comun.TareaAdapter;
        import com.example.weMee7.comun.TimeUtils;
        import com.example.weMee7.model.entities.Reunion;
        import com.example.weMee7.model.entities.Tarea;
        import com.example.weMee7.view._SuperActivity;
        import com.example.wemee7.R;
        import com.google.android.gms.tasks.OnCompleteListener;
        import com.google.android.gms.tasks.Task;
        import com.google.firebase.Timestamp;
        import com.google.firebase.auth.FirebaseAuth;
        import com.google.firebase.firestore.DocumentReference;
        import com.google.firebase.firestore.DocumentSnapshot;
        import com.google.firebase.firestore.FirebaseFirestore;

        import java.util.ArrayList;
        import java.util.List;

//
public class ReunionActivity extends _SuperActivity {


    RecyclerView rcEvento;

    List<Tarea> tareas;

    Reunion reunion;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reunion_detalle);
        obtenerReunion(getIntent().getStringExtra("id"));
    }

    public void initTareasList(Reunion reunion){
        tareas = new ArrayList<>();
        tareas.add(new Tarea(reunion.getNombre(), "Comprar carbon", "Carbon para la carne", 15, "1"));
        tareas.add(new Tarea(reunion.getNombre(), "Comprar carbon", "Carbon para la carne", 15, "1"));
        tareas.add(new Tarea(reunion.getNombre(), "Comprar carbon", "Carbon para la carne", 15, "1"));
        tareas.add(new Tarea(reunion.getNombre(), "Comprar carbon", "Carbon para la carne", 15, "1"));
        tareas.add(new Tarea(reunion.getNombre(), "Comprar carbon", "Carbon para la carne", 15, "1"));

        TareaAdapter tareaAdapter = new TareaAdapter(tareas, this);
        rcEvento = findViewById(R.id.rvReunionTareas);
        rcEvento.setHasFixedSize(true);
        rcEvento.setLayoutManager(new LinearLayoutManager(this));
        rcEvento.setAdapter(tareaAdapter);
    }

    //Deberia de coger el id que pasa por intent, coge la coleccion, crea una array de reuniones y coge la reunion
    private void obtenerReunion(String id){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference reunionRef = db.collection("reuniones").document(id);

        reunionRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        reunion = document.toObject(Reunion.class);
                        cargarDetallesReunion(reunion);

                        // Ahora tienes el objeto Reunion y puedes usar sus datos
                    } else {
                        Log.d(TAG, "No hay reunion");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    private void cargarDetallesReunion(Reunion reunion){

        TextView tvLugarEvento, tvFechaEvento, tvReunion;
        ImageButton botonTarea;

        tvLugarEvento = findViewById(R.id.tvLugarReunion);
        tvFechaEvento = findViewById(R.id.tvFechaEventos);
        tvReunion = findViewById(R.id.tvReunion);
        botonTarea = findViewById(R.id.boton_add);
        botonTarea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBotonTareaDialog();
            }
        });



        Timestamp fechaReunion = reunion.getFecha();
        tvLugarEvento.setText(reunion.getLugar());
        tvFechaEvento.setText(TimeUtils.timestampToFecha(fechaReunion));
        tvReunion.setText(reunion.getNombre());
        //initTareasList(reunion);
    }

    private void showBotonTareaDialog(){
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.boton_add);

        //ConstraintLayout layoutDetallesEvento = findViewById(R.id.contraintlayout_detalles_evento);
        LinearLayout AddLayout = dialog.findViewById(R.id.layoutAdd);
        LinearLayout UnirseLayout = dialog.findViewById(R.id.layoutUnirse);
        LinearLayout AddTareaLayout = dialog.findViewById(R.id.layoutaddTarea);
        ImageView cancelButton = dialog.findViewById(R.id.cancelButton);

        AddLayout.setVisibility(View.GONE);
        UnirseLayout.setVisibility(View.GONE);


        //Se sobrepone el fragment y la activity al darle a crear tarea
        AddTareaLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment selectedFragment = new TareaFragment();
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.contraintlayout_detalles_evento, selectedFragment);
                fragmentTransaction.commit();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });


        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }
}