package com.example.weMee7.model.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Clase POJO que se relaciona
 * con los elementos de la colección usuarios de la BD.
 */
public class Usuario extends _SuperEntity {
    private String nombre;
    private String foto;
    private Map<String,Boolean> credenciales;
    private ArrayList<String> reunionesInvitado;

    //Constructor vacio
    public Usuario() {}

    public ArrayList<String> getReunionesInvitado() {
        return reunionesInvitado;
    }

    public void setReunionesInvitado(ArrayList<String> reunionesInvitado) {
        this.reunionesInvitado = reunionesInvitado;
    }

    /**
     * Constructor habitual.
     * Atributos recibidos por formulario.
     * @param id UID generado por Firebase Auth
     * @param nombre nombre del usuario
     * @param foto imagen de perfil
     * @param method metodo de autenticacion inicial
     */
    public Usuario(String id, String nombre, String foto, SignInMethod method) {
        this.id = id;
        this.nombre = nombre;
        this.foto = foto;
        credenciales = new HashMap<>();
        credenciales.put(method.toString(),true);
        reunionesInvitado = new ArrayList<>();
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

    public Map<String, Boolean> getCredenciales() {
        return credenciales;
    }

    //Si el metodo no esta registrado, devuelve false
    public boolean getSingleCredencial(SignInMethod method){
        if(credenciales.get(method.toString()) != null)
            return true;
        return false;
    }

    public void setCredencial(SignInMethod method, boolean activa){
        if(activa)
            credenciales.put(method.toString(),true);
        else
            credenciales.remove(method.toString());
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

    /**
     * Enumeracion de los posibles metodos de verificacion
     */
    public enum SignInMethod {EMAIL,PHONE,GOOGLE;}
}
