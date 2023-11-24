package com.example.weMee7.model.dao;

import com.example.weMee7.model.entities.Reunion;

/**
 * Acceso a datos de la coleccion 'reuniones'
 */
public class ReunionDAO extends _SuperDAO {
    public ReunionDAO() {
        super("reuniones", Reunion.class);
    }

    /*Metodo de consulta de reunion con 2 condiciones:
     * (String idInvitado, Timestamp fecha < dia siguiente)
     */
}
