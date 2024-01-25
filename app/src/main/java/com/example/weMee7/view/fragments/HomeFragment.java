package com.example.weMee7.view.fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
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
import android.widget.TextView;
import android.widget.Toast;


import com.example.weMee7.model.dao.ReunionDAO;
import com.example.weMee7.model.entities.Invitacion;
import com.example.weMee7.model.entities.Reunion;
import com.example.weMee7.view.activity.UsuarioActivity;
import com.example.weMee7.view.activity._SuperActivity;

import com.example.weMee7.view.subfragments.ReunionesListFragment;
import com.example.weMee7.viewmodel.GestionarDatos;
import com.example.weMee7.viewmodel.InvitarUsuario;
import com.example.weMee7.viewmodel.ValidarUsuario;
import com.example.wemee7.R;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Fragment que gestiona las reuniones del usuario.
 * Dispone de un TabLayout y un ViewPager con 3 pestañas:
 * 0. Invitaciones pendientes
 * 1. Reuniones activas
 * 2. Reuniones pasadas
 */
public class HomeFragment extends Fragment {
    //Componentes de paginador
    private ViewPager2 viewPager;
    private TabLayout tabs;

    //Lista de reuniones y mapa de invitaciones
    private List<Reunion> reunionesList;
    private Map<String,Invitacion> invitacionesMap;

    //Control de cambios
    private boolean hayCambios;

    //Id del usuario
    private String idUsuarioActual;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        idUsuarioActual = FirebaseAuth.getInstance().getCurrentUser().getUid();
        hayCambios = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        viewPager = view.findViewById(R.id.vpHomeViewPager);
        tabs = view.findViewById(R.id.tlHomeTabLayout);

        //ViewModel Validar
        ValidarUsuario vmValidar = new ValidarUsuario(requireActivity());

        //Error en el id del Usuario > Vuelve al login
        if(idUsuarioActual == null){
            _SuperActivity activity = (_SuperActivity)requireActivity();
            activity.lanzarMensaje(R.string.msj_fUser_fail);
            vmValidar.reiniciarCredenciales();
            onDestroy();
            return null;
        }

        //Habilitar menu hamburguesa
        ((_SuperActivity)requireActivity()).setDrawerMenu(true);

        //Mostrar pantalla carga
        ((_SuperActivity)requireActivity()).setCargando(false);

        //Reactivar usuario (si estuviera inactivo)
        vmValidar.rehabilitarUsuario(idUsuarioActual);

        //Añadir reunion de enlace (en su caso)
        String idReunion = ((UsuarioActivity)requireActivity()).getIdReunionLink();
        if(idReunion != null) {
            cargarReunionLink(idReunion);
            ((UsuarioActivity)requireActivity()).nullIdReunionLink();//Se anula el codigo del link
        } else //Si no hay reunion, se realizan las consultas directamente
            cargarDatos(); //Consulta de reuniones e invitaciones

        //Boton inferior
        ImageButton boton_add = view.findViewById(R.id.bt_compartir);
        boton_add.setOnClickListener(view1 -> showBottomDialog());
        return view;
    }

    /**
     * Comprueba el id de reunion pasado por enlace.
     * Posibilidades:
     * 1. Enlace no valido.
     * 2. Reunion creada por el usuario.
     * 3. Reunion a la que ya está invitado el usuario.
     * 4. Se genera una invitación
     * @param idReunion
     */
    private void cargarReunionLink(String idReunion) {
        new ReunionDAO().obtenerRegistroPorId(idReunion, resultado -> {
            Reunion r = (Reunion)resultado;
            int mensaje;

            //Vigilante de tarea completa
            TaskCompletionSource<DocumentSnapshot> tcs = null;

            if(r == null)
                mensaje = R.string.msj_enlace_fail;
            else if(!r.estaActiva())
                mensaje = R.string.msj_reunion_yacelebrada;
            else if(r.getIdCreador().equals(idUsuarioActual))
                mensaje = R.string.msj_invitacion_creador;
            else if(r.getInvitadosList().contains(idUsuarioActual))
                mensaje = R.string.msj_invitacion_yainvitado;
            else{
                //Si se crea una invitacion, la consulta de reuniones debe esperar
                mensaje = R.string.msj_invitacion_annadida;
                tcs = new InvitarUsuario().enviarInvitacion(idReunion,idUsuarioActual);
            }
            if(tcs != null){
                //Cuando termine la tarea de enviar invitacion, se realizan las demas consultas
                Task<DocumentSnapshot> getTask = tcs.getTask();
                getTask.addOnSuccessListener(documentSnapshot -> {
                    cargarDatos();
                });
            }else
                cargarDatos();

            if(mensaje != 0)
                Toast.makeText(requireActivity(),
                        getResources().getString(mensaje),
                        Toast.LENGTH_SHORT).show();
        });
    }

    /**
     * Se llenan la lista de reuniones y el mapa de invitaciones
     * con aquellas vinculadas con el usuario.
     */
    private void cargarDatos() {
        reunionesList = new ArrayList<>();
        invitacionesMap = new HashMap<>();
        GestionarDatos vmDatos = new GestionarDatos();

        TaskCompletionSource<DocumentSnapshot> tcs =
            vmDatos.obtenerReunionesUsuario(reunionesList,invitacionesMap,idUsuarioActual,true);

        if(tcs != null) {
            Task<DocumentSnapshot> getTask = tcs.getTask();
            getTask.addOnSuccessListener(documentSnapshot -> {
                setupViewPager();
            });
        }
    }

    /**
     * Configura el viewPager con 3 fragments,
     * que se aloja en el TabLayout con 3 tabs
     */
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
        if(invitaciones == 0){
            contador.setVisibility(View.GONE);
        }else{
            contador.setText(String.valueOf(invitaciones));
            contador.setVisibility(View.VISIBLE);
        }
        tabs.getTabAt(0).setCustomView(header);
    }

    //GETTERS Y SETTERS para los fragments del viewPager
    public List<Reunion> getReunionesList() {
        return reunionesList;
    }

    public Map<String,Invitacion> getInvitacionesMap() {
        return invitacionesMap;
    }

    public boolean hayCambios() {
        return hayCambios;
    }

    public void setHayCambios(boolean hayCambios) {
        this.hayCambios = hayCambios;
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
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.boton_add);

        View opcionLayout = dialog.findViewById(R.id.llPrimeraOpcion);
        ((TextView)opcionLayout.findViewById(R.id.tvPrimeraOpcion)).setText(R.string.drawer_reunion);
        ImageView cancelButton = dialog.findViewById(R.id.cancelButton);

        opcionLayout.setOnClickListener(v -> {
            Fragment selectedFragment = new AddFragment();
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
            }
            dialog.dismiss();

        });

        opcionLayout = dialog.findViewById(R.id.llSegundaOpcion);
        opcionLayout.setVisibility(View.VISIBLE);
        ((TextView)opcionLayout.findViewById(R.id.tvSegundaOpcion)).setText(R.string.drawer_unirse);

        opcionLayout.setOnClickListener(v -> {
            int mensaje = R.string.msj_unirse_fail;
            ClipboardManager clipboard =
                    (ClipboardManager) requireActivity().getSystemService(Context.CLIPBOARD_SERVICE);
            // Verifica si hay algo en el portapapeles
            ClipData item = clipboard.getPrimaryClip();
            if(item != null && item.getDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)){
                String clipboardContent = item.getItemAt(0).getText().toString();

                Matcher matcher = Pattern
                        .compile("wemee7\\.acd\\/(\\w+)")
                        .matcher(clipboardContent);

                if(matcher.find()) {
                    cargarReunionLink(matcher.group(1));
                    mensaje = 0;
                }
            }
            if(mensaje != 0)
                ((_SuperActivity)requireActivity()).lanzarMensaje(mensaje);

            dialog.dismiss();

        });

        cancelButton.setOnClickListener(view -> dialog.dismiss());

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

    }


}
