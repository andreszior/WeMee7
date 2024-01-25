package com.example.weMee7.viewmodel;

import static com.example.weMee7.model.dao._SuperDAO.Fields.ID_REUNION;

import com.example.weMee7.model.dao.InvitacionDAO;
import com.example.weMee7.model.dao.ReunionDAO;
import com.example.weMee7.model.dao.TareaDAO;
import com.example.weMee7.model.dao.UsuarioDAO;
import com.example.weMee7.model.dao._SuperDAO;
import com.example.weMee7.model.entities.Invitacion;
import com.example.weMee7.model.entities.Reunion;
import com.example.weMee7.model.entities.Tarea;
import com.example.weMee7.model.entities.Usuario;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;
import java.util.Map;

public class GestionarDatos {

    public GestionarDatos() {}

    public TaskCompletionSource<DocumentSnapshot> obtenerReunionesUsuario(
            List<Reunion> reunionesList, Map<String,Invitacion> invitacionesMap, String idUsuario,
            boolean activas){

        ReunionDAO rDAO = new ReunionDAO();
        InvitacionDAO iDAO = new InvitacionDAO();
        TaskCompletionSource<DocumentSnapshot> tcs = new TaskCompletionSource<>();

        rDAO.obtenerReunionesUsuario(idUsuario, activas, rList -> {
            reunionesList.addAll((List)rList);
            iDAO.obtenerInvitacionesActivas(idUsuario,activas, iMap -> {
                invitacionesMap.putAll((Map)iMap);
                tcs.setResult(null);
            });
        });
        return tcs;
    }

    public TaskCompletionSource<DocumentSnapshot> obtenerListaSoloEncargado(
            List<Usuario> encargadosList, String idEncargado){
        UsuarioDAO uDAO = new UsuarioDAO();
        TaskCompletionSource<DocumentSnapshot> tcs = new TaskCompletionSource<>();

        //Modo consulta o editar !creador -> Solo encargado
        uDAO.obtenerRegistroPorId(idEncargado, resultado -> {
            encargadosList.add((Usuario)resultado);
            tcs.setResult(null);
        });

        return tcs;
    }

    public TaskCompletionSource<DocumentSnapshot> obtenerListaTodosEncargados(List<Usuario> encargadosList,
                                                                              String idReunion, String idCreador){
        UsuarioDAO uDAO = new UsuarioDAO();
        TaskCompletionSource<DocumentSnapshot> tcs = new TaskCompletionSource<>();

        //Modo crear o editar & creador
        uDAO.obtenerRegistroPorId(idCreador, resultado -> {
            encargadosList.add((Usuario)resultado);
            uDAO.obtenerListaDesdeArray(_SuperDAO.Fields.INVITADOS_POR_REUNION, idReunion, lista -> {
                //Añadir usuarios invitados
                encargadosList.addAll((List) lista);
                tcs.setResult(null);
            });
        });
        return tcs;
    }

    public TaskCompletionSource<DocumentSnapshot> crearNuevaReunion(Reunion r){
        ReunionDAO rDAO = new ReunionDAO();
        TaskCompletionSource<DocumentSnapshot> tcs = new TaskCompletionSource<>();
        rDAO.insertarRegistro(r, idReunion ->{
            r.setId((String)idReunion);
            tcs.setResult(null);
        });
        return tcs;
    }

    public TaskCompletionSource<DocumentSnapshot> obtenerReunion(String idReunion, List<Reunion> r){
        ReunionDAO rDAO = new ReunionDAO();
        TaskCompletionSource<DocumentSnapshot> tcs = new TaskCompletionSource<>();
        rDAO.obtenerRegistroPorId(idReunion, reunion ->  {
            r.add((Reunion)reunion);
            tcs.setResult(null);
        });
        return tcs;
    }

    public void modificarReunion(Reunion r){
        ReunionDAO rDAO = new ReunionDAO();
        rDAO.actualizarRegistro(r);
    }
    public void eliminarReunion(String idReunion){
        //Eliminar invitaciones
        InvitacionDAO iDAO = new InvitacionDAO();
        iDAO.obtenerListaPorIdForaneo(_SuperDAO.Fields.ID_REUNION, idReunion, lista -> {
            List<Invitacion> iList = (List) lista;
            for(Invitacion i : iList)
                iDAO.borrarRegistro(i.getId());
        });

        //Eliminar tareas
        TareaDAO tDAO = new TareaDAO();
        tDAO.obtenerListaPorIdForaneo(_SuperDAO.Fields.ID_REUNION, idReunion, lista ->{
            List<Tarea> tList = (List) lista;
            for(Tarea t : tList)
                iDAO.borrarRegistro(t.getId());
        });

        //Eliminar reunion de usuarios
        UsuarioDAO uDAO = new UsuarioDAO();
        uDAO.obtenerListaDesdeArray(_SuperDAO.Fields.INVITADOS_POR_REUNION, idReunion, lista ->{
            List<Usuario> uList = (List) lista;
            for(Usuario u : uList){
                u.delReunion(idReunion);
                uDAO.actualizarRegistro(u);
            }
        });
        //Eliminar reunion
        ReunionDAO rDAO = new ReunionDAO();
        rDAO.borrarRegistro(idReunion);
    };

    public TaskCompletionSource<DocumentSnapshot> obtenerListaTareas(List<Tarea> lista, String idReunion){
        TareaDAO tDAO = new TareaDAO();
        TaskCompletionSource<DocumentSnapshot> tcs = new TaskCompletionSource<>();

        tDAO.obtenerListaPorIdForaneo(ID_REUNION, idReunion, resultado -> {
            lista.addAll((List<Tarea>)resultado);
            tcs.setResult(null);
        });
        return tcs;
    }

    public TaskCompletionSource<DocumentSnapshot> crearTarea(Tarea t){
        TaskCompletionSource<DocumentSnapshot> tcs = new TaskCompletionSource<>();
        TareaDAO tDAO = new TareaDAO();
        tDAO.insertarRegistro(t, resultado -> {
            if((Boolean)resultado)
                tcs.setResult(null);
        });

        return tcs;
    }

    public void actualizarTarea (Tarea t){
        TareaDAO tDAO = new TareaDAO();
        tDAO.actualizarRegistro(t);
    }

    public void eliminarTarea(String idTarea){
        TareaDAO tDAO = new TareaDAO();
        tDAO.borrarRegistro(idTarea);
    }


}
