package com.example.weMee7.comun;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.weMee7.model.entities.Usuario;
import com.example.wemee7.R;

import java.util.List;

public class EncargadosAdapter extends ArrayAdapter<Usuario> {
    public EncargadosAdapter(@NonNull Context context, @NonNull List<Usuario> objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_encargado,parent,false);

        ImageView ivIconoEncargado = convertView.findViewById(R.id.ivEncargadoIcono);
        TextView tvNombreEncargado = convertView.findViewById(R.id.tvEncargadoNombre);

        if(position == 0){
            tvNombreEncargado.setText(R.string.text_sin_asignar);
            ivIconoEncargado.setVisibility(View.INVISIBLE);
        }else{
            position--;
            Usuario usuario = getItem(position);
            tvNombreEncargado.setText(usuario.getNombre());
            Avatar avatar = new Avatar(getContext(), usuario.getFoto());
            ivIconoEncargado.setImageBitmap(avatar.toBitmap());
            ivIconoEncargado.setBackgroundTintList(ColorStateList.valueOf(
                    getContext().getResources().getColor(avatar.getColor())));
        }

        return convertView;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_encargado,parent,false);

        Resources r = getContext().getResources();

        ImageView ivIconoEncargado = convertView.findViewById(R.id.ivEncargadoIcono);
        TextView tvNombreEncargado = convertView.findViewById(R.id.tvEncargadoNombre);
        convertView.findViewById(R.id.item_border).setBackgroundColor(r.getColor(R.color.avatar_azul));
        convertView.findViewById(R.id.item_fondo).setBackgroundColor(r.getColor(R.color.light));

        if(position == 0){
            tvNombreEncargado.setText(R.string.text_sin_asignar);
        }else{
            position--;
            Usuario usuario = getItem(position);
            tvNombreEncargado.setText(usuario.getNombre());

        }
        ivIconoEncargado.setVisibility(View.INVISIBLE);

        return convertView;
    }

    @Override
    public int getCount() {
        return super.getCount() + 1;
    }
}
