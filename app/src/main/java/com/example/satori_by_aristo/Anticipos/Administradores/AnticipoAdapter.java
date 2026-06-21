package com.example.satori_by_aristo.Anticipos.Administradores;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
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

        LinearLayout cardBackgroundLayout = convertView.findViewById(R.id.cardBackgroundLayout);

        TextView tvFolio = convertView.findViewById(R.id.tvFolioAnticipo);
        TextView tvOperador = convertView.findViewById(R.id.tvOperadorAnticipo);
        TextView tvImporte = convertView.findViewById(R.id.tvImporteAnticipo);

        if (anticipo != null) {
            tvFolio.setText("Folio: " + anticipo.getIdFolio());
            tvOperador.setText("Unidad: " + anticipo.getUnidadTrans() +
                    " - Operador: " + anticipo.getOperador());
            tvImporte.setText(String.format("Importe: $%.2f", anticipo.getImporte()));

            if (anticipo.getConfirmacion() != null) {
                switch (anticipo.getConfirmacion()) {
                    case 0: // creado
                        cardBackgroundLayout.setBackgroundResource(R.drawable.border_neon_gray);
                        break;
                    case 1: // enviado
                        cardBackgroundLayout.setBackgroundResource(R.drawable.border_neon_yellow);
                        break;
                    case 2: // autorizado
                        cardBackgroundLayout.setBackgroundResource(R.drawable.border_neon_green);
                        break;
                    case 3: // rechazado
                        cardBackgroundLayout.setBackgroundResource(R.drawable.border_neon_red);
                        break;
                    default:
                        cardBackgroundLayout.setBackgroundResource(R.drawable.border_neon_gray);
                        break;
                }
            } else {
                cardBackgroundLayout.setBackgroundResource(R.drawable.border_neon_gray);
            }
        } else {
            tvFolio.setText("");
            tvOperador.setText("");
            tvImporte.setText("");
            if (cardBackgroundLayout != null) {
                cardBackgroundLayout.setBackgroundResource(R.drawable.border_neon_gray);
            }
        }

        return convertView;
    }
}
