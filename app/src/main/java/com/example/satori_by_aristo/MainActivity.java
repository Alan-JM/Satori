package com.example.satori_by_aristo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button inicio,registro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        inicio = findViewById(R.id.InicioDeSesion);
        registro = findViewById(R.id.Registro);

        inicio.setOnClickListener(this);
        registro.setOnClickListener(this);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    public void onClick(View v) {
        Button button = (Button) v;
        String text = button.getText().toString();

        if (text.equals("INICIO DE SESIÓN")) {
            Intent inicio = new Intent(v.getContext(), InicioDeSesion.class);
            v.getContext().startActivity(inicio);
        } else if (text.equals("REGISTRO")) {
            Intent registro = new Intent(v.getContext(), Registro1.class);
            v.getContext().startActivity(registro);
        }
    }
}