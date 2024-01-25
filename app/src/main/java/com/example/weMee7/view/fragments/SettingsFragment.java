package com.example.weMee7.view.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.weMee7.viewmodel.ValidarUsuario;
import com.example.wemee7.R;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Fragmento que gestiona ajustes de la cuenta.
 * Actualmente, cerrar sesión y eliminar cuenta
 */
public class SettingsFragment extends Fragment {

    public SettingsFragment() {
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
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        //Mostrar email del usuario registrado
        ((TextView)view.findViewById(R.id.tvSettingsCuenta))
                .setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());

        //Asignar funciones a botones
        view.findViewById(R.id.btCerrarSesion).setOnClickListener(v-> pulsarCerrarSesion());
        view.findViewById(R.id.btEliminarCuenta).setOnClickListener(v-> pulsarEliminarCuenta());

        return view;
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
        new ValidarUsuario(getActivity()).deshabilitarCuenta();
    }
}