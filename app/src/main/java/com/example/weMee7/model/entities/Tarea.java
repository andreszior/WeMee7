package com.example.weMee7.model.entities;

import com.google.firebase.Timestamp;

/**
 * Clase POJO que se relaciona
 * con los documentos
 * de la colección tareas de la BD.
 */
public class Tarea extends _SuperEntity {

    private String idReunion;
    private String titulo;
    private String idEncargado;
    private String descripcion;
    private EstadoTarea estado;
    private int gasto;//El gasto se almacena como entero
    //P.ej: 45,50 € = 4550.
    private Timestamp fecha_update;//Se modifica automaticamente al cambiar de Estado


    //Constructor vacio
    public Tarea() {}

    public String getIdReunion() {
        return idReunion;
    }

    public void setIdReunion(String idReunion) {
        this.idReunion = idReunion;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    /**
     * Constructor.
     * Recibe los datos basicos por formulario;
     * el estado se inicia como CREADA,
     * la fecha_update la de creacion.
     * @param reunion
     * @param titulo
     * @param descripcion
     * @param gasto
     * @param idEncargado
     */
    public Tarea(String reunion, String titulo,
                 String descripcion, int gasto, String idEncargado) {
        //super(reunion, titulo);
        this.idReunion = reunion;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.gasto = gasto;
        this.idEncargado = idEncargado;
        this.estado = EstadoTarea.CREADA;
        this.fecha_update = Timestamp.now();
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public EstadoTarea getEstado() {
        return estado;
    }

    public void setEstado(EstadoTarea estado) {
        this.estado = estado;
        this.fecha_update = Timestamp.now();
    }

    public int getGasto() {
        return gasto;
    }

    public void setGasto(int gasto) {
        this.gasto = gasto;
    }

    public Timestamp getFecha_update() {
        return fecha_update;
    }

    public void setFecha_update(Timestamp fecha_update) {
        this.fecha_update = fecha_update;
    }

    public String getIdEncargado() {
        return idEncargado;
    }

    public void setIdEncargado(String idEncargado) {
        this.idEncargado = idEncargado;
    }

    /**
     * Enumeracion que recoge valores constantes
     * con los diferentes estados de una invitacion
     */
    public enum EstadoTarea {CREADA,ASIGNADA,COMPLETADA;}
}
