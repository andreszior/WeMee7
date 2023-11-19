package com.example.weMee7.model.dao;

import com.example.weMee7.model.entities.Invitacion;

/**
 * Acceso a datos de la coleccion 'invitaciones'
 */
public class InvitacionDAO extends _SuperDAO{
    public InvitacionDAO() {
        super("invitaciones", Invitacion.class);
    }
}
