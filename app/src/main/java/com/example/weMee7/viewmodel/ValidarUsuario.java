package com.example.weMee7.viewmodel;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.weMee7.model.dao.UsuarioDAO;
import com.example.weMee7.model.entities.Usuario;
import com.example.weMee7.view.usuario.PhoneCodeFragment;
import com.example.weMee7.view.usuario.UserSessionFragment;
import com.example.wemee7.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthMissingActivityForRecaptchaException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

/**
 * Clase que recoge métodos
 * relacionados con la validación y registro de usuarios.
 */
public class ValidarUsuario {

    Activity context;
    FirebaseAuth mAuth;

    public ValidarUsuario(Activity context){
        this.context = context;
        this.mAuth = FirebaseAuth.getInstance();
    }

    //Registrar usuario con TELEFONO
    public void registrarConTelefono(String telefono){
        //String telefono = "+16505553434";
        //String testVerificationCode = "123456";



        //auth.useAppLanguage();

        PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks =
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                // El parametro son las credenciales para iniciar sesion
                Log.d("validar","Validacion automatica");

                validarConTelefono(credential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                // Cuando la verificacion falla
                Log.d("validar","Validacion fallida");
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                } else if (e instanceof FirebaseAuthMissingActivityForRecaptchaException) {
                    // reCAPTCHA verification attempted with null Activity
                }

                // Show a message and update the UI
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.

                // Save verification ID and resending token so we can use them later
//                mVerificationId = verificationId;
//                mResendToken = token;

                Log.d("validar","Introduzca codigo");
                PhoneCodeFragment fragment = PhoneCodeFragment.newInstance(verificationId);

                ((AppCompatActivity)context).getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fcvUsuario, fragment)
                        .addToBackStack(null)
                        .commit();


//                PhoneAuthCredential credential =
//                        PhoneAuthProvider.getCredential(verificationId, testVerificationCode);
//                Log.d("validar","Codigo introducido");
//                validarConTelefono(credential);
            }
        };

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(telefono)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(context)                 // (optional) Activity for callback binding
                        // If no activity is passed, reCAPTCHA verification can not be used.
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();

        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    public void validarCodigo(String id, String codigo){
        PhoneAuthCredential credential =
                        PhoneAuthProvider.getCredential(id, codigo);
                Log.d("validar","Codigo introducido");
                validarConTelefono(credential);
    }

    //Validar usuario registrado con TELEFONO
    private void validarConTelefono(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(context, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("validar","Usuario validado correctamente");
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = task.getResult().getUser();
                            UsuarioDAO usuarioDAO = new UsuarioDAO();
                            usuarioDAO.insertarRegistro(new Usuario(
                                    user.getUid(),
                                    "Son Goku",
                                    null,
                                    Usuario.SignInMethod.PHONE));
                            Log.d("validar","Usuario creado");

                            // Update UI
                            ((AppCompatActivity)context).getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.fcvUsuario, UserSessionFragment.class, null)
                                    .addToBackStack(null)
                                    .commit();

                        } else {
                            // Sign in failed, display a message and update the UI
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }

    //Eliminar usuario del registro

    //Vincular nuevo metodo de autenticacion

    //Devincular metodo de autenticacion

}
