package com.example.weMee7.comun;

import static android.app.PendingIntent.getActivity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weMee7.comun.seguridad.SharedPref;
import com.example.weMee7.model.dao.TareaDAO;
import com.example.weMee7.model.dao.UsuarioDAO;
import com.example.weMee7.model.entities.Tarea;
import com.example.weMee7.model.entities.Usuario;
import com.example.weMee7.view._SuperActivity;
import com.example.wemee7.R;

import java.util.List;

public class TareaAdapter extends RecyclerView.Adapter<TareaAdapter.ViewHolder> {

    private List<Tarea> listaTareas;
    private LayoutInflater layoutInflater;
    Context context;


    int position;


    final TareaAdapter.OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Tarea item);
    }



    public TareaAdapter(List<Tarea> tareaList, Context context,   OnItemClickListener listener) {
        layoutInflater = LayoutInflater.from(context);
        this.context = context;
        listaTareas = tareaList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TareaAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.card_tarea, null);
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
        TextView tareaTitle, precioTarea, personaAsingada;
        ImageView deleteImage;
        Button editarTareaBoton;
        CheckBox tareaCheckBox;


        ViewHolder(View itemView) {
            super(itemView);
            tareaTitle = itemView.findViewById(R.id.tarea_title1);
            precioTarea = itemView.findViewById(R.id.item_precio_tarea);
            personaAsingada = itemView.findViewById(R.id.item_tarea_usuario);

            tareaCheckBox = itemView.findViewById(R.id.checkBox);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION){
                        listener.onItemClick(listaTareas.get(position));
                    }
                }
            });


            //Revisar esto porque el checkbox no se guarda en el firebase, por ende,
            //usuarios que no lo ponen, no veran si esta selañado o no

            tareaCheckBox.setOnCheckedChangeListener(((buttonView, isChecked) -> {
                position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listaTareas.get(position).setChecked(isChecked);
                    new TareaDAO().actualizarRegistro(listaTareas.get(position));
                }
            }));

        }

        void bindData(final Tarea tarea) {
            tareaTitle.setText(tarea.getTitulo());

            String precio = String.valueOf(tarea.getGasto());
            precioTarea.setText(precio);

            new UsuarioDAO().obtenerRegistroPorId(tarea.getIdEncargado(), resultado -> {
                Usuario encargadoTarea = (Usuario) resultado;
                personaAsingada.setText(encargadoTarea.getNombre());
            });

            tareaCheckBox.setChecked(tarea.isChecked());

        }


    }
}
