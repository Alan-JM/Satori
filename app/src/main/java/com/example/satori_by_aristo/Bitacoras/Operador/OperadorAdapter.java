package com.example.satori_by_aristo.Bitacoras.Operador;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.example.satori_by_aristo.R;

import java.util.List;

public class OperadorAdapter extends ArrayAdapter<Perfil> {

    private Context context;
    private List<Perfil> operadores;

    public OperadorAdapter(Context context, List<Perfil> operadores) {
        super(context, R.layout.item_operador, operadores);
        this.context = context;
        this.operadores = operadores;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.item_operador, parent, false);
        }

        Perfil operador = operadores.get(position);

        TextView tvNombre = convertView.findViewById(R.id.tvNombreOperador);
        TextView tvCorreo = convertView.findViewById(R.id.tvCorreoOperador);
        TextView tvTelefono = convertView.findViewById(R.id.tvTelefonoOperador);

        tvNombre.setText(operador.getNombre());
        tvCorreo.setText(operador.getCorreo());
        tvTelefono.setText(operador.getTelefono());

        // Al hacer clic, cargamos el detalle en el mismo fragmento (contenedorDetalle)
        convertView.setOnClickListener(v -> {
            DetalleOperadorFragment detalle = DetalleOperadorFragment.newInstance(
                    operador.getNombre(),
                    operador.getCorreo(),
                    operador.getTelefono()
            );

            ((FragmentActivity) context).getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.contenedorDetalle, detalle)
                    .commit();
        });

        return convertView;
    }
}
