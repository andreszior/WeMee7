package com.example.weMee7.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.weMee7.comun.EncargadosAdapter;
import com.example.weMee7.comun.InputControl;
import com.example.weMee7.model.entities.Tarea;
import com.example.weMee7.model.entities.Usuario;
import com.example.weMee7.view._SuperActivity;
import com.example.weMee7.view.usuario.UsuarioActivity;
import com.example.weMee7.viewmodel.GestionarDatos;
import com.example.wemee7.R;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class TareaFragment extends Fragment {

    private EditText et_tareaTitulo, et_tareaDescripcion, et_tareaGasto;
    private Spinner sp_participantes;
    private CheckBox check_Realizada;

    private List<Usuario> encargadosList;
    private Tarea tareaSeleccionada;
    private GestionarDatos vmDatos;
    private boolean editable;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tarea, container, false);
        tareaSeleccionada = getFragmentPadre().getTareaSeleccionada();

        cargarComponentes(view);

        return view;
    }

    private void cargarComponentes(View view) {
        boolean nuevaTarea = tareaSeleccionada == null;
        boolean esCreador = ((UsuarioActivity) requireActivity()).esCreador();

        if (nuevaTarea) {
            editable = true;
        } else {
            if (!getFragmentPadre().getReunionActual().estaActiva())
                editable = false;
            else {
                Tarea.EstadoTarea estado = tareaSeleccionada.getEstado();
                if (estado == Tarea.EstadoTarea.CREADA) {
                    editable = true;//Si no esta asignada, es editable por todos
                } else {//Si esta asignada, solo por creador y encargado
                    editable = esCreador ||
                            tareaSeleccionada.getIdEncargado().equals(FirebaseAuth.getInstance().getCurrentUser().getUid());
                }
            }
        }

        //Cabecera
        if (nuevaTarea)
            ((TextView) view.findViewById(R.id.tvTagTarea)).setText(R.string.tag_nuevatarea);

        //Campos
        et_tareaTitulo = view.findViewById(R.id.etTareaTitulo);
        et_tareaDescripcion = view.findViewById(R.id.etTareaDescrip);
        et_tareaGasto = view.findViewById(R.id.etTareaGasto);

        if (!nuevaTarea)
            cargarDatosTarea();

        //Spinner
        sp_participantes = view.findViewById(R.id.spEncargado);
        ViewTreeObserver obs = sp_participantes.getViewTreeObserver();
        obs.addOnGlobalLayoutListener(() -> {
            sp_participantes.setDropDownWidth(sp_participantes.getWidth());
        });

        //Carga spinner
        cargarDatosSpinner((!nuevaTarea && (!editable || !esCreador)));


        //CheckBox
        check_Realizada = view.findViewById(R.id.cbTareaRealizada);
        if (!nuevaTarea &&
                tareaSeleccionada.getEstado() == Tarea.EstadoTarea.COMPLETADA)
            check_Realizada.setChecked(true);

        //Elementos no editables
        if (!nuevaTarea && !editable) {
            campoNoEditable(et_tareaTitulo);
            campoNoEditable(et_tareaDescripcion);
            campoNoEditable(et_tareaGasto);
            sp_participantes.setFocusable(false);
            sp_participantes.setClickable(false);
            sp_participantes.setFocusableInTouchMode(false);
            sp_participantes.setEnabled(false);
            sp_participantes.setBackground(requireActivity().getDrawable(R.drawable.rounded_rectangle));
            check_Realizada.setFocusable(false);
            check_Realizada.setClickable(false);
        } else {
            listenerFormatoGasto();
        }


        //Botones
        Button btAccionTarea = view.findViewById(R.id.btActionTarea);
        ImageButton btUndoTarea = view.findViewById(R.id.btTareaUndo);
        ImageButton btBorrarTarea = view.findViewById(R.id.btTareaDelete);
        if (nuevaTarea) {
            btAccionTarea.setText(R.string.bt_crear);
            btAccionTarea.setOnClickListener(v -> accionTarea(nuevaTarea));
            btUndoTarea.setVisibility(View.INVISIBLE);
            btBorrarTarea.setVisibility(View.INVISIBLE);
        } else if (editable) {
            btAccionTarea.setText(R.string.bt_guardar);
            btAccionTarea.setOnClickListener(v -> accionTarea(nuevaTarea));
            btUndoTarea.setVisibility(View.VISIBLE);
            btBorrarTarea.setVisibility(View.VISIBLE);
            btUndoTarea.setOnClickListener(v -> accionDeshacerCambios());
            btBorrarTarea.setOnClickListener(v -> accionBorrarTarea());
        } else{
            btAccionTarea.setVisibility(View.GONE);
            btUndoTarea.setVisibility(View.GONE);
            btBorrarTarea.setVisibility(View.GONE);
        }



        view.findViewById(R.id.btTareaClose).setOnClickListener(v -> accionCerrarTarea(false));
    }

    private void cargarDatosTarea() {
        et_tareaTitulo.setText(tareaSeleccionada.getTitulo());
        et_tareaDescripcion.setText(tareaSeleccionada.getDescripcion());
        et_tareaGasto.setText(tareaSeleccionada.obtenerGastoString());
    }

    private void cargarEncargado() {
        Usuario encargado = new Usuario(tareaSeleccionada.getIdEncargado());
        int position = encargadosList.indexOf(encargado) + 1;
        sp_participantes.setSelection(position);
    }

    private void cargarDatosSpinner(boolean soloEncargado) {
        encargadosList = new ArrayList<>();

        //Vigilante de tarea completa
        TaskCompletionSource<DocumentSnapshot> tcs;

        vmDatos = new GestionarDatos();
        if (soloEncargado)
            tcs = vmDatos.obtenerListaSoloEncargado(encargadosList,
                    tareaSeleccionada.getIdEncargado());
        else {
            String idCreador = getFragmentPadre().getReunionActual().getIdCreador();
            String idReunion = getFragmentPadre().getReunionActual().getId();
            tcs = vmDatos.obtenerListaTodosEncargados(encargadosList,
                    idReunion, idCreador);
        }

        if (tcs != null) {
            //Cuando termine la tarea de enviar invitacion, se realizan las demas consultas
            Task<DocumentSnapshot> getTask = tcs.getTask();
            getTask.addOnSuccessListener(documentSnapshot -> {
                sp_participantes.setAdapter(
                        new EncargadosAdapter(
                                requireActivity(), encargadosList));
                if (tareaSeleccionada != null && tareaSeleccionada.getIdEncargado() != null) {
                    cargarEncargado();
                }
            });
        }
    }

    private void listenerFormatoGasto() {
        et_tareaGasto.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                // Si el usuario borra el símbolo de euro, vuelve a añadirlo
//                if (!s.toString().endsWith("€")) {
//                    et_tareaGasto.setText(s + "€");
//                    et_tareaGasto.setSelection(et_tareaGasto.getText().length() - 1);  // Mueve el cursor antes del símbolo de euro
//                }

                // Si el usuario escribe una coma al principio, añade un 0 delante
                if (s.toString().startsWith(",")) {
                    et_tareaGasto.setText("0" + s);
                    et_tareaGasto.setSelection(et_tareaGasto.getText().length());  // Mueve el cursor al final
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                String input = s.toString();
                String filtrado = input.replaceAll("[^0-9,]", "");
                // Verifica si el texto ha cambiado
                if (!input.equals(filtrado)) {
                    et_tareaGasto.setText(filtrado); // Establece el texto filtrado en el EditText
                    et_tareaGasto.setSelection(filtrado.length()); // Mueve el cursor al final del texto
                }
            }
        });
    }

    private void campoNoEditable(EditText et) {
        et.setBackground(null);
        int padding_in_dp = 2;
        final float scale = getResources().getDisplayMetrics().density + 0.5f;
        int padding_in_px = (int) (padding_in_dp * scale);
        et.setPadding(padding_in_px, padding_in_px, padding_in_px, padding_in_px);
        et.setFocusable(false);
        et.setFocusableInTouchMode(false);
        et.setClickable(false);
    }


    //region EVENTOS BOTONES
    private void accionTarea(boolean crear) {
        //Control de titulo
        String titulo = et_tareaTitulo.getText().toString();

        if (!InputControl.todoCumplimentado(new String[]{titulo})) {
            ((_SuperActivity) requireActivity()).lanzarMensaje(R.string.msj_titulo_tarea);
            return;
        }
        //Control de gasto
        String gastoString = et_tareaGasto.getText().toString();
        int gasto = InputControl.formatoGasto(gastoString);
        if (gasto == -1) {
            ((_SuperActivity) requireActivity()).lanzarMensaje(R.string.msj_gasto_tarea);
            return;
        }

        //Obtener descripcion
        String descripcion = et_tareaDescripcion.getText().toString();

        //Obtener encargado
        int position = sp_participantes.getSelectedItemPosition();
        String idEncargado = position == 0 ? null : encargadosList.get(position - 1).getId();

        //Obtener estado
        Tarea.EstadoTarea estadoTarea;
        if (idEncargado == null)
            estadoTarea = Tarea.EstadoTarea.CREADA;
        else if (check_Realizada.isChecked())
            estadoTarea = Tarea.EstadoTarea.COMPLETADA;
        else
            estadoTarea = Tarea.EstadoTarea.ASIGNADA;

        if(tareaSeleccionada == null){
            String idReunion = getFragmentPadre().getReunionActual().getId();
            vmDatos.crearTarea(new Tarea(idReunion,titulo,descripcion,gasto,idEncargado,estadoTarea));
        }else if(hayCambios(titulo,descripcion,gasto,idEncargado,estadoTarea)) {
            vmDatos.actualizarTarea(tareaSeleccionada);
        }else return;

        accionCerrarTarea(true);
    }

    private boolean hayCambios(String titulo, String descripcion, int gasto, String idEncargado, Tarea.EstadoTarea estado){
        boolean cambios = false;

        if(!titulo.equals(tareaSeleccionada.getTitulo())){
            tareaSeleccionada.setTitulo(titulo);
            cambios = true;
        }
        if(!descripcion.equals(tareaSeleccionada.getDescripcion())){
            tareaSeleccionada.setDescripcion(descripcion);
            if(!cambios) cambios = true;
        }
        if(gasto != tareaSeleccionada.getGasto()){
            tareaSeleccionada.setGasto(gasto);
            if(!cambios) cambios = true;
        }
        if(!idEncargado.equals(tareaSeleccionada.getIdEncargado())){
            tareaSeleccionada.setIdEncargado(idEncargado);
            if(!cambios) cambios = true;
        }
        if(estado != tareaSeleccionada.getEstado()){
            tareaSeleccionada.setEstado(estado);
            if(!cambios) cambios = true;
        }
        return cambios;
    }

    private void accionDeshacerCambios() {
        cargarDatosTarea();
        cargarEncargado();
    }

    private void accionBorrarTarea() {
        dialogBorrar();
    }

    private void dialogBorrar() {
        // Crear AlertDialog.Builder
        Context c = requireActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(c);

        // Campo de texto
        final TextView input = new TextView(c);
        input.setText(R.string.text_borrar_tarea);
        builder.setView(input);

        //Asignacion de botones
        builder.setPositiveButton(R.string.bt_delete_tarea, (dialog, which) -> {
            vmDatos.eliminarTarea(tareaSeleccionada.getId());
            accionCerrarTarea(true);
        });
        builder.setNegativeButton(R.string.bt_cancelar, (dialog, which) -> dialog.cancel());

        final AlertDialog dialog = builder.create();
        dialog.setOnShowListener(dialog1 -> {
            Button btBorrar = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            btBorrar.setBackgroundColor(getResources().getColor(R.color.avatar_rojo));
            btBorrar.setTextColor(getResources().getColor(R.color.white));
        });
        // Mostrar el AlertDialog
        dialog.show();
    }

    private void accionCerrarTarea(boolean actualizar) {
        getFragmentPadre().cerrarTarea(actualizar);
    }

    //endregion

    private ReunionFragment getFragmentPadre() {
        Fragment padre = requireActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (padre instanceof ReunionFragment)
            return (ReunionFragment) padre;
        else
            return null;
    }

}