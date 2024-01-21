package com.example.weMee7.model.entities;

import com.google.firebase.Timestamp;

/**
 * Clase POJO que se relaciona
 * con los documentos
 * de la colección inivitaciones de la BD.
 */
public class Invitacion extends _SuperEntity{

    private String idUsuario;
    private String idReunion;
    private EstadoInvitacion estado;
    private boolean yaCelebrada;

    /* Variable para acotar las consultas de invitaciones,
        del mismo modo que se pueden filtrar las reuniones por fecha.
        Este dato se actualizará cuando el usuario consulte sus reuniones
        y su fecha haya pasado.
         */
    private Timestamp fecha_envio;

    //Constructor vacio
    public Invitacion() {}

    /**
     * Constructor.
     * La invitacion se crea vinculada a un usuario y a una reunion.
     * El estado inicial es siempre PENDIENTE;
     * yaCelebrada empieza siempre como FALSE;
     * la fecha_envio es el momento de creacion.
     * @param idUsuario
     * @param idReunion
     */
    public Invitacion(String idUsuario, String idReunion) {
        this.idUsuario = idUsuario;
        this.idReunion = idReunion;
        this.estado = EstadoInvitacion.ENVIADA;
        this.yaCelebrada = false;
        enviarAhora();
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public String getIdReunion() {
        return idReunion;
    }

    public EstadoInvitacion getEstado() {
        return estado;
    }

    public void setEstado(EstadoInvitacion estado) {
        this.estado = estado;
    }

    public boolean isYaCelebrada() {
        return yaCelebrada;
    }

    public void setYaCelebrada(boolean yaCelebrada) {
        this.yaCelebrada = yaCelebrada;
    }

    public Timestamp getFecha_envio() {
        return fecha_envio;
    }

    public void enviarAhora(){fecha_envio = Timestamp.now();}

    /**
     * Enumeracion que recoge valores constantes
     * con los diferentes estados de una invitacion
     */
    public enum EstadoInvitacion {ENVIADA,ACEPTADA,RECHAZADA;}

}
