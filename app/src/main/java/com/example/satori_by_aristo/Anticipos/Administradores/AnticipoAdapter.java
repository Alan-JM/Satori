package com.example.satori_by_aristo.Anticipos.Administradores;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.satori_by_aristo.Anticipos.Anticipo;
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
        TextView tvOperador = convertView.findViewById(R.id.tvOperadorAnticipo);
        TextView tvImporte = convertView.findViewById(R.id.tvImporteAnticipo);

        if (anticipo != null) {
            // Mostrar folio
            tvFolio.setText("Folio: " + anticipo.getIdFolio());

            // Mostrar unidadTrans y operador
            tvOperador.setText("Unidad: " + anticipo.getUnidadTrans() +
                    " - Operador: " + anticipo.getOperador());

            // Mostrar importe
            tvImporte.setText(String.format("Importe: $%.2f", anticipo.getImporte()));

            // Color de fondo según confirmación
            if (anticipo.getConfirmacion() != null) {
                switch (anticipo.getConfirmacion()) {
                    case 2: // autorizado
                        convertView.setBackgroundColor(Color.parseColor("#4CAF50")); // verde
                        break;
                    case 3: // rechazado
                        convertView.setBackgroundColor(Color.parseColor("#9E9E9E")); // gris
                        break;
                    default: // creado o enviado
                        convertView.setBackgroundColor(Color.parseColor("#F44336")); // rojo
                        break;
                }
            } else {
                convertView.setBackgroundColor(Color.parseColor("#F44336")); // rojo por defecto
            }
        }

        return convertView;
    }
}
