package com.example.weMee7.view.usuario;

import android.app.AlertDialog;
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
import android.widget.Button;
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
    TextView tvMensaje;

    //Gestion de tab seleccionada
    int tab;//tab seleccionada que determina la lista
    public static final int LISTA_INVITACIONES = 0;
    public static final int REUNIONES_ACTIVAS = 1;
    public static final int REUNIONES_PASADAS = 2;

    //Carga de info en segundo plano
    private ExecutorService exec;
    private Future<?> task;
    private boolean cargado;

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
        cargado = false;
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
        tvMensaje = view.findViewById(R.id.tvHomeMensaje);

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
                if(tab == REUNIONES_ACTIVAS) {
                    llenarRecyclerView();
                    cargado = true;
                }
            });
        });

        //Ocultar capa de carga cuando se complete
        new Handler(Looper.getMainLooper()).post(() -> {
            try{
                task.get();
                ((_SuperActivity)requireActivity()).ocultarCargando();
            }catch(InterruptedException | ExecutionException e){
                e.printStackTrace();
            }
        });

        return view;
    }

    /**
     * Estas tareas se realizan
     * solamente cuando el Fragment es visible
     */
    @Override
    public void onResume() {
        super.onResume();

        if(!cargado){
            switch(tab) {
                case LISTA_INVITACIONES://TAB 0
                    //Cargar RecyclerView
                    llenarRecyclerView();
                    break;
                case REUNIONES_PASADAS://TAB 2
                    //Hacer consultas y cargar RecyclerView
                    cargarReunionesPasadas();
                    break;
                default:
            }
            cargado = true;
        }

        //TAB 1: Se actualiza cuando se acepta una invitacion
        if(tab == REUNIONES_ACTIVAS){
            HomeFragment hf = getFragmentPadre();
            if(hf.hayCambios()){
                llenarListasTab(hf.getReunionesList(),hf.getInvitacionesMap());
                llenarRecyclerView();
                hf.setHayCambios(false);
            }
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

    /**
     * Consulta de las reuniones e invitaciones pasadas
     */
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
     * Tabs REUNIONES: Se filtran las listas obtenidas en consultas previas
     * @param rList lista de reuniones
     * @param iMap mapa de invitaciones
     */
    private void llenarListasTab(List<Reunion> rList, Map<String,Invitacion> iMap) {
        //Inicializar las listas
        reunionesTab = new ArrayList<>();
        invitacionesTab = new HashMap<>();
        if(tab != LISTA_INVITACIONES) {
            reunionesTab.addAll(rList);
            invitacionesTab.putAll(iMap);
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
        if(tab == LISTA_INVITACIONES && !invitacionesTab.isEmpty())
            setPopUp();
    }

    private void setPopUp() {
        HomeFragment hf = getFragmentPadre();
        if(hf != null)
            hf.popUpInvitaciones(invitacionesTab.size());
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
        if(reunionesTab.isEmpty()) {
            tvMensaje.setText(R.string.tag_no_reuniones);
            tvMensaje.setVisibility(View.VISIBLE);
        }else{
            tvMensaje.setVisibility(View.GONE);
            ReunionesListAdapter reunionesListAdapter =
                    new ReunionesListAdapter(reunionesTab, invitacionesTab, tab,
                            requireActivity(), item -> pulsarItemReunion(item));
            rvReunionesList.setHasFixedSize(true);
            rvReunionesList.setLayoutManager(new LinearLayoutManager(getActivity()));
            rvReunionesList.setAdapter(reunionesListAdapter);
            rvReunionesList.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Obtiene una instancia del Fragment
     * donde se contiene el viewPager
     * @return
     */
    private HomeFragment getFragmentPadre(){
        Fragment padre = requireActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if(padre != null && padre instanceof HomeFragment)
            return (HomeFragment)padre;
        else
            return null;
    }


    public void pulsarItemReunion(Reunion item) {
        if(tab == LISTA_INVITACIONES)
            mostrarDialogInvitacion(item);
        else
            verReunion(item);
    }

    /**
     * Llama a una nueva Activity
     * con el id de la reunion.
     * @param item
     */
    private void verReunion(Reunion item){
        Intent intent = new Intent(getActivity(), ReunionActivity.class);
        intent.putExtra("id",item.getId());
        intent.putExtra("meeting", item);
        startActivity(intent);
    }

    /**
     * Cuadro de dialogo para aceptar o rechazar
     * una invitacion pendiente.
     * Solo se llama en tab 0 LISTA_INVITACIONES
     * @param item
     */
    private void mostrarDialogInvitacion(Reunion item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_invitacion, null);
        builder.setView(dialogView);

        //Componentes del layout
        ((TextView)dialogView.findViewById(R.id.dialog_title)).setText(item.getNombre());
        ((TextView)dialogView.findViewById(R.id.dialog_descripcion)).setText(item.getDescripcion());
        ((TextView)dialogView.findViewById(R.id.dialog_lugar)).setText(item.getLugar());
        ((TextView)dialogView.findViewById(R.id.dialog_fecha)).setText(item.obtenerFechaString());
        ((TextView)dialogView.findViewById(R.id.dialog_hora)).setText(item.obtenerHoraString());

        Button btUnirse = dialogView.findViewById(R.id.btDialogUnirse);
        Button btRechazar = dialogView.findViewById(R.id.btDialogRechazar);

        //Crear el dialog
        AlertDialog dialog = builder.create();

        //Eventos de los botones
        btUnirse.setOnClickListener(v -> {
            pulsarBtDialog(true,item,dialog);
        });
        btRechazar.setOnClickListener(v -> {
            pulsarBtDialog(false,item,dialog);
        });

        //Mostrar el dialog
        dialog.show();
    }

    /**
     * UNIRSE: Se lanza la Reunion Activity y se actualiza el mapa de invitaciones.
     * RECHAZAR: Se actualiza el mapa de invitaciones.
     * @param unirse aceptar / rechazar
     * @param item reunion de la invitacion
     * @param dialog cuadro de dialogo
     */
    private void pulsarBtDialog(boolean unirse, Reunion item, AlertDialog dialog){
        String idReunion = item.getId();
        Invitacion i = invitacionesTab.get(item.getId());

        new InvitarUsuario().responderInvitacion(i,unirse);
        dialog.dismiss();
        if(unirse) {
            verReunion(item);
            i.setEstado(Invitacion.EstadoInvitacion.ACEPTADA);
            HomeFragment hf = getFragmentPadre();
            hf.getInvitacionesMap().put(idReunion,i);
            hf.setHayCambios(true);
        }
        //Se actualizan las invitaciones y reuniones de la tab 0
        invitacionesTab.remove(idReunion);
        reunionesTab.remove(item);

        llenarRecyclerView();
        setPopUp();
    }

}