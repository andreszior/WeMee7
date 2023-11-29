package com.example.weMee7.comun.seguridad;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import com.example.wemee7.R;

import java.io.IOException;
import java.security.GeneralSecurityException;

/**
 * Clase que gestiona el almacenamiento de datos
 * en las preferencias compartidas de la app.
 * Si el dispositivo lo soporta,
 * utilizara preferencias encriptadas con clave AES256;
 * de no ser compatible, se utilizaran en modo privado.
 */
public class SharedPref {

    private SharedPreferences sp;

    public SharedPref(Context context) {
        sp = getSharedPrefs(context);
    }

    private SharedPreferences getSharedPrefs(Context context){
        try{
            MasterKey masterKey = new MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();
            return EncryptedSharedPreferences.create(
                    context,
                    "secret_shared_prefs",
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        }catch(GeneralSecurityException | IOException e) {
            return context.getSharedPreferences(
                    context.getString(R.string.shared_key),Context.MODE_PRIVATE);
        }
    }

    public boolean get(String clave){
        return sp.getBoolean(clave,false);
    }

    public void put(String clave, boolean valor){
        sp.edit().putBoolean(clave,valor).apply();
    }

    public void remove(String clave){
        sp.edit().remove(clave).apply();
    }

    public void clear(){
        sp.edit().clear().apply();
    }
}
