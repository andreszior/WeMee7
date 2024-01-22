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
    Button bt_crearTarea, bt_editarTarea, bt_borrarTarea, bt_aceptarEdicionTarea;
    Spinner sp_participantes;

    ReunionActivity activity;
    String idUsuarioSeleccionado;
    Map<String, String> mapaNombresIds = new HashMap<>();
    ArrayAdapter<String> adapter;
    Tarea tareaDetalles;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tarea, container, false);

        //Recibe la tarea, por ende deberia abrir el el fragment tarea con los botones editar y borrar
        Bundle args = getArguments();
        if(args != null){
            tareaDetalles = (Tarea) args.getParcelable("TareaSeleccionada");
            cargarComponentes(view, true);
            cargarTareaDetalles(tareaDetalles);
        } else {
            //Proviene de crear tarea
            cargarComponentes(view, false);
        }
        return view;
    }



    //
    private void cargarComponentes(View view, boolean deleteOrEditTask) {

        activity = (ReunionActivity) getActivity();
        et_tareaNombre = view.findViewById(R.id.editTextTaskTitle);
        et_tareaDescripcion = view.findViewById(R.id.editTextTaskDescription);
        et_tareaPrecio = view.findViewById(R.id.editTextPrecioTarea);
        sp_participantes = view.findViewById(R.id.SpinnerTarea);
        bt_aceptarEdicionTarea = view.findViewById(R.id.buttonAcceptEditTask);

        cargarParticipantes(activity);


        if(!deleteOrEditTask){
            bt_crearTarea = view.findViewById(R.id.buttonCreateTask);
            bt_crearTarea.setVisibility(View.VISIBLE);
            accionBotonCrearTarea(bt_crearTarea);
        } else {
            //bt_crearTarea.setVisibility(View.GONE);
            //Lo vuelve inmodificable
            modificacionPermitida(false);
            bt_editarTarea = view.findViewById(R.id.buttonEditTask);
            bt_borrarTarea = view.findViewById(R.id.buttonDeleteTask);


            bt_editarTarea.setVisibility(View.VISIBLE);
            accionBotonEditarTarea(bt_editarTarea);

            bt_borrarTarea.setVisibility(View.VISIBLE);
            accionBotonBorrarTarea(bt_borrarTarea);
        }
    }

    private void modificacionPermitida(boolean esModificable){
        if(!esModificable){
            et_tareaNombre.setEnabled(false);
            et_tareaDescripcion.setEnabled(false);
            et_tareaPrecio.setEnabled(false);
            sp_participantes.setEnabled(false);
        } else {
            et_tareaNombre.setEnabled(true);
            et_tareaDescripcion.setEnabled(true);
            et_tareaPrecio.setEnabled(true);
            sp_participantes.setEnabled(true);
        }
    }


    private void accionBotonCrearTarea(Button button){
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aceptarTarea();
                Toast.makeText(getActivity(), "Tarea Creada", Toast.LENGTH_SHORT).show();
                requireActivity().onBackPressed();
            }
        });
    }

    private void accionBotonEditarTarea(Button button){
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //bt_aceptarEdicionTarea = view.findViewById(R.id.buttonAcceptEditTask);
                editarTarea();

                //requireActivity().onBackPressed();
            }
        });
    }

    private void accionBotonBorrarTarea(Button button){
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                borrarTarea();
                Toast.makeText(getActivity(), "Tarea Borrada", Toast.LENGTH_SHORT).show();
                requireActivity().onBackPressed();
            }
        });
    }

    private void editarTarea(){

        modificacionPermitida(true);
        bt_editarTarea.setVisibility(View.GONE);
        bt_borrarTarea.setVisibility(View.GONE);
        bt_aceptarEdicionTarea.setVisibility(View.VISIBLE);

        bt_aceptarEdicionTarea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String nombreTareaActualizada = et_tareaNombre.getText().toString();
                String descripcionTareaActualizada = et_tareaDescripcion.getText().toString();

                int precioTareaActualizado = 0;



                if(esPrecioValido(et_tareaPrecio.getText().toString())){
                    precioTareaActualizado = Integer.parseInt(et_tareaPrecio.getText().toString());
                    tareaDetalles.setTitulo(nombreTareaActualizada);
                    tareaDetalles.setDescripcion(descripcionTareaActualizada);
                    tareaDetalles.setGasto(precioTareaActualizado);
                    tareaDetalles.setIdEncargado(idUsuarioSeleccionado);
                    new TareaDAO().actualizarRegistro(tareaDetalles);

                    Toast.makeText(getActivity(), "Tarea Modificada", Toast.LENGTH_SHORT).show();
                    requireActivity().onBackPressed();
                } else {
                    Toast.makeText(getActivity(), "Ingrese un precio válido", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void borrarTarea(){
        new TareaDAO().borrarRegistro(tareaDetalles.getId());
    }

    private void aceptarTarea() {
        String tareaNombre = et_tareaNombre.getText().toString();
        String descripcionTarea = et_tareaDescripcion.getText().toString();

        if(esPrecioValido(et_tareaPrecio.getText().toString())){
            int precioTarea = Integer.parseInt(et_tareaPrecio.getText().toString());

            new TareaDAO().insertarRegistro(new Tarea(activity.reunion.getId(),
                    tareaNombre, descripcionTarea, precioTarea, idUsuarioSeleccionado));
        } else {
            Toast.makeText(getActivity(), "Ingrese un precio válido", Toast.LENGTH_SHORT).show();
        }

    }

    // Método auxiliar para verificar si el precio es un entero válido
    private boolean esPrecioValido(String precio) {
        try {
            int valor = Integer.parseInt(precio);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void cargarParticipantes(ReunionActivity actividad) {

        List<String> listaInvitados = actividad.reunion.getInvitadosList();

        for (String idUsuario : listaInvitados) {
            //obtenerNombreUsuario(idUsuario, mapaNombresIds); // Se inicia la carga de nombres
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
        et_tareaNombre.setText(tarea.getTitulo());
        et_tareaDescripcion.setText(tarea.getDescripcion());
        String precioTarea = String.valueOf(tarea.getGasto());
        et_tareaPrecio.setText(precioTarea);

        configurarAdapter(mapaNombresIds);
        int posicionEncargado = obtenerPosicionEnSpinner(sp_participantes, tarea.getIdEncargado());

        // Establece la selección en el Spinner
        if (posicionEncargado != -1) {
            sp_participantes.setSelection(posicionEncargado);
        }
        //sp_participantes.setSelection(obtenerPosicionEnSpinner(sp_participantes, tarea.getIdEncargado()));
        //int posicionAdapter = obtenerPosicionAdapter(adapter, tarea.getIdEncargado());
        //sp_participantes.setSelection(posicionAdapter);

    }

    private int obtenerPosicionEnSpinner(Spinner spinner, String idBuscado) {
        ArrayAdapter<String> adapterAux = (ArrayAdapter<String>) spinner.getAdapter();

        if (adapterAux != null) {
            for (int i = 0; i < adapterAux.getCount(); i++) {
                String idEnSpinner = getKeyFromValue(mapaNombresIds, adapterAux.getItem(i));

                if (idBuscado.equals(idEnSpinner)) {
                    return i;  // Se encontró el ID en la posición 'i'
                }
            }
        }
        return -1;  // No se encontró el ID en el Spinner
    }
}
