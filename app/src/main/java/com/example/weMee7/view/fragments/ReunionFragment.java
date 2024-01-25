package com.example.weMee7.view.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.weMee7.comun.TimeUtils;
import com.example.weMee7.model.dao.ReunionDAO;

import com.example.weMee7.model.entities.Reunion;
import com.example.weMee7.model.entities.Tarea;

import com.example.weMee7.view.adapters.TareaAdapter;
import com.example.weMee7.view.activity._SuperActivity;
import com.example.weMee7.view.activity.UsuarioActivity;
import com.example.weMee7.view.subfragments.InvitadosFragment;
import com.example.weMee7.view.subfragments.TareaFragment;
import com.example.weMee7.viewmodel.GestionarDatos;
import com.example.wemee7.R;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;

import android.app.Dialog;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ReunionFragment extends Fragment {

    private Reunion reunionActual;
    private boolean esCreador;
    private Tarea tareaSeleccionada;
    View llInfoReunion, llEditReunion, llMostrarInvitados, llTareasClosed, llTareasOpen, fragmentContainer;
    EditText [] etCampos;
    ImageButton btAdd, btEditReunion,btBorrarReunion;
    RecyclerView rvTareasReunion;
    Drawable fondoEditable;

    GestionarDatos vmDatos = new GestionarDatos();

    public ReunionFragment(){}

    public Tarea getTareaSeleccionada() {
        return tareaSeleccionada;
    }

    public Reunion getReunionActual(){
        return reunionActual;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_reunion, container, false);

        //Captar id de reunion
        String idReunion = ((UsuarioActivity)requireActivity()).getIdReunionActual();

        //Controlar id == null


        //Databinding
        cargarComponentes(view);


        //Cargar reunion + cargar recyclerView
        cargarReunion(idReunion);
        return view;
    }

    public boolean isUserCreador() {
        return esCreador;
    }

    private void cargarComponentes(View view){
        //Contenedores
        llInfoReunion = view.findViewById(R.id.llReunionInfo);
        llEditReunion = view.findViewById(R.id.llReunionEdit);
        llMostrarInvitados = view.findViewById(R.id.llMostrarInvitados);
        llTareasClosed = view.findViewById(R.id.llTareasClosed);
        llTareasOpen = view.findViewById(R.id.llTareasOpen);
        fragmentContainer = view.findViewById(R.id.reunionContainer);

        //RecyclerView
        rvTareasReunion = view.findViewById(R.id.rvReunionTareas);

        //Campos de texto
        etCampos = new EditText[]{
                view.findViewById(R.id.etNombreReunion),
                view.findViewById(R.id.etReunionDescrip),
                view.findViewById(R.id.etReunionLugar),
                view.findViewById(R.id.etReunionFecha),
                view.findViewById(R.id.etReunionHora)
        };
        fondoEditable = etCampos[0].getBackground();
        setEditable(false);

        //Botones
        btEditReunion = view.findViewById(R.id.btReunionEdit);
        btBorrarReunion = view.findViewById(R.id.btReunionDelete);
        btAdd = view.findViewById(R.id.boton_add);

        //Asignar listeners
        llMostrarInvitados.setOnClickListener(v -> mostrarInvitados());
        llTareasClosed.findViewById(R.id.btMostrarTareas).setOnClickListener(v -> mostrarListaTareas(true));
        llTareasOpen.findViewById(R.id.btOcultarTareas).setOnClickListener(v -> mostrarListaTareas(false));
        btAdd.setOnClickListener(v -> showBottomDialog());
        view.findViewById(R.id.btReunionUndo).setOnClickListener(v -> deshacerCambios());
        view.findViewById(R.id.btReunionGuardar).setOnClickListener(v -> guardarCambios());
    }

    private void cargarReunion(String idReunion){
        new ReunionDAO().obtenerRegistroPorId(idReunion, entity ->{
            reunionActual = (Reunion)entity;
            esCreador = FirebaseAuth.getInstance().getCurrentUser().getUid().equals(reunionActual.getIdCreador());
            cargarDatosReunion();
            llenarRecyclerViewTareas();
        });
    }

    private void cargarDatosReunion(){
        etCampos[0].setText(reunionActual.getNombre());
        etCampos[1].setText(reunionActual.getDescripcion());
        etCampos[2].setText(reunionActual.getLugar());
        etCampos[3].setText(reunionActual.obtenerFechaString());
        etCampos[4].setText(reunionActual.obtenerHoraString());
        ajustarLayout();
    }

    private void ajustarLayout(){
        boolean esModificable =  esCreador && reunionActual.estaActiva();

        btEditReunion.setVisibility(esModificable ? View.VISIBLE : View.GONE);
        btBorrarReunion.setVisibility(esCreador ? View.VISIBLE : View.GONE);

        if(esModificable){
            //Selectores de fecha y hora
            etCampos[3].setOnClickListener(v -> TimeUtils.addSelectFecha(requireActivity(),etCampos[3]));
            etCampos[4].setOnClickListener(v -> TimeUtils.addSelectHora(requireActivity(),etCampos[4]));
            //Botones de edicion
            btEditReunion.setOnClickListener(v -> editarReunion(true));
        }
        if(esCreador)
            btBorrarReunion.setOnClickListener(v -> borrarReunion());
    }

    private void llenarRecyclerViewTareas(){
        List<Tarea> listaTareas = new ArrayList<>();

        //Vigilante de tarea completa
        TaskCompletionSource<DocumentSnapshot> tcs =
                vmDatos.obtenerListaTareas(listaTareas,reunionActual.getId());

        if(tcs != null) {
            //Cuando termine la tarea, se crea el adapter y se establece en el Recycler
            Task<DocumentSnapshot> getTask = tcs.getTask();
            getTask.addOnSuccessListener(documentSnapshot -> {
                rvTareasReunion.setHasFixedSize(true);
                rvTareasReunion.setLayoutManager(new LinearLayoutManager(this.getContext()));
                rvTareasReunion.setAdapter(new TareaAdapter(
                        listaTareas, this.getContext(), esCreador, reunionActual.estaActiva(),
                        item -> {
                            tareaSeleccionada = item;
                            cambiarSubFragment(new TareaFragment());
                            llMostrarInvitados.setVisibility(View.GONE);
                }));
            });
        }
    }


    //region EVENTOS BOTON
    private void editarReunion(boolean editable){
        int vis1 = editable ? View.VISIBLE : View.GONE;
        int vis2 = editable ? View.GONE : View.VISIBLE;
        int vis3 = editable ? View.INVISIBLE : View.VISIBLE;
        llEditReunion.setVisibility(vis1);
        btEditReunion.setVisibility(vis2);
        btBorrarReunion.setVisibility(vis2);
        llMostrarInvitados.setVisibility(vis3);
        llTareasClosed.setVisibility(vis3);
        llTareasOpen.setVisibility(View.GONE);
        fragmentContainer.setVisibility(View.INVISIBLE);
        setEditable(editable);
    }

    private void setEditable(boolean editable){
        for(EditText et : etCampos){
            et.setClickable(editable);
            if(et != etCampos[3] && et != etCampos[4]){
                et.setFocusable(editable);
                et.setFocusableInTouchMode(editable);
            }
            et.setBackground(editable ? fondoEditable : null);
            if(!editable && et != etCampos[0]){
                int padding_in_dp = 2;
                final float scale = getResources().getDisplayMetrics().density + 0.5f;
                int padding_in_px = (int) (padding_in_dp * scale);
                et.setPadding(padding_in_px, padding_in_px, padding_in_px, padding_in_px);

                // Establecer el tamaño del texto
                et.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            }
        }
    }

    private void borrarReunion(){
        dialogBorrar();
    }

    private void dialogBorrar(){
        // Crear AlertDialog.Builder
        Context c = requireActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(c);

        // Campo de texto
        final TextView input = new TextView(c);
        input.setText(R.string.text_borrar_reunion);
        builder.setView(input);

        //Asignacion de botones
        builder.setPositiveButton(R.string.bt_delete_tarea, (dialog, which) -> {
            vmDatos.eliminarReunion(reunionActual.getId());
            ((_SuperActivity)requireActivity()).colocarFragment(new HomeFragment());
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

    private void deshacerCambios(){
        cargarDatosReunion();
        editarReunion(false);
    }

    private void guardarCambios(){
        if(hayCambios()){
            vmDatos.modificarReunion(reunionActual);
            ((_SuperActivity)requireActivity()).lanzarMensaje(R.string.msj_reunion_modificada);
        }

        editarReunion(false);
    }

    private boolean hayCambios(){
        boolean cambios = false;
        String[] campos = new String[]{
            etCampos[0].getText().toString(),
            etCampos[1].getText().toString(),
            etCampos[2].getText().toString(),
            etCampos[3].getText().toString(),
            etCampos[4].getText().toString(),
        };
        if(!campos[0].equals(reunionActual.getNombre())){
            reunionActual.setNombre(campos[0]);
            cambios = true;
        }
        if(!campos[1].equals(reunionActual.getDescripcion())){
            reunionActual.setDescripcion(campos[1]);
            if(!cambios) cambios = true;
        }
        if(!campos[2].equals(reunionActual.getLugar())){
            reunionActual.setLugar(campos[2]);
            if(!cambios) cambios = true;
        }
        if(!campos[3].equals(reunionActual.obtenerFechaString())){
            reunionActual.cambiarFechaString(campos[3]);
            if(!cambios) cambios = true;
        }
        if(!campos[4].equals(reunionActual.obtenerHoraString())){
            reunionActual.setHora(campos[4]);
            if(!cambios) cambios = true;
        }
        return cambios;
    }

    private void mostrarInvitados(){
        ImageView ivBack = llMostrarInvitados.findViewById(R.id.ivInvitadosBack);
        ImageView ivIn = llMostrarInvitados.findViewById(R.id.ivInvitadosIn);

        if(ivBack.getVisibility() == View.GONE){
            //La lista de invitados no se esta mostrando
            ivIn.setVisibility(View.GONE);
            ivBack.setVisibility(View.VISIBLE);
            cambiarSubFragment(new InvitadosFragment());

        }else{
            //La lista de invitados se esta mostrando
            ivIn.setVisibility(View.VISIBLE);
            ivBack.setVisibility(View.GONE);

            llInfoReunion.setVisibility(View.VISIBLE);
            llTareasClosed.setVisibility(View.VISIBLE);
            fragmentContainer.setVisibility(View.INVISIBLE);
        }
    }

    private void cambiarSubFragment(Fragment fragment){
        FragmentManager fm = requireActivity().getSupportFragmentManager();
        Fragment fActual = fm.findFragmentById(R.id.reunionContainer);
        boolean fragmentNuevo = (fActual == null || !fActual.getClass().equals(fragment.getClass()));

        if(fragment instanceof TareaFragment || fragmentNuevo)
            fm.beginTransaction()
                    .replace(R.id.reunionContainer,fragment)
                    .commit();

        //Modificar layout
        llInfoReunion.setVisibility(View.GONE);
        llEditReunion.setVisibility(View.GONE);
        llTareasClosed.setVisibility(View.GONE);
        llTareasOpen.setVisibility(View.GONE);
        btAdd.setVisibility(View.GONE);
        fragmentContainer.setVisibility(View.VISIBLE);
    }

    private void mostrarListaTareas(boolean mostrar){
        llTareasClosed.setVisibility(mostrar ? View.GONE : View.VISIBLE);
        llTareasOpen.setVisibility(mostrar ? View.VISIBLE : View.GONE);
        btAdd.setVisibility(mostrar && reunionActual.estaActiva() ? View.VISIBLE : View.GONE);
        fragmentContainer.setVisibility(mostrar ? View.GONE : View.INVISIBLE);
    }

    public void cerrarTarea(boolean actualizar){
        if(actualizar)
            llenarRecyclerViewTareas();
        tareaSeleccionada = null;

        llMostrarInvitados.setVisibility(View.VISIBLE);
        llInfoReunion.setVisibility(View.VISIBLE);
        llTareasClosed.setVisibility(View.VISIBLE);
        fragmentContainer.setVisibility(View.INVISIBLE);
        FragmentManager fm = requireActivity().getSupportFragmentManager();
        Fragment fActual = fm.findFragmentById(R.id.reunionContainer);
        fm.beginTransaction()
                .remove(fActual)
                .commit();
    }

    private void showBottomDialog() {
        if(getContext() != null){
            final Dialog dialog = new Dialog(getContext());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.boton_add);

            View opcionLayout = dialog.findViewById(R.id.llPrimeraOpcion);
            ((TextView)opcionLayout.findViewById(R.id.tvPrimeraOpcion)).setText(R.string.drawer_tarea);

            ImageView cancelButton = dialog.findViewById(R.id.cancelButton);

            opcionLayout.setOnClickListener(v -> {
                cambiarSubFragment(new TareaFragment());
                llMostrarInvitados.setVisibility(View.GONE);
                dialog.dismiss();
            });

            cancelButton.setOnClickListener(view -> dialog.dismiss());

            dialog.show();
            if(dialog.getWindow() != null){
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                dialog.getWindow().setGravity(Gravity.BOTTOM);
            }
        }
    }
    //endregion
}