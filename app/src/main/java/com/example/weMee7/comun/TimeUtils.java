package com.example.weMee7.comun;


import com.google.firebase.Timestamp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Si no se utilizan en otro sitio,
 * estas funciones podrian integrarse en la clase Reunion
 */
public class TimeUtils {
    public static final String FORMATO_FECHA = "dd/MM/yyyy";
    public static final String FORMATO_HORA = "HH:mm";
    public static final String FECHA_INDETERMINADA = "31/12/9999";

    /**
     * Recibe una fecha en formato dd/MM/yyyy
     * y devuelve un Timestamp
     * @param fecha
     * @return
     */
    public static Timestamp fechaToTimestamp(String fecha) {
        if(fecha == null)
            fecha = FECHA_INDETERMINADA;
        SimpleDateFormat dateFormat =
                new SimpleDateFormat(FORMATO_FECHA);
        try {
            Date date = dateFormat.parse(fecha);
            return new Timestamp(date);
        } catch (ParseException e) {
            return Timestamp.now();
        }
    }

    /**
     * Devuelve la fecha en formato "dd/MM/yyyy"
     * del Timestamp pasado por parametro
     * @param ts marca de tiempo
     * @return
     */
    public static String timestampToFecha(Timestamp ts){
        SimpleDateFormat dateFormat =
                new SimpleDateFormat(FORMATO_FECHA);
        String fecha = dateFormat.format(ts.toDate());
        if(fecha.equals(FECHA_INDETERMINADA))
            return "Fecha por determinar";
        else
            return fecha;
    }

    /**
     * Devuelve la fecha en formato "dd/MM/yyyy"
     * o la hora en formato "HH:mm",
     * del Timestamp pasado por parametro
     * @param ts marca de tiempo
     * @param fecha true : fecha / false : hora
     * @return
     */
    public static String timestampToFechaHora(Timestamp ts, boolean fecha){
        SimpleDateFormat dateFormat =
                new SimpleDateFormat(fecha ? FORMATO_FECHA : FORMATO_HORA);
        return dateFormat.format(ts.toDate());
    }
}
