package com.example.weMee7.model.dao;

import android.util.Log;

import com.example.weMee7.model.entities.Invitacion;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.HashMap;
import java.util.Map;

/**
 * Acceso a datos de la coleccion 'invitaciones'
 */
public class InvitacionDAO extends _SuperDAO{
    public InvitacionDAO() {
        super("invitaciones", Invitacion.class);
    }

    /**
     * Permite obtener todas las invitaciones del usuario
     * de reuniones activas o pasadas.
     * Devuelve un Map para buscar por idReunion
     * @param idUsuario usuario objetivo
     * @param activas reuniones activas / pasadas
     * @param callback
     */
    public void obtenerInvitacionesActivas(String idUsuario, boolean activas, FirebaseCallback callback){
        DB_COLECCION.whereEqualTo(Fields.ID_USUARIO.getField(),idUsuario)
                .whereNotEqualTo("yaCelebrada",activas)
                .get().addOnCompleteListener(task -> {
            if(task.isSuccessful() && task.getResult() != null){
                Map<String,Invitacion> invitaciones = new HashMap<>();
                for (QueryDocumentSnapshot doc : task.getResult()){
                    Invitacion i = doc.toObject(Invitacion.class);
                    invitaciones.put(i.getIdReunion(),i);
                }
                callback.onCallback(invitaciones);
            } else {
                Log.d("MiTag", "Error getting documents: ", task.getException());
            }
        });
    }

    /**
     * Permite obtener todos los usuarios invitados a una reunion.
     * Devuelve un Map para buscar por idUsuario
     * @param idReunion reunion objetivo
     * @param callback
     */
    public void obtenerInvitacionesReunion(String idReunion, FirebaseCallback callback){
        DB_COLECCION.whereEqualTo(Fields.ID_REUNION.getField(),idReunion)
                .get().addOnCompleteListener(task -> {
                    if(task.isSuccessful() && task.getResult() != null){
                        Map<String,Invitacion> invitaciones = new HashMap<>();
                        for (QueryDocumentSnapshot doc : task.getResult()){
                            Invitacion i = doc.toObject(Invitacion.class);
                            invitaciones.put(i.getIdUsuario(),i);
                        }
                        callback.onCallback(invitaciones);
                    } else {
                        Log.d("MiTag", "Error getting documents: ", task.getException());
                    }
                });
    }
}
