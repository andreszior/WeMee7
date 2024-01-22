package com.example.weMee7.activities;

import static com.example.weMee7.model.dao._SuperDAO.Fields.ID_REUNION;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.weMee7.comun.TareaAdapter;
import com.example.weMee7.comun.TimeUtils;

import com.example.weMee7.model.dao.ReunionDAO;
import com.example.weMee7.model.dao.TareaDAO;

import com.example.weMee7.model.entities.Reunion;
import com.example.weMee7.model.entities.Tarea;
import com.example.weMee7.view._SuperActivity;

import com.example.weMee7.view.reunion.InvitadosFragment;
import com.example.weMee7.view.usuario.UsuarioActivity;
import com.example.wemee7.R;

import android.app.Dialog;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ReunionFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class ReunionFragment extends Fragment {

    Reunion reunion;
    View llInfoReunion, llEditReunion, llMostrarInvitados, llMostrarTareas, flTareas, fragmentContainer;
    TextView tvNombreReunion, tvDescripReunion, tvLugarReunion, tvFechaReunion, tvHoraReunion;
    ImageButton btEditReunion,btBorrarReunion;
    RecyclerView rvTareasReunion;

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
        cargarDetallesReunion(idReunion);
        return view;
    }


    private void dataBinding (View view){

        boolean esCreador = ((UsuarioActivity)requireActivity()).esCreador();

        //Contenedores
        llInfoReunion = view.findViewById(R.id.llReunionInfo);
        llEditReunion = view.findViewById(R.id.llReunionEdit);
        llMostrarInvitados = view.findViewById(R.id.llMostrarInvitados);
        llMostrarTareas = view.findViewById(R.id.llMostrarTareas);
        flTareas = view.findViewById(R.id.flTareas);
        fragmentContainer = view.findViewById(R.id.reunionContainer);

        llInfoReunion.setVisibility(View.VISIBLE);
        llEditReunion.setVisibility(View.GONE);
        llMostrarInvitados.setVisibility(View.VISIBLE);
        llMostrarTareas.setVisibility(View.VISIBLE);
        flTareas.setVisibility(View.GONE);

        //RecyclerView
        rvTareasReunion = view.findViewById(R.id.rvReunionTareas);

        //Etiquetas
        tvNombreReunion = view.findViewById(R.id.tvNombreReunion);
        tvDescripReunion = view.findViewById(R.id.tvReunionDescrip);
        tvLugarReunion = view.findViewById(R.id.tvReunionLugar);
        tvFechaReunion = view.findViewById(R.id.tvReunionFecha);
        tvHoraReunion = view.findViewById(R.id.tvReunionHora);

        //Botones
        btEditReunion = view.findViewById(R.id.btReunionEdit);
        btBorrarReunion = view.findViewById(R.id.btReunionDelete);

        int visible = (esCreador ? View.VISIBLE : View.GONE);

        btEditReunion.setVisibility(visible);
        btBorrarReunion.setVisibility(visible);

        //Asignar listeners
        llMostrarInvitados.setOnClickListener(v -> mostrarInvitados());
        llMostrarTareas.findViewById(R.id.btMostrarTareas).setOnClickListener(v -> mostrarTareas());
        view.findViewById(R.id.boton_add).setOnClickListener(v -> showBottomDialog());

        if(esCreador){
            btEditReunion.setOnClickListener(v -> editarReunion());
            btBorrarReunion.setOnClickListener(v -> borrarReunion());
            view.findViewById(R.id.btReunionUndo).setOnClickListener(v -> deshacerCambios());
            view.findViewById(R.id.btReunionGuardar).setOnClickListener(v -> guardarCambios());
        }
    }

    private void cargarDetallesReunion(String idReunion){
        new ReunionDAO().obtenerRegistroPorId(idReunion, entity ->{
            reunion = (Reunion)entity;
            tvNombreReunion.setText(reunion.getNombre());
            tvDescripReunion.setText(reunion.getDescripcion());
            tvLugarReunion.setText(reunion.getLugar());
            tvFechaReunion.setText(reunion.obtenerFechaString());
            tvHoraReunion.setText(reunion.obtenerHoraString());
            llenarRecyclerViewTareas();
        });
    }

    private void llenarRecyclerViewTareas(){
        List<Tarea> listaTareas = new ArrayList<>();

        TareaAdapter tareaAdapter = new TareaAdapter(listaTareas, this.getContext(),  new TareaAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Tarea item) {
                Fragment selectedFragment = new TareaFragment();
                //Tarea tareaDetalles = listaTareas.get(item);
                Bundle args = new Bundle();
                args.putParcelable("TareaSeleccionada", item);
                selectedFragment.setArguments(args);
                ((_SuperActivity)requireActivity()).colocarFragment(selectedFragment);

            }
        });

        new TareaDAO().obtenerListaPorIdForaneo(ID_REUNION
        , reunion.getId(), resultado -> {
                    rvTareasReunion.setHasFixedSize(true);
                    rvTareasReunion.setLayoutManager(new LinearLayoutManager(this.getContext()));
                    rvTareasReunion.setAdapter(tareaAdapter);
            tareaAdapter.setListaTareas((List<Tarea>)resultado);
                });
    }

    //region EVENTOS BOTON
    private void editarReunion(){
        ((_SuperActivity)requireActivity()).lanzarMensaje(R.string.bt_editar_tarea);

    }

    private void borrarReunion(){
        ((_SuperActivity)requireActivity()).lanzarMensaje(R.string.bt_delete_tarea);
    }

    private void deshacerCambios(){

    }

    private void guardarCambios(){

    }

    private void mostrarInvitados(){
        ImageView ivBack = llMostrarInvitados.findViewById(R.id.ivInvitadosBack);
        ImageView ivIn = llMostrarInvitados.findViewById(R.id.ivInvitadosIn);
        FragmentManager fm = requireActivity().getSupportFragmentManager();
        Fragment fActual = fm.findFragmentById(R.id.fragment_container);
        if(ivBack.getVisibility() == View.GONE){
            //La lista de invitados no se esta mostrando
            ivIn.setVisibility(View.GONE);
            ivBack.setVisibility(View.VISIBLE);

            if(!(fActual instanceof TareaFragment))
                fm.beginTransaction()
                        .replace(R.id.reunionContainer,new InvitadosFragment())
                        .commit();
            llInfoReunion.setVisibility(View.GONE);
            llEditReunion.setVisibility(View.GONE);
            llMostrarTareas.setVisibility(View.GONE);
            fragmentContainer.setVisibility(View.VISIBLE);

        }else{
            //La lista de invitados se esta mostrando
            ivIn.setVisibility(View.VISIBLE);
            ivBack.setVisibility(View.GONE);

            llInfoReunion.setVisibility(View.VISIBLE);
            llMostrarTareas.setVisibility(View.VISIBLE);
            fragmentContainer.setVisibility(View.GONE);
        }

    }

    private void mostrarTareas(){
        ImageView ivTareasToggle = llMostrarTareas.findViewById(R.id.ivTareasShow);
        ivTareasToggle.setRotation(180);
        if(flTareas.getVisibility() == View.GONE){
            flTareas.setVisibility(View.VISIBLE);
        }else{
            flTareas.setVisibility(View.GONE);
        }
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
                    Fragment selectedFragment = new TareaFragment();
                    ((_SuperActivity)requireActivity()).colocarFragment(selectedFragment);
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