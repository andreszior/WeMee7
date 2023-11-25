package com.example.weMee7.view.usuario;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.weMee7.viewmodel.ValidarUsuario;
import com.example.wemee7.R;

/**
 * Fragmento que gestiona
 * el registro del telefono del usuario
 * en Firebase Authentication
 */
public class AuthPhoneFragment extends Fragment {

    EditText etTelefono;
    ValidarUsuario validador;

    public AuthPhoneFragment() {
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
        View view = inflater.inflate(R.layout.fragment_auth_phone, container, false);
        etTelefono = view.findViewById(R.id.etAuthTelefono);
        view.findViewById(R.id.btAuthSolicitarCodigo).setOnClickListener(v -> pulsarSolicitarCodigo());

        return view;
    }

    public void pulsarSolicitarCodigo(){
        String telefono = etTelefono.getText().toString();
        if(!telefono.isEmpty())
            validador.registrarConTelefono(telefono);
    }

}