package com.example.satori_by_aristo.Bitacoras.Operador;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.satori_by_aristo.Principal;
import com.example.satori_by_aristo.R;
import com.example.satori_by_aristo.retrofit.ApiClient;
import com.example.satori_by_aristo.retrofit.BitacoraApi;
import com.example.satori_by_aristo.retrofit.BitacoraDto;
import com.example.satori_by_aristo.retrofit.ViajeApi;
import com.example.satori_by_aristo.ViajeDto;

import org.json.JSONObject;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NuevaBitacora3 extends AppCompatActivity {
    ArrayList<Integer> anticiposSeleccionados;
    private ArrayList<Integer> anticiposOriginales;

    // Totales
    private TextView tvSubtotalTarjeta, tvSubtotalEfectivo, tvGranTotal;

    // Bloques de gastos
    private MiniCalculador bloqueCombustibleT, bloqueCasetasT;
    private MiniCalculador bloqueCombustibleE, bloqueCasetasE, bloqueComida,
            bloqueReparaciones, bloqueManiobras, bloqueTransitos, bloqueOtros;

    // Datos generales recibidos
    private String fecha, operador, eco, cliente, destino, ayudantes, iniciado;
    private double odometroInicial, odometroFinal, distanciaTotal, combustibleConsumido;

    private boolean modoEdicion = false;
    private int bitacoraId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nueva_bitacora3);
        anticiposSeleccionados = getIntent().getIntegerArrayListExtra("anticiposSeleccionados");
        // Inicializar subtotales
        tvSubtotalTarjeta = findViewById(R.id.tvSubtotalTarjeta);
        tvSubtotalEfectivo = findViewById(R.id.tvSubtotalEfectivo);
        tvGranTotal = findViewById(R.id.tvGranTotal);

        // Inicializar bloques
        bloqueCombustibleT = new MiniCalculador(findViewById(R.id.blockCombustibleT));
        bloqueCasetasT = new MiniCalculador(findViewById(R.id.blockCasetasT));
        bloqueCombustibleE = new MiniCalculador(findViewById(R.id.blockCombustibleE));
        bloqueCasetasE = new MiniCalculador(findViewById(R.id.blockCasetasE));
        bloqueComida = new MiniCalculador(findViewById(R.id.blockComida));
        bloqueReparaciones = new MiniCalculador(findViewById(R.id.blockReparaciones));
        bloqueManiobras = new MiniCalculador(findViewById(R.id.blockManiobras));
        bloqueTransitos = new MiniCalculador(findViewById(R.id.blockTransitos));
        bloqueOtros = new MiniCalculador(findViewById(R.id.blockOtros));

        // Recibir datos generales
        fecha = getIntent().getStringExtra("fecha");
        operador = getIntent().getStringExtra("operador");
        eco = getIntent().getStringExtra("eco");
        cliente = getIntent().getStringExtra("cliente");
        destino = getIntent().getStringExtra("destino");
        ayudantes = getIntent().getStringExtra("ayudantes");
        iniciado = getIntent().getStringExtra("iniciado");

        odometroInicial = getIntent().getDoubleExtra("odometroInicial", 0);
        odometroFinal = getIntent().getDoubleExtra("odometroFinal", 0);
        distanciaTotal = getIntent().getDoubleExtra("distanciaTotal", 0);
        combustibleConsumido = getIntent().getDoubleExtra("combustibleConsumido", 0);

        // Verificar modo edición
        bitacoraId = getIntent().getIntExtra("bitacora_id", -1);
        modoEdicion = bitacoraId != -1;

        if (modoEdicion) {
            cargarDatosParaEditar();
        }

        // Botones
        findViewById(R.id.siguienteBitacora).setOnClickListener(v -> {
            if (modoEdicion) {
                actualizarBitacora();
            } else {
                guardarBitacora();
            }
        });

        findViewById(R.id.atrasBitacora).setOnClickListener(v -> finish());
    }

    private void cargarDatosParaEditar() {
        Log.d("BitacoraEdit", ">>> INICIO cargarDatosParaEditar con bitacoraId=" + bitacoraId);

        BitacoraApi api = ApiClient.getBitacoraApi(this);
        api.getBitacoraById(bitacoraId).enqueue(new Callback<BitacoraDto>() {
            @Override
            public void onResponse(Call<BitacoraDto> call, Response<BitacoraDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    BitacoraDto dto = response.body();
                    Log.d("BitacoraEdit", "DTO recibido: " + dto.toString());

                    // 🔹 Anticipos originales
                    anticiposOriginales = new ArrayList<>();
                    if (dto.getAnticipos() != null) {
                        anticiposOriginales.addAll(dto.getAnticipos());
                    }
                    Log.d("Anticipos", "Originales cargados desde backend: " + anticiposOriginales);

                    // 🔹 Gastos
                    bloqueCombustibleT.setValor(dto.getGastoTCombustible());
                    bloqueCasetasT.setValor(dto.getGastoTCasetas());
                    bloqueCombustibleE.setValor(dto.getGastoECombustible());
                    bloqueCasetasE.setValor(dto.getGastoECasetas());
                    bloqueComida.setValor(dto.getGastoEComida());
                    bloqueReparaciones.setValor(dto.getGastoEReparaciones());
                    bloqueManiobras.setValor(dto.getGastoEManiobras());
                    bloqueTransitos.setValor(dto.getGastoETransito());
                    bloqueOtros.setValor(dto.getGastoEOtros());

                    actualizarTotales();
                    Log.d("BitacoraEdit", "Totales actualizados correctamente");
                } else {
                    Log.e("BitacoraEdit", "Error cargando bitácora: código=" + response.code());
                    Toast.makeText(NuevaBitacora3.this, "No se pudo cargar la bitácora", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<BitacoraDto> call, Throwable t) {
                Log.e("BitacoraEdit", "Fallo de red al cargar bitácora", t);
                Toast.makeText(NuevaBitacora3.this, "Error cargando bitácora: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


    private void actualizarBitacoraAnticipo(Integer idFolio, Integer bitacoraId) {
        String url = getString(R.string.base_url) + "anticipo/" + idFolio + "/bitacora";

        JSONObject body = new JSONObject();
        try {
            if (bitacoraId != null) {
                body.put("bitacoraid", bitacoraId); // ligar
            } else {
                body.put("bitacoraid", JSONObject.NULL); // liberar
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.PUT,
                url,
                body,
                response -> Log.d("AnticipoUpdate", "Anticipo actualizado: " + idFolio),
                error -> Log.e("AnticipoUpdate", "Error actualizando anticipo " + idFolio + " -> " + error.toString())
        );

        Volley.newRequestQueue(NuevaBitacora3.this).add(request);
    }


    private void actualizarAnticipos(int bitacoraId) {
        Log.d("Anticipos", ">>> INICIO actualizarAnticipos con bitacoraId=" + bitacoraId);

        // Mostrar contenido de las listas
        Log.d("Anticipos", "Seleccionados: " + (anticiposSeleccionados != null ? anticiposSeleccionados.toString() : "null"));
        Log.d("Anticipos", "Originales: " + (anticiposOriginales != null ? anticiposOriginales.toString() : "null"));

        // Ligar anticipos seleccionados
        if (anticiposSeleccionados != null && !anticiposSeleccionados.isEmpty()) {
            for (Integer idFolio : anticiposSeleccionados) {
                Log.d("Anticipos", "Ligando anticipo " + idFolio + " a bitácora " + bitacoraId);
                actualizarBitacoraAnticipo(idFolio, bitacoraId);
            }
        }
        else {
            Log.d("Anticipos", "No hay anticipos seleccionados para ligar");
        }

        // Liberar anticipos desmarcados
        if (anticiposOriginales != null && !anticiposOriginales.isEmpty()) {
            for (Integer idFolio : anticiposOriginales) {
                if (anticiposSeleccionados == null || !anticiposSeleccionados.contains(idFolio)) {
                    Log.d("Anticipos", "Liberando anticipo " + idFolio);
                    actualizarBitacoraAnticipo(idFolio, null);
                }
            }
        } else {
            Log.d("Anticipos", "No hay anticipos originales para liberar");
        }

        Log.d("Anticipos", ">>> FIN actualizarAnticipos");
    }







    private void guardarBitacora() {
        BitacoraDto dto = buildBitacoraDto();
        BitacoraApi api = ApiClient.getBitacoraApi(this);

        api.saveBitacora(dto).enqueue(new Callback<BitacoraDto>() {
            @Override
            public void onResponse(Call<BitacoraDto> call, Response<BitacoraDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    BitacoraDto savedDto = response.body();
                    Toast.makeText(NuevaBitacora3.this, "Bitácora guardada", Toast.LENGTH_SHORT).show();

                    // 🔹 Actualizar anticipos seleccionados con el folio de la bitácora recién creada
                    if (savedDto.getIdFolio() != null) {
                        actualizarAnticipos(savedDto.getIdFolio());
                    } else {
                        Log.w("BitacoraSave", "El DTO guardado no devolvió idFolio");
                    }

                    actualizarViajeIniciado();
                    goToPrincipal();
                } else {
                    Toast.makeText(NuevaBitacora3.this,
                            "Error guardando: código " + response.code(),
                            Toast.LENGTH_LONG).show();

                    try {
                        Log.e("BitacoraSave", "Error en save: " + response.code() +
                                " body=" + (response.errorBody() != null ? response.errorBody().string() : "null"));
                    } catch (Exception e) {
                        Log.e("BitacoraSave", "Error leyendo errorBody", e);
                    }
                }
            }

            @Override
            public void onFailure(Call<BitacoraDto> call, Throwable t) {
                Toast.makeText(NuevaBitacora3.this,
                        "Fallo de red: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
                Log.e("BitacoraSave", "Fallo de red", t);
            }
        });
    }

    private void actualizarBitacora() {
        BitacoraDto dto = buildBitacoraDto();
        dto.setIdFolio(bitacoraId); // aseguramos que se envíe el ID correcto

        // 🔹 Log para ver qué se envía al backend
        Log.d("BitacoraDTO", "Enviando DTO: " + dto.toString());

        BitacoraApi api = ApiClient.getBitacoraApi(this);
        api.updateBitacora(bitacoraId, dto).enqueue(new Callback<BitacoraDto>() {
            @Override
            public void onResponse(Call<BitacoraDto> call, Response<BitacoraDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(NuevaBitacora3.this,
                            "Bitácora actualizada correctamente",
                            Toast.LENGTH_SHORT).show();

                    // 🔹 Confirmamos en log que el backend respondió bien
                    Log.d("BitacoraUpdate", "Respuesta OK: " + response.body());

                    // 🔹 Actualizar anticipos seleccionados con el folio actual
                    actualizarAnticipos(bitacoraId);

                    actualizarViajeIniciado();
                    goToPrincipal();
                } else {
                    Toast.makeText(NuevaBitacora3.this,
                            "Error actualizando: código " + response.code(),
                            Toast.LENGTH_LONG).show();

                    try {
                        Log.e("BitacoraUpdate", "Error en update: " + response.code() +
                                " body=" + (response.errorBody() != null ? response.errorBody().string() : "null"));
                    } catch (Exception e) {
                        Log.e("BitacoraUpdate", "Error leyendo errorBody", e);
                    }
                }
            }




            @Override
            public void onFailure(Call<BitacoraDto> call, Throwable t) {
                Toast.makeText(NuevaBitacora3.this,
                        "Fallo de red: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
                Log.e("BitacoraUpdate", "Fallo de red", t);
            }
        });
    }




    private BitacoraDto buildBitacoraDto() {
        BitacoraDto dto = new BitacoraDto();

        // Datos generales
        if (fecha != null && !fecha.trim().isEmpty()) {
            dto.setFecha(Date.valueOf(fecha.trim()));
        }
        dto.setOperador(operador);
        dto.setUnidadEco(eco);
        dto.setCliente(cliente);
        dto.setDestino(destino);
        dto.setAyudantes(ayudantes);
        dto.setOdometroInicial((int) odometroInicial);
        dto.setOdometroFinal((int) odometroFinal);
        dto.setDistanciaTotal((int) distanciaTotal);
        dto.setCombustibleConsumido(BigDecimal.valueOf(combustibleConsumido));
        dto.setTelefono(SesionUsuario.getTelefonoP());
        dto.setTelefonoAdmin(SesionUsuario.getTelefonoAdmin());
        dto.setConfirmacion(0);
        dto.setLiquidacion(null);

        // 🔹 Validación segura para 'iniciado'
        if (iniciado != null && !iniciado.trim().isEmpty()) {
            try {
                dto.setViaje(Integer.parseInt(iniciado.trim()));
            } catch (NumberFormatException e) {
                Log.e("BitacoraDTO", "Error convirtiendo 'iniciado': " + iniciado, e);
                // No seteamos viaje si no es válido
            }
        }

        // Gastos desde minicalculadores
        dto.setGastoTCombustible(BigDecimal.valueOf(bloqueCombustibleT.getValor()));
        dto.setGastoTCasetas(BigDecimal.valueOf(bloqueCasetasT.getValor()));
        dto.setGastoECombustible(BigDecimal.valueOf(bloqueCombustibleE.getValor()));
        dto.setGastoECasetas(BigDecimal.valueOf(bloqueCasetasE.getValor()));
        dto.setGastoEComida(BigDecimal.valueOf(bloqueComida.getValor()));
        dto.setGastoEReparaciones(BigDecimal.valueOf(bloqueReparaciones.getValor()));
        dto.setGastoEManiobras(BigDecimal.valueOf(bloqueManiobras.getValor()));
        dto.setGastoETransito(BigDecimal.valueOf(bloqueTransitos.getValor()));
        dto.setGastoEOtros(BigDecimal.valueOf(bloqueOtros.getValor()));

        // Subtotales y gran total
        double subtotalT = bloqueCombustibleT.getValor() + bloqueCasetasT.getValor();
        double subtotalE = bloqueCombustibleE.getValor() +
                bloqueCasetasE.getValor() +
                bloqueComida.getValor() +
                bloqueReparaciones.getValor() +
                bloqueManiobras.getValor() +
                bloqueTransitos.getValor() +
                bloqueOtros.getValor();
        double granTotal = subtotalT + subtotalE;

        dto.setSubTotalT(BigDecimal.valueOf(subtotalT));
        dto.setSubTotalE(BigDecimal.valueOf(subtotalE));
        dto.setGranTotal(BigDecimal.valueOf(granTotal));

        return dto;
    }


    private void actualizarTotales() {
        double subtotalT = bloqueCombustibleT.getValor() + bloqueCasetasT.getValor();
        double subtotalE = bloqueCombustibleE.getValor() +
                bloqueCasetasE.getValor() +
                bloqueComida.getValor() +
                bloqueReparaciones.getValor() +
                bloqueManiobras.getValor() +
                bloqueTransitos.getValor() +
                bloqueOtros.getValor();
        double granTotal = subtotalT + subtotalE;

        tvSubtotalTarjeta.setText(String.format("%.2f", subtotalT));
        tvSubtotalEfectivo.setText(String.format("%.2f", subtotalE));
        tvGranTotal.setText(String.format("%.2f", granTotal));
    }

    private void actualizarViajeIniciado() {
        if (iniciado != null && !iniciado.trim().isEmpty()) {
            try {
                int idViaje = Integer.parseInt(iniciado.trim());

                BitacoraApi api = ApiClient.getBitacoraApi(this);
                api.updateConfirmacion(idViaje, 1).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            Log.d("ViajeUpdate", "Viaje actualizado correctamente");
                        } else {
                            Log.e("ViajeUpdate", "Error actualizando viaje: código " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Log.e("ViajeUpdate", "Fallo de red", t);
                    }
                });
            } catch (NumberFormatException e) {
                Log.e("ViajeUpdate", "Error convirtiendo 'iniciado': " + iniciado, e);
            }
        } else {
            Log.w("ViajeUpdate", "No se actualizó viaje porque 'iniciado' está vacío");
        }
    }


    private void goToPrincipal() {
        Intent intent = new Intent(this, Principal.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
         intent.putExtra("open_fragment", "bitacora");
        startActivity(intent);
        finish();
    }


    // Clase interna para manejar cada minicalculador
    private class MiniCalculador {
        private TextView tvTotal;
        private ImageButton btnPlus, btnMinus;
        private EditText etPlus, etMinus;
        private double total = 0.0;

        MiniCalculador(View root) {
            tvTotal = root.findViewById(R.id.tvTotal);
            btnPlus = root.findViewById(R.id.btnPlus);
            btnMinus = root.findViewById(R.id.btnMinus);
            etPlus = root.findViewById(R.id.etPlus);
            etMinus = root.findViewById(R.id.etMinus);

            btnPlus.setOnClickListener(v -> toggleEditText(etPlus, true));
            btnMinus.setOnClickListener(v -> toggleEditText(etMinus, false));
        }

        private void toggleEditText(EditText editText, boolean esSuma) {
            if (editText == etPlus && etMinus.getVisibility() == View.VISIBLE) return;
            if (editText == etMinus && etPlus.getVisibility() == View.VISIBLE) return;

            if (editText.getVisibility() == View.GONE) {
                editText.setVisibility(View.VISIBLE);
                editText.setAlpha(0f);
                editText.animate().alpha(1f).setDuration(300).start();
                editText.requestFocus();

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
            } else {
                String input = editText.getText().toString();
                if (!input.isEmpty()) {
                    try {
                        double value = Double.parseDouble(input);
                        if (esSuma) {
                            total += value;
                        } else {
                            total -= value;
                            if (total < 0) total = 0.0;
                        }
                        actualizarTotalAnimado(total);
                        actualizarTotales();
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
                editText.setText("");
                editText.animate().alpha(0f).setDuration(300).withEndAction(() -> editText.setVisibility(View.GONE)).start();
            }
        }

        private void actualizarTotalAnimado(double nuevoTotal) {
            float valorActual = 0f;
            try {
                valorActual = Float.parseFloat(tvTotal.getText().toString());
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            ValueAnimator anim = ValueAnimator.ofFloat(valorActual, (float) nuevoTotal);
            anim.setDuration(400);
            anim.addUpdateListener(animation -> {
                float valor = (float) animation.getAnimatedValue();
                tvTotal.setText(String.format("%.2f", valor));
            });
            anim.start();
        }

        public double getValor() {
            return total;
        }

        public void setValor(BigDecimal valor) {
            if (valor != null) {
                total = valor.doubleValue();
                tvTotal.setText(String.format("%.2f", total));
            }
        }
    }
}
