package com.example.weMee7.view.usuario;

import static com.example.weMee7.view.usuario.UsuarioActivity.LOGIN_KEY;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;
import androidx.security.crypto.MasterKeys;

import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.weMee7.comun.seguridad.SharedPref;
import com.example.weMee7.model.dao.UsuarioDAO;
import com.example.weMee7.model.entities.Usuario;
import com.example.weMee7.viewmodel.ValidarUsuario;
import com.example.wemee7.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.security.GeneralSecurityException;

/**
 * Fragment que se muestra cuando un usuario inicia sesion
 */
public class UserHomeFragment extends Fragment {

    private TextView id;
    private TextView nombre;
    private Button btVincular;

    private View contentView;
    private View cargandoView;
    private boolean telefonoVinculado;

    public UserHomeFragment() {
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
        View view = inflater.inflate(R.layout.fragment_user_home, container, false);
        contentView = view.findViewById(R.id.contentLayout);
        cargandoView = view.findViewById(R.id.pbUsuarioCargando);

        //Ocultar contenido y mostrar barra
        contentView.setVisibility(View.GONE);
        cargandoView.setVisibility(View.VISIBLE);

        id = view.findViewById(R.id.tvIdUsuario);
        nombre = view.findViewById(R.id.tvNombreUsuario);
        btVincular = view.findViewById(R.id.btVincularTelefono);
        view.findViewById(R.id.btCerrarSesion).setOnClickListener(v -> pulsarCerrarSesion());
        view.findViewById(R.id.btEliminarCuenta).setOnClickListener(v -> pulsarEliminarCuenta());
        btVincular.setOnClickListener(v -> pulsarVincularTlf());

        dataBinding();

        return view;
    }

    private void pulsarCerrarSesion() {
        new ValidarUsuario(getActivity()).cerrarSesion();
    }

    private void pulsarEliminarCuenta() {
        new ValidarUsuario(getActivity()).eliminarCuenta();
    }

    private void pulsarVincularTlf() {
        if(telefonoVinculado)
            new ValidarUsuario(getActivity()).desvincularTelefono();
        else
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fcvUsuario, AuthPhoneFragment.class, null)
                    .addToBackStack(null)
                    .commit();

    }

    private void dataBinding() {
        //Recuperar id del usuario
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        if(fUser != null){
            String idUser = fUser.getUid();
            id.setText(idUser);
            new UsuarioDAO().obtenerRegistroPorId(idUser, resultado -> {
                Usuario u = (Usuario)resultado;
                nombre.setText(u.getNombre());
                telefonoVinculado = u.getSingleCredencial(Usuario.SignInMethod.PHONE);
                btVincular.setText(telefonoVinculado ?
                        R.string.bt_desvincular_tlf : R.string.bt_vincular_tlf);
                cargandoView.setVisibility(View.GONE);
                contentView.setVisibility(View.VISIBLE);
            });
        }
    }
}