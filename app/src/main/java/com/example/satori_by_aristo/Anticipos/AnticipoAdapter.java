package com.example.satori_by_aristo.Anticipos;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.satori_by_aristo.R;

import java.util.List;

public class AnticipoAdapter extends ArrayAdapter<Anticipo> {

    public AnticipoAdapter(Context context, List<Anticipo> anticipos) {
        super(context, 0, anticipos);
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Anticipo anticipo = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.anticipo_item, parent, false);
        }

        TextView tvFolio = convertView.findViewById(R.id.tvFolioAnticipo);
        TextView tvUnidadOperador = convertView.findViewById(R.id.tvOperadorAnticipo);
        TextView tvImporte = convertView.findViewById(R.id.tvImporteAnticipo);

        if (anticipo != null) {
            // 🔹 Mostrar idFolio
            tvFolio.setText("Folio: " + anticipo.getIdFolio());

            // 🔹 Mostrar unidadTrans y operador
            tvUnidadOperador.setText("Unidad: " + anticipo.getUnidadTrans()
                    + " - Operador: " + anticipo.getOperador());

            // 🔹 Mostrar importe
            tvImporte.setText(String.format("$%.2f", anticipo.getImporte()));

            // 🔹 Color de fondo según confirmación usando ifs
            if (anticipo.getConfirmacion() != null) {
                if (anticipo.getConfirmacion() == 0) {
                    convertView.setBackgroundColor(Color.parseColor("#9E9E9E")); // Gris = creado
                } else if (anticipo.getConfirmacion() == 1) {
                    convertView.setBackgroundColor(Color.parseColor("#FFEB3B")); // Amarillo = enviado
                } else if (anticipo.getConfirmacion() == 2) {
                    convertView.setBackgroundColor(Color.parseColor("#4CAF50")); // Verde = autorizado
                } else if (anticipo.getConfirmacion() == 3) {
                    convertView.setBackgroundColor(Color.parseColor("#F44336")); // Rojo = rechazado
                } else {
                    convertView.setBackgroundColor(Color.TRANSPARENT); // Default
                }
            } else {
                convertView.setBackgroundColor(Color.TRANSPARENT); // Sin estado
            }
        } else {
            // Si el anticipo es null, limpiar vista
            tvFolio.setText("");
            tvUnidadOperador.setText("");
            tvImporte.setText("");
            convertView.setBackgroundColor(Color.TRANSPARENT);
        }

        return convertView;
    }
}
