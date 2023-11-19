package com.example.weMee7.model.entities;

import com.example.weMee7.comun.TimeUtils;
import com.example.weMee7.comun.interfaces.Estado;

/**
 * Clase POJO que se relaciona
 * con los documentos
 * de la colección inivitaciones de la BD.
 */
public class Invitacion extends _SuperEntity{

    private String idUsuario;
    private String idReunion;
    private EstadoInvitacion estado;
    private String fecha_envio;//Invariable

    //Constructor vacio
    public Invitacion() {}

    /**
     * Constructor.
     * La invitacion se crea vinculada a un usuario y a una reunion.
     * El estado inicial es siempre ENVIADA;
     * la fecha_envio es el momento de creacion.
     * @param idUsuario
     * @param idReunion
     */
    public Invitacion(String idUsuario, String idReunion) {
        this.idUsuario = idUsuario;
        this.idReunion = idReunion;
        this.estado = EstadoInvitacion.ENVIADA;
        fecha_envio = TimeUtils.ahora();
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

    public String getFecha_envio() {
        return fecha_envio;
    }

    /**
     * Enumeracion que recoge valores constantes
     * con los diferentes estados de una invitacion
     */
    public enum EstadoInvitacion implements Estado {
        ENVIADA(0),
        ACEPTADA(1),
        RECHAZADA(2);

        private int valor;
        EstadoInvitacion(int valor){
            this.valor = valor;
        }
        @Override
        public int getValor() {
            return 0;
        }
    }

}
