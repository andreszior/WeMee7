package com.example.weMee7.model.entities;

import java.util.ArrayList;

/** Clase abstracta
 *  con atributos comunes a encuestas y tareas
 */
public abstract class Cuestion {
    private String idReunion;
    private String titulo;
    private ArrayList<String> idUsuariosExcluidos;//OPCIONAL


    //Constructor vacio
    public Cuestion() {}

    /**
     * Constructor.
     * La cuestion se vincula a una reunion,
     * se le da un id dentro de esa reunion
     * y un titulo recibido por formulario.
     * La lista de excluidos se inicializa (OPCIONAL)
     * @param idReunion
     * @param titulo
     */
    public Cuestion(String idReunion, String titulo) {
        this.idReunion = idReunion;
        this.titulo = titulo;
        //usuariosExcluidos = new ArrayList();
    }

    public String getIdReunion() {
        return idReunion;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
}
