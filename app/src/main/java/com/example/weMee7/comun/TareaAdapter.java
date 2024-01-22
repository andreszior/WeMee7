package com.example.weMee7.comun;

import static android.app.PendingIntent.getActivity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weMee7.model.dao.TareaDAO;
import com.example.weMee7.model.dao.UsuarioDAO;
import com.example.weMee7.model.entities.Tarea;
import com.example.weMee7.model.entities.Usuario;
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
        TextView tituloTarea, precioTarea, encargadoTarea;
        CheckBox checkboxTarea;


        ViewHolder(View itemView) {
            super(itemView);

            //Componentes
            tituloTarea = itemView.findViewById(R.id.tarea_title1);
            precioTarea = itemView.findViewById(R.id.item_precio_tarea);
            encargadoTarea = itemView.findViewById(R.id.item_tarea_usuario);
            checkboxTarea = itemView.findViewById(R.id.checkBox);


            //Comportamiento modificable de los componentes
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION){
                        listener.onItemClick(listaTareas.get(position));
                    }
                }
            });

            checkboxTarea.setOnCheckedChangeListener(((buttonView, isChecked) -> {
                position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listaTareas.get(position).setChecked(isChecked);
                    new TareaDAO().actualizarRegistro(listaTareas.get(position));
                }
            }));

        }


        void bindData(final Tarea tarea) {

            String precio = String.valueOf(tarea.getGasto());

            tituloTarea.setText(tarea.getTitulo());
            precioTarea.setText(precio);

            new UsuarioDAO().obtenerRegistroPorId(tarea.getIdEncargado(), resultado -> {
                Usuario encargadoTarea = (Usuario) resultado;
                this.encargadoTarea.setText(encargadoTarea.getNombre());
            });
            checkboxTarea.setChecked(tarea.isChecked());
        }


    }
}
