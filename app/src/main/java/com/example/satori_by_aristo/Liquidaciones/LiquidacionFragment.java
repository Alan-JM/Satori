package com.example.satori_by_aristo.Liquidaciones;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.satori_by_aristo.Bitacoras.Administrador.SesionAdmin;
import com.example.satori_by_aristo.R;
import com.google.android.material.button.MaterialButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class LiquidacionFragment extends Fragment {

    private ListView listView;
    private MaterialButton btnRegistrar;
    private LiquidacionAdapter adapter;

    // Lista de liquidaciones cargadas desde el backend
    public static List<Liquidacion> liquidacionesRegistradas = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_liquidacion, container, false);

        listView = view.findViewById(R.id.liquidacionesRegistradas);
        btnRegistrar = view.findViewById(R.id.RegistrarLiquidacion);

        adapter = new LiquidacionAdapter(requireContext(), liquidacionesRegistradas);
        listView.setAdapter(adapter);

        // Botón para registrar nueva liquidación
        btnRegistrar.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), NuevaLiquidacion.class);
            startActivity(intent);
        });

        // Al hacer clic en una liquidación, mostrar detalle
        listView.setOnItemClickListener((parent, itemView, position, id) -> {
            Liquidacion liquidacion = liquidacionesRegistradas.get(position);
            mostrarDetalleLiquidacion(liquidacion);
        });

        // 🔹 Cargar liquidaciones desde el backend
        cargarLiquidaciones();

        return view;
    }

    private void mostrarDetalleLiquidacion(Liquidacion liquidacion) {
        DetalleLiquidacionFragment fragment = new DetalleLiquidacionFragment();
        fragment.setLiquidacion(liquidacion);

        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.content_frame, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        cargarLiquidaciones(); // refrescar al volver
    }

    // Metodo para traer liquidaciones desde Spring Boot
    private void cargarLiquidaciones() {
        String url = getString(R.string.base_url) + "liquidacion";

        RequestQueue queue = Volley.newRequestQueue(requireContext());

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    liquidacionesRegistradas.clear();
                    try {
                        String telefonoSesion = SesionAdmin.getTelefonoAdmin();

                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = response.getJSONObject(i);

                            // Mapear correctamente los campos del DTO
                            Liquidacion l = new Liquidacion();
                            l.setIdFolio(obj.optString("idFolio", ""));
                            l.setFecha(Date.valueOf(obj.optString("fecha", "")));
                            l.setOperador(obj.optString("operador", ""));
                            l.setBonoExt(BigDecimal.valueOf(obj.optDouble("bonoExt", 0.0)));
                            l.setResumen(obj.optString("resumen", ""));
                            l.setTelefonoAdmin(obj.optString("telefonoAdmin", ""));

                            // Filtrar por telefonoAdmin de la sesión
                            if (telefonoSesion.equals(l.getTelefonoAdmin())) {
                                liquidacionesRegistradas.add(l);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(requireContext(), "Error procesando datos", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(requireContext(), "Error cargando liquidaciones", Toast.LENGTH_SHORT).show()
        );

        queue.add(request);
    }
}