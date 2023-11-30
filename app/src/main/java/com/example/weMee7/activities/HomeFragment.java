package com.example.weMee7.activities;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.fragment.app.Fragment;
import com.example.weMee7.comun.ListAdapter;
import com.example.weMee7.comun.TimeUtils;
import com.example.weMee7.model.entities.Reunion;
import com.example.wemee7.R;


import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    List<Reunion> reuniones;
    final Dialog dialog = null;
    ImageButton boton_add;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        init(view);
        final Dialog dialog = new Dialog(getContext());
        ImageButton boton_add = view.findViewById(R.id.boton_add);
        boton_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomDialog();

            }
        });


        return view;
    }

    public void init(View view){
        reuniones = new ArrayList<>();
        reuniones.add(new Reunion("1","Cumpleaños Carlos","lorem ipsum", "Bar Santianes,17", TimeUtils.fechaHoraToTimestamp("04/02/2024","18:00")));
        reuniones.add(new Reunion("1","Cumpleaños Carlos","lorem ipsum", "Bar Santianes,17", TimeUtils.fechaHoraToTimestamp("04/02/2024","18:00")));
        reuniones.add(new Reunion("1","Cumpleaños Carlos","lorem ipsum", "Bar Santianes,17", TimeUtils.fechaHoraToTimestamp("04/02/2024","18:00")));
        reuniones.add(new Reunion("1","Cumpleaños Carlos","lorem ipsum", "Bar Santianes,17", TimeUtils.fechaHoraToTimestamp("04/02/2024","18:00")));
        reuniones.add(new Reunion("1","Cumpleaños Carlos","lorem ipsum", "Bar Santianes,17", TimeUtils.fechaHoraToTimestamp("04/02/2024","18:00")));
        reuniones.add(new Reunion("1","Cumpleaños Carlos","lorem ipsum", "Bar Santianes,17", TimeUtils.fechaHoraToTimestamp("04/02/2024","18:00")));

        ListAdapter listAdapter = new ListAdapter(reuniones, getActivity(), new ListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Reunion item) {
                verREunion(item);
            }
        });
        RecyclerView recyclerView = view.findViewById(R.id.listRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(listAdapter);
    }


    public void verREunion(Reunion item){
        Intent intent = new Intent(getActivity(), ReunionActivity.class);
        intent.putExtra("idCreador", item.getIdCreador());
        intent.putExtra("nombre", item.getNombre());
        intent.putExtra("descripcion", item.getDescripcion());
        intent.putExtra("lugar", item.getLugar());
        //intent.putExtra("fechaHora", TimeUtils.timestampToFechaHora(item.getFechaHora(), false));
        startActivity(intent);
    }

    private void showBottomDialog() {
        Fragment selectedFragment = null;
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.boton_add);

        LinearLayout AddLayout = dialog.findViewById(R.id.layoutAdd);
        LinearLayout UnirseLayout = dialog.findViewById(R.id.layoutUnirse);
        ImageView cancelButton = dialog.findViewById(R.id.cancelButton);

        AddLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment selectedFragment = new AddFragment();
                if (getActivity() != null) {
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                }
                dialog.dismiss();



            }
        });

        UnirseLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
