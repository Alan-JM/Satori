package com.example.satori_by_aristo.Bitacoras.Operador;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.BaseAdapter;

import com.example.satori_by_aristo.R;

import java.util.List;

public class OperadorAdapter extends BaseAdapter {

    private Context context;
    private List<OperadorStats> operadores;

    public OperadorAdapter(Context context, List<OperadorStats> operadores) {
        this.context = context;
        this.operadores = operadores;
    }

    @Override
    public int getCount() {
        return operadores.size();
    }

    @Override
    public Object getItem(int position) {
        return operadores.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_operador, parent, false);
        }

        OperadorStats operador = operadores.get(position);

        TextView txtNombre = convertView.findViewById(R.id.txtNombre);
        TextView txtNumViajes = convertView.findViewById(R.id.txtNumViajes);
        TextView txtPromKm = convertView.findViewById(R.id.txtPromKm);
        TextView txtPromGasto = convertView.findViewById(R.id.txtPromGasto);
        TextView txtTotalKm = convertView.findViewById(R.id.txtTotalKm);
        TextView txtTotalGasto = convertView.findViewById(R.id.txtTotalGasto);

        txtNombre.setText(operador.getNombre());
        txtNumViajes.setText(String.valueOf(operador.getNumViajes()));
        txtPromKm.setText(String.format("%.2f", operador.getPromKm()));
        txtPromGasto.setText(String.format("%.2f", operador.getPromGasto()));
        txtTotalKm.setText(String.format("%.2f", operador.getTotalKm()));
        txtTotalGasto.setText(String.format("%.2f", operador.getTotalGasto()));

        return convertView;
    }
}
