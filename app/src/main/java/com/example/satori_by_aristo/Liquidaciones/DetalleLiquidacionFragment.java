package com.example.satori_by_aristo.Liquidaciones;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.satori_by_aristo.Bitacoras.Administrador.SesionAdmin;
import com.example.satori_by_aristo.R;
import com.google.android.material.button.MaterialButton;

import java.math.BigDecimal;

public class DetalleLiquidacionFragment extends Fragment {

    private TextView tvContenido;
    private MaterialButton btnVolver;

    private Liquidacion liquidacion;

    public void setLiquidacion(Liquidacion liquidacion) {
        this.liquidacion = liquidacion;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detalle_liquidacion, container, false);

        tvContenido = view.findViewById(R.id.tvContenidoLiquidacion);
        btnVolver = view.findViewById(R.id.btnVolverDetalleLiquidacion);

        // Si ya tenemos objeto en memoria, mostrarlo
        cargarDatos("Cargando bitácoras...");

        if (liquidacion != null && liquidacion.getIdFolio() != null) {
            cargarLiquidacionDesdeServidor(liquidacion.getIdFolio());
        }

        btnVolver.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        return view;
    }

    private void cargarLiquidacionDesdeServidor(String idFolio) {
        String url = getString(R.string.base_url) + "liquidacion/" + idFolio;

        RequestQueue queue = Volley.newRequestQueue(requireContext());

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    Liquidacion l = new Liquidacion();
                    l.setIdFolio(response.optString("idFolio", ""));
                    l.setFecha(java.sql.Date.valueOf(response.optString("fecha", "")));
                    l.setOperador(response.optString("operador", ""));
                    l.setBonoExt(BigDecimal.valueOf(response.optDouble("bonoExt", 0.0)));
                    l.setResumen(response.optString("resumen", ""));
                    l.setTelefonoAdmin(response.optString("telefonoAdmin", ""));

                    // 🔹 Filtrar por telefonoAdmin de la sesión
                    if (SesionAdmin.getTelefonoAdmin().equals(l.getTelefonoAdmin())) {
                        this.liquidacion = l;
                        cargarBitacorasAsociadas(l.getIdFolio());
                    } else {
                        Toast.makeText(requireContext(),
                                "No tienes permiso para ver esta liquidación",
                                Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(requireContext(), "Error cargando detalle", Toast.LENGTH_SHORT).show()
        );

        queue.add(request);
    }

    private void cargarBitacorasAsociadas(String idFolioLiquidacion) {
        String url = getString(R.string.base_url) + "bitacora";

        RequestQueue queue = Volley.newRequestQueue(requireContext());

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    StringBuilder bitacorasAsociadas = new StringBuilder();

                    for (int i = 0; i < response.length(); i++) {
                        try {
                            String liquidacionId = response.getJSONObject(i).optString("liquidacion", null);
                            int confirmacion = response.getJSONObject(i).optInt("confirmacion", 0);
                            String fecha = response.getJSONObject(i).optString("fecha", "");
                            int idBitacora = response.getJSONObject(i).optInt("idFolio", -1);
                            String telefonoAdmin = response.getJSONObject(i).optString("telefonoAdmin", "");

                            if (idFolioLiquidacion.equals(liquidacionId)
                                    && confirmacion == 3
                                    && SesionAdmin.getTelefonoAdmin().equals(telefonoAdmin)) {
                                bitacorasAsociadas.append(
                                        String.format("• %s | %d\n", fecha, idBitacora)
                                );
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    if (bitacorasAsociadas.length() == 0) {
                        bitacorasAsociadas.append("No hay bitácoras asociadas.\n");
                    }

                    cargarDatos(bitacorasAsociadas.toString());
                },
                error -> {
                    Toast.makeText(requireContext(), "Error cargando bitácoras asociadas", Toast.LENGTH_SHORT).show();
                    cargarDatos("Error al cargar bitácoras.\n");
                }
        );

        queue.add(request);
    }

    private void cargarDatos(String bitacorasTexto) {
        if (liquidacion == null) {
            tvContenido.setText("No hay datos de liquidación disponibles.");
            return;
        }

        StringBuilder contenido = new StringBuilder();
        contenido.append("═══════════════════════════════\n");
        contenido.append("FOLIO: ").append(safeString(liquidacion.getIdFolio())).append("\n");
        contenido.append("FECHA: ").append(liquidacion.getFecha() != null ? liquidacion.getFecha().toString() : "").append("\n");
        contenido.append("OPERADOR: ").append(safeString(liquidacion.getOperador())).append("\n");
        contenido.append("TELÉFONO ADMIN: ").append(safeString(liquidacion.getTelefonoAdmin())).append("\n");
        contenido.append("───────────────────────────────\n");
        contenido.append("BONO EXTRA: $").append(String.format("%.2f", safeDouble(liquidacion.getBonoExt() != null ? liquidacion.getBonoExt().doubleValue() : 0.0))).append("\n");
        contenido.append("═══════════════════════════════\n");
        contenido.append("BITÁCORAS ASOCIADAS:\n");
        contenido.append(bitacorasTexto);

        tvContenido.setText(contenido.toString());
    }

    private String safeString(String value) {
        return value == null ? "" : value;
    }

    private double safeDouble(Double value) {
        return value == null ? 0.0 : value;
    }
}
