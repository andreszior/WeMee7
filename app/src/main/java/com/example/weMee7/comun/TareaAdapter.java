package com.example.weMee7.comun;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weMee7.model.entities.Tarea;
import com.example.wemee7.R;

import java.util.List;

public class TareaAdapter extends RecyclerView.Adapter<TareaAdapter.ViewHolder> {

    private List<Tarea> listaTareas;
    private LayoutInflater layoutInflater;
    Context context;

    public TareaAdapter(List<Tarea> tareaList, Context context){
        layoutInflater = LayoutInflater.from(context);
        this.context = context;
        listaTareas = tareaList;

    }

    @NonNull
    @Override
    public TareaAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.fragment_tarea, null);
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

    public void setListaTareas(List<Tarea> items){
        listaTareas = items;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView tareaTitle, fechaTarea, personaAsingada;
        ImageView image;

        ViewHolder(View itemView){
            super(itemView);
            //tareaTitle = itemView.findViewById(R.id.tarea_title);
            //fechaTarea = itemView.findViewById(R.id.item_fecha_tarea);
            //personaAsingada = itemView.findViewById(R.id.item_tarea_usuario);
        }

        void bindData(final Tarea tarea){
            tareaTitle.setText(tarea.getDescripcion());
            //fechaTarea.setText();
            personaAsingada.setText(tarea.getIdEncargado());
            personaAsingada.setText(tarea.getIdEncargado());
        }

    }
}
