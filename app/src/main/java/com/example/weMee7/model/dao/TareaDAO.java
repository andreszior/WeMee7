package com.example.weMee7.model.dao;

import com.example.weMee7.model.entities.Tarea;

/**
 * Acceso a datos de la coleccion 'tareas'
 */
public class TareaDAO extends _SuperDAO{
    public TareaDAO() {
        super("tareas", Tarea.class);
    }
}
