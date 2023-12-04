package com.example.weMee7.view.usuario;

import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
 * PROVISIONAL
 * Fragment que se muestra cuando un usuario inicia sesion.
 * Una vez ha iniciado sesion,
 * se puede recuperar el id del usuario a traves de Firebase:
 ** FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
 */
public class PerfilFragment extends Fragment {

    private Avatar avatar;
    private TextView nombre;
    private TextView cuenta;
    private ImageView foto;
    private Button btVincular;
    private boolean telefonoVinculado;

    public PerfilFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);

        //Mostrar animacion de carga (que se ocultara cuando se recuperen los datos de la BD)
        ((_SuperActivity)getActivity()).setCargando(true);

        nombre = view.findViewById(R.id.etPerfilNombre);
        cuenta = view.findViewById(R.id.tvPerfilCuenta);
        foto = view.findViewById(R.id.ivPerfilImagen);

        //Botones modificar avatar
        view.findViewById(R.id.btPerfilUpColor).setOnClickListener(v -> pulsarCambioAvatar(true,false));
        view.findViewById(R.id.btPerfilDownColor).setOnClickListener(v -> pulsarCambioAvatar(true,true));
        view.findViewById(R.id.btPerfilLeftImage).setOnClickListener(v -> pulsarCambioAvatar(false,false));
        view.findViewById(R.id.btPerfilRightImage).setOnClickListener(v -> pulsarCambioAvatar(false,true));

        btVincular = view.findViewById(R.id.btVincularTelefono);
        view.findViewById(R.id.btCerrarSesion).setOnClickListener(v -> pulsarCerrarSesion());
        view.findViewById(R.id.btEliminarCuenta).setOnClickListener(v -> pulsarEliminarCuenta());
        btVincular.setOnClickListener(v -> pulsarVincularTlf());

        dataBinding();

        return view;
    }

    /**
     * Recupera datos de la base de datos
     * y los muestra en los distintos componentes del layout
     */
    private void dataBinding() {
        //Recuperar id del usuario
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        if(fUser != null){
            String idUser = fUser.getUid();
            cuenta.setText(fUser.getEmail());
            new UsuarioDAO().obtenerRegistroPorId(idUser, resultado -> {
                Usuario u = (Usuario)resultado;
                nombre.setText(u.getNombre());
                avatar = new Avatar(getContext(),u.getFoto());
                refreshFoto();
                telefonoVinculado = u.getSingleCredencial(Usuario.SignInMethod.PHONE);
                btVincular.setText(telefonoVinculado ?
                        R.string.bt_desvincular_tlf : R.string.bt_vincular_tlf);
                //Aqui se oculta la animacion de carga
                ((_SuperActivity)getActivity()).ocultarCargando();
            });
        }
    }

    private void refreshFoto(){
        foto.setImageBitmap(avatar.toBitmap());
        foto.setBackgroundTintList(ColorStateList.valueOf(
                getResources().getColor(avatar.getColor())));
    }

    /**
     * Cierra la sesion actual de Firebase en el dispositivo
     */
    private void pulsarCerrarSesion() {
        new ValidarUsuario(getActivity()).cerrarSesion();
    }

    /**
     * Elimina al usuario de Firebase y de la base de datos
     */
    private void pulsarEliminarCuenta() {
        new ValidarUsuario(getActivity()).eliminarCuenta();
    }

    /**
     * Segun el usuario tenga o no el telefono vinculado,
     * se llama a la funcion correspondiente
     */
    private void pulsarVincularTlf() {
        if(telefonoVinculado)
            new ValidarUsuario(getActivity()).desvincularTelefono();

        else //Se llama al fragment para introducir telefono
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, AuthPhoneFragment.class, null)
                    .addToBackStack(null)
                    .commit();
    }

    private void pulsarCambioAvatar(boolean color, boolean next){
        if(color)
            avatar.setColor(next);
        else
            avatar.setImagen(next);
        refreshFoto();
    }

}