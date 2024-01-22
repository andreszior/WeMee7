package com.example.weMee7.view.usuario;

import android.content.res.ColorStateList;
import android.graphics.text.LineBreaker;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.weMee7.model.dao.UsuarioDAO;
import com.example.weMee7.model.entities.Usuario;
import com.example.weMee7.view._SuperActivity;
import com.example.weMee7.comun.Avatar;
import com.example.weMee7.viewmodel.ValidarUsuario;
import com.example.wemee7.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Fragment que gestiona el perfil de usuario.
 * Posibilidad de modificar atributos de la BD (nombre y foto).
 * La primera vez que se registra se muestra esta pantalla antes que el Home
 ** FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
 */
public class PerfilFragment extends Fragment {

    //Instancia de usuario al iniciar el fragment
    private Usuario usuarioPerfil;

    //Gestion de imagen de perfil
    private Avatar avatar;

    //Componentes
    private EditText etNombre;
    private ImageView ivFoto;
    private TextView tagVincular;
    private TextView subtitleVincular;
    private ImageButton btColorAnt;
    private ImageButton btColorPost;
    private ImageButton btDeshacer;
    private Button btGuardar;

    //Tras registrarse por primera vez
    //obliga a confirmar nombre y foto
    private boolean primerAcceso;


    public PerfilFragment() {
        // Required empty public constructor
    }

    /**
     * Nueva instancia del fragment con un boolean
     * @param primerAcceso true : obliga a guardar datos
     * @return fragment
     */
    public static PerfilFragment newInstance (boolean primerAcceso){
        PerfilFragment fragment = new PerfilFragment();
        Bundle args = new Bundle();
        args.putBoolean("INIT_KEY",primerAcceso);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null)
            primerAcceso = getArguments().getBoolean("INIT_KEY");
        else
            primerAcceso = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);

        //Mostrar animacion de carga (que se ocultara cuando se recuperen los datos de la BD)
        ((_SuperActivity)getActivity()).setCargando(true);

        //Databinding
        etNombre = view.findViewById(R.id.etPerfilNombre);
        ivFoto = view.findViewById(R.id.ivPerfilImagen);
        tagVincular = view.findViewById(R.id.tagVincularTelefono);
        subtitleVincular = view.findViewById(R.id.subtitleVincularTelefono);
        btColorAnt = view.findViewById(R.id.btPerfilLeftColor);
        btColorPost = view.findViewById(R.id.btPerfilRightColor);
        btDeshacer = view.findViewById(R.id.btPerfilUndo);
        btGuardar = view.findViewById(R.id.btPerfilGuardar);

        if(primerAcceso)
            btGuardar.setVisibility(View.VISIBLE);

        //Botones modificar avatar
        btColorAnt
                .setOnClickListener(v -> pulsarCambioAvatar(true,false));
        btColorPost
                .setOnClickListener(v -> pulsarCambioAvatar(true,true));
        view.findViewById(R.id.btPerfilUpImage)
                .setOnClickListener(v -> pulsarCambioAvatar(false,false));
        view.findViewById(R.id.btPerfilDownImage)
                .setOnClickListener(v -> pulsarCambioAvatar(false,true));
        view.findViewById(R.id.btVincularTelefono)
                .setOnClickListener(v -> pulsarVincularTlf());
        btDeshacer
                .setOnClickListener(v-> pulsarDeshacer());
        btGuardar
                .setOnClickListener(v-> pulsarGuardar());

        consultaUsuario();

        return view;
    }

    /**
     * Recupera datos de la base de datos
     * y los muestra en los distintos componentes del layout
     */
    private void consultaUsuario() {
        //Recuperar id del usuario
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        if(fUser != null){
            String idUser = fUser.getUid();
            new UsuarioDAO().obtenerRegistroPorId(idUser, resultado -> {
                //Instancia del usuario
                usuarioPerfil = (Usuario)resultado;
                //Avatar a partir del atributo foto
                avatar = new Avatar(getContext(), usuarioPerfil.getFoto());
                etNombre.setText(usuarioPerfil.getNombre());
                refreshFoto();
                setBotonVincular();
                addTextListeners();
                //Aqui se oculta la animacion de carga
                ((_SuperActivity)getActivity()).ocultarCargando();
            });
        }
    }

    /**
     * El boton de Vincular telefono
     * muestra un texto diferente segun el usuario
     * tenga o no el telefono vinculado.
     */
    private void setBotonVincular(){
        boolean telefonoVinculado = usuarioPerfil.getSingleCredencial(Usuario.SignInMethod.PHONE);
        tagVincular.setText(telefonoVinculado ?
                R.string.tag_desvincular_tlf : R.string.tag_vincular_tlf);
        if(!telefonoVinculado){
            subtitleVincular.setText(R.string.text_subtitulo_vincular);
            subtitleVincular.setJustificationMode(LineBreaker.JUSTIFICATION_MODE_INTER_WORD);
        }
    }

    /**
     * Agrega un listener al EditText del nombre,
     * para que muestre los botones deshacer y guardar
     * cuando el nombre no coincida con el actual del usuario
     */
    private void addTextListeners() {
        etNombre.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                comprobarCambios();
            }
        });
    }

    /**
     * La foto de perfil puede modificarse con cuatro flechas,
     * dos para la imagen y dos para el color de fondo.
     * Las 4 opciones se manejan con los dos parametros boolean.
     * @param color true : color / false : imagen
     * @param next true : siguiente / false : anterior
     */
    private void pulsarCambioAvatar(boolean color, boolean next){
        if(color)
            avatar.setColor(next);
        else
            avatar.setImagen(next);
        refreshFoto();
        comprobarCambios();
    }

    /**
     * Muestra la imagen guardada en Avatar
     * en el ImageView del perfil,
     * y adapta los colores de los botones de cambiar color.
     */
    private void refreshFoto(){
        //Imagen del Avatar
        ivFoto.setImageBitmap(avatar.toBitmap());
        //Color del Avatar
        ivFoto.setBackgroundTintList(ColorStateList.valueOf(
                getResources().getColor(avatar.getColor())));
        //Boton anterior > color anterior
        btColorAnt.setColorFilter(getResources().getColor(avatar.getNextColor(false)));
        //Boton siguiente > color siguiente
        btColorPost.setColorFilter(getResources().getColor(avatar.getNextColor(true)));
    }

    /**
     * Siempre que no sea el primer acceso,
     * si hay un cambio en el nombre o foto,
     * se muestran los botones deshacer y guardar.
     */
    private void comprobarCambios(){
        if(!primerAcceso) {
            int v = usuarioPerfil.getNombre().equals(etNombre.getText().toString())
                && usuarioPerfil.getFoto().equals(avatar.toString()) ?
                View.GONE : View.VISIBLE;
            btGuardar.setVisibility(v);
            btDeshacer.setVisibility(v);
        }
    }

    /**
     * Segun el usuario tenga o no el telefono vinculado,
     * se llama a la funcion correspondiente
     */
    private void pulsarVincularTlf() {
        if(usuarioPerfil.getSingleCredencial(Usuario.SignInMethod.PHONE)){
            guardarDatos();
            new ValidarUsuario(getActivity()).desvincularTelefono();
        }


        else //Se llama al fragment para introducir telefono
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, AuthPhoneFragment.class, null)
                    .addToBackStack(null)
                    .commit();
    }

    /**
     * Restaura los valores del EditText e ImageView
     * al nombre y foto del usuario sin cambiar.
     */
    private void pulsarDeshacer(){
        etNombre.setText(usuarioPerfil.getNombre());
        avatar = new Avatar(getContext(), usuarioPerfil.getFoto());
        comprobarCambios();
        refreshFoto();
    }

    /**
     * Si se ha hecho algun cambio (o es el primer acceso)
     * se modifica el usuario en la BD
     * y se vuelve al fragment Home
     */
    private void pulsarGuardar(){
        guardarDatos();
        ((_SuperActivity)getActivity()).colocarFragment(new HomeFragment());
    }

    private void guardarDatos(){
        if(btDeshacer.getVisibility() == View.VISIBLE || primerAcceso) {
            usuarioPerfil.setNombre(etNombre.getText().toString());
            usuarioPerfil.setFoto(avatar.toString());
            new UsuarioDAO().actualizarRegistro(usuarioPerfil);
        }
    }
}