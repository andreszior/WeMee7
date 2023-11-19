package com.example.weMee7.comun;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class TimeUtils {

    public static final ZoneId ZONE_ID = ZoneId.of("UTC+1");
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    /**
     * Recibe una fecha en formato dd/MM/yyyy
     * y una hora en formato HH:mm
     * y devuelve un String
     * con la hora correspondiente
     * en la zona horaria del cliente, si es la fecha de la reunion;
     * en la zona horaria UTC+1, en los demás casos
     * @param fecha
     * @param hora
     * @param fechaReunion esta fecha se devuelve sin convertir
     * @return
     */
    public static String fusionarFechaYHora(String fecha, String hora, boolean fechaReunion){

        LocalDateTime ldt = LocalDateTime.of(
                LocalDate.parse(fecha, DATE_FORMATTER),
                LocalTime.parse(hora, TIME_FORMATTER));

        //Hora en el dispositivo del usuario
        ZonedDateTime zdt = ldt.atZone(ZoneId.systemDefault());

        if(!fechaReunion)
            //Hora en UTC+1 para almacenar en base de datos
            zdt = zdt.withZoneSameInstant(ZONE_ID);

        return zdt.toLocalDateTime().toString();
    }

    /**
     * Devuelve la fecha y hora actuales
     * de la zona horaria UTC+1
     * @return
     */
    public static String ahora(){
        return ZonedDateTime.now(ZONE_ID).toLocalDateTime().toString();
    }

    /**
     * Devuelve la fecha en formato "dd/MM/yyyy"
     * o la hora en formato "HH:mm",
     * en la zona horaria original, si es la fecha de la reunion;
     * en la zona horaria del dispositivo, en los demás casos
     * @param dateTime cadena con fecha, hora y zona horaria
     * @param fechaReunion esta fecha se devuelve sin convertir
     * @param fecha true : fecha / false : hora
     * @return
     */
    public static String separarFechaHora(String dateTime, boolean fechaReunion,boolean fecha){
        ZonedDateTime zdt = ZonedDateTime.parse(dateTime);

        //Si no es fecha de reunión, la convierte a la zona del cliente
        if(!fechaReunion)
            zdt = zdt.withZoneSameInstant(ZoneId.systemDefault());

        if(fecha)//Devuelve la fecha
            return zdt.format(DATE_FORMATTER);
        else //Devuelve la hora
            return zdt.format(TIME_FORMATTER);
    }
}
