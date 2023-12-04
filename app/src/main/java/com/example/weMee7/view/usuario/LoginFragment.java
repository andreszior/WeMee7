package com.example.weMee7.view.usuario;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.weMee7.comun.InputControl;
import com.example.weMee7.view._SuperActivity;
import com.example.weMee7.viewmodel.ValidarUsuario;
import com.example.wemee7.R;

/**
 * Fragmento que gestiona el registro y acceso de usuarios
 */
public class LoginFragment extends Fragment {

    EditText etEmail;
    EditText etPass;
    ValidarUsuario validador;


    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        validador = new ValidarUsuario(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        //Deshabilitar menu hamburguesa
        ((_SuperActivity)getActivity()).setDrawerMenu(false);

        //Vincular componentes
        etEmail = view.findViewById(R.id.etLoginEmail);
        etPass = view.findViewById(R.id.etLoginPass);

        //Agregar eventos a botones
        view.findViewById(R.id.btLoginRegistrar).setOnClickListener(v -> pulsarRegistrar());
        view.findViewById(R.id.btLoginAcceder).setOnClickListener(v -> pulsarAcceder());
        view.findViewById(R.id.btLoginGoogle).setOnClickListener(v -> pulsarGoogle());

        return view;
    }

    /**
     * Registro de nuevo usuario con email y contraseña
     */
    private void pulsarRegistrar() {
        String email = etEmail.getText().toString();
        String pass = etPass.getText().toString();

        if(checkCredenciales(email,pass))
            validador.registrarConEmail(email,pass);

    }

    /**
     * Inicio de sesion con email y contraseña
     * de usuario ya registrado
     */
    private void pulsarAcceder() {
        String email = etEmail.getText().toString();
        String pass = etPass.getText().toString();

        if(checkCredenciales(email,pass))
            validador.validarConEmail(email,pass);
    }

    /**
     * Comprueba que ambos campos esten completos
     * y el formato del email
     * @param email
     * @param pass
     * @return
     */
    private boolean checkCredenciales(String email, String pass){
        //Control de formato de email
        if(!InputControl.emailOk(email)) {
            ((_SuperActivity) getActivity()).lanzarMensaje(R.string.msj_email_fail);
            return false;
        }
        //Control de campos vacios
        if(!InputControl.todoCumplimentado(new String[]{email,pass})) {
            ((_SuperActivity) getActivity()).lanzarMensaje(R.string.msj_rellenar_campos);
            return false;
        }
        return true;
    }

    /**
     * Inicia el registro/inicio de sesion con google
     */
    private void pulsarGoogle() {
        validador.validarConGoogle();
    }

    /**
     * Se invoca cuando se selecciona una cuenta de google;
     * el evento lo recibe UsuarioActivity (onActivityResult)
     * @param data
     */
    void capturarToken(Intent data){
        validador.obtenerGoogleToken(data);
    }
}