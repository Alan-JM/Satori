package com.example.satori_by_aristo;

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

public class Registro1 extends AppCompatActivity implements View.OnClickListener {

    Button siguiente, atras;
    EditText nombreCompleto, correoElectronico, telefono;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registro1);

        siguiente = findViewById(R.id.SiguienteRegistro);
        atras = findViewById(R.id.Anterior);
        nombreCompleto = findViewById(R.id.nombre);
        correoElectronico = findViewById(R.id.correo);
        telefono = findViewById(R.id.telefono);

        siguiente.setOnClickListener(this);
        atras.setOnClickListener(this);

        // Recuperar datos si vienen del Intent (cuando se regresa desde Registro2)
        Intent intent = getIntent();
        if (intent != null) {
            String nombre = intent.getStringExtra("nombre");
            String correo = intent.getStringExtra("correo");
            String tel = intent.getStringExtra("telefono");

            if (nombre != null) nombreCompleto.setText(nombre);
            if (correo != null) correoElectronico.setText(correo);
            if (tel != null) telefono.setText(tel);
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        String nombre = nombreCompleto.getText().toString().trim();
        String correo = correoElectronico.getText().toString().trim();
        String tel = telefono.getText().toString().trim();

        if (id == R.id.Anterior) {
            Intent inicio = new Intent(this, MainActivity.class);
            startActivity(inicio);
        } else if (id == R.id.SiguienteRegistro) {
            if (nombre.isEmpty() && correo.isEmpty() && tel.isEmpty()) {
                Toast.makeText(this, "Ingresa los datos solicitados", Toast.LENGTH_SHORT).show();
            } else if (nombre.isEmpty()) {
                Toast.makeText(this, "Ingresa tu nombre completo", Toast.LENGTH_SHORT).show();
            } else if (correo.isEmpty()) {
                Toast.makeText(this, "Ingresa tu correo electrónico", Toast.LENGTH_SHORT).show();
            } else if (tel.isEmpty()) {
                Toast.makeText(this, "Ingresa tu número de teléfono", Toast.LENGTH_SHORT).show();
            } else if (!tel.matches("\\d{10}")) {
                Toast.makeText(this, "El número de teléfono no es válido", Toast.LENGTH_SHORT).show();
            } else {
                // Pasar datos a Registro2
                Intent registro2 = new Intent(this, Registro2.class);
                registro2.putExtra("nombre", nombre);
                registro2.putExtra("correo", correo);
                registro2.putExtra("telefono", tel);
                startActivity(registro2);
            }
        }
    }
}