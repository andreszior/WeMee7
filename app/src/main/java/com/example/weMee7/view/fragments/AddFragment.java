package com.example.weMee7.view.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.weMee7.comun.InputControl;
import com.example.weMee7.comun.TimeUtils;
import com.example.weMee7.model.entities.Reunion;
import com.example.weMee7.view.activity._SuperActivity;
import com.example.weMee7.view.activity.UsuarioActivity;
import com.example.weMee7.viewmodel.GestionarDatos;
import com.example.wemee7.R;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

public class AddFragment extends Fragment {
    EditText etNombre;
    EditText etDescrip;
    EditText etLugar;
    EditText etFecha;
    EditText etHora;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        etNombre = view.findViewById(R.id.etAddNombre);
        etDescrip = view.findViewById(R.id.etAddDescripcion);
        etLugar = view.findViewById(R.id.etAddLugar);
        etFecha = view.findViewById(R.id.etAddFecha);
        etHora =  view.findViewById(R.id.etAddHora);
        etFecha.setFocusable(false);
        etFecha.setClickable(true);
        etHora.setFocusable(false);
        etHora.setClickable(true);
        etFecha.setOnClickListener(v -> TimeUtils.addSelectFecha(requireActivity(),etFecha));
        etHora.setOnClickListener(v -> TimeUtils.addSelectHora(requireActivity(),etHora));

        view.findViewById(R.id.btAddReunion).setOnClickListener(v -> crearReunion());
    }
    public void crearReunion() {
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        if (fUser != null) {
            String idCreador = fUser.getUid();
            String nombreReunion = etNombre.getText().toString();
            String descripcionReunion = etDescrip.getText().toString();
            String lugarReunion = etLugar.getText().toString();
            String fechaReunion = etFecha.getText().toString();
            String horaReunion = etFecha.getText().toString();

            // Comprobar que todos los campos están rellenos
            if (!InputControl.todoCumplimentado(new String[]{nombreReunion})){
                ((_SuperActivity)requireActivity()).lanzarMensaje(R.string.msj_nombre_reunion);
                return;
            }

            if(lugarReunion.isEmpty()) lugarReunion = getResources().getString(R.string.text_undefined);

            //Crear reunion
            Reunion reunion = new Reunion(idCreador, nombreReunion,
                    descripcionReunion, lugarReunion, fechaReunion, horaReunion);

            //Persistir en Firebase
            GestionarDatos vmDatos = new GestionarDatos();
            TaskCompletionSource<DocumentSnapshot> tcs =
                    vmDatos.crearNuevaReunion(reunion);

            if (tcs != null) {
                //Cuando termine la tarea, se muestra el ReunionFragment
                Task<DocumentSnapshot> getTask = tcs.getTask();
                getTask.addOnSuccessListener(documentSnapshot -> {
                    UsuarioActivity activity = (UsuarioActivity) requireActivity();
                    activity.setIdReunionActual(reunion.getId());
                    activity.colocarFragment(new ReunionFragment());
                });
            }
        }
    }
}