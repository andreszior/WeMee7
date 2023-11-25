package com.example.weMee7.view.usuario;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.weMee7.comun.InputControl;
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

        //Vincular componentes
        etEmail = view.findViewById(R.id.etLoginEmail);
        etPass = view.findViewById(R.id.etLoginPass);

        //Agregar eventos a botones
        view.findViewById(R.id.btLoginRegistrar).setOnClickListener(v -> pulsarRegistrar());
        view.findViewById(R.id.btLoginAcceder).setOnClickListener(v -> pulsarAcceder());
        view.findViewById(R.id.btLoginGoogle).setOnClickListener(v -> pulsarGoogle());

        return view;
    }


    private void pulsarRegistrar() {
        //Comprobar correo y contraseña no vacios
        //Comprobar formato email
        String email = etEmail.getText().toString();
        String pass = etPass.getText().toString();

        validador.registrarConEmail(email,pass);

    }

    private void pulsarAcceder() {
        //Control de formato de email
        String email = etEmail.getText().toString();
        if(!InputControl.emailOk(email)) {
            ((UsuarioActivity) getActivity()).lanzarMensaje(R.string.msj_email_fail);
            return;
        }
        //Control de campos vacios
        String pass = etPass.getText().toString();
        if(!InputControl.todoCumplimentado(new String[]{email,pass})) {
            ((UsuarioActivity) getActivity()).lanzarMensaje(R.string.msj_rellenar_campos);
            return;
        }

        validador.validarConEmail(email,pass);
    }

    private void pulsarGoogle() {
        validador.validarConGoogle();
    }

    void capturarToken(Intent data){
        validador.obtenerGoogleToken(data);
    }
}