package com.example.weMee7.view.usuario;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        //Deshabilitar menu hamburguesa
        ((_SuperActivity)requireActivity()).setDrawerMenu(false);

        //Vincular componentes
        etEmail = view.findViewById(R.id.etLoginEmail);
        etPass = view.findViewById(R.id.etLoginPass);

        //Agregar eventos a botones
        view.findViewById(R.id.btLoginRegistrar).setOnClickListener(v -> pulsarRegistrar());
        view.findViewById(R.id.btLoginAcceder).setOnClickListener(v -> pulsarAcceder());
        view.findViewById(R.id.btLoginGoogle).setOnClickListener(v -> pulsarGoogle());
        view.findViewById(R.id.btShowPass).setOnTouchListener((v, event) -> pulsarMostrarPass(event));

        return view;
    }

    /**
     * Registro de nuevo usuario con email y contraseña
     */
    private void pulsarRegistrar() {
        String email = etEmail.getText().toString();
        String pass = etPass.getText().toString();

        if(checkCredenciales(email,pass)){
            dialogRegistrar(email,pass);
        }
    }

    private void dialogRegistrar(String email, String pass){
        // Crear AlertDialog.Builder
        Context c = requireActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(c);

        //Titulo
        builder.setTitle(R.string.tag_confirmar_pass);

        // Campo de texto
        final EditText input = new EditText(c);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

        //Asignacion de botones
        builder.setPositiveButton(R.string.bt_confirmar, (dialog, which) -> {
            validador.registrarConEmail(email,pass);
        });
        builder.setNegativeButton(R.string.bt_cancelar, (dialog, which) -> dialog.cancel());

        final AlertDialog dialog = builder.create();

        // Configurar boton confirmar
        dialog.setOnShowListener(dialog1 -> {
            Button btConfirmar = ((AlertDialog) dialog1).getButton(AlertDialog.BUTTON_POSITIVE);
            btConfirmar.setEnabled(false);
            btConfirmar.setTextColor(getResources().getColor(R.color.avatar_gris));

            //Listener de input
            input.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}
                @Override
                public void afterTextChanged(Editable s) {
                    boolean mismaPass = s.toString().equals(pass);
                    int colorFondo = (mismaPass ? R.color.medium : R.color.white);
                    int colorTexto = (mismaPass ? R.color.white : R.color.avatar_gris);
                    btConfirmar.setEnabled(mismaPass);
                    btConfirmar.setBackgroundColor(getResources().getColor(colorFondo));
                    btConfirmar.setTextColor(getResources().getColor(colorTexto));
                }
            });
        });
        // Mostrar el AlertDialog
        dialog.show();

    }

    /**
     * Inicio de sesion con email y contraseña
     * de usuario ya registrado
     */
    private void pulsarAcceder() {
        String email = etEmail.getText().toString().toLowerCase();
        String pass = etPass.getText().toString();

        if(checkCredenciales(email,pass))
            validador.validarConEmail(email,pass);
    }

    /**
     * Comprueba que ambos campos esten completos
     * y el formato del email y longitud de contraseña
     * @param email correo introducido
     * @param pass contraseña introducida
     * @return correcto / incorrecto
     */
    private boolean checkCredenciales(String email, String pass){
        int mensaje = 0;
        //Control de campos vacios
        if(!InputControl.todoCumplimentado(new String[]{email,pass}))
            mensaje = R.string.msj_rellenar_campos;

        //Control de formato de email
        if(!InputControl.emailOk(email))
            mensaje = R.string.msj_email_fail;

        //Control longitud de password
        if(!InputControl.passOk(pass))
            mensaje = R.string.msj_pass_fail;

        //Control correcto
        if(mensaje == 0)
            return true;

        //Control incorrecto: mostrar mensaje
        ((_SuperActivity) requireActivity()).lanzarMensaje(mensaje);
        return false;
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

    /**
     * Si se ha escrito algo en el campo pass,
     * se muestra el contenido durante la pulsación,
     * más medio segundo después de levantar
     * @param ev evento de pulsacion (DOWN / UP)
     * @return true
     */
    private boolean pulsarMostrarPass(MotionEvent ev){
        if(etPass.getText().toString().equals(""))
            return false;
        int cursor = etPass.getSelectionStart();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                etPass.setInputType(//Texto visible
                        InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                etPass.setSelection(cursor);
                break;
            case MotionEvent.ACTION_UP:
                new Handler().postDelayed(() -> {
                            etPass.setInputType(
                                    InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            etPass.setSelection(cursor);},
                        500);
                break;
        }
        return true;
    }
}