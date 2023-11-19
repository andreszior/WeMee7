package com.example.weMee7.model.dao;

import com.example.weMee7.model.entities.Usuario;
import com.example.weMee7.model.entities._SuperEntity;

/**
 * Acceso a datos de la coleccion 'usuarios'
 */
public class UsuarioDAO extends _SuperDAO {

    public UsuarioDAO() {
        super("usuarios",Usuario.class);
    }

    /**
     * Se sobreescribe la funcion
     * para evitar que Firestore le asigne un nuevo ID
     * ya que el ID del usuario deriva de Firebase Authentication
     * @param usuario
     */
    @Override
    public void insertarRegistro(_SuperEntity usuario) {
        DB_COLECCION.document(usuario.getId()).set(usuario)
                .addOnSuccessListener(documentReference -> {
                    System.out.println("usuario registrado");
                });
    }
}
