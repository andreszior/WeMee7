package com.example.weMee7.viewmodel;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.widget.Toast;

import com.example.weMee7.model.dao.InvitacionDAO;
import com.example.weMee7.model.dao.ReunionDAO;
import com.example.weMee7.model.dao.UsuarioDAO;
import com.example.weMee7.model.dao._SuperDAO;
import com.example.weMee7.model.entities.Invitacion;
import com.example.weMee7.model.entities.Reunion;
import com.example.weMee7.model.entities.Usuario;
import com.example.weMee7.model.entities._SuperEntity;
import com.example.wemee7.R;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.firestore.DocumentSnapshot;

public class InvitarUsuario {

    TaskCompletionSource<DocumentSnapshot> tcs;

    /**
     * Crea la invitacion,
     * e incorpora la reunion al usuario (reunionesList)
     * y el usuario a la reunion (invitadosList).
     * Devuelve un TaskCompletionSource para poder controlar
     * la finalización de esta tarea
     * @param idInvitado
     * @param idReunion
     */
    public TaskCompletionSource<DocumentSnapshot> enviarInvitacion(String idReunion, String idInvitado){
        tcs = new TaskCompletionSource<>();

        //Crear y registrar invitacion
        new InvitacionDAO().insertarRegistro(new Invitacion(idInvitado, idReunion), resultado -> {});

        //Registrar la reunion en el usuario
        registrarEnArray(new UsuarioDAO(),idInvitado,idReunion,true);

        //Registrar el usuario en la reunion
        registrarEnArray(new ReunionDAO(),idReunion,idInvitado,true);

        return tcs;
    }

    /**
     * Registra o borra un usuario/reunion
     * del array de una reunion/usuario
     * @param dao usuario / reunion dao de la coleccion a la que se accede
     * @param idDoc usuario / reunion a la que se agrega el item
     * @param idItem usuario / reunion que se agrega a la lista
     * @param registrar registrar / borrar
     */
    private void registrarEnArray(_SuperDAO dao, String idDoc, String idItem, boolean registrar){
        dao.obtenerRegistroPorId(idDoc, resultado ->{
            _SuperEntity e = (_SuperEntity)resultado;
            if(dao instanceof UsuarioDAO)
                if (registrar)
                    ((Usuario) e).addReunion(idItem);
                else
                    ((Usuario) e).delReunion(idItem);
            else
                if(registrar)
                    ((Reunion)e).addInvitado(idItem);
                else
                    ((Reunion)e).delInvitado(idItem);
            dao.actualizarRegistro(e);

            //Notifica que la reunion ha sido añadida al usuario
            if(dao instanceof UsuarioDAO)
                tcs.setResult(null);
        });
    }

    /** Elimina la invitacion de la BD,
     * asi como la reunion del usuario (reunionesList),
     * y el usuario de la reunion (invitadosList)
     * @param i
     */
    public void eliminarInvitacion(Invitacion i){
        String idUsuario = i.getIdUsuario();
        String idReunion = i.getIdReunion();

        //Eliminar invitacion
        new InvitacionDAO().borrarRegistro(i.getId());

        //Borrar la reunion en el usuario
        registrarEnArray(new UsuarioDAO(),idUsuario,idReunion,false);

        //Borrar el usuario en la reunion
        registrarEnArray(new ReunionDAO(),idReunion,idUsuario,false);
    }

    /**
     * Cambia el estado de la invitacion segun el parametro
     * @param i objeto Invitacion
     * @param aceptar true : ACEPTADA / false : RECHAZADA
     */
    public void responderInvitacion(Invitacion i, boolean aceptar){
        Invitacion.EstadoInvitacion estado = aceptar ?
                Invitacion.EstadoInvitacion.ACEPTADA :
                Invitacion.EstadoInvitacion.RECHAZADA;
        i.setEstado(estado);
        new InvitacionDAO().actualizarRegistro(i);
    }

    /**
     * Configura como yaCelebrada
     * las invitaciones cuya reunion ya ha pasado.
     * Si estaba ENVIADA, se considera RECHAZADA
     * @param i
     */
    public void actualizarInvitacion(Invitacion i){
        i.setYaCelebrada(true);
        if(i.getEstado() == Invitacion.EstadoInvitacion.ENVIADA)
            i.setEstado(Invitacion.EstadoInvitacion.RECHAZADA);
        new InvitacionDAO().actualizarRegistro(i);
    }

    /**
     * Genera un enlace, lo copia en el portapapeles
     * y muestra un mensaje al usuario
     * @param idReunion id de la reunion
     * @param c contexto
     */
    public void generarEnlaceInvitacion(String idReunion, Context c){
        // Servicio de portapapeles
        ClipboardManager clipboard = (ClipboardManager) c.getSystemService(Context.CLIPBOARD_SERVICE);

        // Copiar enlace con id reunion (dominio + idReunion)
        String enlace = c.getResources().getString(R.string.dominio) + idReunion;
        clipboard.setPrimaryClip(ClipData.newPlainText("tag", enlace));

        // Mensaje feedback
        Toast.makeText(c,
                c.getResources().getString(R.string.msj_enlace_invitacion),
                Toast.LENGTH_SHORT).show();
    }
}
