package com.example.satori_by_aristo.Bitacoras.Operador;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.satori_by_aristo.R;

public class Estadisticas extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_estadisticas, container, false);

        Button btnGeneral = view.findViewById(R.id.btnGeneral);
        Button btnPersonal = view.findViewById(R.id.btnPersonal);

        btnGeneral.setOnClickListener(v -> {
            Fragment generalFragment = new GeneralFragment();
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.contenedorDetalle, generalFragment)
                    .commit();
        });

        btnPersonal.setOnClickListener(v -> {
            Fragment personalFragment = new PersonalFragment();
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.contenedorDetalle, personalFragment)
                    .commit();
        });

        return view;
    }
}
