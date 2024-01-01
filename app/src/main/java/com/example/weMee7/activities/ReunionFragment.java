package com.example.weMee7.activities;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.weMee7.comun.TimeUtils;
import com.example.weMee7.model.dao.ReunionDAO;
import com.example.weMee7.model.entities.Reunion;
import com.example.weMee7.view._SuperActivity;
import com.example.weMee7.viewmodel.InvitarUsuario;
import com.example.wemee7.R;
import com.google.firebase.Timestamp;
import android.app.Dialog;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ReunionFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class ReunionFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";


    // TODO: Rename and change types of parameters
    private String mParam1;

    Reunion reunion;




    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ReunionFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ReunionFragment newInstance(Reunion idReunion) {
        ReunionFragment fragment = new ReunionFragment();
        Bundle args = new Bundle();
        args.putParcelable("meeting", idReunion);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            reunion = (Reunion) getArguments().getParcelable("meeting");

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

        tvLugarEvento = view.findViewById(R.id.tvLugarReunion);
        tvFechaEvento = view.findViewById(R.id.tvFechaEventos);
        tvReunion = view.findViewById(R.id.tvReunion);
        botonTarea = view.findViewById(R.id.boton_add);

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


    private void showBottomDialog() {
        Fragment selectedFragment = null;
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


                /*
                if (getActivity() != null) {
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                }

                 */
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
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

    }
}