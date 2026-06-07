package com.example.satori_by_aristo.Bitacoras.Operador;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.satori_by_aristo.Anticipos.Anticipo;
import com.example.satori_by_aristo.R;

import java.util.List;

public class AnticipoBitacoraAdapter extends BaseAdapter {
    private Context context;
    private List<Anticipo> anticipos;

    public AnticipoBitacoraAdapter(Context context, List<Anticipo> anticipos) {
        this.context = context;
        this.anticipos = anticipos;
    }

    @Override
    public int getCount() { return anticipos.size(); }

    @Override
    public Object getItem(int position) { return anticipos.get(position); }

    @Override
    public long getItemId(int position) { return position; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_anticipo, parent, false);
        }

        Anticipo anticipo = anticipos.get(position);

        CheckBox checkBox = convertView.findViewById(R.id.checkBoxAnticipo);
        TextView tvConcepto = convertView.findViewById(R.id.textConcepto);
        TextView tvImporte = convertView.findViewById(R.id.textImporte);

        tvConcepto.setText(anticipo.getConcepto());
        tvImporte.setText("$" + anticipo.getImporte());
        checkBox.setChecked(anticipo.isSeleccionado());

        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            anticipo.setSeleccionado(isChecked);
        });

        return convertView;
    }

    public List<Anticipo> getSeleccionados() {
        return anticipos;
    }
}
