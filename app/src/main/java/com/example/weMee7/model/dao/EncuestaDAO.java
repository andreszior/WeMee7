package com.example.weMee7.model.dao;

import com.example.weMee7.model.entities.Encuesta;

/**
 * Acceso a datos de la coleccion 'encuestas'
 */
public class EncuestaDAO extends _SuperDAO{
    public EncuestaDAO() {
        super("encuestas", Encuesta.class);
    }
}
