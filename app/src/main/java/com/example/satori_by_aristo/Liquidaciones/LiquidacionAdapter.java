package com.example.satori_by_aristo.Liquidaciones;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.satori_by_aristo.R;

import java.util.List;

public class LiquidacionAdapter extends ArrayAdapter<Liquidacion> {

    public LiquidacionAdapter(Context context, List<Liquidacion> liquidaciones) {
        super(context, 0, liquidaciones);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Liquidacion liquidacion = getItem(position);
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.liquidacion_item, parent, false);

            holder = new ViewHolder();
            holder.tvFolio = convertView.findViewById(R.id.tvFolioLiquidacion);
            holder.tvOperador = convertView.findViewById(R.id.tvOperadorLiquidacion);
            holder.tvBono = convertView.findViewById(R.id.tvPagoLiquidacion);
            holder.tvResumen = convertView.findViewById(R.id.observacionesAnticipo); // l

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (liquidacion != null) {
            holder.tvFolio.setText("Folio: " + safeString(liquidacion.getIdFolio()));
            holder.tvOperador.setText("Operador: " + safeString(liquidacion.getOperador()));

            double bono = liquidacion.getBonoExt() != null ? liquidacion.getBonoExt().doubleValue() : 0.0;
            holder.tvBono.setText(String.format("Bono Extra: $%.2f", bono));

            // 🔹 Mostrar resumen si existe
            if (holder.tvResumen != null) {
                holder.tvResumen.setText(safeString(liquidacion.getResumen()));
            }

            // 🔹 Color condicional SOLO en el campo de bono
            if (bono > 0) {
                holder.tvBono.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.green_light));
            } else if (bono == 0) {
                holder.tvBono.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.yellow_light));
            } else {
                holder.tvBono.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.red_light));
            }
        }

        return convertView;
    }

    private static class ViewHolder {
        TextView tvFolio;
        TextView tvOperador;
        TextView tvBono;
        TextView tvResumen; // 🔹 nuevo campo
    }

    private String safeString(String value) {
        return value == null ? "" : value;
    }
}