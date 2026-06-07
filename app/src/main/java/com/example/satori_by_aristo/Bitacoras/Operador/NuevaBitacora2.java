package com.example.satori_by_aristo.Bitacoras.Operador;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.satori_by_aristo.Anticipos.Anticipo;
import com.example.satori_by_aristo.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class NuevaBitacora2 extends AppCompatActivity {

    Button atras, siguiente;
    EditText odometroInicial, odometroFinal, combustibleConsumido;
    TextView distanciaTotal;

    // Datos de la pantalla anterior
    private String fecha, operador, eco, cliente, destino, ayudantes;
    private boolean modoEdicion = false;
    private int bitacoraId = -1;
    private String iniciado;

    ListView listViewAnticipos;
    AnticipoBitacoraAdapter anticipoAdapter;
    List<Anticipo> listaAnticipos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nueva_bitacora2);

        // Recibir datos con validación
        iniciado = getIntent().getStringExtra("iniciado");
        fecha = getIntent().getStringExtra("fecha");
        operador = getIntent().getStringExtra("operador");
        eco = getIntent().getStringExtra("eco");
        cliente = getIntent().getStringExtra("cliente");
        destino = getIntent().getStringExtra("destino");
        ayudantes = getIntent().getStringExtra("ayudantes");

        // Logs para depuración
        Log.d("NuevaBitacora2", "Recibido: fecha=" + fecha + ", operador=" + operador +
                ", eco=" + eco + ", cliente=" + cliente + ", destino=" + destino +
                ", ayudantes=" + ayudantes + ", iniciado=" + iniciado);

        // Verificar si es modo edición
        bitacoraId = getIntent().getIntExtra("bitacora_id", -1);
        modoEdicion = bitacoraId != -1;

        // Inicializar vistas
        atras = findViewById(R.id.atrasBitacora);
        siguiente = findViewById(R.id.siguienteBitacora);
        odometroInicial = findViewById(R.id.odometroInicialBitacora);
        odometroFinal = findViewById(R.id.odometroFinalBitacora);
        distanciaTotal = findViewById(R.id.distanciaTotalBitacora);
        combustibleConsumido = findViewById(R.id.combustibleConsumidoBitacora);

        // Inicializar ListView de anticipos
        listViewAnticipos = findViewById(R.id.listViewAnticipos);
        listaAnticipos = new ArrayList<>();
        cargarAnticipos(); // método que hace la petición con Volley

        if (modoEdicion) {
            cargarDatosParaEditar();
        }

        // Calcular distancia automáticamente cuando cambian los odómetros
        odometroInicial.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                calcularDistancia();
            }
        });

        odometroFinal.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                calcularDistancia();
            }
        });

        atras.setOnClickListener(v -> finish());

        siguiente.setOnClickListener(v -> {
            String odomInicial = odometroInicial.getText().toString().trim();
            String odomFinal = odometroFinal.getText().toString().trim();

            if (odomInicial.isEmpty() || odomFinal.isEmpty()) {
                Toast.makeText(this, "Completa los datos de kilometraje", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    double odomInicialVal = Double.parseDouble(odomInicial);
                    double odomFinalVal = Double.parseDouble(odomFinal);
                    double distanciaVal = Double.parseDouble(distanciaTotal.getText().toString());

                    // Pasar TODOS los datos a la siguiente pantalla con validación
                    Intent intent = new Intent(this, NuevaBitacora3.class);
                    intent.putExtra("fecha", fecha != null ? fecha : "");
                    intent.putExtra("operador", operador != null ? operador : "");
                    intent.putExtra("eco", eco != null ? eco : "");
                    intent.putExtra("cliente", cliente != null ? cliente : "");
                    intent.putExtra("destino", destino != null ? destino : "");
                    intent.putExtra("ayudantes", ayudantes != null ? ayudantes : "");
                    intent.putExtra("odometroInicial", odomInicialVal);
                    intent.putExtra("odometroFinal", odomFinalVal);
                    intent.putExtra("distanciaTotal", distanciaVal);
                    intent.putExtra("combustibleConsumido", getDoubleValue(combustibleConsumido));

                    // Folio iniciado
                    if (iniciado != null) {
                        intent.putExtra("iniciado", iniciado);
                    }

                    // Pasar el ID si es modo edición
                    if (modoEdicion) {
                        intent.putExtra("bitacora_id", bitacoraId);
                    }

                    // Pasar anticipos seleccionados
                    ArrayList<Integer> seleccionados = new ArrayList<>();
                    for (Anticipo a : listaAnticipos) {
                        if (a.isSeleccionado()) {
                            seleccionados.add(Integer.valueOf(a.getIdFolio()));
                        }
                    }
                    intent.putIntegerArrayListExtra("anticiposSeleccionados", seleccionados);


                    startActivity(intent);
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Kilometraje inválido", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void cargarAnticipos() {
        String url = getString(R.string.base_url) + "anticipo";
        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    listaAnticipos.clear();
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject obj = response.getJSONObject(i);
                            int confirmacion = obj.getInt("confirmacion");
                            String bitacoraIdStr = obj.optString("bitacoraid", null);

                            boolean esDisponible = (bitacoraIdStr == null || bitacoraIdStr.equals("null"));
                            boolean esDeEstaBitacora = (modoEdicion && bitacoraIdStr != null && bitacoraIdStr.equals(String.valueOf(bitacoraId)));

                            if (confirmacion == 2 && (esDisponible || esDeEstaBitacora)) {
                                int idFolio = obj.getInt("idFolio");   // 🔹 ahora Integer
                                String concepto = obj.getString("concepto");
                                double importe = obj.getDouble("importe");

                                Anticipo anticipo = new Anticipo(
                                        idFolio,   // 🔹 pasa Integer
                                        null, null, null, null,
                                        importe,
                                        concepto,
                                        null,
                                        confirmacion,
                                        null, null, null
                                );

                                if (esDeEstaBitacora) {
                                    anticipo.setSeleccionado(true);
                                }

                                listaAnticipos.add(anticipo);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    anticipoAdapter = new AnticipoBitacoraAdapter(this, listaAnticipos);
                    listViewAnticipos.setAdapter(anticipoAdapter);
                },
                error -> Toast.makeText(this, "Error cargando anticipos", Toast.LENGTH_SHORT).show()
        );

        Volley.newRequestQueue(this).add(request);
    }



    private void calcularDistancia() {
        try {
            double inicial = Double.parseDouble(odometroInicial.getText().toString().trim());
            double fin = Double.parseDouble(odometroFinal.getText().toString().trim());
            double total = fin - inicial;
            if (total >= 0) {
                distanciaTotal.setText(String.valueOf(total));
            } else {
                distanciaTotal.setText("0");
            }
        } catch (NumberFormatException e) {
            distanciaTotal.setText("0");
        }
    }

    private void cargarDatosParaEditar() {
        Bitacora bitacora = null;
        for (Bitacora b : BitacoraFragment.bitacorasRegistradas) {
            if (b.getId() == bitacoraId) {
                bitacora = b;
                break;
            }
        }

        if (bitacora != null) {
            odometroInicial.setText(String.valueOf(bitacora.getOdometroInicial()));
            odometroFinal.setText(String.valueOf(bitacora.getOdometroFinal()));
            distanciaTotal.setText(String.valueOf(bitacora.getDistanciaTotal()));
            combustibleConsumido.setText(String.valueOf(bitacora.getCombustibleConsumido()));
        }
    }

    private double getDoubleValue(EditText editText) {
        try {
            String text = editText.getText().toString().trim();
            return text.isEmpty() ? 0.0 : Double.parseDouble(text);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
}
