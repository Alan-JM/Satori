package com.example.satori_by_aristo.Bitacoras.Operador;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.satori_by_aristo.R;

public class ImagenFragment extends Fragment {

    private static final String ARG_POS = "pos";

    public static ImagenFragment newInstance(int pos) {
        ImagenFragment fragment = new ImagenFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_POS, pos);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_imagen, container, false);
        ImageView imageView = view.findViewById(R.id.imageView);

        int pos = getArguments().getInt(ARG_POS);
        switch (pos) {
            case 0: imageView.setImageResource(R.drawable.carruseluno); break;
            case 1: imageView.setImageResource(R.drawable.carruseldos); break;
            case 2: imageView.setImageResource(R.drawable.carruseltres); break;
        }

        return view;
    }
}
