package com.example.weMee7.activities;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.weMee7.model.entities.Tarea;
import com.example.weMee7.model.entities._SuperEntity;
import com.example.weMee7.view._SuperActivity;
import com.example.wemee7.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TareaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */


//HomeFragment.java linea195 lleva aqui cuando se le da click a crear tarea

public class TareaFragment extends Fragment {

   EditText et_tareaNombre, et_tareaDescripcion, et_tareaPrecio;
   TextView asignado;
   Button bt_aceptarTarea;
   Spinner sp_participantes;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        cargarComponentes(view);

    }





    private void cargarComponentes(View view){
        et_tareaNombre = view.findViewById(R.id.editTextTaskTitle);
        et_tareaDescripcion = view.findViewById(R.id.editTextTaskDescription);
        et_tareaPrecio = view.findViewById(R.id.editTextPrecioTarea);
        bt_aceptarTarea = view.findViewById(R.id.buttonCreateTask);
        sp_participantes = view.findViewById(R.id.SpinnerTarea);

        bt_aceptarTarea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                crearTarea();
            }
        });
    }


    private void crearTarea(){
        String idTarea = "id_de_tarea"; // Aquí debes obtener el id de la tarea
        String tareaNombre = et_tareaNombre.getText().toString();
        String descripcionTarea = et_tareaDescripcion.getText().toString();
        boolean formatoPrecioCorrecto = precioValido(et_tareaPrecio.getText().toString());
        //pillar lo del spinner

        if(formatoPrecioCorrecto){
            String precioTarea = et_tareaPrecio.getText().toString();
            ReunionActivity activity = (ReunionActivity) getActivity();

            //Tarea tarea = new Tarea(activity.reunion, tareaNombre, descripcionTarea, precioTarea, encargado);
        } else{
            Toast.makeText(getActivity(), "Ingrese un precio válido", Toast.LENGTH_SHORT).show();
        }


    }

    private boolean precioValido(String precio){
        String regex = "^[0-9]+(\\.[0-9]{0,2})?$";
        return precio.matches(regex);
    }
}