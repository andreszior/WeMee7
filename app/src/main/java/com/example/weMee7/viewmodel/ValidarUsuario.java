package com.example.weMee7.viewmodel;

import static com.example.weMee7.view.usuario.UsuarioActivity.GOOGLE_SIGN_IN;
import static com.example.weMee7.view.usuario.UsuarioActivity.LOGIN_KEY;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.weMee7.comun.seguridad.SharedPref;
import com.example.weMee7.model.dao.UsuarioDAO;
import com.example.weMee7.model.entities.Usuario;
import com.example.weMee7.view.usuario.LoginFragment;
import com.example.weMee7.view.usuario.PhoneCodeFragment;
import com.example.weMee7.view.usuario.UserHomeFragment;
import com.example.weMee7.view.usuario.UsuarioActivity;
import com.example.wemee7.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthMissingActivityForRecaptchaException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
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

    //region FUNCIONES COMUNES

    private void registrarUsuarioBD(String userId, String userNombre, Usuario.SignInMethod method){
        new UsuarioDAO().insertarRegistro(new Usuario(
                userId,
                userNombre,
                null,
                method));
        Log.d("validar","Usuario creado");
    }

    private void mostrarHome() {
        //Actualizar las preferencias compartidas
        SharedPref sharedPref = new SharedPref(context);
        if(!sharedPref.get(LOGIN_KEY))
            sharedPref.put(LOGIN_KEY,true);

        //Lanzar fragment de home de usuario
        reemplazarFragmentUsuario(UserHomeFragment.class);
    }

    private void reemplazarFragmentUsuario(Class fragmentClass){
        ((AppCompatActivity)context).getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fcvUsuario, fragmentClass, null)
                .addToBackStack(null)
                .commit();

    }

    private boolean hayUsuario(){
        if(mAuth.getCurrentUser() == null){
            ((UsuarioActivity)context).lanzarMensaje(R.string.msj_fUser_fail);
            return false;
        }
        return true;
    }

    //endregion

    //region VINCULAR TELEFONO


    //Vincular TELEFONO a usuario existente
    public void validarTelefono(String telefono){
        //En teoria esta funcion solo puede ser llamada por un usuario validado
        //y esta condicion no se cumpliria nunca
        if(!hayUsuario())
            return;

        PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks =
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                // El parametro son las credenciales para iniciar sesion
                Log.d("validar","Validacion automatica");
                vincularTelefono(credential);
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
                vincularTelefono(credential);
    }

    //Vincular el telefono validado al usuario
    private void vincularTelefono(PhoneAuthCredential credential) {
        mAuth.getCurrentUser().linkWithCredential(credential)
                .addOnCompleteListener(context, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            modificarPhoneBD(true);
                        } else {
                            // Sign in failed, display a message and update the UI
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // Codigo erroneo
                            }
                        }
                    }
                });
    }

    //Desvincular el telefono del usuario actual
    public void desvincularTelefono(){
        //En teoria esta funcion solo puede ser llamada por un usuario validado
        //y esta condicion no se cumpliria nunca
        if(!hayUsuario())
            return;

        mAuth.getCurrentUser().unlink(PhoneAuthProvider.PROVIDER_ID)
                .addOnCompleteListener(context, task -> {
                    if (task.isSuccessful()) {
                        modificarPhoneBD(false);
                    } else {
                        // Ha ocurrido un error al intentar desvincular el número de teléfono.
                    }
                });
    }

    private void modificarPhoneBD(boolean vincular){
        UsuarioDAO dao = new UsuarioDAO();
        FirebaseUser fUser = mAuth.getCurrentUser();
        dao.obtenerRegistroPorId(fUser.getUid(), resultado -> {
            Usuario u = (Usuario) resultado;
            u.setCredencial(Usuario.SignInMethod.PHONE,vincular);
            dao.actualizarRegistro(u);
            int mensaje = vincular ? R.string.msj_vincular_tlf :
                    R.string.msj_desvincular_tlf;
            ((UsuarioActivity)context).lanzarMensaje(mensaje);
            mostrarHome();
        });
    }


    //endregion

    //region AUTH EMAIL
    public void registrarConEmail(String email, String pass){
        mAuth.createUserWithEmailAndPassword(email,pass)
                .addOnCompleteListener(context, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("validar","Usuario validado");

                            //Registrar usuario
                            String userId = mAuth.getCurrentUser().getUid();
                            String userNombre = email.split("@")[0];
                            registrarUsuarioBD(userId,userNombre, Usuario.SignInMethod.EMAIL);

                            //Mostrar home usuario
                            mostrarHome();
                        } else {
                            String mensajeError = "";
                            try {
                                throw task.getException();
                            } catch(FirebaseAuthUserCollisionException e) {
                                mensajeError = "Este email ya está registrado";
                            } catch(Exception e) {
                                mensajeError = "Credenciales incorrectas";
                            } finally{
                                Toast.makeText(context, mensajeError,
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    public void validarConEmail(String email, String pass){
        mAuth.signInWithEmailAndPassword(email,pass)
                .addOnCompleteListener(context, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("validar","Usuario validado");

                            //Mostrar home usuario
                            mostrarHome();
                        } else {
                            /* Podria detectarse si el email ya esta registrado o no,
                            pero actualmente Firebase no habilita esa opcion por defecto
                            por motivos de seguridad */
                            Toast.makeText(context, "Credenciales incorrectas",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                });

    }
    //endregion


    //region AUTH GOOGLE
    public void validarConGoogle(){

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(context, gso);

        //Lanza el intent de validación de Google desde UsuarioActivity
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        context.startActivityForResult(signInIntent, GOOGLE_SIGN_IN);
    }

    public void obtenerGoogleToken(Intent data){
        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
        try {
            // Google Sign In was successful, authenticate with Firebase
            GoogleSignInAccount account = task.getResult(ApiException.class);
            //Inicia sesión con google
            signInConGoogle(account.getIdToken());
        } catch (ApiException e) {
            // Google Sign In failed, update UI appropriately
            Log.d("validar", "No se ha podido recuperar el token");


        }
    }

    private void signInConGoogle(String idToken){
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(context, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        FirebaseUser user = mAuth.getCurrentUser();
                        boolean isNewUser = task.getResult().getAdditionalUserInfo().isNewUser();
                        if (isNewUser) {
                            Log.d("validar", "New User");
                            registrarUsuarioBD(
                                    user.getUid(),
                                    user.getDisplayName(),
                                    Usuario.SignInMethod.GOOGLE
                            );
                        } else {
                            Log.d("validar", "Existing User");
                            //setear GOOGLE = true
                        }
                        mostrarHome();
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.d("validar", "Error en sign in");
                    }
                });
    }
    //endregion

    //region LOGOUT
    public void cerrarSesion(){
        //Cerrar sesion de Firebase
        mAuth.signOut();

        reiniciarCredenciales();
    };

    public void eliminarCuenta(){
        //Solicitar de nuevo la validacion de credenciales !!!
        FirebaseUser fUser = mAuth.getCurrentUser();
        new UsuarioDAO().borrarRegistro(fUser.getUid());
        //Eliminar reuniones creadas por el usuario !!!
        //Eliminar invitaciones a nombre del usuario !!!
        //Desasignar encomiendas del usuario !!!

        fUser.delete();

        reiniciarCredenciales();
    }

    private void reiniciarCredenciales(){
        //Limpiar preferencias compartidas
        SharedPref sharedPref = new SharedPref(context);
        if(sharedPref.get(LOGIN_KEY))
            sharedPref.remove(LOGIN_KEY);

        //Lanzar el fragment de login
        reemplazarFragmentUsuario(LoginFragment.class);
    }
    //endregion

}
