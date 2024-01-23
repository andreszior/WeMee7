package com.example.weMee7.comun;

import static android.app.PendingIntent.getActivity;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weMee7.model.dao.TareaDAO;
import com.example.weMee7.model.dao.UsuarioDAO;
import com.example.weMee7.model.entities.Tarea;
import com.example.weMee7.model.entities.Usuario;
import com.example.weMee7.view.usuario.UsuarioActivity;
import com.example.wemee7.R;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class TareaAdapter extends RecyclerView.Adapter<TareaAdapter.ViewHolder> {

    private List<Tarea> listaTareas;
    Context context;
    int position;
    final TareaAdapter.OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Tarea item);
    }



    public TareaAdapter(List<Tarea> tareaList, Context context, OnItemClickListener listener) {
        this.context = context;
        listaTareas = tareaList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TareaAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_tarea, null);
        return new TareaAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TareaAdapter.ViewHolder holder, int position) {
        holder.bindData(listaTareas.get(position));
    }

    @Override
    public int getItemCount() {
        return listaTareas.size();
    }

    public void setListaTareas(List<Tarea> items) {
        listaTareas = items;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTituloTarea, tvFechaTarea, tvNombreEncargado, tvGastoTarea;
        ImageView ivAvatarEncargado;
        CheckBox cbRealizada;
        CardView tareaFondo;


        ViewHolder(View itemView) {
            super(itemView);
            tvTituloTarea = itemView.findViewById(R.id.tvTareaTitulo);
            tvFechaTarea  = itemView.findViewById(R.id.tvTareaFecha);
            tvGastoTarea = itemView.findViewById(R.id.tvTareaGasto);
            tvNombreEncargado = itemView.findViewById(R.id.tvEncargadoNombre);
            ivAvatarEncargado = itemView.findViewById(R.id.ivEncargadoIcono);
            cbRealizada = itemView.findViewById(R.id.checkRealizada);
            tareaFondo = itemView.findViewById(R.id.tarea_fondo);

        }

        void bindData(final Tarea tarea) {
            //Datos de tarea
            tvTituloTarea.setText(tarea.getTitulo());
            tvFechaTarea.setText(TimeUtils.timestampToFecha(tarea.getFecha_update()));
            tvGastoTarea.setText(tarea.obtenerGastoString());

            //Datos de usuario encargado
            boolean esCreador = ((UsuarioActivity)context).esCreador();
            if(tarea.getEstado() == Tarea.EstadoTarea.CREADA){
                //La tarea todavía no está asignada
                tvNombreEncargado.setText(R.string.text_sin_asignar);
                cbRealizada.setChecked(false);
                if(esCreador)
                    setListenerCheckBox(tarea);
            }else{
                //La tarea ya esta asignada o completada
                new UsuarioDAO().obtenerRegistroPorId(tarea.getIdEncargado(), resultado ->{
                    Usuario encargado = (Usuario)resultado;
                    tvNombreEncargado.setText(encargado.getNombre());

                    Avatar avatar = new Avatar(context, encargado.getFoto());
                    ivAvatarEncargado.setImageBitmap(avatar.toBitmap());
                    ivAvatarEncargado.setBackgroundTintList(ColorStateList.valueOf(
                            context.getResources().getColor(avatar.getColor())));

                    boolean completada = tarea.getEstado() == Tarea.EstadoTarea.COMPLETADA;

                    cbRealizada.setChecked(completada);
                    if(completada)
                        tareaFondo.setCardBackgroundColor(context.getResources().getColor(R.color.task_done));

                    boolean esEncargado = FirebaseAuth.getInstance().getCurrentUser().getUid().equals(encargado.getId());
                    if(esEncargado || (esCreador && tarea.getEstado() != Tarea.EstadoTarea.COMPLETADA))
                        setListenerCheckBox(tarea);
                });
            }

            //Evento onClick sobre la fila
            itemView.setOnClickListener(v -> {
                position = getAdapterPosition();
                if(position != RecyclerView.NO_POSITION){
                    listener.onItemClick(listaTareas.get(position));
                }
            });
        }

        private void setListenerCheckBox(Tarea tarea){
            cbRealizada.setOnCheckedChangeListener(((buttonView, isChecked) -> {
                if(getAbsoluteAdapterPosition() == RecyclerView.NO_POSITION)
                    return;

                //Si no estaba asignada y la completa el creador, se la asigna
                if(isChecked && tarea.getEstado() == Tarea.EstadoTarea.CREADA){
                    String idCreador = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    tarea.setIdEncargado(idCreador);
                    new UsuarioDAO().obtenerRegistroPorId(idCreador, resultado -> {
                        tvNombreEncargado.setText(((Usuario)resultado).getNombre());
                    });
                }

                //Actualizar fecha
                tarea.setFecha_update(Timestamp.now());
                tvFechaTarea.setText(TimeUtils.timestampToFecha(tarea.getFecha_update()));

                //Cambio de estado
                tarea.setEstado(isChecked ?
                        Tarea.EstadoTarea.COMPLETADA :
                        Tarea.EstadoTarea.ASIGNADA);

                //Persistir cambio
                new TareaDAO().actualizarRegistro(tarea);
                pintarFondo(isChecked);
            }));

        }

        private void pintarFondo(boolean isChecked){
            int light = context.getResources().getColor(R.color.light);
            int done = context.getResources().getColor(R.color.task_done);
            int[] colores = {
                    isChecked ? light : done,
                    isChecked ? done : light
            };

            ValueAnimator animadorColor = ValueAnimator.ofArgb(colores[0], colores[1]);

            animadorColor.addUpdateListener(animation -> {
                tareaFondo.setCardBackgroundColor((int) animation.getAnimatedValue());
            });

            // Establece la duración de la animación
            animadorColor.setDuration(300);

            // Inicia la animación
            animadorColor.start();
        }
    }
}
