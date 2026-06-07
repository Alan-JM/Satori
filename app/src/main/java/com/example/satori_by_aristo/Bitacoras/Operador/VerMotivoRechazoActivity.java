package com.example.satori_by_aristo.Bitacoras.Operador;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.satori_by_aristo.Bitacoras.Operador.Bitacora;
import com.example.satori_by_aristo.Bitacoras.Operador.BitacoraFragment;
import com.example.satori_by_aristo.R;
import com.google.android.material.button.MaterialButton;

public class VerMotivoRechazoActivity extends AppCompatActivity {

    private TextView tvMotivo;
    private MaterialButton btnAceptar;
    private int bitacoraId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_motivo_rechazo);

        bitacoraId = getIntent().getIntExtra("bitacora_id", -1);

        tvMotivo = findViewById(R.id.tvMotivoRechazo);
        btnAceptar = findViewById(R.id.btnAceptarRechazo);

        // Buscar bitácora
        Bitacora bitacoraEncontrada = null;
        for (Bitacora b : BitacoraFragment.bitacorasRegistradas) {
            if (b.getId() == bitacoraId) {
                bitacoraEncontrada = b;
                break;
            }
        }

        if (bitacoraEncontrada != null) {
            String motivo = bitacoraEncontrada.getMotivoRechazo();
            tvMotivo.setText(motivo != null ? motivo : "Sin motivo registrado");
        } else {
            tvMotivo.setText("Bitácora no encontrada");
        }

        btnAceptar.setOnClickListener(v -> {
            // Cambiar color a gris para poder modificar
            for (Bitacora b : BitacoraFragment.bitacorasRegistradas) {
                if (b.getId() == bitacoraId) {
                    b.setColor("gris");
                    break;
                }
            }
            finish();
        });
    }
}