package com.example.satori_by_aristo.Supervisor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.satori_by_aristo.R;

import java.util.List;

public class RegistroAdapter extends ArrayAdapter<Registro> {
    public RegistroAdapter(Context context, List<Registro> registros) {
        super(context, 0, registros);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.registros_adapter, parent, false);
        }

        Registro registro = getItem(position);

        TextView nombre = convertView.findViewById(R.id.nombreCompleto);
        TextView telefono = convertView.findViewById(R.id.telefono);
        TextView correo = convertView.findViewById(R.id.correo);

        //   Si uso=1, anteponer "ENVIADO " al nombre
        if (registro.getUso() == 1) {
            nombre.setText("ENVIADO " + registro.getNombre());
        } else {
            nombre.setText(registro.getNombre());
        }

        telefono.setText(registro.getTelefono());
        correo.setText(registro.getCorreo());

        return convertView;
    }
}