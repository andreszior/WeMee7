package com.example.weMee7.view.reunion;

import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.weMee7.comun.Avatar;
import com.example.weMee7.comun.InvitadosAdapter;
import com.example.weMee7.comun.TimeUtils;
import com.example.weMee7.model.dao.InvitacionDAO;
import com.example.weMee7.model.dao.ReunionDAO;
import com.example.weMee7.model.dao.UsuarioDAO;
import com.example.weMee7.model.dao._SuperDAO;
import com.example.weMee7.model.entities.Invitacion;
import com.example.weMee7.model.entities.Reunion;
import com.example.weMee7.model.entities.Usuario;
import com.example.weMee7.view.usuario.UsuarioActivity;
import com.example.weMee7.viewmodel.InvitarUsuario;
import com.example.wemee7.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class InvitadosFragment extends Fragment {

    private String idReunion;
    private List<Usuario> asistentesList;
    private Map<String, Invitacion> invitacionMap;
    private View fondoCreador;
    private TextView tvNombreInvitado;
    private TextView tvCargando;
    private RecyclerView rvInvitados;
    private ImageButton btCompartir;
    private boolean esModificable;
    //La lista es modificable por el creador de la reunion & si la reunion esta activa


    public InvitadosFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        idReunion = ((UsuarioActivity)requireActivity()).getIdReunionActual();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_invitados, container, false);
        fondoCreador = view.findViewById(R.id.llInvitadoFondo);
        fondoCreador.setBackgroundColor(requireActivity().getResources().getColor(R.color.medium));
        tvNombreInvitado = fondoCreador.findViewById(R.id.tvInvitadoNombre);
        tvNombreInvitado.setTextColor(getResources().getColor(R.color.white));
        tvCargando = view.findViewById(R.id.tvCargandoInvitados);
        tvCargando.setVisibility(View.VISIBLE);
        rvInvitados = view.findViewById(R.id.rvInvitadosList);
        btCompartir = view.findViewById(R.id.bt_compartir);
        btCompartir.setOnClickListener(v -> copiarEnlace());

        //Consultas
        consultaCreador();

        return view;
    }

    private void copiarEnlace() {
        new InvitarUsuario().generarEnlaceInvitacion(idReunion,requireActivity());
    }

    private void consultaCreador(){
        new ReunionDAO().obtenerRegistroPorId(idReunion, reunion -> {
            Reunion r = (Reunion) reunion;
            new UsuarioDAO().obtenerRegistroPorId(((Reunion) reunion).getIdCreador(), creador -> {
                //Añadir usuario creador
                Usuario u = (Usuario) creador;
                esModificable = ((UsuarioActivity)requireActivity()).esCreador()
                    && r.estaActiva();
                if (esModificable)
                    btCompartir.setVisibility(View.VISIBLE);
                cargarCreador(u);
                consultaInvitados();
            });
        });
    }

    private void cargarCreador(Usuario creador){
        //Cargar icono del creador
        ImageView ivIconoInvitado = fondoCreador.findViewById(R.id.ivInvitadoIcono);
        Avatar avatar = new Avatar(requireActivity(), creador.getFoto());
        ivIconoInvitado.setImageBitmap(avatar.toBitmap());
        ivIconoInvitado.setBackgroundTintList(ColorStateList.valueOf(
                requireActivity().getResources().getColor(avatar.getColor())));

        //Cargar nombre del creador
        tvNombreInvitado.setTypeface(null, Typeface.BOLD);
        tvNombreInvitado.setTextColor(requireActivity().getResources().getColor(R.color.light));
        tvNombreInvitado.setText(creador.getNombre());
    }


    private void consultaInvitados(){
        asistentesList = new ArrayList<>();
        new UsuarioDAO().obtenerListaDesdeArray(_SuperDAO.Fields.INVITADOS_POR_REUNION, idReunion, lista ->{
            //Añadir usuarios invitados
            asistentesList.addAll((List)lista);
            new InvitacionDAO().obtenerInvitacionesReunion(idReunion, map -> {
                //Añadir invitaciones
                invitacionMap = (Map)map;
                llenarLista();
            });
        });
    }

    private void llenarLista() {
        InvitadosAdapter adapter = new InvitadosAdapter(
                asistentesList,invitacionMap, esModificable,
                requireActivity(),item -> pulsarOpcionInvitado(item));
        rvInvitados.setHasFixedSize(true);
        rvInvitados.setLayoutManager(new LinearLayoutManager(getActivity()));
        tvCargando.setVisibility(View.GONE);
        rvInvitados.setAdapter(adapter);
    }

    private void pulsarOpcionInvitado(Invitacion i) {
        //Modificar invitacion
        boolean reenviar = i.getEstado() == Invitacion.EstadoInvitacion.RECHAZADA;

        i.setEstado(reenviar ? //Cambio de estado
            Invitacion.EstadoInvitacion.ENVIADA :
            Invitacion.EstadoInvitacion.RECHAZADA);

        if(reenviar) //Cambio fecha de envio
            i.enviarAhora();

        //Actualizar base de datos
        new InvitacionDAO().actualizarRegistro(i);

        //Actualizar iMap
        invitacionMap.put(i.getIdUsuario(),i);

        //Refrescar recyclerView
        llenarLista();
    }
}