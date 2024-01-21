package com.example.weMee7.model.entities;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.example.weMee7.comun.TimeUtils;
import com.google.firebase.Timestamp;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Clase POJO que se relaciona
 * con los documentos
 * de la colección reuniones de la BD.
 *
 * !!! La fecha y hora se han dividido en 2 atributos separados.
 * Para acceder y cambiar se han creado getters y setters especiales
 * que funcionan directamente con Strings:
 *** obtenerFechaString / cambiarFechaString
 *** obtenerHoraString / cambiarHoraString
 */
public class Reunion extends _SuperEntity implements Comparable<Reunion>, Parcelable {
    private String idCreador; //Invariable
    private ArrayList<String> invitadosList;
    private String nombre;
    private String descripcion;
    private String lugar;
    private Timestamp fecha;
    private String hora;
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
     * @param fecha
     * @param hora
     */

    public Reunion(String idCreador, String nombre,
                   String descripcion, String lugar,
                   String fecha, String hora) {
        this.idCreador = idCreador;
        this.invitadosList = new ArrayList();
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.lugar = lugar;
        cambiarFechaString(fecha);
        cambiarHoraString(hora);
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

    public Timestamp getFecha() {
        return fecha;
    }

    public void setFecha(Timestamp fecha) {
        this.fecha = fecha;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    /**
     * Devuelve si la fecha de reunion es igual o posterior
     * a la fecha de hoy, es decir, si se ha celebrado o no.
     * @return true / false
     */
    public boolean estaActiva(){
        return this.getFecha().compareTo(TimeUtils.hoy()) >= 0;
    }

    //Getters y setters de fecha y hora en formato String
    public String obtenerFechaString(){
        return TimeUtils.timestampToFecha(this.fecha);
    }
    public void cambiarFechaString(String fecha){
        this.fecha = TimeUtils.fechaToTimestamp(fecha);
    }
    public String obtenerHoraString(){
        return hora.equals("24:00") ? "Hora por determinar" : hora;
    }

    public void cambiarHoraString(String hora){
        this.hora = hora == null ? "24:00" : hora;
    }


    public Timestamp getFecha_creacion() {
        return fecha_creacion;
    }

    public ArrayList<String> getInvitadosList() {
        return invitadosList;
    }

    public void setInvitadosList(ArrayList<String> invitadosList) {
        this.invitadosList = invitadosList;
    }

    /**
     * Metodo para ordenar reuniones por fecha
     * @param r the object to be compared.
     * @return
     */
    @Override
    public int compareTo(Reunion r) {
        return this.fecha.compareTo(r.fecha);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(idCreador);
        dest.writeStringList(invitadosList);
        dest.writeString(nombre);
        dest.writeString(descripcion);
        dest.writeString(lugar);
        dest.writeParcelable(fecha, flags);
        dest.writeString(hora);
        dest.writeParcelable(fecha_creacion, flags);
    }


    // Constructor para Parcelable
    protected Reunion(Parcel in) {
        idCreador = in.readString();
        invitadosList = in.createStringArrayList();
        nombre = in.readString();
        descripcion = in.readString();
        lugar = in.readString();
        fecha = in.readParcelable(Timestamp.class.getClassLoader());
        hora = in.readString();
        fecha_creacion = in.readParcelable(Timestamp.class.getClassLoader());
    }

    // Método estático para crear instancias desde un Parcel
    public static final Creator<Reunion> CREATOR = new Creator<Reunion>() {
        @Override
        public Reunion createFromParcel(Parcel in) {
            return new Reunion(in);
        }

        @Override
        public Reunion[] newArray(int size) {
            return new Reunion[size];
        }
    };
}
