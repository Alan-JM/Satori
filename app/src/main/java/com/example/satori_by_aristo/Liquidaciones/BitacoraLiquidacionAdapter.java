package com.example.satori_by_aristo.Liquidaciones;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.satori_by_aristo.R;
import com.example.satori_by_aristo.Bitacoras.Operador.Bitacora;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public class BitacoraLiquidacionAdapter extends android.widget.BaseAdapter {

    private final Context context;
    private final List<Bitacora> bitacoras;
    private final List<Integer> seleccionadas; // IDs de bitácoras seleccionadas
    private final String BASE_URL;

    public BitacoraLiquidacionAdapter(Context context, List<Bitacora> bitacoras, List<Integer> seleccionadas) {
        this.context = context;
        this.bitacoras = bitacoras;
        this.seleccionadas = seleccionadas;
        this.BASE_URL = context.getString(R.string.base_url);
    }

    @Override
    public int getCount() {
        return bitacoras.size();
    }

    @Override
    public Object getItem(int position) {
        return bitacoras.get(position);
    }

    @Override
    public long getItemId(int position) {
        return bitacoras.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Bitacora bitacora = bitacoras.get(position);
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_bitacora_liquidacion, parent, false);
            holder = new ViewHolder();
            holder.checkBox = convertView.findViewById(R.id.checkBitacora);
            holder.tvFolio = convertView.findViewById(R.id.tvFolioBitacora);
            holder.tvGasto = convertView.findViewById(R.id.tvGastoBitacora);
            holder.tvAnticipos = convertView.findViewById(R.id.tvAnticiposBitacora);
            holder.tvTiempo = convertView.findViewById(R.id.tvTiempoBitacora);
            holder.tvDistancia = convertView.findViewById(R.id.tvDistanciaBitacora);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // Pintar datos de la bitácora
        holder.tvFolio.setText("Folio: " + bitacora.getId());
        holder.tvGasto.setText("Gasto total: $" + safeDouble(bitacora.getGranTotal()));
        holder.tvTiempo.setText("Fecha: " + safeString(bitacora.getFecha()));
        holder.tvDistancia.setText("Distancia: " + safeDouble(bitacora.getDistanciaTotal()) + " km");

        // 🔹 Consultar anticipos reales con Volley usando el idFolio
        String url = BASE_URL + "anticipo/bitacora/" + bitacora.getId();
        RequestQueue queue = Volley.newRequestQueue(context);

        JsonArrayRequest anticiposRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    double totalAnticipos = 0.0;
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = response.getJSONObject(i);
                            if (obj.has("importe") && !obj.isNull("importe")) {
                                totalAnticipos += obj.getDouble("importe");
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    holder.tvAnticipos.setText("Anticipos: $" + String.format("%.2f", totalAnticipos));
                },
                error -> holder.tvAnticipos.setText("Anticipos: Error")
        );

        queue.add(anticiposRequest);

        // CheckBox seleccionado
        holder.checkBox.setOnCheckedChangeListener(null); // evitar bug de reciclado
        holder.checkBox.setChecked(seleccionadas.contains(bitacora.getId()));

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if (!seleccionadas.contains(bitacora.getId())) {
                    seleccionadas.add(bitacora.getId());
                }
            } else {
                seleccionadas.remove(Integer.valueOf(bitacora.getId()));
            }
        });

        // Color condicional (ejemplo: gasto alto en rojo)
        double gasto = safeDouble(bitacora.getGranTotal());
        if (gasto > 10000) {
            holder.tvGasto.setTextColor(ContextCompat.getColor(context, R.color.red_light));
        } else {
            holder.tvGasto.setTextColor(ContextCompat.getColor(context, R.color.black));
        }

        return convertView;
    }

    private static class ViewHolder {
        CheckBox checkBox;
        TextView tvFolio, tvGasto, tvAnticipos, tvTiempo, tvDistancia;
    }

    private String safeString(String value) {
        return value == null ? "" : value;
    }

    private double safeDouble(Double value) {
        return value == null ? 0.0 : value;
    }
}
