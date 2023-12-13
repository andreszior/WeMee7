package com.example.weMee7.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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

import com.example.weMee7.model.entities.Reunion;
import com.example.wemee7.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AddFragment extends Fragment {

    EditText nombre;
    EditText lugar;
    EditText descrip;
    EditText fecha;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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
                new DatePickerDialog(getActivity(),dateSetListener,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show();
            }

        });
        Button botonCrear =view.findViewById(R.id.buttoncrear);
        botonCrear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                crearReunion();
            }
        });
    }
    public void crearReunion() {
        String idCreador = "id_del_creador"; // Aquí debes obtener el id del creador
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


    }

}