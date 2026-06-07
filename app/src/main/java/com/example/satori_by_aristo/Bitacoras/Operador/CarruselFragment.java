package com.example.satori_by_aristo.Bitacoras.Operador;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.satori_by_aristo.R;

public class CarruselFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_carrusel, container, false);

        ViewPager2 viewPager = view.findViewById(R.id.viewPager);
        CarruselAdapter adapter = new CarruselAdapter(this);
        viewPager.setAdapter(adapter);

        Button btnOmitir = view.findViewById(R.id.btnOmitir);
        btnOmitir.setOnClickListener(v -> {
            // Ocultar carrusel y fondo difuminado
            requireActivity().findViewById(R.id.contenedorCarrusel).setVisibility(View.GONE);
            requireActivity().findViewById(R.id.fondoDifuminado).setVisibility(View.GONE);
        });

        return view;
    }
}
