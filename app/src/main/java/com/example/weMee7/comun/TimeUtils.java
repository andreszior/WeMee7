package com.example.weMee7.comun;


import com.google.firebase.Timestamp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

public class TimeUtils {
    public static final String FORMATO_FECHA = "dd/MM/yyyy";
    public static final String FORMATO_HORA = "HH:mm";

    /**
     * Recibe una fecha en formato dd/MM/yyyy
     * y una hora en formato HH:mm
     * y devuelve un Timestamp
     * @param fecha
     * @param hora
     * @return
     */
    public static Timestamp fechaHoraToTimestamp(String fecha, String hora) {
        Timestamp resultado;
        SimpleDateFormat dateFormat = new SimpleDateFormat(FORMATO_FECHA + FORMATO_HORA);
        Date date = null;
        try {
            date = dateFormat.parse(fecha + hora);
        } catch (ParseException e) {
            return null;// !!! fecha que devolver cuando no este determinada !!!
        }
        return new Timestamp(date);
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
