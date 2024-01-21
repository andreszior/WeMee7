package com.example.weMee7.model.dao;

import com.example.weMee7.comun.TimeUtils;
import com.example.weMee7.model.entities.Reunion;
import com.example.weMee7.model.entities._SuperEntity;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import com.google.firebase.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Acceso a datos de la coleccion 'reuniones'
 */
public class ReunionDAO extends _SuperDAO {
    public ReunionDAO() {
        super("reuniones", Reunion.class);
    }

    /**
     * Obtiene todas las reuniones de un usuario,
     * tanto las creadas como de las que es invitado,
     * anteriores o posteriores a la fecha de hoy
     * @param idUsuario usuario objetivo
     * @param activas posteriores / anteriores
     * @param callback callback
     */
    public void obtenerReunionesUsuario(String idUsuario, boolean activas, FirebaseCallback callback){
        // Crear Timestamp con la fecha de hoy (a las 0:00)
        Timestamp hoy = TimeUtils.hoy();

        //Consulta reuniones creadas
        Query q1 = DB_COLECCION
                .whereEqualTo(Fields.REUNIONES_POR_CREADOR.getField(), idUsuario);
        Task<QuerySnapshot> query1;

        //Consulta reuniones invitado
        Query q2 = DB_COLECCION
                .whereArrayContains(Fields.REUNIONES_POR_INVITADO.getField(), idUsuario);
        Task<QuerySnapshot> query2;

        //Consulta reuniones activas / pasadas
        if(activas){
            query1 = q1.whereGreaterThanOrEqualTo(Fields.FECHA_REUNION.getField(), hoy).get();
            query2 = q2.whereGreaterThanOrEqualTo(Fields.FECHA_REUNION.getField(), hoy).get();
        }else{
            query1 = q1.whereLessThan(Fields.FECHA_REUNION.getField(), hoy).get();
            query2 = q2.whereLessThan(Fields.FECHA_REUNION.getField(), hoy).get();
        }

        Task<List<QuerySnapshot>> allTasks = Tasks.whenAllSuccess(query1, query2);

        allTasks.addOnSuccessListener(querySnapshots -> {
            // Crear una lista para almacenar los resultados combinados
            List<Reunion> listaCombinada = new ArrayList<>();

            // Agregar los resultados de la primera consulta a la lista
            for (DocumentSnapshot document : querySnapshots.get(0).getDocuments())
                listaCombinada.add(document.toObject(Reunion.class));
            for (DocumentSnapshot document : querySnapshots.get(1).getDocuments())
                listaCombinada.add(document.toObject(Reunion.class));

            //Devolver en calllback los resultados combinados
            callback.onCallback(listaCombinada);
        });
    }

    /**
     * Consulta las reuniones activas creadas por un usuario,
     * y devuelve una lista con sus id
     * @param idUsuario id del usuario creador de la reunion
     * @param callback callback
     */
    public void obtenerIdReunionesActivasCreadas (String idUsuario, FirebaseCallback callback) {
        DB_COLECCION
                .whereEqualTo(Fields.REUNIONES_POR_CREADOR.getField(), idUsuario)
                .whereGreaterThanOrEqualTo(Fields.FECHA_REUNION.getField(), TimeUtils.hoy())
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<String> idReunionesList = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            Reunion reunion = (Reunion) doc.toObject(CLASE_DAO);
                            idReunionesList.add(reunion.getId());
                        }
                        callback.onCallback(idReunionesList);
                    }
                });
    }

}
