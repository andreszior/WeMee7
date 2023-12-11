package com.example.weMee7.viewmodel;

import com.example.weMee7.model.dao.InvitacionDAO;
import com.example.weMee7.model.dao.ReunionDAO;
import com.example.weMee7.model.dao.UsuarioDAO;
import com.example.weMee7.model.dao._SuperDAO;
import com.example.weMee7.model.entities.Invitacion;
import com.example.weMee7.model.entities.Reunion;
import com.example.weMee7.model.entities.Usuario;
import com.example.weMee7.model.entities._SuperEntity;

public class InvitarUsuario {

    InvitacionDAO invitacionDAO;
    UsuarioDAO usuarioDAO;
    ReunionDAO reunionDAO;
    /**
     * Crea la invitacion,
     * e incorpora la reunion al usuario (reunionesList)
     * y el usuario a la reunion (invitadosList)
     * @param idInvitado
     * @param idReunion
     */
    public void enviarInvitacion(String idReunion, String idInvitado){
        //Crear y registrar invitacion
        new InvitacionDAO().insertarRegistro(new Invitacion(idInvitado,idReunion));

        //Registrar la reunion en el usuario
        registrarEnArray(new UsuarioDAO(),idInvitado,idReunion,true);

        //Registrar el usuario en la reunion
        registrarEnArray(new ReunionDAO(),idReunion,idInvitado,true);
    }

    /**
     * Registra o borra un usuario/reunion
     * del array de una reunion/usuario
     * @param dao usuario / reunion dao de la coleccion a la que se accede
     * @param idDoc usuario / reunion a la que se agrega el item
     * @param idItem usuario / reunion que se agrega a la lista
     * @param registrar registrar / borrar
     */
    private void registrarEnArray(_SuperDAO dao, String idDoc, String idItem, boolean registrar){
        dao.obtenerRegistroPorId(idDoc, resultado ->{
            _SuperEntity e = (_SuperEntity)resultado;
            if(dao instanceof UsuarioDAO)
                if (registrar)
                    ((Usuario) e).addReunion(idItem);
                else
                    ((Usuario) e).delReunion(idItem);
            else
                if(registrar)
                    ((Reunion)e).addInvitado(idItem);
                else
                    ((Reunion)e).delInvitado(idItem);
            dao.actualizarRegistro(e);
        });
    }

    /** Elimina la invitacion de la BD,
     * asi como la reunion del usuario (reunionesList),
     * y el usuario de la reunion (invitadosList)
     * @param i
     */
    public void eliminarInvitacion(Invitacion i){
        String idUsuario = i.getIdUsuario();
        String idReunion = i.getIdReunion();

        //Eliminar invitacion
        new InvitacionDAO().borrarRegistro(i.getId());

        //Borrar la reunion en el usuario
        registrarEnArray(new UsuarioDAO(),idUsuario,idReunion,false);

        //Borrar el usuario en la reunion
        registrarEnArray(new ReunionDAO(),idReunion,idUsuario,false);
    }

    public void responderInvitacion(Invitacion i, boolean aceptar){
        Invitacion.EstadoInvitacion estado = aceptar ?
                Invitacion.EstadoInvitacion.ACEPTADA :
                Invitacion.EstadoInvitacion.RECHAZADA;
        i.setEstado(estado);
        new InvitacionDAO().actualizarRegistro(i);
    }

    public void actualizarInvitacion(Invitacion i){
        i.setYaCelebrada(true);
        if(i.getEstado() == Invitacion.EstadoInvitacion.ENVIADA)
            i.setEstado(Invitacion.EstadoInvitacion.RECHAZADA);
        new InvitacionDAO().actualizarRegistro(i);
    }
}
