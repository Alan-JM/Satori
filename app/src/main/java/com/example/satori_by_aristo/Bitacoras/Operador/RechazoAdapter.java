package com.example.satori_by_aristo.Bitacoras.Operador;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.satori_by_aristo.R;
import com.example.satori_by_aristo.retrofit.RechazoDto;

import java.util.List;

public class RechazoAdapter extends ArrayAdapter<RechazoDto> {

    public RechazoAdapter(Context context, List<RechazoDto> rechazos) {
        super(context, 0, rechazos);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        RechazoDto rechazo = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.rechazos_operador_adapter, parent, false);
        }

        TextView tvIdFolio = convertView.findViewById(R.id.idFolio);
        TextView tvFecha = convertView.findViewById(R.id.fecha);
        TextView tvMotivo = convertView.findViewById(R.id.motivo);

        if (rechazo != null) {
            tvIdFolio.setText("Folio: " + rechazo.getId());
                 tvFecha.setText("Telefono: " + rechazo.getTelefonoOp() );
            tvMotivo.setText("Motivo: " + rechazo.getMotivo());
        }

        return convertView;
    }
}