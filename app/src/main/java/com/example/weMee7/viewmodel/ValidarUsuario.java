package com.example.weMee7.viewmodel;

import static com.example.weMee7.view.usuario.UsuarioActivity.GOOGLE_SIGN_IN;
import static com.example.weMee7.view.usuario.UsuarioActivity.LOGIN_KEY;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.weMee7.comun.seguridad.SharedPref;
import com.example.weMee7.model.dao.UsuarioDAO;
import com.example.weMee7.model.entities.Usuario;
import com.example.weMee7.view._SuperActivity;
import com.example.weMee7.view.usuario.LoginFragment;
import com.example.weMee7.view.usuario.PhoneCodeFragment;
import com.example.weMee7.view.usuario.UserHomeFragment;
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
 * relacionados con la validación y registro de usuarios
 * en Firebase Authentication.
 */
public class ValidarUsuario {

    Activity context;//UsuarioActivity
    FirebaseAuth mAuth;//Instancia de FirebaseAuth

    public ValidarUsuario(Activity context){
        this.context = context;
        this.mAuth = FirebaseAuth.getInstance();
    }

    //region AUTH GOOGLE
    /**
     * Primera parte de la validacion de google:
     * Se muestra lanza un intent para seleccionar cuenta;
     * si ya se selecciono anteriormente,
     * no se muestra ninguna ventana
     * y toda la logica funciona internamente.
     */
    public void validarConGoogle(){
        //Muestra animacion de carga
        mostrarCargando();

        //Establece las opciones de signIn de Google

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(context, gso);

        //Lanza el intent de validación de Google desde UsuarioActivity
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        context.startActivityForResult(signInIntent, GOOGLE_SIGN_IN);
        /* Si nunca se ha utilizado una cuenta de Google con la app,
        se muestra una ventana para seleccionarla.
        * El resultado es recogido por UsuarioActivity (onActivityResult),
        que lo reenvia al LoginFragment, que llama a la funcion obtenerGoogleToken

        * Si ya se ha utilizado una cuenta de Google
        (por ejemplo, el usuario se ha registrado y ha cerrado sesion),
        no se muestra la ventana de seleccion de cuenta, sino que accede directamente.

         */
        Log.d("validar", "Pedir cuenta google");
    }

    /**
     * Esta funcion es llamada
     * una vez seleccionada una cuenta de Google
     * para validarla.
     * Si se obtiene acceso, se inicia sesion en Firebase Auth;
     * si no, se muestra mensaje.
     * @param data
     */
    public void obtenerGoogleToken(Intent data){
        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
        try {
            //Inicia sesión con google
            GoogleSignInAccount account = task.getResult(ApiException.class);
            signInConGoogle(account.getIdToken());
        } catch (ApiException e) {
            // Error al inciar sesion con Google
            lanzarMensaje(R.string.msj_googleToken_fail);
        }
    }

    /**
     * Una vez validada la cuenta de Google,
     * se inicia sesion en la app con Firebase.
     * Si no existe el usuario, se crea en la BD.
     * Salvo que el inicio de sesion falle,
     * se muestra la pantalla Home del usuario.
     * @param idToken
     */
    private void signInConGoogle(String idToken){
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(context, task -> {
                    if (task.isSuccessful()) {
                        // Inicio de sesion OK
                        FirebaseUser user = mAuth.getCurrentUser();
                        boolean isNewUser = task.getResult().getAdditionalUserInfo().isNewUser();

                        if (isNewUser) {//Si el usuario es nuevo, se crea en la BD
                            Log.d("validar", "New User");
                            registrarUsuarioBD(
                                    user.getUid(),
                                    user.getDisplayName(),
                                    Usuario.SignInMethod.GOOGLE
                            );
                        } else //Si ya existe, no hace nada
                            Log.d("validar", "Existing User");

                        mostrarHome();//Se muestra home de usuario

                    } else // Inicio de sesion fail
                        lanzarMensaje(R.string.msj_googleSignIn_fail);
                });
    }
    //endregion

    //region AUTH EMAIL

    /**
     * Funcion para registrar en Firebase
     * un usuario nuevo con email y contraseña
     * @param email
     * @param pass
     */
    public void registrarConEmail(String email, String pass){
        mAuth.createUserWithEmailAndPassword(email,pass)
                .addOnCompleteListener(context, task -> {
                    if (task.isSuccessful()) {//Validacion OK
                        Log.d("validar","Usuario validado");

                        //Registrar usuario
                        String userId = mAuth.getCurrentUser().getUid();
                        String userNombre = email.split("@")[0];
                        registrarUsuarioBD(userId,userNombre, Usuario.SignInMethod.EMAIL);

                        //Mostrar home usuario
                        mostrarHome();

                    } else {//Validacion FAIL
                        int mensajeError = 0;
                        try {
                            throw task.getException();
                        } catch(FirebaseAuthUserCollisionException e) {
                            mensajeError = R.string.msj_emailYaRegistrado;
                        } catch(Exception e) {
                            mensajeError = R.string.msj_credenciales_fail;
                        } finally{
                            lanzarMensaje(mensajeError);
                        }
                    }
                });
    }

    /**
     * Funcion para iniciar sesion en Firebase
     * con email y contraseña
     * de un usuario YA REGISTRADO
     * @param email
     * @param pass
     */
    public void validarConEmail(String email, String pass){
        mAuth.signInWithEmailAndPassword(email,pass)
                .addOnCompleteListener(context, task -> {
                    if (task.isSuccessful()) {
                        // Inicio de sesion OK
                        Log.d("validar","Usuario validado");

                        //Mostrar home usuario
                        mostrarHome();
                    } else //Validacion FAIL
                        lanzarMensaje(R.string.msj_credenciales_fail);
                    /* Este mensaje tambien se lanzara
                    cuando se intente acceder con email y contraseña
                    y ese mismo email haya sido registrado como cuenta de google
                     */
                });

    }
    //endregion

    //region VINCULAR TELEFONO
    /**
     * PRIMER PASO: Verifica el numero de telefono
     * y segun el resultado salta un callback
     * 1. Verificacion automatica (?)
     * 2. Verificacion fallida (mensaje y de vuelta)
     * 3. Enviar codigo (mostrar pantalla para introducirlo)
     * @param telefono
     */
    public void validarTelefono(String telefono){
        //En teoria esta funcion solo puede ser llamada por un usuario validado
        //y esta condicion no se cumpliria nunca
        if(!hayUsuario())
            return;

        //Mostrar animacion de carga
        mostrarCargando();

        //Configuracion de callbacks
        PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks =
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                        // Cuando la verificacion se hace de forma automatica (?)
                        Log.d("validar","Validacion automatica");
                        vincularTelefono(credential);
                    }
                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        // Cuando la verificacion del numero de telefono FALLA
                        Log.d("validar",e.getMessage());

                        if (e instanceof FirebaseAuthInvalidCredentialsException) {
                            // Invalid request
                        } else if (e instanceof FirebaseTooManyRequestsException) {
                            // The SMS quota for the project has been exceeded
                        } else if (e instanceof FirebaseAuthMissingActivityForRecaptchaException) {
                            // reCAPTCHA verification attempted with null Activity
                        }
                        lanzarMensaje(R.string.msj_vincular_fail);
                        mostrarHome();
                    }
                    @Override
                    public void onCodeSent(@NonNull String verificationId,
                                           @NonNull PhoneAuthProvider.ForceResendingToken token) {
                        Log.d("validar","Introduzca codigo");
                        /* Normalmente despues de introducir el telefono saltara este callback*/

                        //Ocultar animacion de carga
                        ocultarCargando();

                        //Llamar al fragment de introducir codigo
                        reemplazarFragmentUsuario(
                                PhoneCodeFragment.newInstance(verificationId));
                    }
                };

        //Configuracion de opciones
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(telefono)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(context)
                        .setCallbacks(mCallbacks)//Declarados arriba
                        .build();

        //Funcion de verificacion
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    /**
     * SEGUNDO PASO: Se introduce el codigo
     * y se lanza la funcion de validacion
     * @param id
     * @param codigo
     */
    public void validarCodigo(String id, String codigo){
        //Animacion de carga
        mostrarCargando();

        //Verificar codigo
        PhoneAuthCredential credential =
                PhoneAuthProvider.getCredential(id, codigo);
        Log.d("validar","Codigo introducido");
        vincularTelefono(credential);
    }

    /**
     * TERCER PASO: Se valida la peticion y el codigo
     * Si es correcto, se vincula el telefono al usuario en Firebase
     * y actualiza la BD
     * @param credential
     */
    private void vincularTelefono(PhoneAuthCredential credential) {
        mAuth.getCurrentUser().linkWithCredential(credential)
                .addOnCompleteListener(context, task -> {
                    if (task.isSuccessful()) {
                        //Actualiza la base de datos
                        modificarPhoneBD(true);
                    } else {
                        //Si el codigo no es correcto
                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            lanzarMensaje(R.string.msj_vincular_fail);
                            mostrarHome();
                        }
                    }
                });
    }

    /**
     * Funcion opuesta a la anterior,
     * desvincula el telefono del usuario en Firebase
     * y actualiza la BD
     */
    public void desvincularTelefono(){
        //En teoria esta funcion solo puede ser llamada por un usuario validado
        //y esta condicion no se cumpliria nunca
        if(!hayUsuario())
            return;

        mAuth.getCurrentUser().unlink(PhoneAuthProvider.PROVIDER_ID)
                .addOnCompleteListener(context, task -> {
                    if (task.isSuccessful())
                        modificarPhoneBD(false);
                    else
                        lanzarMensaje(R.string.msj_desvincular_fail);

                    ocultarCargando();
                });
    }

    /**
     * Actualiza la BD en los casos de vinculacion
     * o desvinculacion del telefono
     * @param vincular true (vincula) / false (desvincula)
     */
    private void modificarPhoneBD(boolean vincular){
        UsuarioDAO dao = new UsuarioDAO();
        FirebaseUser fUser = mAuth.getCurrentUser();
        dao.obtenerRegistroPorId(fUser.getUid(), resultado -> {
            Usuario u = (Usuario) resultado;
            u.setCredencial(Usuario.SignInMethod.PHONE,vincular);
            dao.actualizarRegistro(u);
            int mensaje = vincular ? R.string.msj_vincular_tlf :
                    R.string.msj_desvincular_tlf;
            lanzarMensaje(mensaje);
            mostrarHome();
        });
    }


    //endregion

    //region LOGOUT

    /**
     * Cierra la sesion en Firebase del usuario en el dispositivo.
     * La proxima vez que inicie la aplicacion,
     * aparecera la pantalla de login.
     */
    public void cerrarSesion(){
        //Cerrar sesion de Firebase
        mAuth.signOut();
        reiniciarCredenciales();
    };

    /**
     * Elimina al usuario de Firebase Authentication
     * y de la BD.
     * !!! FALTA IMPLEMENTAR EL BORRADO DE ENTIDADES DEPENDIENTES
     * PARA GARANTIZAR LA INTEGRIDAD REFERENCIAL
     */
    public void eliminarCuenta(){
        mostrarCargando();
        //Solicitar de nuevo la validacion de credenciales !!!
        FirebaseUser fUser = mAuth.getCurrentUser();
        new UsuarioDAO().borrarRegistro(fUser.getUid());
        //Eliminar reuniones creadas por el usuario !!!
        //Eliminar invitaciones a nombre del usuario !!!
        //Desasignar encomiendas del usuario !!!
        fUser.delete();
        reiniciarCredenciales();
        ocultarCargando();
    }

    /**
     * Borra el inicio de sesion de las preferencias compartidas
     */
    private void reiniciarCredenciales(){
        //Limpiar preferencias compartidas
        SharedPref sharedPref = new SharedPref(context);
        if(sharedPref.get(LOGIN_KEY))
            sharedPref.remove(LOGIN_KEY);

        //Lanzar el fragment de login
        reemplazarFragmentUsuario(new LoginFragment());
    }
    //endregion

    //region FUNCIONES COMUNES
    /**
     * Se registra el usuario
     * cuando es validado por primera vez
     * en Firebase Authentication
     * @param userId
     * @param userNombre
     * @param method
     */
    private void registrarUsuarioBD(String userId, String userNombre, Usuario.SignInMethod method){

        // !!! TODO - IMPLEMENTAR FUNCION PARA GENERAR IMAGEN DE PERFIL

        new UsuarioDAO().insertarRegistro(new Usuario(
                userId,
                userNombre,
                null,
                method));
        Log.d("validar","Usuario creado");
    }

    /**
     * Actualiza las preferencias compartidas
     * para persistir el inicio de sesion,
     * y lanza el Fragment de inicio de Usuario.
     */
    private void mostrarHome() {
        //Actualizar las preferencias compartidas
        SharedPref sharedPref = new SharedPref(context);
        if(!sharedPref.get(LOGIN_KEY))
            sharedPref.put(LOGIN_KEY,true);

        //Lanzar fragment de home de usuario
        reemplazarFragmentUsuario(new UserHomeFragment());

        //Ocultar capa cargando
        ocultarCargando();
    }

    /**
     * Reemplaza el fragment cargado en UsuarioActivity
     * por el fragment pasado por parametro
     * @param fragment
     */
    private void reemplazarFragmentUsuario(Fragment fragment){
        ((AppCompatActivity)context).getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack(null)
                .commit();
    }

    /**
     * Comprueba si hay una sesion de Firebase activa
     * @return true / false
     */
    private boolean hayUsuario(){
        if(mAuth.getCurrentUser() == null){
            ((_SuperActivity)context).lanzarMensaje(R.string.msj_fUser_fail);
            return false;
        }
        return true;
    }

    //REDIRECCION A FUNCIONES DE LA ACTIVITY

    /**
     * Reenvia el mensaje a la activity
     * para que lance el Toast
     * @param idString
     */
    private void lanzarMensaje(int idString){
        ((_SuperActivity)context).lanzarMensaje(idString);
    }

    /**
     * Hace visible la animacion de carga
     * y oscurece el fondo
     */
    private void mostrarCargando(){
        ((_SuperActivity)context).mostrarCargando(false);
    }

    /**
     * Retira la animacion de carga
     */
    private void ocultarCargando(){
        ((_SuperActivity)context).ocultarCargando();
    }
    //endregion

}
