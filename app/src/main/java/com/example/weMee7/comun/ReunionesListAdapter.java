package com.example.weMee7.comun;

import static com.example.weMee7.view.usuario.ReunionesListFragment.LISTA_INVITACIONES;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weMee7.model.entities.Invitacion;
import com.example.weMee7.model.entities.Reunion;
import com.example.wemee7.R;

import java.util.List;
import java.util.Map;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


public class ReunionesListAdapter extends RecyclerView.Adapter<ReunionesListAdapter.ViewHolder> {
    private final List<Reunion> rList;
    private final Map<String,Invitacion> iMap;
    private final Context context;
    private final int tab;//Pestaña del ViewPager
    final ReunionesListAdapter.OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Reunion item);
    }

    public ReunionesListAdapter(List<Reunion> rList, Map<String,Invitacion> iMap, int tab,
                                Context context, ReunionesListAdapter.OnItemClickListener listener){
        this.context= context;
        this.tab = tab;
        this.rList = rList;
        this.iMap = iMap;
        this.listener = listener;
    }


    @NonNull
    @Override
    public ReunionesListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_menu,parent,false);
        return new ReunionesListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReunionesListAdapter.ViewHolder holder, int position) {
        holder.bindData(rList.get(position));
    }

    @Override
    public int getItemCount() {
        return rList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView item_image;
        TextView item_title, item_lugar, item_fecha;

        ViewHolder(View itemView){
            super(itemView);
            item_image = itemView.findViewById(R.id.item_image);
            item_title = itemView.findViewById(R.id.item_title);
            item_lugar = itemView.findViewById(R.id.item_lugar);
            item_fecha = itemView.findViewById(R.id.item_fecha);
        }
        void bindData(final Reunion item){
            int imagen;
            if(tab == LISTA_INVITACIONES)
                imagen = R.drawable.tab_mail;
            else{
                if(iMap.get(item.getId()) == null)
                    imagen = R.drawable.event;
                else
                    imagen = R.drawable.mail_accept;
            }
            item_image.setImageResource(imagen);
            item_title.setText(item.getNombre());
            item_lugar.setText(item.getLugar());
            item_fecha.setText(item.obtenerFechaString());

            itemView.setOnClickListener(v -> listener.onItemClick(item));
        }


    }
}
