package com.example.satori_by_aristo;

import static android.graphics.Color.RED;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class Registro2 extends AppCompatActivity implements View.OnClickListener {

    Button atras, crearCuenta;
    EditText usuario, contrasena, confirmContrasena;

    // Datos recibidos de Registro1
    private String nombre, correo, telefono;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registro2);

        atras = findViewById(R.id.Anterior);
        crearCuenta = findViewById(R.id.crearCuenta);
        usuario = findViewById(R.id.usuario);
        contrasena = findViewById(R.id.contraseña);
        confirmContrasena = findViewById(R.id.contraseñaConfirm);

        atras.setOnClickListener(this);
        crearCuenta.setOnClickListener(this);

        // Recuperar datos de Registro1
        Intent intent = getIntent();
        nombre = intent.getStringExtra("nombre");
        correo = intent.getStringExtra("correo");
        telefono = intent.getStringExtra("telefono");

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        String user = usuario.getText().toString().trim();
        String contra = contrasena.getText().toString().trim();
        String confirm = confirmContrasena.getText().toString().trim();

        if (id == R.id.Anterior) {
            Intent regreso = new Intent(this, Registro1.class);
            regreso.putExtra("nombre", nombre);
            regreso.putExtra("correo", correo);
            regreso.putExtra("telefono", telefono);
            startActivity(regreso);
            finish();
        } else if (id == R.id.crearCuenta) {
            if (user.isEmpty() && contra.isEmpty() && confirm.isEmpty()) {
                Toast.makeText(this, "Ingresa los datos solicitados", Toast.LENGTH_SHORT).show();
            } else if (contra.isEmpty()) {
                Toast.makeText(this, "Ingresa tu contraseña", Toast.LENGTH_SHORT).show();
            } else if (confirm.isEmpty()) {
                Toast.makeText(this, "Ingresa tu confirmación de contraseña", Toast.LENGTH_SHORT).show();
            } else if (!contra.equals(confirm)) {
                Toast.makeText(this, "Tus contraseñas no coinciden, verifícalas", Toast.LENGTH_SHORT).show();
                contrasena.setTextColor(RED);
                confirmContrasena.setTextColor(RED);
            } else {
                // Validar que el teléfono coincida con el recibido
                if (telefono == null || telefono.isEmpty()) {
                    Toast.makeText(this, "Error: no se recibió el teléfono desde Registro1", Toast.LENGTH_LONG).show();
                } else if (!telefono.equals(user)) {
                    Toast.makeText(this, "Error: el teléfono no coincide", Toast.LENGTH_LONG).show();
                } else {
                    guardarConVolley(contra);
                }
            }
        }
    }

    private void guardarConVolley(String contra) {
        String urlCheck = getString(R.string.base_url) + "perfil/" + telefono;

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest checkRequest = new JsonObjectRequest(Request.Method.GET, urlCheck, null,
                response -> {
                    Toast.makeText(Registro2.this, "Teléfono ya registrado", Toast.LENGTH_LONG).show();
                },
                error -> {
                    if (error.networkResponse != null && error.networkResponse.statusCode == 404) {
                        registrarNuevoPerfil(contra, queue);
                    } else {
                        Toast.makeText(Registro2.this, "Error validando teléfono: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

        queue.add(checkRequest);
    }

    private void registrarNuevoPerfil(String contra, RequestQueue queue) {
        String url = getString(R.string.base_url) + "perfil";

        JSONObject perfilJson = new JSONObject();
        try {
            perfilJson.put("nombre", nombre);
            perfilJson.put("correo", correo);
            perfilJson.put("telefono", telefono);
            perfilJson.put("contrasena", contra);
            perfilJson.put("inicio", 0);
            perfilJson.put("rol", 0);
            perfilJson.put("clave", JSONObject.NULL);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, perfilJson,
                response -> {
                    Toast.makeText(Registro2.this, "Cuenta creada correctamente", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Registro2.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                },
                error -> {
                    Toast.makeText(Registro2.this, "Error al crear cuenta: " + error.getMessage(), Toast.LENGTH_LONG).show();
                });

        queue.add(request);
    }
}