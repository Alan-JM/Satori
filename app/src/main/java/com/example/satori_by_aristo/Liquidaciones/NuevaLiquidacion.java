package com.example.satori_by_aristo.Liquidaciones;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ArrayAdapter;
import android.view.View;
import android.widget.AdapterView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.satori_by_aristo.Bitacoras.Administrador.SesionAdmin;
import com.example.satori_by_aristo.Bitacoras.Operador.Bitacora;
import com.example.satori_by_aristo.R;
import com.example.satori_by_aristo.retrofit.AnticipoApi;
import com.example.satori_by_aristo.retrofit.AnticipoDto;
import com.example.satori_by_aristo.retrofit.ApiClient;
import com.example.satori_by_aristo.retrofit.BitacoraApi;
import com.example.satori_by_aristo.retrofit.BitacoraDto;
import com.example.satori_by_aristo.retrofit.LiquidacionApi;
import com.google.android.material.button.MaterialButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NuevaLiquidacion extends AppCompatActivity {

    private EditText etFolio, etFecha, etBonoExtra;
    private Spinner spinnerOperadores;
    private ListView listViewBitacoras;
    private MaterialButton btnAtras, btnCalcular, btnGuardar;
    private TextView tvResumenCalculo;

    private final List<Bitacora> bitacorasFiltradas = new ArrayList<>();
    private final List<Integer> seleccionadas = new ArrayList<>();
    private BitacoraLiquidacionAdapter adapter;

    private String BASE_URL;
    private boolean resumenCalculado = false;

    private String telefonoOperadorSeleccionado;
    private String nombreOperadorSeleccionado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nueva_liquidacion);

        BASE_URL = getString(R.string.base_url);
        inicializarVistas();
        cargarOperadoresDesdeBackend(); // primero cargamos operadores del admin
        configurarEventos();
    }
    private void inicializarVistas() {
        etFolio = findViewById(R.id.folioLiquidacion);
        etFecha = findViewById(R.id.fechaLiquidacion);
        etBonoExtra = findViewById(R.id.bonoExtraLiquidacion);

        spinnerOperadores = findViewById(R.id.spinnerOperadores);
        listViewBitacoras = findViewById(R.id.listViewBitacorasLiquidacion);
        btnAtras = findViewById(R.id.atrasLiquidacion);
        btnCalcular = findViewById(R.id.calcularLiquidacion);
        btnGuardar = findViewById(R.id.guardarLiquidacion);
        tvResumenCalculo = findViewById(R.id.tvResumenCalculo);

        String fechaHoy = new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date());
        etFecha.setText(fechaHoy);
        etFecha.setEnabled(false);

        generarFolioUnico();
    }

    private void generarFolioUnico() {
         int length = new java.util.Random().nextInt(6) + 5; // entre 5 y 10
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder folioBuilder = new StringBuilder();

        java.util.Random random = new java.util.Random();
        for (int i = 0; i < length; i++) {
            folioBuilder.append(chars.charAt(random.nextInt(chars.length())));
        }

        String nuevoFolio = folioBuilder.toString();
        etFolio.setText(nuevoFolio);
        etFolio.setEnabled(false);
        Log.d("FolioDebug", "Nuevo folio aleatorio asignado: " + nuevoFolio);
    }


    private void cargarOperadoresDesdeBackend() {
        String url = BASE_URL + "operador/admin/" + SesionAdmin.getTelefonoAdmin();
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    List<String> nombres = new ArrayList<>();
                    List<String> telefonos = new ArrayList<>();

                    for (int i = 0; i < response.length(); i++) {
                        JSONObject obj = response.optJSONObject(i);
                        if (obj != null) {
                            String telefonoP = obj.optString("telefonoP");

                            // 🔹 Consultar perfil para obtener el nombre
                            String perfilUrl = BASE_URL + "perfil/" + telefonoP;
                            JsonObjectRequest perfilRequest = new JsonObjectRequest(
                                    Request.Method.GET,
                                    perfilUrl,
                                    null,
                                    perfilResp -> {
                                        String nombre = perfilResp.optString("nombre", "SIN NOMBRE");
                                        nombres.add(nombre);
                                        telefonos.add(telefonoP);

                                        ArrayAdapter<String> adapterOperadores = new ArrayAdapter<>(
                                                NuevaLiquidacion.this,
                                                android.R.layout.simple_spinner_item,
                                                nombres
                                        );
                                        adapterOperadores.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                        spinnerOperadores.setAdapter(adapterOperadores);

                                        spinnerOperadores.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                            @Override
                                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                nombreOperadorSeleccionado = nombres.get(position);
                                                telefonoOperadorSeleccionado = telefonos.get(position);

                                                cargarBitacorasDesdeBackend(); // recargar filtradas por operador
                                            }

                                            @Override
                                            public void onNothingSelected(AdapterView<?> parent) {}
                                        });
                                    },
                                    error -> Toast.makeText(this, "Error cargando perfil", Toast.LENGTH_SHORT).show()
                            );
                            queue.add(perfilRequest);
                        }
                    }
                },
                error -> Toast.makeText(this, "Error cargando operadores", Toast.LENGTH_SHORT).show()
        );

        queue.add(request);
    }



    //   Filtra bitácoras por teléfono del operador seleccionado y confirmación = 3
    private void cargarBitacorasDesdeBackend() {
        BitacoraApi api = ApiClient.getBitacoraApi(this);
        Call<List<BitacoraDto>> call = api.getAllBitacoras();

        call.enqueue(new Callback<List<BitacoraDto>>() {
            @Override
            public void onResponse(Call<List<BitacoraDto>> call, Response<List<BitacoraDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<BitacoraDto> bitacoras = response.body();
                    bitacorasFiltradas.clear();

                    for (BitacoraDto b : bitacoras) {
                        Log.d("BitacorasDebug", "Bitácora recibida: folio=" + b.getIdFolio() +
                                " tel=" + b.getTelefono() +
                                " confirmacion=" + b.getConfirmacion() +
                                " liquidacion=" + b.getLiquidacion());

                        if (b.getConfirmacion() == 3
                                && b.getLiquidacion() == null
                                && telefonoOperadorSeleccionado != null
                                && telefonoOperadorSeleccionado.equalsIgnoreCase(b.getTelefono())) {

                            Bitacora bitacora = new Bitacora();
                            bitacora.setId(b.getIdFolio());
                            bitacora.setFecha(b.getFecha() != null ? b.getFecha().toString() : "");
                            bitacora.setConfirmacion(b.getConfirmacion());
                            bitacora.setGranTotal(b.getGranTotal() != null ? b.getGranTotal().doubleValue() : 0.0);
                            bitacora.setSubtotalEfectivo(b.getSubTotalE() != null ? b.getSubTotalE().doubleValue() : 0.0);
                            bitacora.setDistanciaTotal(b.getDistanciaTotal() != null ? b.getDistanciaTotal().doubleValue() : 0.0);
                            bitacora.setOperador(b.getOperador());

                            // 🔹 Guardar anticipos asociados (IDs) si vienen en el DTO
                            bitacora.setAnticipos(b.getAnticipos());

                            bitacorasFiltradas.add(bitacora);

                            Log.d("BitacorasDebug", "Bitácora filtrada y agregada: folio=" + b.getIdFolio());
                        }
                    }

                    Log.d("BitacorasDebug", "Total bitácoras filtradas=" + bitacorasFiltradas.size());

                    adapter = new BitacoraLiquidacionAdapter(NuevaLiquidacion.this, bitacorasFiltradas, seleccionadas);
                    listViewBitacoras.setAdapter(adapter);

                } else {
                    Toast.makeText(NuevaLiquidacion.this,
                            "No se encontraron bitácoras",
                            Toast.LENGTH_SHORT).show();
                    Log.e("BitacorasDebug", "Respuesta no exitosa: código=" + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<BitacoraDto>> call, Throwable t) {
                Log.e("BitacorasDebug", "Error cargando bitácoras: " + t.getMessage());
                Toast.makeText(NuevaLiquidacion.this,
                        "Error cargando bitácoras: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void configurarEventos() {
        btnAtras.setOnClickListener(v -> finish());
        btnCalcular.setOnClickListener(v -> calcularYMostrarResumen());
        btnGuardar.setOnClickListener(v -> {
            if (!resumenCalculado) {
                Toast.makeText(this, "Genera el resumen", Toast.LENGTH_SHORT).show();
                return;
            }
            guardarLiquidacion();
        });
    }

    private void calcularYMostrarResumen() {
        if (!validarCamposCompletos()) return;

        double bonoExtra = getDoubleValue(etBonoExtra);
        double totalGastos = 0.0;

        // 🔹 Sumar gastos de las bitácoras seleccionadas
        for (Bitacora b : bitacorasFiltradas) {
            if (seleccionadas.contains(b.getId())) {
                totalGastos += b.getGranTotal();
            }
        }

        final double gastosFinal = totalGastos;

        // 🔹 Ahora pedimos anticipos por cada bitácora seleccionada
        double[] totalAnticipos = {0.0};
        AnticipoApi api = ApiClient.getAnticipoApi(this);

        // Contador para saber cuándo terminamos todas las llamadas
        final int totalBitacoras = seleccionadas.size();
        final int[] llamadasCompletadas = {0};

        for (Integer idBitacora : seleccionadas) {
            Call<List<AnticipoDto>> call = api.getAnticiposByBitacora(idBitacora);
            call.enqueue(new Callback<List<AnticipoDto>>() {
                @Override
                public void onResponse(Call<List<AnticipoDto>> call, Response<List<AnticipoDto>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        for (AnticipoDto a : response.body()) {
                            if (a.getImporte() != null) {
                                totalAnticipos[0] += a.getImporte().doubleValue();
                            }
                        }
                    } else {
                        Log.e("AnticiposDebug", "No se encontraron anticipos para bitácora " + idBitacora);
                    }

                    llamadasCompletadas[0]++;
                    if (llamadasCompletadas[0] == totalBitacoras) {
                         double resultado = gastosFinal - totalAnticipos[0] + bonoExtra;
                        String resumen =
                                "OPERADOR: " + nombreOperadorSeleccionado + "\n\n" +
                                        "Gasto total de bitácoras: $" + String.format("%.2f", gastosFinal) + "\n" +
                                        "- Anticipos: $" + String.format("%.2f", totalAnticipos[0]) + "\n" +
                                        "+ Bono extra: $" + String.format("%.2f", bonoExtra) + "\n" +
                                        "= TOTAL: $" + String.format("%.2f", resultado) + "\n";


                        tvResumenCalculo.setText(resumen);
                        tvResumenCalculo.setVisibility(View.VISIBLE);
                        resumenCalculado = true;

                        Log.d("ResumenDebug", "Resumen generado:\n" + resumen);
                    }
                }

                @Override
                public void onFailure(Call<List<AnticipoDto>> call, Throwable t) {
                    Log.e("AnticiposDebug", "Error cargando anticipos: " + t.getMessage());
                    llamadasCompletadas[0]++;
                    if (llamadasCompletadas[0] == totalBitacoras) {
                        Toast.makeText(NuevaLiquidacion.this, "Error obteniendo anticipos", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }







    private void guardarLiquidacion() {
        if (!validarCamposCompletos()) return;

        if (!resumenCalculado) {
            Toast.makeText(this, "Genera el resumen", Toast.LENGTH_SHORT).show();
            return;
        }

        String folio = etFolio.getText().toString().trim();
        String fecha = etFecha.getText().toString().trim();
        double bonoExtra = getDoubleValue(etBonoExtra);

        String resumenTexto = tvResumenCalculo.getText().toString();
        if (resumenTexto.length() > 250) {
            resumenTexto = resumenTexto.substring(0, 250);
        }

        JSONObject liquidacionJson = new JSONObject();
        try {
            liquidacionJson.put("idFolio", folio);
            liquidacionJson.put("fecha", fecha);
            liquidacionJson.put("operador", nombreOperadorSeleccionado);
            liquidacionJson.put("bonoExt", bonoExtra);
            liquidacionJson.put("resumen", resumenTexto);
            liquidacionJson.put("telefonoAdmin", SesionAdmin.getTelefonoAdmin());
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("LiquidacionDebug", "Error creando JSON: " + e.getMessage());
        }

        String url = BASE_URL + "liquidacion";
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                liquidacionJson,
                response -> {
                    Toast.makeText(this, "Liquidación registrada exitosamente", Toast.LENGTH_SHORT).show();
                    Log.d("LiquidacionDebug", "Respuesta OK: " + response);

                    // 🔹 Actualizar bitácoras seleccionadas con el idFolio de la liquidación
                    for (Integer idBitacora : seleccionadas) {
                        asociarLiquidacionABitacora(idBitacora, folio);
                    }

                    finish();
                },
                error -> {
                    String mensaje = error.getMessage();
                    if (mensaje == null && error.networkResponse != null) {
                        mensaje = "Código: " + error.networkResponse.statusCode;
                    }
                    Log.e("LiquidacionDebug", "Error guardando liquidación: " + mensaje);
                    Toast.makeText(this, "Error guardando liquidación: " + mensaje, Toast.LENGTH_LONG).show();
                }
        );

        queue.add(request);
    }

    private void asociarLiquidacionABitacora(int idBitacora, String folioLiquidacion) {
        String url = BASE_URL + "bitacoras/" + idBitacora + "/liquidacion?idFolioLiquidacion=" + folioLiquidacion;

        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest patchRequest = new StringRequest(
                Request.Method.PATCH,
                url,
                resp -> {
                    Log.d("BitacorasDebug", "Bitácora " + idBitacora + " asociada a liquidación " + folioLiquidacion);
                    Toast.makeText(this, "Bitácora asociada a liquidación", Toast.LENGTH_SHORT).show();
                },
                err -> {
                    Log.e("BitacorasDebug", "Error asociando bitácora: " + err.getMessage());
                    Toast.makeText(this, "Error asociando bitácora", Toast.LENGTH_SHORT).show();
                }
        );

        queue.add(patchRequest);
    }



    private boolean validarCamposCompletos() {
        String folio = etFolio.getText().toString().trim();
        String fecha = etFecha.getText().toString().trim();

        if (folio.isEmpty()) {
            Toast.makeText(this, "Ingresa el folio", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (fecha.isEmpty()) {
            Toast.makeText(this, "Ingresa la fecha", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (nombreOperadorSeleccionado == null || nombreOperadorSeleccionado.isEmpty()) {
            Toast.makeText(this, "Selecciona un operador", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private double getDoubleValue(EditText editText) {
        try {
            String text = editText.getText().toString().trim();
            return text.isEmpty() ? 0.0 : Double.parseDouble(text);
        } catch (NumberFormatException e) {
            Log.e("ParseDebug", "Error convirtiendo número: " + e.getMessage());
            return 0.0;
        }
    }

    private double safeDouble(Double value) {
        return value == null ? 0.0 : value;
    }
}
