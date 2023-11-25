package com.example.weMee7.model;

import java.time.ZoneId;
import com.example.weMee7.comun.TimeUtils;
import com.example.weMee7.model.dao.InvitacionDAO;
import com.example.weMee7.model.dao.ReunionDAO;
import com.example.weMee7.model.dao.UsuarioDAO;
import com.example.weMee7.model.entities.Reunion;
import com.example.weMee7.model.entities.Usuario;
import com.example.weMee7.model.dao._SuperDAO.FirebaseCallback;

import java.time.ZonedDateTime;
import java.util.List;

public class TESTpruebasDAO {

    UsuarioDAO usuarioDAO = new UsuarioDAO();
    ReunionDAO reunionDAO = new ReunionDAO();
    InvitacionDAO invitacionDAO = new InvitacionDAO();
    List<Reunion> reuniones;

    Usuario a = new Usuario("usuario1","Daniel",null, Usuario.SignInMethod.EMAIL);
    Usuario b = new Usuario("usuario2","Andres",null, Usuario.SignInMethod.GOOGLE);
    Usuario c = new Usuario("usuario3","Carlos",null, Usuario.SignInMethod.PHONE);

    public void insertarDatos(){
        Reunion prueba= new Reunion(a.getId(),"Reunion de prueba",
                "Esto es una prueba","Playa de prueba",
                TimeUtils.fechaHoraToTimestamp("25/11/2023", "20:00"));

        reunionDAO.insertarRegistro(prueba);

//        usuarioDAO.insertarRegistro(a);
//        usuarioDAO.insertarRegistro(b);
//        usuarioDAO.insertarRegistro(c);
//
//        Reunion prueba = new Reunion(a.getId(),"Reunion de prueba",
//                "Esto es una prueba","Playa de prueba",
//                TimeUtils.generarDateTime("18/11/2023","20:00"));
//
//        reunionDAO.insertarRegistro(prueba);
//
//        Invitacion i1 = new Invitacion(b.getId(),prueba.getId());
//        Invitacion i2 = new Invitacion(c.getId(),prueba.getId());
//
//        invitacionDAO.insertarRegistro(i1);
//        invitacionDAO.insertarRegistro(i2);
    }

    public void consultarDatos(){

        reunionDAO.obtenerRegistroPorId("6iJr44J2ClaQdJy5qNeD", new FirebaseCallback() {
            @Override
            public void onCallback(Object resultado) {
                Reunion reunion = (Reunion) resultado;
                System.out.println(TimeUtils.timestampToFechaHora(reunion.getFechaHora(),true) + " - " +
                        TimeUtils.timestampToFechaHora(reunion.getFechaHora(),false));
            }
        });

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
        b.setNombre("Juan Alberto");
        usuarioDAO.actualizarRegistro(b);
    }
}
