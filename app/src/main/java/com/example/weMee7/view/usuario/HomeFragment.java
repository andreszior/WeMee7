package com.example.weMee7.view.usuario;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.app.Dialog;
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
import android.widget.TextView;

import com.example.weMee7.activities.AddFragment;
import com.example.weMee7.model.dao.InvitacionDAO;
import com.example.weMee7.model.dao.ReunionDAO;
import com.example.weMee7.model.entities.Invitacion;
import com.example.weMee7.model.entities.Reunion;
import com.example.weMee7.view._SuperActivity;
import com.example.wemee7.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment {
    ViewPager2 viewPager;
    TabLayout tabs;

    private List<Reunion> reunionesList;
    private Map<String,Invitacion> invitacionesMap;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        //Habilitar menu hamburguesa
        ((_SuperActivity)requireActivity()).setDrawerMenu(true);

        //Mostrar pantalla carga
        ((_SuperActivity)requireActivity()).setCargando(false);

        //Consulta de reuniones e invitaciones
        viewPager = view.findViewById(R.id.vpHomeViewPager);
        tabs = view.findViewById(R.id.tlHomeTabLayout);
        dataBinding();

        //Boton inferior
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

    private void dataBinding() {
        String idUsuario = FirebaseAuth.getInstance().getCurrentUser().getUid();
        new ReunionDAO().obtenerReunionesUsuario(idUsuario, true, rList -> {
            reunionesList = (List)rList;
            new InvitacionDAO().obtenerInvitacionesActivas(idUsuario,true, iMap -> {
                invitacionesMap = (Map)iMap;
                setupViewPager();
            });
        });
    }

    private void setupViewPager(){
        //Configuracion ViewPager
        viewPager.setAdapter(new PagerAdapter(requireActivity()));

        //Configuracion de pestañas
        new TabLayoutMediator(tabs, viewPager, (tab, position) -> {
            switch(position){
                case 0:
                    tab.setCustomView(R.layout.tab_header);
                    break;
                case 1:
                    tab.setText("Activas");
                    break;
                case 2:
                    tab.setText("Pasadas");
                    break;
                default:
            }
        }).attach();

        viewPager.setCurrentItem(1);
        tabs.getTabAt(1).select();
    }


    /**
     * Si hay invitaciones pendientes,
     * muestra un numero rojo en la primera tab
     * (este metodo es llamado por el fragment de la tab de invitaciones)
     * @param invitaciones
     */
    public void popUpInvitaciones (int invitaciones){
        View header = LayoutInflater.from(requireActivity()).inflate(R.layout.tab_header,null);
        TextView contador = header.findViewById(R.id.tab_contador);
        contador.setText(String.valueOf(invitaciones));
        contador.setVisibility(View.VISIBLE);
        tabs.getTabAt(0).setCustomView(header);
    }

    public List<Reunion> getReunionesList() {
        return reunionesList;
    }

    public Map<String,Invitacion> getInvitacionesMap() {
        return invitacionesMap;
    }

    /**
     * Adapter personalizado del ViewPager.
     * Muestra tres paginas de ReunionesListFragment,
     * cuyo contenido viene determinado por la posicion
     */
    class PagerAdapter extends FragmentStateAdapter{
        public PagerAdapter(@NonNull FragmentActivity fa) {
            super(fa);
        }
        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return ReunionesListFragment.newInstance(position);
        }
        @Override
        public int getItemCount() {
            return 3;
        }
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
