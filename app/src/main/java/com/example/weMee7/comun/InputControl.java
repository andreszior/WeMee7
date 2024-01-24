package com.example.weMee7.comun;

import java.util.regex.Pattern;

public class InputControl {

    public static boolean emailOk(String email){
        return Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$",
                        Pattern.CASE_INSENSITIVE).matcher(email).matches();
    }

    public static boolean passOk(String pass){
        return pass.length() >= 6 && pass.length() <= 16;
    }

    public static String phoneOk(String prefijo, String telefono){
        String completo = prefijo.concat(telefono);
        if(completo.matches("\\+\\d{11}"))
            return completo;
        return null;
    }

    public static boolean todoCumplimentado(String[] campos){
        for(String campo : campos)
            if(campo.isEmpty())
                return false;

        return true;
    }

    public static int formatoGasto(String gasto){
        if(gasto.isEmpty())
            return 0;
        int resultado = -1;
        try{
            float x = Float.parseFloat(gasto.replace(",", "."));
            resultado = Math.round(x * 100);
        }catch(NumberFormatException ex){
            //Nothing to do here
        }
        return resultado;
    }
}
