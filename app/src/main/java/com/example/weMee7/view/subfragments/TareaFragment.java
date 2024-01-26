package com.example.weMee7.view.subfragments;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
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
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.weMee7.comun.InputControl;
import com.example.weMee7.model.entities.Tarea;
import com.example.weMee7.model.entities.Usuario;
import com.example.weMee7.view.adapters.EncargadosAdapter;
import com.example.weMee7.view.activity._SuperActivity;
import com.example.weMee7.view.adapters.TareaAdapter;
import com.example.weMee7.view.fragments.ReunionFragment;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tarea, container, false);
        tareaSeleccionada = getFragmentPadre().getTareaSeleccionada();

        cargarComponentes(view);

        return view;
    }

    private void cargarComponentes(View view) {
        boolean nuevaTarea = tareaSeleccionada == null;
        boolean esCreador = getFragmentPadre().isUserCreador();

        boolean editable;
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

        //Spinner
        sp_participantes = view.findViewById(R.id.spEncargado);
        ViewTreeObserver obs = sp_participantes.getViewTreeObserver();
        obs.addOnGlobalLayoutListener(() -> {
            sp_participantes.setDropDownWidth(sp_participantes.getWidth());
        });

        //CheckBox
        check_Realizada = view.findViewById(R.id.cbTareaRealizada);

        //Cargar datos de la tarea
        if (!nuevaTarea)
            cargarDatosTarea();

        //Cargar posibles encargados
        cargarDatosSpinner(!esCreador && editable);


        //Elementos no editables
        if (!nuevaTarea && !editable) {
            campoNoEditable(et_tareaTitulo);
            campoNoEditable(et_tareaDescripcion);
            campoNoEditable(et_tareaGasto);
            habilitarSpinner(false);
            check_Realizada.setFocusable(false);
            check_Realizada.setClickable(false);
        } else {
            listenerFormatoGasto();
            listenerCheckBox();
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

    private void habilitarSpinner(boolean activo){
        sp_participantes.setFocusable(activo);
        sp_participantes.setClickable(activo);
        sp_participantes.setFocusableInTouchMode(activo);
        sp_participantes.setEnabled(activo);
        int drawableBg = activo ? R.drawable.spinner_bg : R.drawable.rounded_rectangle;
        sp_participantes.setBackground(requireActivity().getDrawable(drawableBg));
    }

    private void listenerCheckBox() {
        check_Realizada.setOnCheckedChangeListener(((buttonView, isChecked) -> {
            habilitarSpinner(!isChecked);
            if(isChecked && sp_participantes.getSelectedItemPosition() == 0)
                cargarEncargado(FirebaseAuth.getInstance().getCurrentUser().getUid());
        }));
    }

    private void cargarDatosTarea() {
        et_tareaTitulo.setText(tareaSeleccionada.getTitulo());
        et_tareaDescripcion.setText(tareaSeleccionada.getDescripcion());
        et_tareaGasto.setText(tareaSeleccionada.obtenerGastoString());
        if (tareaSeleccionada != null &&
                tareaSeleccionada.getEstado() == Tarea.EstadoTarea.COMPLETADA){
            check_Realizada.setChecked(true);
            habilitarSpinner(false);
        }
    }

    private void cargarEncargado(String idEncargado) {
        Usuario encargado = new Usuario(idEncargado);
        int position = encargadosList.indexOf(encargado) + 1;
        sp_participantes.setSelection(position);
    }

    private void cargarDatosSpinner(boolean soloEncargado) {
        encargadosList = new ArrayList<>();

        //Vigilante de tarea completa
        TaskCompletionSource<DocumentSnapshot> tcs;

        vmDatos = new GestionarDatos();
        if (soloEncargado) {
            String idUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
            tcs = vmDatos.obtenerListaSoloEncargado(encargadosList,idUser);
        }else {
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
                if (tareaSeleccionada != null && !tareaSeleccionada.getIdEncargado().equals("")) {
                    cargarEncargado(tareaSeleccionada.getIdEncargado());
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
                // Si el usuario escribe una coma al principio, añade un 0 delante
                if (s.toString().startsWith(",")) {
                    et_tareaGasto.setText("0" + s);
                    et_tareaGasto.setSelection(et_tareaGasto.getText().length());  // Mueve el cursor al final
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                String input = s.toString();
                String filtrado = input.replaceAll("[^0-9,.]", "");
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
        String idEncargado = position == 0 ? "" : encargadosList.get(position - 1).getId();

        //Obtener estado
        Tarea.EstadoTarea estadoTarea;
        if (idEncargado.equals(""))
            estadoTarea = Tarea.EstadoTarea.CREADA;
        else if (check_Realizada.isChecked())
            estadoTarea = Tarea.EstadoTarea.COMPLETADA;
        else
            estadoTarea = Tarea.EstadoTarea.ASIGNADA;

        //Actualizar base de datos
        int mensaje;
        if(tareaSeleccionada == null){
            String idReunion = getFragmentPadre().getReunionActual().getId();
            mensaje = R.string.msj_tarea_creada;
            TaskCompletionSource<DocumentSnapshot> tcs =
                    vmDatos.crearTarea(new Tarea(idReunion,titulo,descripcion,gasto,idEncargado,estadoTarea));
            tcs.getTask().addOnSuccessListener(documentSnapshot -> {
                ((_SuperActivity)requireActivity()).lanzarMensaje(mensaje);
                accionCerrarTarea(true);
            });

        }else if(hayCambios(titulo,descripcion,gasto,idEncargado,estadoTarea)) {
            vmDatos.actualizarTarea(tareaSeleccionada);
            mensaje = R.string.msj_tarea_modificada;
            ((_SuperActivity)requireActivity()).lanzarMensaje(mensaje);
            accionCerrarTarea(true);
        }else {
            accionCerrarTarea(false);
        }
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
        cargarEncargado(tareaSeleccionada.getIdEncargado());
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
            ((_SuperActivity)requireActivity()).lanzarMensaje(R.string.msj_tarea_eliminada);
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

    protected ReunionFragment getFragmentPadre() {
        Fragment padre = requireActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (padre instanceof ReunionFragment)
            return (ReunionFragment) padre;
        else
            return null;
    }
}