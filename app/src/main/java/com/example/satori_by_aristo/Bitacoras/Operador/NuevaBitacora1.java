package com.example.satori_by_aristo.Bitacoras.Operador;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.satori_by_aristo.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class NuevaBitacora1 extends AppCompatActivity implements View.OnClickListener {

    Button atras, siguiente;
    EditText fecha, nombre, unidad, cliente, destino, ayudantes;
    TextView tvTitulo, tvSubtitulo;

    private boolean modoEdicion = false;
    private int bitacoraId = -1;
    private String iniciado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_nueva_bitacora);

        iniciado = getIntent().getStringExtra("iniciado");

        atras = findViewById(R.id.atrasBitacora);
        siguiente = findViewById(R.id.siguienteBitacora);
        fecha = findViewById(R.id.fechaBitacora);
        nombre = findViewById(R.id.operadorBitacora);
        unidad = findViewById(R.id.ecoBitacora);
        cliente = findViewById(R.id.clienteBitacora);
        destino = findViewById(R.id.destinoBitacora);
        ayudantes = findViewById(R.id.ayudantesBitacora);

        atras.setOnClickListener(this);
        siguiente.setOnClickListener(this);

        // Verificar si es modo edición
        bitacoraId = getIntent().getIntExtra("bitacora_id", -1);
        modoEdicion = bitacoraId != -1;

        if (modoEdicion) {
            cargarDatosParaEditar();
        } else {
            // Autollenar fecha actual
            String fechaHoy = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            fecha.setText(fechaHoy);

            // Cargar datos del viaje según folio
            cargarDatosViaje(iniciado);
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    private void cargarNombreOperadorPorTelefono(String telefono) {
        if (telefono == null || telefono.isEmpty()) {
            Toast.makeText(this, "Teléfono no disponible", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = getString(R.string.base_url) + "perfil/" + telefono;

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        String nombreOperador = response.optString("nombre", "N/A");
                        nombre.setText(nombreOperador);
                        nombre.setEnabled(false);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(this, "Error cargando nombre del operador", Toast.LENGTH_SHORT).show()
        );

        queue.add(request);
    }

    private void cargarDatosParaEditar() {
        // Buscar la bitácora por ID
        Bitacora bitacora = null;
        for (Bitacora b : BitacoraFragment.bitacorasRegistradas) {
            if (b.getId() == bitacoraId) {
                bitacora = b;
                break;
            }
        }

        if (bitacora != null) {
            fecha.setText(bitacora.getFecha());
            nombre.setText(bitacora.getOperador());
            unidad.setText(bitacora.getEco());
            cliente.setText(bitacora.getCliente());
            destino.setText(bitacora.getDestino());
            ayudantes.setText(bitacora.getAyudantes());
        }
    }

    private void cargarDatosViaje(String folio) {
        String url = getString(R.string.base_url) + "viaje";
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = response.getJSONObject(i);
                            String folioApi = obj.getString("folio");

                            if (folio.equals(folioApi)) {

                                String telefonoOperador = obj.getString("operador");


                                cargarNombreOperadorPorTelefono(telefonoOperador);

                                cliente.setText(obj.optString("cliente", ""));
                                destino.setText(obj.optString("destino", ""));


                                cliente.setEnabled(false);
                                destino.setEnabled(false);
                                break;
                            }
                        }
                    } catch (Exception e) {
                        Toast.makeText(this, "Error procesando datos", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Error de conexión: " + error.getMessage(), Toast.LENGTH_LONG).show()
        );

        queue.add(request);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.atrasBitacora) {
            finish();
        } else if (id == R.id.siguienteBitacora) {
            String date = fecha.getText().toString().trim();
            String operador = nombre.getText().toString().trim();
            String eco = unidad.getText().toString().trim();
            String clientes = cliente.getText().toString().trim();
            String direccion = destino.getText().toString().trim();
            String ayuda = ayudantes.getText().toString().trim();

            if (date.isEmpty()) {
                Toast.makeText(this, "Ingresa la fecha en que se abre la Bitácora", Toast.LENGTH_SHORT).show();
            } else if (operador.isEmpty()) {
                Toast.makeText(this, "Ingresa el nombre del operador a cargo", Toast.LENGTH_SHORT).show();
            } else if (eco.isEmpty()) {
                Toast.makeText(this, "Ingresa la unidad eco correspondiente", Toast.LENGTH_SHORT).show();
            } else if (clientes.isEmpty()) {
                Toast.makeText(this, "Ingresa el nombre del cliente", Toast.LENGTH_SHORT).show();
            } else if (direccion.isEmpty()) {
                Toast.makeText(this, "Ingresa el destino del viaje", Toast.LENGTH_SHORT).show();
            } else {
                Intent siguienteB = new Intent(this, NuevaBitacora2.class);
                siguienteB.putExtra("fecha", date != null ? date : "");
                siguienteB.putExtra("operador", operador != null ? operador : "");
                siguienteB.putExtra("eco", eco != null ? eco : "");
                siguienteB.putExtra("cliente", clientes != null ? clientes : "");
                siguienteB.putExtra("destino", direccion != null ? direccion : "");
                siguienteB.putExtra("ayudantes", ayuda != null ? ayuda : "");
                siguienteB.putExtra("iniciado", iniciado != null ? iniciado : "");

                if (modoEdicion) {
                    siguienteB.putExtra("bitacora_id", bitacoraId);
                }
                startActivity(siguienteB);
            }
        }
    }

}
