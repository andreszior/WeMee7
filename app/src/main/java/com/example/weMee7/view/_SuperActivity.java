package com.example.weMee7.view;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.widget.Toast;

import com.example.wemee7.R;

/**
 * Clase abstracta que implementa
 * metodos comunes a todas las Activities de la app.
 * Debe ser heredada por todas las Activities.
 */
public abstract class _SuperActivity extends AppCompatActivity{

    /**
     * Función para mandar mensajes Toast
     * @param idMensaje
     */
    public void lanzarMensaje(int idMensaje){
        Toast.makeText(this,getString(idMensaje),Toast.LENGTH_SHORT).show();
    }

    /**
     * Comprueba si hay conexión a Internet.
     * Si no la hay, muestra un mensaje y devuelve true
     * @return true / false
     */
    public boolean noHayConexion() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo == null) {//No hay conexion
                Toast.makeText(this,
                        getString(R.string.msj_offline), Toast.LENGTH_SHORT).show();
                return true;
            }
        }
        return false;//Hay conexion
    }


    /* FUNCIONES DE ANIMACION DE CARGA.
    PERMITE LLAMAR A UNA FUNCION PARA QUE SE HAGA VISIBLE
    UN LAYOUT CON UNA PROGRESSBAR CIRCULAR,
    Y OTRA PARA OCULTARLA.
    PARA ELLO, ES NECESARIO QUE EN EL LAYOUT DE LA ACTIVIDAD
    EXISTA UN CONSTRAINT LAYOUT CON UNA PROGRESSBAR
    CON @ID capaCargando; Y UN FRAGMENT CONTAINER VIEW
    LLAMADO fragmentContainer.

    *** VER activity_usuario.xml y usages de estas funciones */

    /**
     * Muestra la animación de carga
     * y puede ocultar el contenido el fragment
     * @param ocultarContent true / false
     */
    public void mostrarCargando(boolean ocultarContent){
        findViewById(R.id.capaCargando).setVisibility(View.VISIBLE);
        if(ocultarContent)
            findViewById(R.id.fragmentContainer).setVisibility(View.GONE);
    }

    /**
     * Oculta la animacion de carga
     * y vuelve a mostrar el contenido del fragment
     */
    public void ocultarCargando(){
        findViewById(R.id.capaCargando).setVisibility(View.GONE);
        findViewById(R.id.fragmentContainer).setVisibility(View.VISIBLE);
    }
}
