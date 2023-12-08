package com.example.weMee7.comun;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.wemee7.R;

/**
 * Clase que gestiona
 * la creacion y modificacion
 * de avatares de usuario.
 * Cada avatar tiene una imagen,
 * definida por su fila y columna
 * en el Bitmap drawable/avatares,
 * y un color de fondo
 */
public class Avatar {

    private Context context;

    //Lista de colores disponibles
    private final int coloresList [] = {
        R.color.avatar_rojo, R.color.avatar_azul,
        R.color.avatar_verde, R.color.avatar_naranja,
        R.color.avatar_morado, R.color.avatar_rosa,
        R.color.avatar_amarillo, R.color.avatar_gris};

    //Filas y columnas del drawable/avatares
    private final int maxFilas = 4;
    private final int maxCols = 5;

    //Ubicacion de la imagen seleccionada
    private int fila; //Entre 0-3
    private int columna;//Entre 0-4

    //Color de fondo
    private int color;//Entre 0-7

    //Constructor vacio: genera avatar aleatorio
    public Avatar(){
        generarAleatorio();
    }

    //Constructor que recupera la imagen de la BD a partir de un String
    public Avatar(Context context, String claveBD){
        this.context = context;
        String regex = new StringBuilder("[0-")
                .append(maxFilas - 1)
                .append("]-[0-")
                .append(maxCols - 1)
                .append("]-[0-")
                .append(coloresList.length - 1)
                .append("]").toString();
        if(claveBD != null && claveBD.matches(regex)){
            generarConClave(claveBD);
        }else
            generarAleatorio();
    }

    /**
     * Genera un avatar aleatorio
     */
    private void generarAleatorio(){
        this.fila = randomNumber(maxFilas);
        this.columna = randomNumber(maxCols);
        this.color = randomNumber(coloresList.length);
    }

    /**
     * Genera un numero aleatorio
     * @param max
     * @return
     */
    private int randomNumber(int max){
        return (int) Math.floor(Math.random() * max);
    }

    /**
     * Genera un avatar a partir de un String
     * @param claveBD
     */
    private void generarConClave(String claveBD){
        String claves [] = claveBD.split("-");
        this.fila = Integer.parseInt(claves[0]);
        this.columna = Integer.parseInt(claves[1]);
        this.color = Integer.parseInt(claves[2]);
    }

    /**
     * Devuelve el id del color de fondo
     * @return
     */
    public int getColor(){
        return coloresList[color];
    }

    public int getNextColor(boolean next){
        return coloresList[modificarValor(color,coloresList.length,next)];
    }

    /**
     * Cambia la imagen a la anterior o siguiente
     * segun el parametro pasado
     * @param next true : siguiente / false : anterior
     */
    public void setImagen(boolean next) {
        this.columna = modificarValor(columna,maxCols,next);
        if(next && this.columna == 0 || !next && this.columna == maxCols - 1)
            this.fila = modificarValor(fila,maxFilas,next);
    }

    /**
     * Cambia el color de fondo al anterior o siguiente
     * segun el parametro pasado
     * @param next true : siguiente / false : anterior
     */
    public void setColor(boolean next) {
        this.color = modificarValor(color,coloresList.length,next);
    }

    /**
     * Modifica el valor de fila, columna o color
     * dentro de los limites establecidos
     * @param actual valor actual
     * @param max valor maximo
     * @param next siguiente / anterior
     * @return
     */
    private int modificarValor(int actual, int max, boolean next){
        int nuevoValor = actual + (next ? 1 : -1);
        if(nuevoValor == max)
            return 0;
        else if(nuevoValor < 0)
            return max - 1;
        else
            return nuevoValor;
    }

    /**
     * Devuelve un Bitmap
     * a partir de los atributos del avatar
     * @return
     */
    public Bitmap toBitmap(){
        Bitmap avatares = BitmapFactory.decodeResource(
                context.getResources(),
                R.drawable.avatares);
        int alto = avatares.getHeight() / 4;
        int ancho = avatares.getWidth() / 5;

        return Bitmap.createBitmap(avatares,
                columna * ancho, fila * alto, ancho, alto);
    }

    /**
     * Devuelve la clave String del avatar
     * en base a sus atributos
     * @return
     */
    @Override
    public String toString() {
        return fila + "-" + columna + "-" + color;
    }
}
