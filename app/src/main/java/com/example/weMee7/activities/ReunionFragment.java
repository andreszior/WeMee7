package com.example.weMee7.activities;

import static com.example.weMee7.model.dao._SuperDAO.Fields.ID_REUNION;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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
import android.widget.LinearLayout;

import com.example.weMee7.comun.TareaAdapter;

import com.example.weMee7.comun.TimeUtils;
import com.example.weMee7.model.dao.ReunionDAO;
import com.example.weMee7.model.dao.TareaDAO;

import com.example.weMee7.model.entities.Reunion;
import com.example.weMee7.model.entities.Tarea;
import com.example.weMee7.view._SuperActivity;

import com.example.weMee7.view.reunion.InvitadosFragment;
import com.example.weMee7.view.usuario.HomeFragment;
import com.example.weMee7.view.usuario.UsuarioActivity;
import com.example.weMee7.viewmodel.GestionarReunion;
import com.example.wemee7.R;

import android.app.Dialog;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ReunionFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class ReunionFragment extends Fragment {

    Reunion reunionActual;
    private Tarea tareaSeleccionada;
    View llInfoReunion, llEditReunion, llMostrarInvitados, llTareasClosed, llTareasOpen, fragmentContainer;
    EditText [] etCampos;
    ImageButton btAdd, btEditReunion,btBorrarReunion;
    RecyclerView rvTareasReunion;
    Drawable fondoEditable;

    GestionarReunion vmReunion = new GestionarReunion();

    public ReunionFragment(){}


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ReunionFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ReunionFragment newInstance(Reunion reunion) {
        ReunionFragment fragment = new ReunionFragment();
        Bundle args = new Bundle();
        args.putParcelable("meeting", reunion);
        args.putString("id", reunion.getId());
        fragment.setArguments(args);
        return fragment;
    }

    public Tarea getTareaSeleccionada() {
        return tareaSeleccionada;
    }

    public String getIdReunionActual(){
        return reunionActual.getId();
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
        dataBinding(view);


        //Cargar reunion + cargar recyclerView
        cargarReunion(idReunion);
        return view;
    }


    private void dataBinding (View view){
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
        etCampos[3].setFocusable(false);
        etCampos[4].setFocusable(false);
        setEditable(false);

        //Botones
        btEditReunion = view.findViewById(R.id.btReunionEdit);
        btBorrarReunion = view.findViewById(R.id.btReunionDelete);
        btAdd = view.findViewById(R.id.boton_add);

        //Asignar listeners
        llMostrarInvitados.setOnClickListener(v -> mostrarInvitados());
        llTareasClosed.findViewById(R.id.btMostrarTareas).setOnClickListener(v -> mostrarTareas(true));
        llTareasOpen.findViewById(R.id.btOcultarTareas).setOnClickListener(v -> mostrarTareas(false));
        btAdd.setOnClickListener(v -> showBottomDialog());
        view.findViewById(R.id.btReunionUndo).setOnClickListener(v -> deshacerCambios());
        view.findViewById(R.id.btReunionGuardar).setOnClickListener(v -> guardarCambios());
    }

    private void cargarReunion(String idReunion){
        new ReunionDAO().obtenerRegistroPorId(idReunion, entity ->{
            reunionActual = (Reunion)entity;
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
        boolean esCreador = ((UsuarioActivity)requireActivity()).esCreador();
        boolean esModificable =  esCreador && reunionActual.estaActiva();

        btEditReunion.setVisibility(esModificable ? View.VISIBLE : View.GONE);
        btBorrarReunion.setVisibility(esCreador ? View.VISIBLE : View.GONE);

        if(esModificable){
            //Selectores de fecha y hora
            etCampos[3].setOnClickListener(v -> selectFecha());
            etCampos[4].setOnClickListener(v -> selectHora());
            //Botones de edicion
            btEditReunion.setOnClickListener(v -> editarReunion(true));
        }
        if(esCreador)
            btBorrarReunion.setOnClickListener(v -> borrarReunion());
    }

    private void llenarRecyclerViewTareas(){
        List<Tarea> listaTareas = new ArrayList<>();
        TareaAdapter tareaAdapter = new TareaAdapter(listaTareas, this.getContext(), item -> {
            tareaSeleccionada = item;
            cambiarSubFragment(new TareaFragment());
            llMostrarInvitados.setVisibility(View.GONE);
        });

        new TareaDAO().obtenerListaPorIdForaneo(ID_REUNION, reunionActual.getId(), resultado -> {
                    rvTareasReunion.setHasFixedSize(true);
                    rvTareasReunion.setLayoutManager(new LinearLayoutManager(this.getContext()));
                    rvTareasReunion.setAdapter(tareaAdapter);
            tareaAdapter.setListaTareas((List<Tarea>)resultado);
        });
    }


    private void selectFecha(){
        Calendar calendar= Calendar.getInstance();
        DatePickerDialog.OnDateSetListener dateSetListener= (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(TimeUtils.FORMATO_FECHA);
            etCampos[3].setText(simpleDateFormat.format(calendar.getTime()));
        };
        DatePickerDialog datePicker = new DatePickerDialog(requireActivity(),
                dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));

        datePicker.getDatePicker().setMinDate(System.currentTimeMillis());
        datePicker.show();
    }

    private void selectHora(){
        Calendar calendar= Calendar.getInstance();

        TimePickerDialog.OnTimeSetListener timeSetListener = (view, hourOfDay, minute) -> {
            calendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
            calendar.set(Calendar.MINUTE, minute);

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(TimeUtils.FORMATO_HORA);
            etCampos[4].setText(simpleDateFormat.format(calendar.getTime()));
        };
        new TimePickerDialog(requireActivity(),
                timeSetListener,calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE), true).show();
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
            vmReunion.eliminarReunion(reunionActual.getId());
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
        if(hayCambios())
            vmReunion.modificarReunion(reunionActual);
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
        boolean fragmentNuevo = (fActual == null || fActual.getClass().equals(fragment.getClass()));

        if(fragment instanceof TareaFragment || fragmentNuevo)
            fm.beginTransaction()
                    .replace(R.id.reunionContainer,fragment)
                    .commit();

        //Modificar layout
        llInfoReunion.setVisibility(View.GONE);
        llEditReunion.setVisibility(View.GONE);
        llTareasClosed.setVisibility(View.GONE);
        llTareasOpen.setVisibility(View.GONE);
        fragmentContainer.setVisibility(View.VISIBLE);
    }

    private void mostrarTareas(boolean mostrar){
        llTareasClosed.setVisibility(mostrar ? View.GONE : View.VISIBLE);
        llTareasOpen.setVisibility(mostrar ? View.VISIBLE : View.GONE);
        btAdd.setVisibility(mostrar ? View.VISIBLE : View.GONE);
        fragmentContainer.setVisibility(mostrar ? View.GONE : View.INVISIBLE);
    }


    private void showBottomDialog() {
        if(getContext() != null){
            final Dialog dialog = new Dialog(getContext());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.boton_add);

            LinearLayout AddLayout = dialog.findViewById(R.id.llPrimeraOpcion);
            LinearLayout UnirseLayout = dialog.findViewById(R.id.llSegundaOpcion);
            LinearLayout AddTareaLayout = dialog.findViewById(R.id.llTerceraOpcion);
            ImageView cancelButton = dialog.findViewById(R.id.cancelButton);

            UnirseLayout.setVisibility(View.GONE);
            AddLayout.setVisibility(View.GONE);

            AddTareaLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cambiarSubFragment(new TareaFragment());
                    llMostrarInvitados.setVisibility(View.GONE);
                    dialog.dismiss();
                }
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