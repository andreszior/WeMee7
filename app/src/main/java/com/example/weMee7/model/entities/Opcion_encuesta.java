package com.example.weMee7.model.entities;

import java.util.ArrayList;

public class Opcion_encuesta {
    private int numOpcion; //Invariable
    private String idEncuesta;//Invariable
    private String descripcion;
    private ArrayList<String> usuariosAFavor;
    private ArrayList<String> usuariosEnContra;

    //Constructor vacio
    public Opcion_encuesta() {}

    public Opcion_encuesta(int numOpcion, String idEncuesta, String descripcion) {
        this.numOpcion = numOpcion;
        this.idEncuesta = idEncuesta;
        this.descripcion = descripcion;
        usuariosAFavor = new ArrayList();
        usuariosEnContra = new ArrayList();
    }

    public int getNumOpcion() {
        return numOpcion;
    }

    public String getIdEncuesta() {
        return idEncuesta;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    //Vincula un usuario al voto a favor
    public void addTarea(String id){
        usuariosAFavor.add(id);
    }

    //Desvincula un usuario del voto a favor
    public void delTarea(String id){
        usuariosAFavor.remove(id);
    }

    //Vincula un usuario al voto en contra
    public void addEncuesta(String id){
        usuariosEnContra.add(id);
    }

    //Desvincula un usuario del voto en contra
    public void delEncuesta(String id){
        usuariosEnContra.remove(id);
    }
}
