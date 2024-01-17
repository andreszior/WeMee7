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

    private SharedPref sharedPref;

    int position;

    private _SuperActivity superActivity;

    final TareaAdapter.OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Tarea item);
    }



    public TareaAdapter(List<Tarea> tareaList, Context context, SharedPref sharedPref,  OnItemClickListener listener) {
        layoutInflater = LayoutInflater.from(context);
        this.context = context;
        listaTareas = tareaList;
        this.sharedPref = sharedPref;
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

        holder.bindData(listaTareas.get(position), position);
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
            /*
            tareaCheckBox.setOnCheckedChangeListener(((buttonView, isChecked) -> {
                position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listaTareas.get(position).setChecked(tareaCheckBox.isChecked());
                    sharedPref.setCheckboxState(position, isChecked);
                }
            }));

             */

            editarTareaBoton = itemView.findViewById(R.id.editarboton_tarea);
            editarTareaBoton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Fragment selectedFragment = new TareaFragment();
                    position = getAdapterPosition();
                    if (listener != null) {
                        listener.onItemClick(listaTareas.get(position));
                    }
                    //dialog.dismiss();

                }
            });


            deleteImage = itemView.findViewById(R.id.delete_icon_image);
            deleteImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION){
                        new TareaDAO().borrarRegistro(listaTareas.get(position).getId());
                        notifyItemRemoved(position);
                    }
                }
            });

        }

        void bindData(final Tarea tarea, int pos) {
            tareaTitle.setText(tarea.getTitulo());

            String precio = String.valueOf(tarea.getGasto());
            precioTarea.setText(precio);

            new UsuarioDAO().obtenerRegistroPorId(tarea.getIdEncargado(), resultado -> {
                Usuario encargadoTarea = (Usuario) resultado;
                personaAsingada.setText(encargadoTarea.getNombre());
            });

            boolean checkBoxState = sharedPref.getCheckboxState(pos);
            tareaCheckBox.setChecked(checkBoxState);

        }


    }
}
