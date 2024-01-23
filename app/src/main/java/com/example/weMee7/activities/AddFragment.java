package com.example.weMee7.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.weMee7.comun.ReunionesListAdapter;
import com.example.weMee7.model.dao._SuperDAO;
import com.example.weMee7.model.entities.Reunion;
import com.example.wemee7.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AddFragment extends Fragment {
    Button botonCrear;
    EditText nombre;
    EditText lugar;
    EditText descrip;
    EditText fecha;
    //todo sustituir por la funcion de Dani
    private CollectionReference DB_COLECCION;
    protected FirebaseFirestore getDatabase(){
        return FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        botonCrear= view.findViewById(R.id.buttoncrear);
        fecha = view.findViewById(R.id.fecha);
        fecha.setFocusable(false);
        fecha.setClickable(true);
        nombre = view.findViewById(R.id.Nombre);
        lugar = view.findViewById(R.id.Lugar);
        descrip = view.findViewById(R.id.Descripcion);
        fecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectFecha_Hora(fecha);
            }

            private void selectFecha_Hora(EditText fecha) {
                Calendar calendar=Calendar.getInstance();
                DatePickerDialog.OnDateSetListener dateSetListener= new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);

                        TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                calendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
                                calendar.set(Calendar.MINUTE, minute);

                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yy-MM-dd HH:mm");
                                fecha.setText(simpleDateFormat.format(calendar.getTime()));
                            }
                        };
                        new TimePickerDialog(getActivity(), timeSetListener,calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE), false).show();
                    }
                };
                new DatePickerDialog(requireActivity(),dateSetListener,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show();
            }

        });

        botonCrear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                crearReunion();
            }
        });
    }
    public void crearReunion() {
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        if(fUser != null){
            String idCreador = fUser.getUid();
            String nombreReunion = nombre.getText().toString();
            String descripcionReunion = descrip.getText().toString();
            String lugarReunion = lugar.getText().toString();
            String fechaReunion = fecha.getText().toString();

            // Comprobar que todos los campos están rellenos
            if (nombreReunion.isEmpty() || descripcionReunion.isEmpty() || lugarReunion.isEmpty() || fechaReunion.isEmpty()) {
                Toast.makeText(getActivity(), "Por favor, rellena todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            String[] fechaHora = fechaReunion.split(" ");
            String fecha = fechaHora[0]; // Fecha de la reunión
            String hora = fechaHora[1]; // Hora de la reunión

            Reunion reunion = new Reunion(idCreador, nombreReunion, descripcionReunion, lugarReunion, fecha, hora);
            subirReunion(reunion);
            verReunion(reunion);
        }
    }
    //todo sustituir por la funcion de Dani
    private void subirReunion(Reunion r){
        DB_COLECCION = getDatabase().collection("reuniones");
        DB_COLECCION.add(r).addOnSuccessListener(documentReference -> {
            (r).setId(documentReference.getId());
            DB_COLECCION.document(r.getId()).set(r).
                    addOnSuccessListener(unused -> {
                        System.out.println("elemento registrado");
                    });
        });
    }
    private void verReunion(Reunion item){
        Intent intent = new Intent(getActivity(), ReunionActivity.class);
        intent.putExtra("id",item.getId());
        intent.putExtra("meeting", item);
        startActivity(intent);
    }

}