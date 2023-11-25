package com.example.weMee7.view.usuario;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.weMee7.comun.seguridad.SharedPref;
import com.example.wemee7.R;

/**
 * Activity que gestiona los fragments
 * relacionados con el registro y validacion de usuarios,
 * perfil del usuario logueado
 * y lista de reuniones del usuario
 */
public class UsuarioActivity extends AppCompatActivity {

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
                .add(R.id.fcvUsuario, primerFragment, null)
                .commit();
    }

    /**
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
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fcvUsuario);

        //Si el fragment activo es de Login y el intent que devuelve el resultado es de Google
        if (requestCode == GOOGLE_SIGN_IN && fragment != null && fragment instanceof LoginFragment)
            ((LoginFragment) fragment).capturarToken(data);
    }

    /**
     * MOVER A UNA SUPERCLASE ACTIVITY, SI SE CREA !!!
     * Función para mandar mensajes Toast
     * @param mensaje
     */
    public void lanzarMensaje(int mensaje){
        Toast.makeText(this,getString(mensaje),Toast.LENGTH_SHORT).show();
    }
}