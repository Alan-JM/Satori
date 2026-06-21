package com.example.satori_by_aristo.Anticipos;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.satori_by_aristo.Bitacoras.Operador.SesionUsuario;
import com.example.satori_by_aristo.SesionActual;
import com.example.satori_by_aristo.R;
import com.google.android.material.button.MaterialButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NuevoAnticipo extends AppCompatActivity {

    private EditText etFolio, etFecha, etUnidadTrans, etOperador;
    private EditText etImporte, etConcepto, etObservaciones;
    private MaterialButton btnAtras, btnGuardar;

    private boolean modoEdicion = false;
    private int anticipoIdFolio = -1;
    private Integer confirmacionPrevia = null;

    private String BASE_URL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_anticipo);

        BASE_URL = getString(R.string.base_url);
        inicializarVistas();

        anticipoIdFolio = getIntent().getIntExtra("anticipo_idFolio", -1);
        modoEdicion = anticipoIdFolio != -1;

        if (modoEdicion) {
            cargarDatosParaEditar();
            btnGuardar.setText("ACTUALIZAR");
            etFolio.setEnabled(false);
        } else {
            etFolio.setEnabled(false);
            autollenarCampos();
        }

        btnAtras.setOnClickListener(v -> finish());
        btnGuardar.setOnClickListener(v -> {
            if (modoEdicion) {
                actualizarAnticipo();
            } else {
                guardarAnticipo();
            }
        });
    }

    private void inicializarVistas() {
        etFolio = findViewById(R.id.folioAnticipo);
        etFecha = findViewById(R.id.fechaAnticipo);
        etUnidadTrans = findViewById(R.id.operadorAnticipo);
        etOperador = findViewById(R.id.nombreOperadorAnticipo);
        etImporte = findViewById(R.id.importeAnticipo);
        etConcepto = findViewById(R.id.conceptoAnticipo);
        etObservaciones = findViewById(R.id.observacionesAnticipo);
        btnAtras = findViewById(R.id.atrasAnticipo);
        btnGuardar = findViewById(R.id.guardarAnticipo);
    }

    private void autollenarCampos() {
        String nombreUsuario = SesionActual.obtenerInstancia().getNombreUsuario();
        if (nombreUsuario != null) {
            etOperador.setText(nombreUsuario);
        }
        String fechaActual = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        etFecha.setText(fechaActual);
    }

    private void cargarDatosParaEditar() {
        String url = BASE_URL + "anticipo/" + anticipoIdFolio;
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        etFolio.setText(response.getString("idFolio"));
                        etFecha.setText(response.getString("fecha"));
                        etUnidadTrans.setText(response.getString("unidadTrans"));
                        etOperador.setText(response.getString("operador"));
                        etImporte.setText(String.valueOf(response.getDouble("importe")));
                        etConcepto.setText(response.getString("concepto"));
                        etObservaciones.setText(response.getString("observaciones"));
                        confirmacionPrevia = response.optInt("confirmacion", 0);
                    } catch (JSONException e) {
                        Toast.makeText(this, "Error parseando datos", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Error cargando anticipo", Toast.LENGTH_SHORT).show()
        );
        queue.add(request);
    }

    private void guardarAnticipo() {
        if (!validarCampos()) return;

        String url = BASE_URL + "anticipo";
        JSONObject anticipoJson = new JSONObject();
        try {
            anticipoJson.put("fecha", etFecha.getText().toString().trim());
            anticipoJson.put("unidadTrans", etUnidadTrans.getText().toString().trim());
            anticipoJson.put("operador", etOperador.getText().toString().trim());
            anticipoJson.put("importe", Double.parseDouble(etImporte.getText().toString().trim()));
            anticipoJson.put("concepto", etConcepto.getText().toString().trim());
            anticipoJson.put("observaciones", etObservaciones.getText().toString().trim());
            anticipoJson.put("confirmacion", 0);
            anticipoJson.put("telefonoAdmin", SesionUsuario.getTelefonoAdmin());
            anticipoJson.put("telefono", SesionUsuario.getTelefonoAdmin());
            anticipoJson.put("telefonop", SesionActual.obtenerInstancia().getTelefono());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                anticipoJson,
                response -> {
                    Toast.makeText(this, "Anticipo registrado en servidor", Toast.LENGTH_SHORT).show();
                    finish();
                },
                error -> Toast.makeText(this, "Error guardando anticipo", Toast.LENGTH_SHORT).show()
        );
        queue.add(request);
    }

    private void actualizarAnticipo() {
        if (!validarCampos()) return;

        double importe;
        try {
            importe = Double.parseDouble(etImporte.getText().toString().trim());
        } catch (NumberFormatException e) {
            Toast.makeText(this, "El importe no es un número válido", Toast.LENGTH_SHORT).show();
            return;
        }

        if (anticipoIdFolio == -1) {
            Toast.makeText(this, "Error: ID de anticipo no válido", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = BASE_URL + "anticipo/" + anticipoIdFolio;
        JSONObject anticipoJson = new JSONObject();

        try {
            anticipoJson.put("idFolio", anticipoIdFolio);
            anticipoJson.put("fecha", etFecha.getText().toString().trim());
            anticipoJson.put("unidadTrans", etUnidadTrans.getText().toString().trim());
            anticipoJson.put("operador", etOperador.getText().toString().trim());
            anticipoJson.put("importe", importe);
            anticipoJson.put("concepto", etConcepto.getText().toString().trim());
            anticipoJson.put("observaciones", etObservaciones.getText().toString().trim());
            anticipoJson.put("confirmacion", confirmacionPrevia);
            anticipoJson.put("telefonoAdmin", SesionUsuario.getTelefonoAdmin());
            anticipoJson.put("telefono", SesionUsuario.getTelefonoAdmin());
            anticipoJson.put("telefonop", SesionActual.obtenerInstancia().getTelefono());

            RequestQueue queue = Volley.newRequestQueue(this);
            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.PUT,
                    url,
                    anticipoJson,
                    response -> {
                        Toast.makeText(this, "Anticipo actualizado correctamente", Toast.LENGTH_SHORT).show();
                        finish();
                    },
                    error -> {
                        if (error.networkResponse != null) {
                            android.util.Log.e("VolleyError", "Código de estado: " + error.networkResponse.statusCode);
                        }
                        Toast.makeText(this, "Error al actualizar en el servidor", Toast.LENGTH_SHORT).show();
                    }
            );
            queue.add(request);

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error interno al procesar los datos", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validarCampos() {
        String fecha = etFecha.getText().toString().trim();
        String unidadTrans = etUnidadTrans.getText().toString().trim();
        String operador = etOperador.getText().toString().trim();
        String importe = etImporte.getText().toString().trim();
        String concepto = etConcepto.getText().toString().trim();

        if (fecha.isEmpty() || unidadTrans.isEmpty() || operador.isEmpty() ||
                importe.isEmpty() || concepto.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos obligatorios", Toast.LENGTH_SHORT).show();
            return false;
        }
        try {
            Double.parseDouble(importe);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "El importe debe ser un número válido", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
