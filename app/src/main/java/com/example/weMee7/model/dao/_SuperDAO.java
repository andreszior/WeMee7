package com.example.weMee7.model.dao;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import com.example.weMee7.model.entities._SuperEntity;
import java.util.ArrayList;
import java.util.List;
import android.util.Log;

/**
 * Superclase abstracta
 * con los métodos CRUD basicos
 * para el acceso a datos (DAO)
 * de las diferentes colecciones.
 *
 * Las clases que la heredan
 * simplemente han de pasar por parámetro
 * el nombre de su coleccion
 * y su Clase entity;
 * si no necesitan sobreescribir ningún método
 * ni crear uno propio,
 * no requieren mayor implementación.
 */
public abstract class _SuperDAO {
    //Gestión de la conexion con la base de datos
    //en una unica instancia siguiendo el patron Singleton.
    protected FirebaseFirestore getDatabase(){
        return FirebaseFirestore.getInstance();
    }

    //Variables de conexion a cada conexion
    protected final CollectionReference DB_COLECCION;
    protected final Class CLASE_DAO;

    /**
     * Por constructor de la subclase recibira
     * el nombre de la coleccion
     * y la clase entity correspondiente al DAO
     */
    public _SuperDAO(String coleccion, Class c) {
        this.DB_COLECCION = getDatabase()
                .collection(coleccion);
        this.CLASE_DAO = c;
    }

    //INTERFAZ DE CALLBACK FIREBASE
    /**
     * Callback de firebase para obtener
     * un objeto/entidad
     * o una lista de entidades
     */
    public interface FirebaseCallback {
        void onCallback(Object resultado);
    }

    //FUNCIONES BASICAS DAO
    /**
     * Recibe un objeto/entidad,
     * le setea un id generado por Firebase
     * y lo inserta en la coleccion
     * @param entity
     */
    public void insertarRegistro(_SuperEntity entity) {
        DB_COLECCION.add(entity).addOnSuccessListener(documentReference -> {
            (entity).setId(documentReference.getId());
            DB_COLECCION.document(entity.getId()).set(entity).
                    addOnSuccessListener(unused -> {
                        System.out.println("elemento registrado");
                    });
        });
    }

    /**
     * Permite obtener un registro por su id
     * @param id
     * @param callback
     */
    public void obtenerRegistroPorId(String id, FirebaseCallback callback) {
        DB_COLECCION.whereEqualTo("id", id).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                _SuperEntity entity = null;
                for (QueryDocumentSnapshot doc : task.getResult()){
                    entity = (_SuperEntity) doc.toObject(CLASE_DAO);
                }
                callback.onCallback(entity);
            } else {
                Log.d("MiTag", "Error getting documents: ", task.getException());
            }
        });
    }

    /**
     * Permite obtener una lista de entidades
     * a partir del id de un objeto
     * que es atributo en otra coleccion.
     * @param f atributo que almacena el id del objeto
     *              P.ej.: "idCreador"
     * @param id id de la otra clase
     *           P.ej.: usuario.getId()
     */
    public void obtenerListaPorIdForaneo(Fields f, String id, FirebaseCallback callback){
        DB_COLECCION.whereEqualTo(f.getField(),id).get().addOnCompleteListener(task -> {
            if(task.isSuccessful() && task.getResult() != null){
                List<_SuperEntity> entities = new ArrayList<>();
                for (QueryDocumentSnapshot doc : task.getResult()){
                    _SuperEntity entity = (_SuperEntity) doc.toObject(CLASE_DAO);
                    entities.add(entity);
                }
                callback.onCallback(entities);
            } else {
                Log.d("MiTag", "Error getting documents: ", task.getException());
            }
        });
    }

    /**
     * Permite obtener una lista de entidades
     * a partir del id de un objeto
     * que esta en un array de otra coleccion
     * @param f atributo que almacena el array
     *              P. ej.: "invitados"
     * @param id id de la otra clase
     *           P.ej.: usuario.getId()
     * @param callback
     */
    public void obtenerListaDesdeArray(Fields f, String id, FirebaseCallback callback){
        DB_COLECCION.whereArrayContains(f.getField(), id)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<_SuperEntity> entities = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            entities.add((_SuperEntity) document.toObject(CLASE_DAO));
                        }
                        callback.onCallback(entities);
                    } else {
                        Log.d("MiTag", "Error getting documents: ", task.getException());
                    }
                });
    }

    /**
     * Actualiza el documento de la coleccion
     * con el mismo id que el recibido por parametro
     * con los datos recibidos
     * @param entity
     */
    public void actualizarRegistro(_SuperEntity entity) {
        DB_COLECCION.document(entity.getId()).set(entity)
                .addOnSuccessListener(unused -> {
                    System.out.println("elemento modificado");
                });
    }

    /**
     * Elimina de la colección
     * el documento con el id
     * pasado por parametro
     * @param id
     */
    public void borrarRegistro(String id) {
        DB_COLECCION.document(id).delete()
            .addOnSuccessListener(unused -> {
                System.out.println("elemento eliminado");
            });
    }

    /**
     * Enumeracion que contiene
     * las claves foraneas que se pueden utilizar
     * para filtrar consultas
     */
    public enum Fields{
        ID_USUARIO("idUsuario"),
        ID_REUNION("idReunion"),
        ID_ENCUESTA("idEncuesta"),
        REUNIONES_POR_CREADOR("idCreador"),
        REUNIONES_POR_INVITADO("invitadosList"),
        INVITADOS_POR_REUNION("reunionesInvitado"),
        FECHA_REUNION("fecha");

        private final String field;

        Fields(String field) {
            this.field = field;
        }
        public String getField() {
            return field;
        }
    }
}
