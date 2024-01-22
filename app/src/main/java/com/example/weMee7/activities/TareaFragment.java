package com.example.weMee7.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.weMee7.model.dao.TareaDAO;
import com.example.weMee7.model.dao.UsuarioDAO;
import com.example.weMee7.model.entities.Tarea;
import com.example.weMee7.model.entities.Usuario;
import com.example.wemee7.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TareaFragment extends Fragment {

    private EditText et_tareaNombre, et_tareaDescripcion, et_tareaPrecio;
    private Button bt_editarTarea;
    private Button bt_borrarTarea;
    private Button bt_aceptarEdicionTarea;
    private Spinner sp_participantes;

    private ReunionActivity activity;
    private String idUsuarioSeleccionado;
    private Map<String, String> mapNombresUsuarioPorId = new HashMap<>();
    private Tarea tareaSeleccionada;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tarea, container, false);

        // Recibe la tarea, por ende debería abrir el fragment tarea con los botones editar y borrar
        Bundle args = getArguments();
        if (args != null) {
            tareaSeleccionada = args.getParcelable("TareaSeleccionada");
            cargarComponentes(view, true);
            cargaInfoTareaSeleccionada(tareaSeleccionada);
        } else {
            // Proviene de crear tarea
            cargarComponentes(view, false);
        }

        return view;
    }

    private void cargarComponentes(View view, boolean deleteOrEditTask) {
        activity = (ReunionActivity) getActivity();

        //Etiquetas
        et_tareaNombre = view.findViewById(R.id.editTextTaskTitle);
        et_tareaDescripcion = view.findViewById(R.id.editTextTaskDescription);
        et_tareaPrecio = view.findViewById(R.id.editTextPrecioTarea);

        //Spinner
        sp_participantes = view.findViewById(R.id.SpinnerTarea);

        //Botones
        bt_aceptarEdicionTarea = view.findViewById(R.id.buttonAcceptEditTask);

        //Carga de informacion en el spinner
        cargaParticipantesEnSpinner(activity);


        //Carga de boton si debe crear una tarea nueva
        if (!deleteOrEditTask) {
            Button bt_crearTarea = view.findViewById(R.id.buttonCreateTask);
            bt_crearTarea.setVisibility(View.VISIBLE);
            accionBotonCrearTarea(bt_crearTarea);

        } else {
            //Carga de botones y comportamiento si debe editar o eliminar una tarea
            setPermisosModificarCampos(false);
            bt_editarTarea = view.findViewById(R.id.buttonEditTask);
            bt_borrarTarea = view.findViewById(R.id.buttonDeleteTask);

            bt_editarTarea.setVisibility(View.VISIBLE);
            accionBotonEditarTarea(bt_editarTarea);

            bt_borrarTarea.setVisibility(View.VISIBLE);
            accionBotonBorrarTarea(bt_borrarTarea);
        }
    }

    //Permite edicion de los campos que se necesita para editar una tarea
    private void setPermisosModificarCampos(boolean esModificable) {
        et_tareaNombre.setEnabled(esModificable);
        et_tareaDescripcion.setEnabled(esModificable);
        et_tareaPrecio.setEnabled(esModificable);
        sp_participantes.setEnabled(esModificable);
    }

    //Comportamiento del boton de aceptar tarea
    private void accionBotonCrearTarea(Button button) {
        button.setOnClickListener(v -> {
            accionAceptarTarea();
            Toast.makeText(getActivity(), "Tarea Creada", Toast.LENGTH_SHORT).show();
            requireActivity().onBackPressed();
        });
    }

    //Comportamiento del boton de editar tarea
    private void accionBotonEditarTarea(Button button) {
        button.setOnClickListener(v -> accionEditarTarea());
    }

    //Comportamiento del boton de eliminar tarea
    private void accionBotonBorrarTarea(Button button) {
        button.setOnClickListener(v -> {
            accionBorrarTarea();
            Toast.makeText(getActivity(), "Tarea Borrada", Toast.LENGTH_SHORT).show();
            requireActivity().onBackPressed();
        });
    }


    //Logica para editar una tarea
    private void accionEditarTarea() {
        setPermisosModificarCampos(true);
        bt_editarTarea.setVisibility(View.GONE);
        bt_borrarTarea.setVisibility(View.GONE);
        bt_aceptarEdicionTarea.setVisibility(View.VISIBLE);

        bt_aceptarEdicionTarea.setOnClickListener(v -> {
            String nombreTareaActualizada = et_tareaNombre.getText().toString();
            String descripcionTareaActualizada = et_tareaDescripcion.getText().toString();

            int precioTareaActualizado = 0;

            if (esPrecioValido(et_tareaPrecio.getText().toString())) {
                precioTareaActualizado = Integer.parseInt(et_tareaPrecio.getText().toString());
                tareaSeleccionada.setTitulo(nombreTareaActualizada);
                tareaSeleccionada.setDescripcion(descripcionTareaActualizada);
                tareaSeleccionada.setGasto(precioTareaActualizado);
                tareaSeleccionada.setIdEncargado(idUsuarioSeleccionado);
                new TareaDAO().actualizarRegistro(tareaSeleccionada);

                Toast.makeText(getActivity(), "Tarea Modificada", Toast.LENGTH_SHORT).show();
                requireActivity().onBackPressed();
            } else {
                Toast.makeText(getActivity(), "Ingrese un precio válido", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Logica para borrar una tarea
    private void accionBorrarTarea() {
        new TareaDAO().borrarRegistro(tareaSeleccionada.getId());
    }

    //Logica para aceptar una tarea
    private void accionAceptarTarea() {
        String tareaNombre = et_tareaNombre.getText().toString();
        String descripcionTarea = et_tareaDescripcion.getText().toString();

        if (esPrecioValido(et_tareaPrecio.getText().toString())) {
            int precioTarea = Integer.parseInt(et_tareaPrecio.getText().toString());

            new TareaDAO().insertarRegistro(new Tarea(activity.reunion.getId(),
                    tareaNombre, descripcionTarea, precioTarea, idUsuarioSeleccionado));
        } else {
            Toast.makeText(getActivity(), "Ingrese un precio válido", Toast.LENGTH_SHORT).show();
        }
    }

    //Funcion auxiliar para comprobar si el precio es válido
    private boolean esPrecioValido(String precio) {
        try {
            Integer.parseInt(precio);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    //Carga de información en el spinner
    private void cargaParticipantesEnSpinner(ReunionActivity actividad) {
        List<String> listaInvitados = actividad.reunion.getInvitadosList();

        for (String idUsuario : listaInvitados) {
            new UsuarioDAO().obtenerRegistroPorId(idUsuario, resultado -> {
                Usuario user = (Usuario) resultado;
                String nombreUsuario = user.getNombre();
                mapNombresUsuarioPorId.put(idUsuario, nombreUsuario);

                if (mapNombresUsuarioPorId.size() == actividad.reunion.getInvitadosList().size()) {
                    configuracionAdapter(mapNombresUsuarioPorId);
                }
            });
        }
    }

    //Configuracion del adapter del spinner
    private void configuracionAdapter(Map<String, String> mapaNombresIds) {
        List<String> listaNombres = new ArrayList<>(mapaNombresIds.values());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(activity, android.R.layout.simple_dropdown_item_1line, listaNombres);

        sp_participantes.setAdapter(adapter);

        sp_participantes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String nombreSeleccionado = parent.getItemAtPosition(position).toString();
                idUsuarioSeleccionado = idParticipanteEnSpinner(mapaNombresIds, nombreSeleccionado);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    //Carga de los campos con la informacion de la tarea
    private void cargaInfoTareaSeleccionada(Tarea tarea) {
        et_tareaNombre.setText(tarea.getTitulo());
        et_tareaDescripcion.setText(tarea.getDescripcion());
        et_tareaPrecio.setText(String.valueOf(tarea.getGasto()));

        configuracionAdapter(mapNombresUsuarioPorId);
        int posicionEncargado = obtenerPosicionEnSpinner(sp_participantes, tarea.getIdEncargado());

        if (posicionEncargado != -1) {
            sp_participantes.setSelection(posicionEncargado);
        }
    }

    //Funcion auxiliar para obtener la posicion seleccionada del spinner dado un id
    private int obtenerPosicionEnSpinner(Spinner spinner, String idBuscado) {
        ArrayAdapter<String> adapterAux = (ArrayAdapter<String>) spinner.getAdapter();

        if (adapterAux != null) {
            for (int i = 0; i < adapterAux.getCount(); i++) {
                String idEnSpinner = idParticipanteEnSpinner(mapNombresUsuarioPorId, adapterAux.getItem(i));

                if (idBuscado.equals(idEnSpinner)) {
                    return i;
                }
            }
        }
        return -1;
    }

    //Funcion auxiliar para obtener el id del nombre seleccionado en el spinner
    private String idParticipanteEnSpinner(Map<String, String> map, String value) {
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (entry.getValue().equals(value)) {
                return entry.getKey();
            }
        }
        return null;
    }
}
