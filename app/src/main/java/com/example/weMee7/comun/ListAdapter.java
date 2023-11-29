package com.example.weMee7.comun;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weMee7.model.entities.Reunion;
import com.example.wemee7.R;

import java.security.Timestamp;
import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {
    private List<Reunion> rdata;
    private LayoutInflater mInflater;
    private Context context;
    final ListAdapter.OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Reunion item);
    }

    public ListAdapter(List<Reunion> itemlist, Context context, ListAdapter.OnItemClickListener listener){
    this.mInflater=LayoutInflater.from(context);
    this.context= context;
    this.rdata= itemlist;
    this.listener = listener;
    }


    @NonNull
    @Override
    public ListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.card_menu,parent,false);
        return new ListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListAdapter.ViewHolder holder, int position) {
        holder.item_image.setImageResource(R.drawable.groups_fill0_wght400_grad0_opsz24);
        holder.bindData(rdata.get(position));
        holder.item_lugar.setText(rdata.get(position).getLugar());
        holder.item_title.setText(rdata.get(position).getNombre());

        // Convertir Timestamp a String
        String fechaHora = TimeUtils.timestampToFechaHora(rdata.get(position).getFechaHora(), true);
        holder.item_fecha.setText(fechaHora);
    }

    @Override
    public int getItemCount() {
        return rdata.size();
    }


    public void setItems(List<Reunion> items){rdata = items;}
    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView item_image;
        TextView item_title, item_lugar,item_fecha;

        ViewHolder(View itemView){
            super(itemView);
            item_image = itemView.findViewById(R.id.item_image);
            item_title = itemView.findViewById(R.id.item_title);
            item_lugar = itemView.findViewById(R.id.item_lugar);
            item_fecha = itemView.findViewById(R.id.item_fecha);
        }
        void bindData(final Reunion item){
            item_title.setText(item.getNombre());
            item_lugar.setText(item.getLugar());

            // Asegúrate de tener un objeto Timestamp válido para pasar al método.
            String ts = TimeUtils.timestampToFechaHora(item.getFechaHora(), false);
            item_fecha.setText(ts);
            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    listener.onItemClick(item);
                }
            });
        }


    }
}
