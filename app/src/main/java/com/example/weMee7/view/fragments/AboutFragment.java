package com.example.weMee7.view.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.fragment.app.Fragment;

import com.example.wemee7.R;

public class AboutFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_about, container, false);
        setupImageButton(view, R.id.btCreador1, "https://www.linkedin.com/in/andresmonvi/");
        setupImageButton(view, R.id.btCreador2, "https://es.linkedin.com/in/carlos-blanco-ferm%C3%ADn-b9ab13229");
        setupImageButton(view, R.id.btCreador3, "https://www.linkedin.com/in/daniel-liñera-aa6088295");
        setupImageButton(view, R.id.btgithub, "https://github.com/andreszior/WeMee7");

        return view;
    }

    private void setupImageButton(View view, int buttonId, String url) {
        ImageButton button = view.findViewById(buttonId);
        button.setOnClickListener(v -> openUrl(url));
    }

    private void openUrl(String url) {
        Uri link = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, link);
        startActivity(intent);
    }
}