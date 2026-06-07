package com.example.satori_by_aristo.Bitacoras.Administrador;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.satori_by_aristo.R;
import com.example.satori_by_aristo.retrofit.ApiClient;
import com.example.satori_by_aristo.retrofit.BitacoraApi;
import com.example.satori_by_aristo.retrofit.RechazoDto;
import com.google.android.material.button.MaterialButton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RechazarBitacoraActivity extends AppCompatActivity {

    private EditText etMotivo;
    private MaterialButton btnEnviar, btnCancelar;
    private int bitacoraId;
    private String telefonoOp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rechazar_bitacora);

        bitacoraId = getIntent().getIntExtra("bitacora_id", -1);
        telefonoOp = getIntent().getStringExtra("telefono"); // 🔹 Recuperar teléfono del Intent

        etMotivo = findViewById(R.id.motivoRechazo);
        btnEnviar = findViewById(R.id.enviarRechazo);
        btnCancelar = findViewById(R.id.cancelarRechazo);

        // 🔹 Mostrar Toast apenas entras a la actividad
        Toast.makeText(this,
                "Teléfono operador recibido: " + telefonoOp,
                Toast.LENGTH_LONG).show();

        btnEnviar.setOnClickListener(v -> {
            String motivo = etMotivo.getText().toString().trim();
            if (motivo.isEmpty()) {
                Toast.makeText(this, "Debes ingresar un motivo", Toast.LENGTH_SHORT).show();
                return;
            }

            // Crear DTO con teléfono y motivo
            RechazoDto rechazoDto = new RechazoDto();
            rechazoDto.setTelefonoOp(telefonoOp);
            rechazoDto.setMotivo(motivo);

            BitacoraApi api = ApiClient.getBitacoraApi(this);

            // Guardar rechazo
            api.saveRechazo(rechazoDto).enqueue(new Callback<RechazoDto>() {
                @Override
                public void onResponse(Call<RechazoDto> call, Response<RechazoDto> response) {
                    if (response.isSuccessful()) {
                        // 🔹 Actualizar confirmacion a 4
                        api.updateConfirmacion(bitacoraId, 4).enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response2) {
                                if (response2.isSuccessful()) {
                                    Toast.makeText(RechazarBitacoraActivity.this,
                                            "Bitácora rechazada correctamente (confirmacion=4)",
                                            Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {
                                    Toast.makeText(RechazarBitacoraActivity.this,
                                            "Error actualizando confirmacion: " + response2.code(),
                                            Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                Toast.makeText(RechazarBitacoraActivity.this,
                                        "Error de red al actualizar confirmacion: " + t.getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(RechazarBitacoraActivity.this,
                                "Error guardando rechazo: " + response.code(),
                                Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<RechazoDto> call, Throwable t) {
                    Toast.makeText(RechazarBitacoraActivity.this,
                            "Error de red al guardar rechazo: " + t.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            });
        });

        btnCancelar.setOnClickListener(v -> finish());
    }
}