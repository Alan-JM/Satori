package com.example.satori_by_aristo.Anticipos.Administradores;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.satori_by_aristo.Anticipos.Anticipo;
import com.example.satori_by_aristo.R;

import org.json.JSONException;
import org.json.JSONObject;

public class AccionesAnticipoAdminFragment extends Fragment {

    private TextView tvFolio, tvFecha, tvUnidadTrans, tvOperador, tvImporte, tvConcepto, tvObservaciones;
    private View btnAutorizar, btnRechazar, btnVolver;

    private Anticipo anticipo;
    private String BASE_URL;

    public void setAnticipo(Anticipo anticipo) {
        this.anticipo = anticipo;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_acciones_anticipo_admin, container, false);

        BASE_URL = getString(R.string.base_url);

        inicializarVistas(view);
        cargarDatos();
        configurarBotones();

        return view;
    }

    private void inicializarVistas(View view) {
        tvFolio = view.findViewById(R.id.tvFolioDetalleAnticipo);
        tvFecha = view.findViewById(R.id.tvFechaDetalleAnticipo);
        tvUnidadTrans = view.findViewById(R.id.tvOperadorDetalleAnticipo);
        tvOperador = view.findViewById(R.id.tvNombreOperadorDetalleAnticipo);
        tvImporte = view.findViewById(R.id.tvImporteDetalleAnticipo);
        tvConcepto = view.findViewById(R.id.tvConceptoDetalleAnticipo);
        tvObservaciones = view.findViewById(R.id.tvObservacionesDetalleAnticipo);

        btnAutorizar = view.findViewById(R.id.btnAutorizarAnticipo);
        btnRechazar = view.findViewById(R.id.btnRechazarAnticipo);
        btnVolver = view.findViewById(R.id.btnVolverAnticipo);
    }

    private void cargarDatos() {
        if (anticipo == null) return;

        tvFolio.setText("Folio: " + anticipo.getIdFolio());
        tvFecha.setText("Fecha: " + anticipo.getFecha());
        tvUnidadTrans.setText("Unidad: " + anticipo.getUnidadTrans());
        tvOperador.setText("Operador: " + anticipo.getOperador());
        tvImporte.setText(String.format("Importe: $%.2f", anticipo.getImporte()));
        tvConcepto.setText("Concepto: " + anticipo.getConcepto());
        tvObservaciones.setText("Observaciones: " + anticipo.getObservaciones());
    }

    private void configurarBotones() {
        // Si el anticipo ya fue autorizado (2) o rechazado (3), deshabilitar botones
        if (anticipo != null && anticipo.getConfirmacion() != null &&
                (anticipo.getConfirmacion() == 2 || anticipo.getConfirmacion() == 3)) {

            btnAutorizar.setEnabled(false);
            btnRechazar.setEnabled(false);

            // Opcional: cambiar color para indicar que ya no se puede usar
            btnAutorizar.setAlpha(0.5f);
            btnRechazar.setAlpha(0.5f);

            Toast.makeText(getContext(),
                    "Este anticipo ya fue procesado y no puede modificarse",
                    Toast.LENGTH_SHORT).show();

        } else {
            // Solo si está pendiente (0 o 1), permitir acciones
            btnAutorizar.setOnClickListener(v ->
                    actualizarEstadoAnticipo(2, "Anticipo autorizado correctamente"));
            btnRechazar.setOnClickListener(v ->
                    actualizarEstadoAnticipo(3, "Anticipo rechazado correctamente"));
        }

        btnVolver.setOnClickListener(v -> getParentFragmentManager().popBackStack());
    }


    private void actualizarEstadoAnticipo(int nuevoEstado, String mensaje) {
        if (anticipo == null) return;

        anticipo.setConfirmacion(nuevoEstado);

        // Usar PUT y pasar el idFolio en la URL
        String url = BASE_URL + "anticipo/" + anticipo.getIdFolio();

        JSONObject anticipoJson = new JSONObject();
        try {
            anticipoJson.put("idFolio", anticipo.getIdFolio());
            anticipoJson.put("fecha", anticipo.getFecha());
            anticipoJson.put("unidadTrans", anticipo.getUnidadTrans());
            anticipoJson.put("operador", anticipo.getOperador());
            anticipoJson.put("importe", anticipo.getImporte());
            anticipoJson.put("concepto", anticipo.getConcepto());
            anticipoJson.put("observaciones", anticipo.getObservaciones());
            anticipoJson.put("confirmacion", anticipo.getConfirmacion());

             anticipoJson.put("telefonoAdmin", anticipo.getTelefonoAdmin());
            anticipoJson.put("telefono", anticipo.getTelefono());
            anticipoJson.put("telefonop", anticipo.getTelefonop());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue queue = Volley.newRequestQueue(requireContext());
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.PUT,
                url,
                anticipoJson,
                response -> {
                    Toast.makeText(getContext(), mensaje, Toast.LENGTH_SHORT).show();
                    getParentFragmentManager().popBackStack();
                },
                error -> Toast.makeText(getContext(), "Error actualizando anticipo", Toast.LENGTH_SHORT).show()
        );
        queue.add(request);
    }

}
