package com.example.weMee7.model.entities;

/**
 * Clase POJO que se relaciona
 * con los documentos
 * de la colección encuestas de la BD.
 */
public class Encuesta extends Cuestion{
    private int resultado;
    private String fecha_limite;

    //Constructor vacio
    public Encuesta() {}

    /**
     * Constructor.
     * Recibe los datos basicos por formulario
     * @param reunion
     * @param titulo
     * @param fecha_limite
     */
    public Encuesta(String reunion, String titulo,
                    String fecha_limite) {
        super(reunion, titulo);
        this.resultado = -1; //Por defecto sin resultado
        this.fecha_limite = fecha_limite;
    }

    public int getResultado() {
        return resultado;
    }

    public void setResultado(int resultado) {
        this.resultado = resultado;
    }

    public String getFecha_limite() {
        return fecha_limite;
    }

    public void setFecha_limite(String fecha_limite) {
        this.fecha_limite = fecha_limite;
    }
}
