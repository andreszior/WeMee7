package com.example.weMee7.model.entities;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.firebase.Timestamp;

/**
 * Clase POJO que se relaciona
 * con los documentos
 * de la colección tareas de la BD.
 */
public class Tarea extends _SuperEntity implements Comparable<Tarea>, Parcelable {

    private String idReunion;
    private String titulo;
    private String idEncargado;
    private String descripcion;
    private EstadoTarea estado;


    private boolean isChecked;
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
                 String descripcion, int gasto, String idEncargado,
                 EstadoTarea estado) {
        //super(reunion, titulo);
        this.idReunion = reunion;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.gasto = gasto;
        this.idEncargado = idEncargado;
        this.estado = estado;
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

    @SuppressLint("DefaultLocale")
    public String obtenerGastoString(){
        if(this.gasto == 0)
            return "¿?";
        return String.format("%.2f",this.gasto / 100f).concat(" €");
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




    protected Tarea(Parcel in) {
        //idTarea = in.readString();
        idReunion = in.readString();
        titulo = in.readString();
        idEncargado = in.readString();
        descripcion = in.readString();
        isChecked = in.readByte() != 0;
        gasto = in.readInt();
        fecha_update = in.readParcelable(Timestamp.class.getClassLoader());
    }

    public static final Creator<Tarea> CREATOR = new Creator<Tarea>() {
        @Override
        public Tarea createFromParcel(Parcel in) {
            return new Tarea(in);
        }

        @Override
        public Tarea[] newArray(int size) {
            return new Tarea[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        //dest.writeString(idTarea);
        dest.writeString(idReunion);
        dest.writeString(titulo);
        dest.writeString(idEncargado);
        dest.writeString(descripcion);
        dest.writeByte((byte) (isChecked ? 1 : 0));
        dest.writeInt(gasto);
        dest.writeParcelable(fecha_update, flags);
    }

    @Override
    public int compareTo(Tarea o) {
        return 0;
    }





}
