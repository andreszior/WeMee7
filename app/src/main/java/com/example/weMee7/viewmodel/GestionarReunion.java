package com.example.weMee7.viewmodel;

import com.example.weMee7.model.dao.InvitacionDAO;
import com.example.weMee7.model.dao.ReunionDAO;
import com.example.weMee7.model.dao.TareaDAO;
import com.example.weMee7.model.dao.UsuarioDAO;
import com.example.weMee7.model.dao._SuperDAO;
import com.example.weMee7.model.entities.Invitacion;
import com.example.weMee7.model.entities.Reunion;
import com.example.weMee7.model.entities.Tarea;
import com.example.weMee7.model.entities.Usuario;

import java.util.List;

public class GestionarReunion {

    ReunionDAO rDAO;

    public GestionarReunion() {
        rDAO = new ReunionDAO();
    }

    public void modificarReunion(Reunion r){
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
        rDAO.borrarRegistro(idReunion);
    };


}
