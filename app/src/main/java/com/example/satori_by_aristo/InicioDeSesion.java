package com.example.satori_by_aristo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.satori_by_aristo.Bitacoras.Operador.SesionUsuario;
import com.example.satori_by_aristo.Bitacoras.Administrador.SesionAdmin;

import org.json.JSONException;
import org.json.JSONObject;

public class InicioDeSesion extends AppCompatActivity {

    Button inicio, registro;
    EditText telefono, contrasena, claveAdmin;
    LottieAnimationView truckAnimation;
    LinearLayout loginContainer; // contenedor del formulario

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_inicio_de_sesion);

        inicio = findViewById(R.id.InicioDeSesion);
        telefono = findViewById(R.id.telefono);
        contrasena = findViewById(R.id.contrasena);
        claveAdmin = findViewById(R.id.claveAdmin);
        registro = findViewById(R.id.Registro);

        truckAnimation = findViewById(R.id.truckAnimation);
        loginContainer = findViewById(R.id.loginContainer);

        registro.setOnClickListener(v -> irregistro());
        inicio.setOnClickListener(v -> iniciarSesion());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (view, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void actualizarTelefonoOperador(String claveOperador, String telefonoP) {
        String url = getString(R.string.base_url) + "operador/" + claveOperador;

        JSONObject body = new JSONObject();
        try {
            body.put("telefonoP", telefonoP);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.PUT,
                url,
                body,
                response -> {
                    Toast.makeText(this,
                            "Teléfono del operador actualizado correctamente",
                            Toast.LENGTH_SHORT).show();
                    SesionUsuario.setTelefonoP(telefonoP);
                },
                error -> {
                    error.printStackTrace();
//                    Toast.makeText(this,
//                            "Error actualizando teléfono operador",
//                            Toast.LENGTH_LONG).show();
                }
        );

        queue.add(request);
    }

    private void irregistro() {
        startActivity(new Intent(this, Registro1.class));
        finish();
    }

    private void iniciarSesion() {
        String tel = telefono.getText().toString().trim();
        String contra = contrasena.getText().toString().trim();
        String clave = claveAdmin.getText().toString().trim();

        if (tel.isEmpty() || contra.isEmpty()) {
            Toast.makeText(this, "Ingresa tu número de teléfono y contraseña", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!tel.matches("\\d{10}")) {
            Toast.makeText(this, "El número de teléfono no es válido", Toast.LENGTH_SHORT).show();
            return;
        }

        // Caso especial: Supervisor
        if (tel.equals("0000000000") && contra.equals("TET0")) {
            SesionActual.obtenerInstancia().iniciarSesion(tel, "Supervisor", "supervisor@empresa.com", Rol.JEFE);
            Toast.makeText(this, "Iniciando sesión como Supervisor", Toast.LENGTH_SHORT).show();
            iniciarTransicionAnimada();
            return;
        }

        String url = getString(R.string.base_url) + "login";

        JSONObject loginJson = new JSONObject();
        try {
            loginJson.put("telefono", tel);
            loginJson.put("contrasena", contra);
            loginJson.put("clave", clave);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, loginJson,
                response -> {
                    try {
                        Log.d("LOGIN_RESPONSE", response.toString()); // 🔹 imprime respuesta completa

                        int rol = response.getInt("rol");
                        String claveResp = response.optString("clave", "");

                        // Ajusta las claves según tu backend
                        String nombre = response.optString("nombreUsuario", response.optString("nombre", ""));
                        String correo = response.optString("email", response.optString("correo", ""));

                        Toast.makeText(this, "Login exitoso. Rol=" + rol, Toast.LENGTH_SHORT).show();

                        Rol rolUsuario;
                        if (rol == 2) {
                            rolUsuario = Rol.ADMINISTRADOR;
                        } else if (rol == 1) {
                            rolUsuario = Rol.OPERADOR;
                        } else {
                            rolUsuario = Rol.DESEMPLEADO;
                        }

                        // Guardar sesión global con nombre y correo reales
                        SesionActual.obtenerInstancia().iniciarSesion(tel, nombre, correo, rolUsuario);

                        if (!claveResp.isEmpty()) {
                            if (rolUsuario == Rol.OPERADOR) {
                                cargarDatosOperador(claveResp);
                                actualizarTelefonoOperador(claveResp, tel);
                            } else if (rolUsuario == Rol.ADMINISTRADOR) {
                                cargarDatosAdministrador(claveResp);
                            }
                        }

                        iniciarTransicionAnimada();

                    } catch (JSONException e) {
                        Toast.makeText(this, "Error parseando respuesta", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(this, "Error de conexión", Toast.LENGTH_LONG).show();
                });

        queue.add(request);
    }

    private void cargarDatosOperador(String claveOperador) {
        String url = getString(R.string.base_url) + "operador/" + claveOperador;

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        String telAdmin = response.optString("telefonoAdmin");
                        String telOperador = response.optString("telefonoP");

                        SesionUsuario.setClaveOperador(claveOperador);
                        SesionUsuario.setTelefonoAdmin(telAdmin);
                        SesionUsuario.setTelefonoP(telOperador);

                        Toast.makeText(this,
                                "Teléfono del operador: " + telOperador,
                                Toast.LENGTH_LONG).show();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(this,
                            "Error cargando operador",
                            Toast.LENGTH_LONG).show();
                }
        );

        queue.add(request);
    }

    private void cargarDatosAdministrador(String claveAdmin) {
        String url = getString(R.string.base_url) + "administrador/" + claveAdmin;

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        String telAdmin = response.optString("telefono");
                        SesionAdmin.iniciarSesion(telAdmin);

                        Toast.makeText(this,
                                "Sesión admin cargada\nTeléfono: " + telAdmin,
                                Toast.LENGTH_LONG).show();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(this,
                            "Error cargando administrador",
                            Toast.LENGTH_LONG).show();
                }
        );

        queue.add(request);
    }

    private void iniciarTransicionAnimada() {
        loginContainer.setVisibility(View.GONE);
        truckAnimation.setVisibility(View.VISIBLE);
        truckAnimation.playAnimation();

        new Handler().postDelayed(() -> {
            Intent intent = new Intent(InicioDeSesion.this, Principal.class);
            startActivity(intent);
            finish();
        }, 2500);
    }
}