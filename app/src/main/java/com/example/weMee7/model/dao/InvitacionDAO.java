package com.example.weMee7.model.dao;

import com.example.weMee7.model.entities.Invitacion;

/**
 * Acceso a datos de la coleccion 'invitaciones'
 */
public class InvitacionDAO extends _SuperDAO{
    public InvitacionDAO() {
        super("invitaciones", Invitacion.class);
    }

    /*Metodo de consulta de invitacion con 3 condiciones:
    * (String field, String idUsuario/idReunion, boolean yaCelebrada, Estado estadoInvitacion)
     */



}
