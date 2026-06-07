package com.example.satori_by_aristo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class LinersitoAdapter extends ArrayAdapter<String> {

    public LinersitoAdapter(@NonNull Context context, @NonNull List<String> operadores) {
        super(context, 0, operadores);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.linersito, parent, false);
        }

        String operador = getItem(position);

        TextView nombreOperador = convertView.findViewById(R.id.nombreOperador);
        ImageView enviado = convertView.findViewById(R.id.enviado);
        ImageView oprogreso = convertView.findViewById(R.id.oprogreso);
        ImageView lapiz = convertView.findViewById(R.id.lapiz);
        ImageView bote = convertView.findViewById(R.id.bote);

        // Asignar nombre
        nombreOperador.setText(operador);

        // Ejemplo: cambiar íconos según posición
        switch (position % 4) {
            case 0:
                enviado.setImageResource(R.drawable.doblecirculo);
                oprogreso.setImageResource(R.drawable.triangulor);
                break;
            case 1:
                enviado.setImageResource(R.drawable.circulo);
                oprogreso.setImageResource(R.drawable.triangulo);
                break;
            case 2:
                enviado.setImageResource(R.drawable.triangulor);
                oprogreso.setImageResource(R.drawable.tache);
                break;
            case 3:
                enviado.setImageResource(R.drawable.circulo);
                oprogreso.setImageResource(R.drawable.doblecirculo);
                break;
        }

        // Click en lápiz "Editar"
        lapiz.setOnClickListener(v ->
                Toast.makeText(getContext(), "Editar", Toast.LENGTH_SHORT).show()
        );

        // Click en bote "Borrar"
        bote.setOnClickListener(v ->
                Toast.makeText(getContext(), "Borrar", Toast.LENGTH_SHORT).show()
        );

        return convertView;
    }
}