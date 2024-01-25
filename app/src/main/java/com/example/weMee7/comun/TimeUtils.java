package com.example.weMee7.comun;


import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.widget.EditText;

import com.google.firebase.Timestamp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
        if(fecha.isEmpty())
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

    public static Timestamp hoy(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return new Timestamp(calendar.getTime());
    }

    public static void addSelectFecha(Context c, EditText et){
        Calendar calendar= Calendar.getInstance();
        et.setFocusable(false);
        et.setClickable(true);

        DatePickerDialog.OnDateSetListener dateSetListener= (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(TimeUtils.FORMATO_FECHA);
            et.setText(simpleDateFormat.format(calendar.getTime()));
        };
        DatePickerDialog datePicker = new DatePickerDialog(c,
                dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));

        datePicker.getDatePicker().setMinDate(System.currentTimeMillis());
        datePicker.show();
    }

    public static void addSelectHora(Context c, EditText et){
        Calendar calendar= Calendar.getInstance();
        TimePickerDialog.OnTimeSetListener timeSetListener = (view, hourOfDay, minute) -> {
            calendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
            calendar.set(Calendar.MINUTE, minute);

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(TimeUtils.FORMATO_HORA);
            et.setText(simpleDateFormat.format(calendar.getTime()));
        };
        new TimePickerDialog(c,
                timeSetListener,calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE), true).show();

    }
}
