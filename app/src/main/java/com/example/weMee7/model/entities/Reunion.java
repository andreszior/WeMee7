package com.example.weMee7.model.entities;

import com.example.weMee7.comun.TimeUtils;
import java.util.ArrayList;

/**
 * Clase POJO que se relaciona
 * con los documentos
 * de la colección reuniones de la BD.
 */
public class Reunion extends _SuperEntity {
    private String idCreador; //Invariable
    private ArrayList<String> invitadosList;
    private String nombre;
    private String descripcion;
    private String lugar;
    private String fechaHora;
    private String fecha_creacion; //Invariable

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
                   String descripcion, String lugar, String fechaHora) {
        this.idCreador = idCreador;
        this.invitadosList = new ArrayList();
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.lugar = lugar;
        this.fechaHora = fechaHora;
        fecha_creacion = TimeUtils.ahora();
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

    public String getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(String fechaHora) {
        this.fechaHora = fechaHora;
    }

    public String getFecha_creacion() {
        return fecha_creacion;
    }

}
