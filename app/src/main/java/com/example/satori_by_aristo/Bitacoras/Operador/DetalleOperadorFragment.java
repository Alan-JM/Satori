package com.example.satori_by_aristo.Bitacoras.Operador;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.satori_by_aristo.R;

import org.json.JSONObject;

public class DetalleOperadorFragment extends Fragment {

    private static final String ARG_NOMBRE = "nombre";
    private static final String ARG_CORREO = "correo";
    private static final String ARG_TELEFONO = "telefono";

    public static DetalleOperadorFragment newInstance(String nombre, String correo, String telefono) {
        DetalleOperadorFragment fragment = new DetalleOperadorFragment();
        Bundle args = new Bundle();
        args.putString(ARG_NOMBRE, nombre);
        args.putString(ARG_CORREO, correo);
        args.putString(ARG_TELEFONO, telefono);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detalle_operador, container, false);

        TextView tvNombre = view.findViewById(R.id.tvNombreDetalle);
        TextView tvCorreo = view.findViewById(R.id.tvCorreoDetalle);
        TextView tvTelefono = view.findViewById(R.id.tvTelefonoDetalle);
        TextView tvKm = view.findViewById(R.id.tvKmRecorridos);
        TextView tvCombustible = view.findViewById(R.id.tvCombustibleGastado);
        TextView tvRendimiento = view.findViewById(R.id.tvRendimiento);

        String nombre = getArguments().getString(ARG_NOMBRE);
        String correo = getArguments().getString(ARG_CORREO);
        String telefono = getArguments().getString(ARG_TELEFONO);

        tvNombre.setText("Nombre: " + nombre);
        tvCorreo.setText("Correo: " + correo);
        tvTelefono.setText("Teléfono: " + telefono);

        String url = getString(R.string.base_url) + "bitacoras/operador/" + telefono;

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    double kmTotal = 0;
                    double combustibleTotal = 0;

                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject obj = response.getJSONObject(i);
                            kmTotal += obj.optDouble("distanciaTotal", 0);
                            combustibleTotal += obj.optDouble("combustibleConsumido", 0);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    double rendimiento = combustibleTotal > 0 ? kmTotal / combustibleTotal : 0;

                    tvKm.setText("Km recorridos: " + kmTotal);
                    tvCombustible.setText("Combustible gastado: " + combustibleTotal);
                    tvRendimiento.setText("Rendimiento actual: " + String.format("%.2f km/l", rendimiento));
                },
                error -> {
                    tvKm.setText("Error al cargar datos");
                    tvCombustible.setText("");
                    tvRendimiento.setText("");
                });

        Volley.newRequestQueue(requireContext()).add(request);

        return view;
    }
}
