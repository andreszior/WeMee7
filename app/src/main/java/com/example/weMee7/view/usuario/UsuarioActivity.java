package com.example.weMee7.view.usuario;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;

import com.example.weMee7.comun.seguridad.SharedPref;
import com.example.weMee7.view._SuperActivity;
import com.example.wemee7.R;

/**
 * Activity que gestiona los fragments
 * relacionados con el registro y validacion de usuarios,
 * perfil del usuario logueado
 * y lista de reuniones del usuario
 */
public class UsuarioActivity extends _SuperActivity {

    public static int GOOGLE_SIGN_IN = 7;
    public static String LOGIN_KEY = "login";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario);

        Class primerFragment;
        //Comprobar si hay sesion iniciada
        if(new SharedPref(this).get(LOGIN_KEY))
            primerFragment = UserHomeFragment.class;
        else
            primerFragment = LoginFragment.class;

        //Llamar al primer fragment
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragmentContainer, primerFragment, null)
                .commit();
    }

    /**
     * En el caso de que el fragment cargado sea el Login o el Home de usuario,
     * cierra la aplicacion
     */
    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
        if(fragment instanceof LoginFragment || fragment instanceof UserHomeFragment)
            this.finishAffinity();
        else
            super.onBackPressed();
    }

    /**
     * Caso previsto 1: Resultado devuelto por el Intent de Google para seleccionar cuenta.
     *                  En este caso, se reenvia al LoginFragment para que lo gestione.
     *
     *
     * @param requestCode The integer request code originally supplied to
     *                    startActivityForResult(), allowing you to identify who this
     *                    result came from.
     * @param resultCode The integer result code returned by the child activity
     *                   through its setResult().
     * @param data An Intent, which can return result data to the caller
     *               (various data can be attached to Intent "extras").
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);

        //Si el fragment activo es de Login y el intent que devuelve el resultado es de Google
        if (requestCode == GOOGLE_SIGN_IN && fragment != null && fragment instanceof LoginFragment)
            ((LoginFragment) fragment).capturarToken(data);
            /* Esta funcion se ejecuta siempre que se valide con cuenta de Google.
            El resultado de la seleccion de cuenta de Google llega a esta funcion,
            y se reenvia al fragment para que capture el token,
            a traves de la funcion correspondiente de ValidarUsuario. */
    }
}