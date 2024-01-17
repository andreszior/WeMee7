package com.example.weMee7.activities;

import static com.example.weMee7.model.dao._SuperDAO.Fields.ID_REUNION;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.weMee7.comun.TareaAdapter;
import com.example.weMee7.comun.TimeUtils;

import com.example.weMee7.comun.seguridad.SharedPref;
import com.example.weMee7.model.dao.TareaDAO;

import com.example.weMee7.model.entities.Reunion;
import com.example.weMee7.model.entities.Tarea;
import com.example.weMee7.view._SuperActivity;

import com.example.wemee7.R;
import com.google.firebase.Timestamp;
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

    private SharedPref sharedPref;




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
        if (getArguments() != null) {
            reunion = getArguments().getParcelable("meeting");
            assert reunion != null;
            reunion.setId(getArguments().getString("id"));

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.activity_reunion_detalle, container, false);
        cargarDetallesReunion(reunion, view);
        return view;
    }

    private void cargarDetallesReunion(Reunion reunion, View view){

        TextView tvLugarEvento, tvFechaEvento, tvReunion;
        ImageButton botonTarea;
        RecyclerView rcReunionTareas;

        tvLugarEvento = view.findViewById(R.id.tvLugarReunion);
        tvFechaEvento = view.findViewById(R.id.tvFechaEventos);
        tvReunion = view.findViewById(R.id.tvReunion);
        botonTarea = view.findViewById(R.id.boton_add);

        rcReunionTareas = view.findViewById(R.id.rvReunionTareas);
        //initTareasList(reunion, rcReunionTareas);
        llenarRecyclerViewTareas(rcReunionTareas);



        Timestamp fechaReunion = reunion.getFecha();
        tvLugarEvento.setText(reunion.getLugar());
        tvFechaEvento.setText(TimeUtils.timestampToFecha(fechaReunion));
        tvReunion.setText(reunion.getNombre());

        botonTarea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomDialog();
            }
        });




        //initTareasList(reunion);
    }

    private void llenarRecyclerViewTareas(RecyclerView rcEvento){
        List<Tarea> listaTareas = new ArrayList<>();

        sharedPref = new SharedPref(getContext(), true);

        TareaAdapter tareaAdapter = new TareaAdapter(listaTareas, this.getContext(), sharedPref, new TareaAdapter.OnItemClickListener() {
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
                    rcEvento.setHasFixedSize(true);
                    rcEvento.setLayoutManager(new LinearLayoutManager(this.getContext()));
                    rcEvento.setAdapter(tareaAdapter);
            tareaAdapter.setListaTareas((List<Tarea>)resultado);
                });
    }

    private void showBottomDialog() {
        if(getContext() != null){
            final Dialog dialog = new Dialog(getContext());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.boton_add);

            LinearLayout AddLayout = dialog.findViewById(R.id.layoutAdd);
            LinearLayout UnirseLayout = dialog.findViewById(R.id.layoutUnirse);
            LinearLayout AddTareaLayout = dialog.findViewById(R.id.layoutaddTarea);
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

            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            dialog.show();
            if(dialog.getWindow() != null){
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                dialog.getWindow().setGravity(Gravity.BOTTOM);
            }

        }

    }
}