package com.example.weMee7.model.entities;

import com.google.firebase.Timestamp;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Clase POJO que se relaciona
 * con los documentos
 * de la colección reuniones de la BD.
 */
public class Reunion extends _SuperEntity implements Serializable {
    private String idCreador; //Invariable
    private ArrayList<String> invitadosList;
    private String nombre;
    private String descripcion;
    private String lugar;
    private Timestamp fechaHora;
    private Timestamp fecha_creacion; //Invariable

    //Constructor vacio
    public Reunion() {}

    /**
     * Constructor.
     * La reunion se crea con datos basicos
     * recogidos de un formulario de la app;
     * la fecha_envio es el momento de creacion;
     * @param idCreador
     * @param nombre
     * @param descripcion
     * @param lugar
     * @param fechaHora
     */
    public Reunion(String idCreador, String nombre,
                   String descripcion, String lugar, Timestamp fechaHora) {
        this.idCreador = idCreador;
        this.invitadosList = new ArrayList();
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.lugar = lugar;
        this.fechaHora = fechaHora;
        this.fecha_creacion = Timestamp.now();
    }

    public String getIdCreador() {
        return idCreador;
    }

    /**
     * Vincula un usuario a la reunion
     * @param idUsuario
     */
    public void addInvitado(String idUsuario){
        invitadosList.add(idUsuario);
    }

    /**
     * Desvincula a un usuario de una reunion
     * @param idUsuario
     */
    public void delInvitado(String idUsuario){
        invitadosList.remove(idUsuario);
    }


    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getLugar() {
        return lugar;
    }

    public void setLugar(String lugar) {
        this.lugar = lugar;
    }

    public Timestamp getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(Timestamp fechaHora) {
        this.fechaHora = fechaHora;
    }

    public Timestamp getFecha_creacion() {
        return fecha_creacion;
    }

}
