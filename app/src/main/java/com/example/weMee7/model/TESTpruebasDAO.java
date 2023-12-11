package com.example.weMee7.model;

import com.example.weMee7.comun.TimeUtils;
import com.example.weMee7.model.dao.InvitacionDAO;
import com.example.weMee7.model.dao.ReunionDAO;
import com.example.weMee7.model.dao.UsuarioDAO;
import com.example.weMee7.model.dao._SuperDAO;
import com.example.weMee7.model.entities.Invitacion;
import com.example.weMee7.model.entities.Reunion;
import com.example.weMee7.model.entities.Usuario;
import com.example.weMee7.model.dao._SuperDAO.FirebaseCallback;
import com.example.weMee7.viewmodel.InvitarUsuario;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TESTpruebasDAO {

    UsuarioDAO usuarioDAO = new UsuarioDAO();
    ReunionDAO reunionDAO = new ReunionDAO();
    InvitacionDAO invitacionDAO = new InvitacionDAO();
    List<Reunion> reuniones;
    String idUsuario1 = "2HRPfpJClDW8qKiggiRoPBW2Xyj1";
    String idUsuario2 = "e2dEtOs9PLbXnAkcD8DoJcbSVDz2";
    String idUsuario3 = "ebPWZb74ykfhZ5qsB7gSOQD5CnH2";
    String reunionPasada = "lAOS1n4zum6RCrEzQa4m";
    String reunionSinFecha = "VMgNVoRHrLEdSfDH8CS9";
    String reunionSinHora = "A7JozOiSzQpadQvPONhg";
    String reunionSinHora2 = "1KIBspSlWcSObp9cspDX";

    public void insertarDatos(){
//        Reunion r1= new Reunion(idUsuario1,"Reunion pasada",
//                "Esto es una prueba","Playa de prueba",
//                "25/07/2022", "20:00");
//        Reunion r2= new Reunion(idUsuario2,"Reunion sin fecha",
//                "Esto es una prueba","Playa de prueba",
//                null, "20:00");
//        Reunion r3= new Reunion(idUsuario1,"Reunion sin hora",
//                "Esto es una prueba","Playa de prueba",
//                "25/12/2023", null);
//        Reunion r4= new Reunion(idUsuario3,"Reunion sin hora",
//                "Esto es una prueba","Playa de prueba",
//                "02/01/2024", "12:40");
//
//        reunionDAO.insertarRegistro(r1);
//        reunionDAO.insertarRegistro(r2);
//        reunionDAO.insertarRegistro(r3);
//        reunionDAO.insertarRegistro(r4);
//        InvitarUsuario vm = new InvitarUsuario();
//        vm.enviarInvitacion(reunionPasada,idUsuario2);
//        vm.enviarInvitacion(reunionPasada,idUsuario3);
//        vm.enviarInvitacion(reunionSinFecha,idUsuario1);
//        vm.enviarInvitacion(reunionSinHora2,idUsuario1);


    }

    public void consultarDatos(){
        InvitacionDAO dao = new InvitacionDAO();

//        invitacionDAO.obtenerInvitacionesActivas(idUsuario1, true, new FirebaseCallback() {
//            @Override
//            public void onCallback(Object resultado) {
//                List<Invitacion> list = (List)resultado;
//                list.size();
//            }
//        });

//        invitacionDAO.obtenerRegistroPorId("ercqNdCccOOQjTYAPPAE", new FirebaseCallback() {
//            @Override
//            public void onCallback(Object resultado) {
//                Invitacion i = (Invitacion) resultado;
//                new InvitarUsuario().responderInvitacion(i,true);
//            }
//        });

//        reunionDAO.obtenerReunionesUsuario(idUsuario2, false, new FirebaseCallback() {
//            @Override
//            public void onCallback(Object resultado) {
//                List<Reunion> lista = (List)resultado;
//                Collections.sort(lista,null);
//                Collections.sort(lista,Collections.reverseOrder());
//                lista.size();
//            }
//        });

//        usuarioDAO.obtenerRegistroPorId("usuario1", new FirebaseCallback() {
//            @Override
//            public void onCallback(Object resultado) {
//                a = (Usuario) resultado;
//                reunionDAO.obtenerListaPorIdForaneo("idCreador", a.getId(), new FirebaseCallback() {
//                    @Override
//                    public void onCallback(Object resultado) {
//                        reuniones = (List) resultado;
//                        Log.d("Resultado",reuniones.size() + "");
//                    }
//                });
//            }
//        });
    }

    public void borrarDatos(){
        reunionDAO.borrarRegistro("o4InA6e0S7wTOTjMtTLj");
    }

    public void modificarDatos(){

    }
}
