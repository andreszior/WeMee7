package com.example.weMee7.model.dao;

import com.example.weMee7.model.entities.Reunion;

/**
 * Acceso a datos de la coleccion 'opcionesEncuesta'
 */
public class OpcionEncuestaDAO extends _SuperDAO{
    public OpcionEncuestaDAO() {
        super("opcionesEncuesta", Reunion.class);
    }
}
