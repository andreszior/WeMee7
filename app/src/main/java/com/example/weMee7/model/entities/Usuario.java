package com.example.weMee7.model.entities;

import java.util.ArrayList;

/**
 * Clase POJO que se relaciona
 * con los elementos de la colección usuarios de la BD.
 */
public class Usuario extends _SuperEntity {
    private String nombre;
    private String foto;
    private ArrayList<String> reunionesInvitado;

    //Constructor vacio
    public Usuario() {}

    /**
     * Constructor habitual.
     * Atributos recibidos por formulario.
     * @param id UID generado por Firebase Auth
     * @param nombre nombre del usuario
     * @param foto imagen de perfil
     */
    public Usuario(String id, String nombre, String foto) {
        this.id = id;
        this.nombre = nombre;
        this.foto = foto;
    }

    //Getters y setters
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    /**
     * Vincula un reunion al usuario (como invitado)
     * @param idReunion
     */
    public void addReunion(String idReunion){
        reunionesInvitado.add(idReunion);
    }

    /**
     * Desvincula a una reunion al usuario (como invitado)
     * @param idReunion
     */
    public void delReunion(String idReunion){
        reunionesInvitado.remove(idReunion);
    }

}
