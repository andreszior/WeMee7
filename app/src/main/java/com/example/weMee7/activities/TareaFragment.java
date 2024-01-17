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
import android.widget.TextView;
import android.widget.Toast;

import com.example.weMee7.model.dao.TareaDAO;
import com.example.weMee7.model.dao.UsuarioDAO;
import com.example.weMee7.model.entities.Tarea;
import com.example.weMee7.model.entities.Usuario;
import com.example.wemee7.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    String idUsuarioSeleccionado;
    Map<String, String> mapaNombresIds = new HashMap<>();
    ArrayAdapter<String> adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tarea, container, false);

        Bundle args = getArguments();
        if(args != null){
            Tarea tareaDetalles = (Tarea) args.getParcelable("TareaSeleccionada");
            cargarComponentes(view);
            cargarTareaDetalles(tareaDetalles);
            botonCrearTarea(bt_aceptarTarea);
        } else {
            cargarComponentes(view);
            botonCrearTarea(bt_aceptarTarea);
        }
        return view;
    }



    //
    private void cargarComponentes(View view) {
        activity = (ReunionActivity) getActivity();

        et_tareaNombre = view.findViewById(R.id.editTextTaskTitle);
        et_tareaDescripcion = view.findViewById(R.id.editTextTaskDescription);
        et_tareaPrecio = view.findViewById(R.id.editTextPrecioTarea);
        bt_aceptarTarea = view.findViewById(R.id.buttonCreateTask);
        sp_participantes = view.findViewById(R.id.SpinnerTarea);
        cargarParticipantes(activity);
    }

    //
    private void botonCrearTarea(Button button){
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aceptarTarea();
                Toast.makeText(getActivity(), "Tarea Creada", Toast.LENGTH_SHORT).show();
                requireActivity().onBackPressed();
            }
        });
    }



    private void aceptarTarea() {
        String tareaNombre = et_tareaNombre.getText().toString();
        String descripcionTarea = et_tareaDescripcion.getText().toString();
        boolean formatoPrecioCorrecto = precioValido(et_tareaPrecio.getText().toString());
        //pillar lo del spinner

        if (formatoPrecioCorrecto) {
            int precioTarea = Integer.parseInt(et_tareaPrecio.getText().toString());

            new TareaDAO().insertarRegistro(new Tarea(activity.reunion.getId(),
                    tareaNombre, descripcionTarea, precioTarea, idUsuarioSeleccionado));


        } else {
            Toast.makeText(getActivity(), "Ingrese un precio válido", Toast.LENGTH_SHORT).show();
        }

    }

    private boolean precioValido(String precio) {
        String regex = "^[0-9]+(\\.[0-9]{0,2})?$";
        return precio.matches(regex);
    }

    private void cargarParticipantes(ReunionActivity actividad) {

        List<String> listaInvitados = actividad.reunion.getInvitadosList();

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

        adapter = new ArrayAdapter<>(activity,
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



    private void cargarTareaDetalles(Tarea tarea){
        String idTarea = tarea.getId();
        et_tareaNombre.setText(tarea.getTitulo());
        et_tareaDescripcion.setText(tarea.getDescripcion());
        String precioTarea = String.valueOf(tarea.getGasto());
        et_tareaPrecio.setText(precioTarea);
        //int posicionAdapter = obtenerPosicionAdapter(adapter, tarea.getIdEncargado());
        //sp_participantes.setSelection(posicionAdapter);


    }

    private int obtenerPosicionAdapter(ArrayAdapter<String> adapter, String id){
        for(int i = 0; i< adapter.getCount(); i++) {
            if(adapter.getItem(i).equals(id)){
                return i;
            }
        }
        return -1;
    }

}
