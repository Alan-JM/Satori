package com.example.satori_by_aristo.Bitacoras.Operador;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.satori_by_aristo.R;

import org.json.JSONArray;
import org.json.JSONObject;

public class ContrasenaViaje extends AppCompatActivity {

    private EditText edtFolio, edtContrasena;
    private Button btnIngresar;
    private String BASE_URL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_contrasena_viaje);

        // Tomar la URL desde strings.xml
        BASE_URL = getString(R.string.base_url) + "viaje";

        // Mostrar carrusel al inicio
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.contenedorCarrusel, new CarruselFragment())
                .commit();

        edtFolio = findViewById(R.id.edtFolio);
        edtContrasena = findViewById(R.id.edtContrasena);
        btnIngresar = findViewById(R.id.btnIngresar);

        btnIngresar.setOnClickListener(v -> validarAcceso());
    }

    private void validarAcceso() {
        String folio = edtFolio.getText().toString().trim();
        String password = edtContrasena.getText().toString().trim();

        if (folio.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Ingresa folio y contraseña", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET,
                BASE_URL, null,
                response -> comprobarCredenciales(response, folio, password),
                error -> Toast.makeText(this, "Error de conexión: " + error.getMessage(),
                        Toast.LENGTH_LONG).show());

        queue.add(request);
    }

    private void comprobarCredenciales(JSONArray viajes, String folio, String password) {
        try {
            for (int i = 0; i < viajes.length(); i++) {
                JSONObject obj = viajes.getJSONObject(i);
                String folioApi = obj.getString("folio");
                String passApi = obj.getString("password");

                if (folio.equals(folioApi) && password.equals(passApi)) {
                    // Coincide - pasar a NuevaBitacora1 con variable "iniciado"
                    Intent intent = new Intent(this, NuevaBitacora1.class);
                    intent.putExtra("iniciado", folio); // viaje = folio
                    startActivity(intent);
                    finish();
                    return;
                }
            }
            Toast.makeText(this, "Folio o contraseña incorrectos", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Error procesando datos", Toast.LENGTH_SHORT).show();
        }
    }
}
