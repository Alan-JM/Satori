package com.example.satori_by_aristo.Bitacoras.Operador;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.satori_by_aristo.R;
import java.util.List;

public class BitacoraAdapter extends ArrayAdapter<Bitacora> {

    public BitacoraAdapter(Context context, List<Bitacora> bitacoras) {
        super(context, 0, bitacoras);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Bitacora bitacora = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.bitacora_itemodel, parent, false);
        }

        TextView tvFecha = convertView.findViewById(R.id.tvFecha);
        TextView tvOperador = convertView.findViewById(R.id.tvOperador);
        TextView tvCliente = convertView.findViewById(R.id.tvCliente);

        if (bitacora != null) {
            tvFecha.setText(bitacora.getFecha());
            tvOperador.setText("Op: " + bitacora.getOperador() + " | Eco: " + bitacora.getEco());
            tvCliente.setText(bitacora.getCliente() + " → " + bitacora.getDestino());

             if (bitacora.getConfirmacion() == 2) {
                convertView.setBackgroundColor(Color.YELLOW);   // pendiente admin
            } else if (bitacora.getConfirmacion() == 3) {
                convertView.setBackgroundColor(Color.GREEN);    // autorizado
            } else if (bitacora.getConfirmacion() == 4) {
                convertView.setBackgroundColor(Color.RED);      // rechazado
            } else {
                convertView.setBackgroundColor(Color.LTGRAY);   // otros estados
            }
        }

        return convertView;
    }
}