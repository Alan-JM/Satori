package com.example.satori_by_aristo.Bitacoras.Operador;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.satori_by_aristo.R;
import com.example.satori_by_aristo.SesionActual;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Liquidaciones extends Fragment {

    private ListView listView;
    private LiquidacionOpAdapter adapter;
    private List<Bitacora> bitacorasFiltradas;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_liquidaciones, container, false);

        listView = view.findViewById(R.id.liquidacionesRegistradas);

        cargarBitacorasDesdeApiVolley();

        return view;
    }

    private void cargarBitacorasDesdeApiVolley() {
        String url = getString(R.string.base_url) + "bitacora";

        RequestQueue queue = Volley.newRequestQueue(getContext());

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    bitacorasFiltradas = new ArrayList<>();
                    String telefonoSesion = SesionActual.obtenerInstancia().getTelefono();

                    for (int i = 0; i < response.length(); i++) {
                        JSONObject obj = response.optJSONObject(i);
                        if (obj != null) {
                            Bitacora b = new Bitacora();
                            b.setId(obj.optInt("id"));
                            b.setFecha(obj.optString("fecha"));
                            b.setCliente(obj.optString("cliente"));
                            b.setDestino(obj.optString("destino"));
                            b.setTelefono(obj.optString("telefono"));
                            b.setLiquidacion(obj.optString("liquidacion")); // ✅ String folioLiquidacion

                            // filtro: liquidacion != null y teléfono coincide
                            if (b.getLiquidacion() != null && !b.getLiquidacion().isEmpty()
                                    && telefonoSesion.equals(b.getTelefono())) {
                                bitacorasFiltradas.add(b);
                            }
                        }
                    }

                    adapter = new LiquidacionOpAdapter(getContext(), bitacorasFiltradas);
                    listView.setAdapter(adapter);
                },
                error -> android.widget.Toast.makeText(getContext(),
                        "Error al cargar bitácoras: " + error.getMessage(),
                        android.widget.Toast.LENGTH_LONG).show());

        queue.add(request);
    }
}
