package com.example.weMee7.comun;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weMee7.model.entities.Invitacion;
import com.example.weMee7.model.entities.Usuario;
import com.example.wemee7.R;

import java.util.List;
import java.util.Map;

import android.content.res.ColorStateList;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


public class InvitadosAdapter extends RecyclerView.Adapter<InvitadosAdapter.ViewHolder> {
    private final List<Usuario> uList;
    private final Map<String,Invitacion> iMap;
    private final boolean esModificable;
    private final Context context;
    private int ultimaFilaSeleccionada = -1;

    final InvitadosAdapter.OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Invitacion i);
    }

    public InvitadosAdapter(List<Usuario> uList, Map<String,Invitacion> iMap, boolean esModificable,
                                Context context, InvitadosAdapter.OnItemClickListener listener){
        this.uList = uList;
        this.iMap = iMap;
        this.esModificable = esModificable;
        this.context= context;
        this.listener = listener;
    }


    @NonNull
    @Override
    public InvitadosAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_invitado,parent,false);
        return new InvitadosAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InvitadosAdapter.ViewHolder holder, int position) {
        holder.bindData(position);
    }

    @Override
    public int getItemCount() {
        return uList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivEstadoInvitado;
        ImageView ivIconoInvitado;
        TextView tvNombreInvitado;
        Button btConfirmar;
        View llFondo;
        Usuario invitado;
        Invitacion i;
        int imagenEstado;
        String nombreInvitado;
        boolean seleccionada;
        ColorDrawable colorSeleccion;

        ViewHolder(View itemView) {
            super(itemView);
            ivEstadoInvitado = itemView.findViewById(R.id.ivInvitadoEstado);
            ivIconoInvitado = itemView.findViewById(R.id.ivInvitadoIcono);
            tvNombreInvitado = itemView.findViewById(R.id.tvInvitadoNombre);
            btConfirmar = itemView.findViewById(R.id.btInvitadoConfirmar);
            llFondo = itemView.findViewById(R.id.llInvitadoFondo);
        }

        void bindData(int position) {
            invitado = uList.get(position);
            nombreInvitado = invitado.getNombre();
            i = iMap.get(invitado.getId());
            colorSeleccion = new ColorDrawable(ContextCompat.getColor(context,
                    (i.getEstado() == Invitacion.EstadoInvitacion.RECHAZADA ?
                            R.color.avatar_verde : R.color.avatar_rojo)));
            filaNormal();

            if(esModificable && invitado.isActivo()){
                //Listener click en fila
                llFondo.setOnClickListener(view -> {
                    int filaSeleccionada = getAbsoluteAdapterPosition();
                    if (ultimaFilaSeleccionada == -1){
                        filaSeleccionada();
                    }else if (ultimaFilaSeleccionada == filaSeleccionada){
                        if(seleccionada)
                            filaNormal();
                        else
                            filaSeleccionada();
                    }else{
                        filaSeleccionada();
                        notifyItemChanged(ultimaFilaSeleccionada);
                    }
                    ultimaFilaSeleccionada = filaSeleccionada;
                });

                //Listener boton confirmar
                btConfirmar.setOnClickListener(v -> listener.onItemClick(i));
            }

        }

        private void filaNormal() {
            if(!invitado.isActivo())
                imagenEstado = R.drawable.icon_disabled;
            else if(i.getEstado() == Invitacion.EstadoInvitacion.ENVIADA)
                imagenEstado = R.drawable.icon_unknown;
            else if (i.getEstado() == Invitacion.EstadoInvitacion.ACEPTADA)
                imagenEstado = R.drawable.icon_accepted;
            else
                imagenEstado = R.drawable.icon_rejected;

            //Icono estado invitacion
            ivEstadoInvitado.setImageResource(imagenEstado);

            //Icono de usuario
            Avatar avatar = new Avatar(context, invitado.getFoto());
            ivIconoInvitado.setImageBitmap(avatar.toBitmap());
            ivIconoInvitado.setBackgroundTintList(ColorStateList.valueOf(
                    context.getResources().getColor(avatar.getColor())));

            //Nombre de usuario
            tvNombreInvitado.setText(nombreInvitado);
            int colorTexto = (invitado.isActivo() ? R.color.black : R.color.avatar_gris);
            tvNombreInvitado.setTextColor(context.getResources().getColor(colorTexto));

            if(!invitado.isActivo())
                tvNombreInvitado.setTypeface(null, Typeface.ITALIC);
            else if(esModificable)
                tvNombreInvitado.setPaintFlags(tvNombreInvitado.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            ivEstadoInvitado.setVisibility(View.VISIBLE);
            ivIconoInvitado.setVisibility(View.VISIBLE);

            //Boton OK desaparece
            btConfirmar.setVisibility(View.GONE);

            if(seleccionada){
                seleccionada = false;
                cambiarFondo();
            }
        }

        private void filaSeleccionada() {
            String mensaje = new StringBuilder(context.getResources()
                    .getString(imagenEstado == R.drawable.icon_rejected ?
                    R.string.tag_reenviar : R.string.tag_expulsar))
                    .append(" ")
                    .append(nombreInvitado)
                    .append("?").toString();
            ivEstadoInvitado.setVisibility(View.GONE);
            ivIconoInvitado.setVisibility(View.GONE);
            tvNombreInvitado.setText(mensaje);
            tvNombreInvitado.setTextColor(context.getResources().getColor(R.color.white));
            if(esModificable)
                tvNombreInvitado.setPaintFlags(tvNombreInvitado.getPaintFlags() & ~Paint.UNDERLINE_TEXT_FLAG);
            btConfirmar.setVisibility(View.VISIBLE);

            seleccionada = true;
            cambiarFondo();
        }

        private void cambiarFondo(){
            final ColorDrawable blanco =  new ColorDrawable(ContextCompat.getColor(context, R.color.white));
            ColorDrawable[] colores = {
                    (seleccionada ? blanco : colorSeleccion),
                    (seleccionada ? colorSeleccion : blanco)};

            TransitionDrawable transicion = new TransitionDrawable(colores);
            llFondo.setBackground(transicion);
            transicion.startTransition(300);
        }
    }
}
