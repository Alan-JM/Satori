package com.example.satori_by_aristo.Anticipos;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
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

         LinearLayout cardBackgroundLayout = convertView.findViewById(R.id.cardBackgroundLayout);

        TextView tvFolio = convertView.findViewById(R.id.tvFolioAnticipo);
        TextView tvUnidadOperador = convertView.findViewById(R.id.tvOperadorAnticipo);
        TextView tvImporte = convertView.findViewById(R.id.tvImporteAnticipo);

        if (anticipo != null) {
             tvFolio.setText("Folio: " + anticipo.getIdFolio());

             tvUnidadOperador.setText("Unidad: " + anticipo.getUnidadTrans()
                    + " - Operador: " + anticipo.getOperador());

             tvImporte.setText(String.format("$%.2f", anticipo.getImporte()));

             if (anticipo.getConfirmacion() != null) {
                if (anticipo.getConfirmacion() == 0) {
                    cardBackgroundLayout.setBackgroundResource(R.drawable.border_neon_gray);    // Gris = creado
                } else if (anticipo.getConfirmacion() == 1) {
                    cardBackgroundLayout.setBackgroundResource(R.drawable.border_neon_yellow);  // Amarillo = enviado
                } else if (anticipo.getConfirmacion() == 2) {
                    cardBackgroundLayout.setBackgroundResource(R.drawable.border_neon_green);   // Verde = autorizado
                } else if (anticipo.getConfirmacion() == 3) {
                    cardBackgroundLayout.setBackgroundResource(R.drawable.border_neon_red);     // Rojo = rechazado
                } else {
                    cardBackgroundLayout.setBackgroundResource(R.drawable.border_neon_gray);    // Default
                }
            } else {
                cardBackgroundLayout.setBackgroundResource(R.drawable.border_neon_gray);        // Sin estado
            }
        } else {
             tvFolio.setText("");
            tvUnidadOperador.setText("");
            tvImporte.setText("");
            if (cardBackgroundLayout != null) {
                cardBackgroundLayout.setBackgroundResource(R.drawable.border_neon_gray);
            }
        }

        return convertView;
    }
}
