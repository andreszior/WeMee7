package com.example.weMee7.activities;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.weMee7.model.dao.ReunionDAO;
import com.example.weMee7.model.dao.TareaDAO;
import com.example.weMee7.model.dao.UsuarioDAO;
import com.example.weMee7.model.dao._SuperDAO;
import com.example.weMee7.model.entities.Reunion;
import com.example.weMee7.model.entities.Tarea;
import com.example.weMee7.model.entities.Usuario;
import com.example.weMee7.model.entities._SuperEntity;
import com.example.weMee7.view._SuperActivity;
import com.example.wemee7.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A simple {@link Fragment} subclass.
 * Use the  factory method to
 * create an instance of this fragment.
 */


//HomeFragment.java linea195 lleva aqui cuando se le da click a crear tarea

public class TareaFragment extends Fragment {

    EditText et_tareaNombre, et_tareaDescripcion, et_tareaPrecio;
    TextView asignado;
    Button bt_aceptarTarea;
    Spinner sp_participantes;

    ReunionActivity activity;
    String idSeleccion;
    String idUsuarioSeleccionado;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tarea, container, false);
        cargarComponentes(view);
        return view;
    }

    private void cargarComponentes(View view) {
        activity = (ReunionActivity) getActivity();

        et_tareaNombre = view.findViewById(R.id.editTextTaskTitle);
        et_tareaDescripcion = view.findViewById(R.id.editTextTaskDescription);
        et_tareaPrecio = view.findViewById(R.id.editTextPrecioTarea);
        bt_aceptarTarea = view.findViewById(R.id.buttonCreateTask);

        sp_participantes = view.findViewById(R.id.SpinnerTarea);
        cargarParticipantes(activity);

/*
        List listaParticipantes = nombres2ParticipantesSpinner(activity.reunion.getInvitadosList());

        ArrayAdapter<String> adapter = new ArrayAdapter<>(activity,
                android.R.layout.simple_dropdown_item_1line, listaParticipantes);

        sp_participantes.setAdapter(adapter);
        sp_participantes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String seleccion = parent.getItemAtPosition(position).toString();
                new UsuarioDAO().obtenerRegistroPorId(seleccion, resultado -> {
                    Usuario user = (Usuario) resultado;
                    idSeleccion = user.getId();
                });
            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

 */

        bt_aceptarTarea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                crearTarea();
                Toast.makeText(getActivity(), "Tarea Creada", Toast.LENGTH_SHORT).show();
                requireActivity().onBackPressed();
            }
        });
    }


    private void crearTarea() {
        String tareaNombre = et_tareaNombre.getText().toString();
        String descripcionTarea = et_tareaDescripcion.getText().toString();
        boolean formatoPrecioCorrecto = precioValido(et_tareaPrecio.getText().toString());
        //pillar lo del spinner

        if (formatoPrecioCorrecto) {
            int precioTarea = Integer.parseInt(et_tareaPrecio.getText().toString());

            new TareaDAO().insertarRegistro(new Tarea(activity.reunion.getId(), tareaNombre, descripcionTarea, precioTarea, idUsuarioSeleccionado));


        } else {
            Toast.makeText(getActivity(), "Ingrese un precio válido", Toast.LENGTH_SHORT).show();
        }

    }

    private boolean precioValido(String precio) {
        String regex = "^[0-9]+(\\.[0-9]{0,2})?$";
        return precio.matches(regex);
    }

    private ArrayList<String> nombres2ParticipantesSpinner(ArrayList lista) {
        ArrayList<String> nombres = new ArrayList<>();
        for (Object elemento : lista) {
            if (elemento instanceof String) {
                new UsuarioDAO().obtenerRegistroPorId((String) elemento, resultado -> {
                    Usuario user = (Usuario) resultado;
                    nombres.add(user.getNombre());
                });
            }
        }
        return nombres;
    }

    private void cargarParticipantes(ReunionActivity actividad) {

        List<String> listaInvitados = actividad.reunion.getInvitadosList();
        Map<String, String> mapaNombresIds = new HashMap<>();

        for (String idUsuario : listaInvitados) {
            obtenerNombreUsuario(idUsuario, mapaNombresIds); // Se inicia la carga de nombres
        }
    }

    private void obtenerNombreUsuario(String idUsuario, Map<String, String> mapaNombresIds) {
        new UsuarioDAO().obtenerRegistroPorId(idUsuario, resultado -> {
            Usuario user = (Usuario) resultado;
            String nombreUsuario = user.getNombre();

            // Asocia directamente el ID con el nombre en el mapa
            mapaNombresIds.put(idUsuario, nombreUsuario);

            // Verifica si se completaron todas las llamadas asíncronas
            if (mapaNombresIds.size() == activity.reunion.getInvitadosList().size()) {
                // Todas las llamadas asíncronas han completado, ahora podemos configurar el adaptador
                configurarAdapter(mapaNombresIds);
            }
        });
    }

    private void configurarAdapter(Map<String, String> mapaNombresIds) {
        // Crear una lista de nombres únicos para el adaptador
        List<String> listaNombres = new ArrayList<>(mapaNombresIds.values());

        ArrayAdapter<String> adapter = new ArrayAdapter<>(activity,
                android.R.layout.simple_dropdown_item_1line, listaNombres);

        sp_participantes.setAdapter(adapter);

        sp_participantes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String nombreSeleccionado = parent.getItemAtPosition(position).toString();
                idUsuarioSeleccionado = getKeyFromValue(mapaNombresIds, nombreSeleccionado);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Acciones cuando no se selecciona nada
            }
        });
    }


    // Método auxiliar para obtener la clave (ID) a partir del valor (nombre) en el mapa
    private String getKeyFromValue(Map<String, String> map, String value) {
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (entry.getValue().equals(value)) {
                return entry.getKey();
            }
        }
        return null; // Manejar el caso en el que no se encuentra la clave
    }

}
