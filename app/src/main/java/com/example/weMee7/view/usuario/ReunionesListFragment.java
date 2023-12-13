package com.example.weMee7.view.usuario;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.weMee7.activities.ReunionActivity;
import com.example.weMee7.comun.ReunionesListAdapter;
import com.example.weMee7.model.dao.InvitacionDAO;
import com.example.weMee7.model.dao.ReunionDAO;
import com.example.weMee7.model.entities.Invitacion;
import com.example.weMee7.model.entities.Reunion;
import com.example.weMee7.view._SuperActivity;
import com.example.weMee7.viewmodel.InvitarUsuario;
import com.example.wemee7.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ReunionesListFragment extends Fragment {
    //Listas de datos
    List<Reunion> reunionesTab;
    Map<String,Invitacion> invitacionesTab;

    //Componentes del layout
    RecyclerView rvReunionesList;
    TextView tvSinReuniones;

    //Gestion de tab seleccionada
    int tab;//tab seleccionada que determina la lista
    public static final int LISTA_INVITACIONES = 0;
    public static final int REUNIONES_ACTIVAS = 1;
    public static final int REUNIONES_PASADAS = 2;

    //Carga de info en segundo plano
    private ExecutorService exec;
    private Future<?> task;

    public static ReunionesListFragment newInstance(int tab){
        ReunionesListFragment fragment = new ReunionesListFragment();
        Bundle args = new Bundle();
        args.putInt("TAB_KEY",tab);
        fragment.setArguments(args);
        return fragment;
    }
    public ReunionesListFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null)
            tab = getArguments().getInt("TAB_KEY");
        else
            tab = REUNIONES_ACTIVAS;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflar layout
        View view = inflater.inflate(R.layout.fragment_reuniones_list, container, false);
        rvReunionesList = view.findViewById(R.id.rvHomeLista);
        tvSinReuniones = view.findViewById(R.id.tvHomeSinReuniones);

        //Carga de informacion en un hilo secundario
        exec = Executors.newSingleThreadExecutor();
        task = exec.submit(() -> {
            if(tab != REUNIONES_PASADAS){
                HomeFragment hf = getFragmentPadre();
                if(hf != null)
                    llenarListasTab(hf.getReunionesList(),hf.getInvitacionesMap());
            }
            // Actualizar la UI en el hilo principal
            getActivity().runOnUiThread(() -> {
                if(tab == REUNIONES_ACTIVAS)
                    llenarRecyclerView();
            });
        });

        //Ocultar capa de carga cuando se complete
        new Handler(Looper.getMainLooper()).post(() -> {
            try{
                task.get();
                ((_SuperActivity)requireActivity()).ocultarCargando();
            }catch(InterruptedException | ExecutionException e){
                e.printStackTrace();
                //Controlar esta excepcion !!!
            }
        });

        return view;
    }

    /**
     * Aplazar tareas para que se realicen
     * solamente cuando el Fragment es visible
     */
    @Override
    public void onResume() {
        super.onResume();
        switch(tab) {
            case LISTA_INVITACIONES:
                //Cargar RecyclerView
                llenarRecyclerView();
                break;
            case REUNIONES_PASADAS:
                //Hacer consultas y cargar RecyclerView
                cargarReunionesPasadas();
                break;
            default:
        }
    }

    /**
     * Cancela la carga de datos en segundo plano,
     * si no ha terminado.
     * En cualquier caso, cierra el ExecutorService.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (task != null && !task.isDone())
            task.cancel(true);
        if (exec != null)
            exec.shutdownNow();
    }

    private void cargarReunionesPasadas() {
        String idUsuario = FirebaseAuth.getInstance().getCurrentUser().getUid();
        new ReunionDAO().obtenerReunionesUsuario(idUsuario, false, rList -> {
            new InvitacionDAO().obtenerInvitacionesActivas(idUsuario,false, iMap -> {
                llenarListasTab((List)rList,(Map)iMap);
                llenarRecyclerView();
            });
        });
    }

    /**
     * Se llenan las listas de reuniones e invitaciones
     * Tab LISTA_INVITACIONES: Se crean de 0 y se añaden reuniones pendientes
     * Tabs REUNIONES: Se filtran las listas obtenidas en el fragment padre
     * @param rList
     * @param iMap
     */
    private void llenarListasTab(List<Reunion> rList, Map<String,Invitacion> iMap) {
        //Inicializar las listas
        if(tab == LISTA_INVITACIONES){
            reunionesTab = new ArrayList<>();
            invitacionesTab = new HashMap<>();
        }else {
            reunionesTab = rList;
            invitacionesTab = iMap;
        }

        //Iterar sobre las invitaciones
        Iterator<Map.Entry<String, Invitacion>> it = iMap.entrySet().iterator();
        while(it.hasNext()){
            Invitacion i = it.next().getValue();
            if(tab == LISTA_INVITACIONES) {
                if (i.getEstado() == Invitacion.EstadoInvitacion.ENVIADA)
                    //Añadir invitaciones pendientes
                    addInvitacionPendiente(i, rList);
            }else
                //Excluir invitaciones rechazadas o pendientes
                excluirInvitacion(i);
        }

        Collections.sort(reunionesTab,null);
        if(tab == LISTA_INVITACIONES && !invitacionesTab.isEmpty()) {
            HomeFragment hf = getFragmentPadre();
            if(hf != null)
                hf.popUpInvitaciones(iMap.size());
        }
    }

    /**
     * Añade a las listas Tab (reunionesTab e invitacionesTab)
     * las reuniones e invitaciones pendientes.
     * Si hay una invitacion pendiente sin reunion activa,
     * se actualiza la invitacion
     */
    private void addInvitacionPendiente(Invitacion i, List rList){
        Reunion r = new Reunion();
        r.setId(i.getIdReunion());
        int index = rList.indexOf(r);
        if(index != -1){
            reunionesTab.add((Reunion)rList.get(index));
            invitacionesTab.put(i.getIdReunion(),i);
        }else
            new InvitarUsuario().actualizarInvitacion(i);
    }

    /**
     * Excluye de las listas Tab (reunionesTab e invitacionesTab)
     * las reuniones e invitaciones rechazadas y pendientes
     * Si hay una invitacion pendiente sin reunion activa,
     * se actualiza la invitacion
     */
    private void excluirInvitacion(Invitacion i){
        Reunion r = new Reunion();
        r.setId(i.getIdReunion());
        if(i.getEstado() != Invitacion.EstadoInvitacion.ACEPTADA){
            reunionesTab.remove(r);
            invitacionesTab.remove(i);
        }else if(tab != REUNIONES_PASADAS && !reunionesTab.contains(r))
            //Si hay alguna invitacion sin reunion, esta desactualizada
                new InvitarUsuario().actualizarInvitacion(i);
    }

    /**
     * Llena el RecyclerView y muestra las reuniones;
     * si no hay reuniones a mostrar,
     * lo sustituye por un TextView
     */
    private void llenarRecyclerView() {
        if(reunionesTab.isEmpty())
            tvSinReuniones.setVisibility(View.VISIBLE);
        else{
            ReunionesListAdapter reunionesListAdapter =
                    new ReunionesListAdapter(reunionesTab, invitacionesTab, tab,
                            requireActivity(), item -> verReunion(item));
            rvReunionesList.setHasFixedSize(true);
            rvReunionesList.setLayoutManager(new LinearLayoutManager(getActivity()));
            rvReunionesList.setAdapter(reunionesListAdapter);
            rvReunionesList.setVisibility(View.VISIBLE);
        }
    }

    private HomeFragment getFragmentPadre(){
        Fragment padre = requireActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if(padre != null && padre instanceof HomeFragment)
            return (HomeFragment)padre;
        else
            return null;
    }


    public void verReunion(Reunion item) {
        Intent intent = new Intent(getActivity(), ReunionActivity.class);
        intent.putExtra("id",item.getId());
//        intent.putExtra("idCreador", item.getIdCreador());
//        intent.putExtra("nombre", item.getNombre());
//        intent.putExtra("descripcion", item.getDescripcion());
//        intent.putExtra("lugar", item.getLugar());
//        intent.putExtra("fechaHora", TimeUtils.timestampToFechaHora(item.getFechaHora(), false));
        startActivity(intent);
    }

}