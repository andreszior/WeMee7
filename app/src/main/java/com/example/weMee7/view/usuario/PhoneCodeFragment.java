package com.example.weMee7.view.usuario;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.weMee7.viewmodel.ValidarUsuario;
import com.example.wemee7.R;

/**
 * Fragmento que gestiona la validacion
 * del codigo de verificacion enviado por SMS
 */
public class PhoneCodeFragment extends Fragment {

    private static final String VERIFICATION_ID = "verificationId";

    ValidarUsuario validador;
    private String mVerificationId;
    private EditText etCodigo;

    public PhoneCodeFragment() {
        // Required empty public constructor
    }


    public static PhoneCodeFragment newInstance(String verificationId) {
        PhoneCodeFragment fragment = new PhoneCodeFragment();
        Bundle args = new Bundle();
        args.putString(VERIFICATION_ID, verificationId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mVerificationId = getArguments().getString(VERIFICATION_ID);
        }
        validador = new ValidarUsuario(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_phone_code, container, false);
        etCodigo = view.findViewById(R.id.etPhoneCodigo);
        view.findViewById(R.id.btAuthEnviarCodigo).setOnClickListener(v -> pulsarVerificarCodigo());

        return view;
    }

    private void pulsarVerificarCodigo() {
        String codigo = etCodigo.getText().toString();
        if(!codigo.isEmpty())
            validador.validarCodigo(mVerificationId,codigo);
    }
}