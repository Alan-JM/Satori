package com.example.satori_by_aristo.Bitacoras.Operador;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.satori_by_aristo.R;

import java.util.List;

public class LiquidacionOpAdapter extends ArrayAdapter<Bitacora> {

    private Context context;
    private List<Bitacora> bitacoras;

    public LiquidacionOpAdapter(Context context, List<Bitacora> bitacoras) {
        super(context, R.layout.item_liquiop, bitacoras);
        this.context = context;
        this.bitacoras = bitacoras;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.item_liquiop, parent, false);
        }

        Bitacora b = bitacoras.get(position);

        TextView tvFolioFecha = convertView.findViewById(R.id.tvFolioFecha);
        TextView tvClienteDestino = convertView.findViewById(R.id.tvClienteDestino);
        TextView tvResumen = convertView.findViewById(R.id.tvResumen);

        // Folio | Fecha (usamos liquidacion como folio)
        tvFolioFecha.setText("Folio " + b.getLiquidacion() + " | " + b.getFecha());

        // Cliente → Destino
        tvClienteDestino.setText(b.getCliente() + " → " + b.getDestino());

        // Resumen desde API /liquidacion/{idFolio}
        if (b.getLiquidacion() != null && !b.getLiquidacion().isEmpty()) {
            cargarResumenVolley(b.getLiquidacion(), tvResumen);
        } else {
            tvResumen.setVisibility(View.GONE);
        }

        return convertView;
    }

    private void cargarResumenVolley(String folioLiquidacion, TextView tvResumen) {
        String url = context.getString(R.string.base_url) + "liquidacion/" + folioLiquidacion;

        RequestQueue queue = Volley.newRequestQueue(context);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    String resumen = response.optString("resumen", "");
                    if (!resumen.isEmpty()) {
                        tvResumen.setText(resumen);
                        tvResumen.setVisibility(View.VISIBLE);
                    } else {
                        tvResumen.setVisibility(View.GONE);
                    }
                },
                error -> tvResumen.setVisibility(View.GONE)
        );

        queue.add(request);
    }
}
