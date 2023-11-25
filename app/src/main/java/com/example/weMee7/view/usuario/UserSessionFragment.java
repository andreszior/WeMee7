package com.example.weMee7.view.usuario;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.weMee7.model.dao.UsuarioDAO;
import com.example.weMee7.model.dao._SuperDAO;
import com.example.weMee7.model.entities.Usuario;
import com.example.wemee7.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Fragment que se muestra cuando un usuario inicia sesion
 */
public class UserSessionFragment extends Fragment {

    private TextView id;
    private TextView nombre;

    public UserSessionFragment() {
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
        View view = inflater.inflate(R.layout.fragment_user_session, container, false);
        id = view.findViewById(R.id.tvIdUsuario);
        nombre = view.findViewById(R.id.tvNombreUsuario);
        dataBinding();

        return view;
    }

    private void dataBinding() {
        //Recuperar id del usuario
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        if(fUser != null){
            String idUser = fUser.getUid();
            id.setText(idUser);
            new UsuarioDAO().obtenerRegistroPorId(idUser, resultado -> {
                String nombreUser = ((Usuario)resultado).getNombre();
                nombre.setText(nombreUser);
            });
        }
    }
}