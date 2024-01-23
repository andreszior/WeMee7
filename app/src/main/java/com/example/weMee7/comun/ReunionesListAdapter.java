package com.example.weMee7.comun;

import static com.example.weMee7.view.usuario.ReunionesListFragment.LISTA_INVITACIONES;
import static com.example.weMee7.view.usuario.ReunionesListFragment.REUNIONES_ACTIVAS;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weMee7.model.dao.UsuarioDAO;
import com.example.weMee7.model.entities.Invitacion;
import com.example.weMee7.model.entities.Reunion;
import com.example.weMee7.model.entities.Usuario;
import com.example.wemee7.R;
import com.google.firebase.Timestamp;

import java.util.List;
import java.util.Map;

import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
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
        int cardLayout;
        if(tab == 0)
            cardLayout = R.layout.card_invitacion;
        else
            cardLayout = R.layout.card_reunion;
        View view = LayoutInflater.from(context).inflate(cardLayout,parent,false);
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
        ImageView item_image,creador_icon;
        TextView item_title, item_1, item_fecha, item_hora;
        View item_fondo;

        ViewHolder(View itemView){
            super(itemView);
            item_fondo = itemView.findViewById(R.id.reunion_fondo);
            item_image = itemView.findViewById(R.id.item_image);
            item_title = itemView.findViewById(R.id.item_title);
            item_1 = itemView.findViewById(tab == 0 ? R.id.item_creador :
                                                    R.id.item_lugar);
            item_fecha = itemView.findViewById(R.id.item_fecha);
            item_hora = itemView.findViewById(R.id.item_hora);
            if(tab == 0)
                creador_icon = itemView.findViewById(R.id.icon_creador);

        }
        void bindData(final Reunion item){
            //Seleccion de icono
            int imagen;
            if(tab == LISTA_INVITACIONES)
                imagen = R.drawable.tab_mail;
            else{
                if(iMap.get(item.getId()) == null){
                    imagen = R.drawable.event;
                    if(tab == REUNIONES_ACTIVAS)
                        item_image.setColorFilter(new PorterDuffColorFilter(
                                context.getResources().getColor(R.color.medium), PorterDuff.Mode.SRC_IN));
                }
                else
                    imagen = R.drawable.mail_accept;
            }

            item_image.setImageResource(imagen);


            //Titulo de reunion
            item_title.setText(item.getNombre());


            if(tab == 0){
                //Invitacion (creador y fecha de envio)
                new UsuarioDAO().obtenerRegistroPorId(item.getIdCreador(), resultado -> {
                    Usuario u = (Usuario)resultado;
                    item_1.setText(u.getNombre());
                    Avatar a = new Avatar(context,u.getFoto());
                    creador_icon.setImageBitmap(a.toBitmap());
                    creador_icon.setBackgroundTintList(ColorStateList.valueOf(
                            context.getResources().getColor(a.getColor())));
                    item_fondo.setVisibility(View.VISIBLE);
                });
                Timestamp envio = iMap.get(item.getId()).getFecha_envio();
                item_fecha.setText(TimeUtils.timestampToFechaHora(envio,true));
                item_hora.setText(TimeUtils.timestampToFechaHora(envio,false));

            }else {
                //Reunion (lugar y fecha)
                item_1.setText(item.getLugar());
                item_fecha.setText(item.obtenerFechaString());
                item_hora.setText(item.obtenerHoraString());
            }

            //Color sepia para reuniones pasadas
            if(tab == 2)
                item_fondo.setBackgroundTintList(ColorStateList.valueOf(
                        context.getResources().getColor(R.color.sepia)));

            itemView.setOnClickListener(v -> listener.onItemClick(item));
        }


    }
}
