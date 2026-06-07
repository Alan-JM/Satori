package com.example.satori_by_aristo.Bitacoras.Operador;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.satori_by_aristo.Bitacoras.Operador.Bitacora;
import com.example.satori_by_aristo.R;
import com.example.satori_by_aristo.retrofit.ApiClient;
import com.example.satori_by_aristo.retrofit.BitacoraApi;
import com.google.android.material.button.MaterialButton;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccionesBitacoraFragment extends Fragment {

    // Datos generales
    private EditText etFecha, etOperador, etEco, etCliente, etDestino, etAyudantes;

    // Kilometraje
    private EditText etOdometroInicial, etOdometroFinal, etDistanciaTotal, etCombustibleConsumido;

    // Conceptos
    private EditText etConcepto1, etFecha1, etImporte1;
    private EditText etConcepto2, etFecha2, etImporte2;
    private EditText etConcepto3, etFecha3, etImporte3;
    private TextView tvTotal;

    // Gastos
    private EditText etCombustibleTarjeta, etCasetasTarjeta, etSubtotalTarjeta;
    private EditText etCombustibleEfectivo, etCasetasEfectivo, etComida;
    private EditText etReparaciones, etManiobras, etTransitosFederal, etOtros, etSubtotalEfectivo;
    private TextView tvGranTotal;

    // Botones
    private MaterialButton btnModificar, btnEnviar, btnEliminar, btnVolver;

    private OnAccionBitacoraListener listener;
    private Bitacora bitacora;

    public interface OnAccionBitacoraListener {
        void onModificar(Bitacora bitacora);
        void onEnviar(Bitacora bitacora);
        void onEliminar(Bitacora bitacora);
        void onVolver();
    }

    public void setOnAccionBitacoraListener(OnAccionBitacoraListener listener) {
        this.listener = listener;
    }

    public void setBitacora(Bitacora bitacora) {
        this.bitacora = bitacora;
    }
    private void cargarBitacoraDesdeServidor(int idFolio) {
        String url = getString(R.string.base_url) + "bitacora/" + idFolio;

        RequestQueue queue = Volley.newRequestQueue(requireContext());

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        //  Mapear el JSON a tu objeto Bitacora
                        Bitacora b = new Bitacora();
                        b.setId(response.getInt("idFolio"));
                        b.setFecha(response.getString("fecha"));
                        b.setOperador(response.getString("operador"));
                        b.setEco(response.getString("unidadEco"));
                        b.setCliente(response.getString("cliente"));
                        b.setDestino(response.getString("destino"));
                        b.setAyudantes(response.getString("ayudantes"));
                        b.setOdometroInicial(response.getInt("odometroInicial"));
                        b.setOdometroFinal(response.getInt("odometroFinal"));
                        b.setDistanciaTotal(response.getInt("distanciaTotal"));
                        b.setCombustibleConsumido(response.getDouble("combustibleConsumido"));
                        b.setCombustibleTarjeta(response.getDouble("gastoTCombustible"));
                        b.setCasetasTarjeta(response.getDouble("gastoTCasetas"));
                        b.setSubtotalTarjeta(response.getDouble("subTotalT"));
                        b.setCombustibleEfectivo(response.getDouble("gastoECombustible"));
                        b.setCasetasEfectivo(response.getDouble("gastoECasetas"));
                        b.setComida(response.getDouble("gastoEComida"));
                        b.setReparaciones(response.getDouble("gastoEReparaciones"));
                        b.setManiobras(response.getDouble("gastoEManiobras"));
                        b.setTransitosFederal(response.getDouble("gastoETransito"));
                        b.setOtros(response.getDouble("gastoEOtros"));
                        b.setSubtotalEfectivo(response.getDouble("subTotalE"));
                        b.setGranTotal(response.getDouble("granTotal"));
                        b.setConfirmacion(response.getInt("confirmacion"));
                        this.bitacora = b;
                        cargarDatos();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(requireContext(), "Error cargando bitácora", Toast.LENGTH_SHORT).show()
        );

        queue.add(request);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_acciones_bitacora, container, false);

        inicializarVistas(view);


        if (getArguments() != null) {
            int idFolio = getArguments().getInt("bitacora_id", -1);
            if (idFolio != -1) {
                cargarBitacoraDesdeServidor(idFolio);
            }
        } else {

            cargarDatos();
        }

        configurarBotones();

        return view;
    }

    private void inicializarVistas(View view) {
         etFecha = view.findViewById(R.id.fechaBitacoraDetalle);
        etOperador = view.findViewById(R.id.operadorBitacoraDetalle);
        etEco = view.findViewById(R.id.ecoBitacoraDetalle);
        etCliente = view.findViewById(R.id.clienteBitacoraDetalle);
        etDestino = view.findViewById(R.id.destinoBitacoraDetalle);
        etAyudantes = view.findViewById(R.id.ayudantesBitacoraDetalle);

        // Kilometraje
        etOdometroInicial = view.findViewById(R.id.odometroInicialBitacoraDetalle);
        etOdometroFinal = view.findViewById(R.id.odometroFinalBitacoraDetalle);
        etDistanciaTotal = view.findViewById(R.id.distanciaTotalBitacoraDetalle);
        etCombustibleConsumido = view.findViewById(R.id.combustibleConsumidoBitacoraDetalle);

        // Conceptos
        etConcepto1 = view.findViewById(R.id.concepto1BitacoraDetalle);
        etFecha1 = view.findViewById(R.id.fecha1BitacoraDetalle);
        etImporte1 = view.findViewById(R.id.importe1BitacoraDetalle);
        etConcepto2 = view.findViewById(R.id.concepto2BitacoraDetalle);
        etFecha2 = view.findViewById(R.id.fecha2BitacoraDetalle);
        etImporte2 = view.findViewById(R.id.importe2BitacoraDetalle);
        etConcepto3 = view.findViewById(R.id.concepto3BitacoraDetalle);
        etFecha3 = view.findViewById(R.id.fecha3BitacoraDetalle);
        etImporte3 = view.findViewById(R.id.importe3BitacoraDetalle);
        tvTotal = view.findViewById(R.id.totalBitacoraDetalle);

        // Gastos
        etCombustibleTarjeta = view.findViewById(R.id.combustibleBitacoraDetalle);
        etCasetasTarjeta = view.findViewById(R.id.casetasBitacoraDetalle);
        etSubtotalTarjeta = view.findViewById(R.id.subtotalGastosTarjetaBitacoraDetalle);
        etCombustibleEfectivo = view.findViewById(R.id.combustible2BitacoraDetalle);
        etCasetasEfectivo = view.findViewById(R.id.casetas2BitacoraDetalle);
        etComida = view.findViewById(R.id.comidaBitacoraDetalle);
        etReparaciones = view.findViewById(R.id.reparacionesBitacoraDetalle);
        etManiobras = view.findViewById(R.id.maniobrasBitacoraDetalle);
        etTransitosFederal = view.findViewById(R.id.transitosFederalBitacoraDetalle);
        etOtros = view.findViewById(R.id.otrosBitacoraDetalle);
        etSubtotalEfectivo = view.findViewById(R.id.subtotalGastosBitacoraDetalle);
        tvGranTotal = view.findViewById(R.id.granTotalBitacoraDetalle);

        // Botones
        btnModificar = view.findViewById(R.id.btnModificarBitacora);
        btnEnviar = view.findViewById(R.id.btnEnviarBitacora);
        btnEliminar = view.findViewById(R.id.btnEliminarBitacora);
        btnVolver = view.findViewById(R.id.btnVolverBitacora);
    }

    private void cargarDatos() {
        if (bitacora == null) return;

        // Datos generales
        etFecha.setText(bitacora.getFecha() != null ? bitacora.getFecha() : "");
        etOperador.setText(bitacora.getOperador() != null ? bitacora.getOperador() : "");
        etEco.setText(bitacora.getEco() != null ? bitacora.getEco() : "");
        etCliente.setText(bitacora.getCliente() != null ? bitacora.getCliente() : "");
        etDestino.setText(bitacora.getDestino() != null ? bitacora.getDestino() : "");
        etAyudantes.setText(bitacora.getAyudantes() != null ? bitacora.getAyudantes() : "");

        // Kilometraje
        etOdometroInicial.setText(String.valueOf(bitacora.getOdometroInicial()));
        etOdometroFinal.setText(String.valueOf(bitacora.getOdometroFinal()));
        etDistanciaTotal.setText(String.valueOf(bitacora.getDistanciaTotal()));
        etCombustibleConsumido.setText(String.valueOf(bitacora.getCombustibleConsumido()));

        // Conceptos
        etConcepto1.setText(bitacora.getConcepto1() != null ? bitacora.getConcepto1() : "");
        etFecha1.setText(bitacora.getFecha1() != null ? bitacora.getFecha1() : "");
        etImporte1.setText(String.format("%.2f", bitacora.getImporte1()));
        etConcepto2.setText(bitacora.getConcepto2() != null ? bitacora.getConcepto2() : "");
        etFecha2.setText(bitacora.getFecha2() != null ? bitacora.getFecha2() : "");
        etImporte2.setText(String.format("%.2f", bitacora.getImporte2()));
        etConcepto3.setText(bitacora.getConcepto3() != null ? bitacora.getConcepto3() : "");
        etFecha3.setText(bitacora.getFecha3() != null ? bitacora.getFecha3() : "");
        etImporte3.setText(String.format("%.2f", bitacora.getImporte3()));

        // Gastos
        etCombustibleTarjeta.setText(String.format("%.2f", bitacora.getCombustibleTarjeta()));
        etCasetasTarjeta.setText(String.format("%.2f", bitacora.getCasetasTarjeta()));
        etSubtotalTarjeta.setText(String.format("%.2f", bitacora.getSubtotalTarjeta()));
        etCombustibleEfectivo.setText(String.format("%.2f", bitacora.getCombustibleEfectivo()));
        etCasetasEfectivo.setText(String.format("%.2f", bitacora.getCasetasEfectivo()));
        etComida.setText(String.format("%.2f", bitacora.getComida()));
        etReparaciones.setText(String.format("%.2f", bitacora.getReparaciones()));
        etManiobras.setText(String.format("%.2f", bitacora.getManiobras()));
        etTransitosFederal.setText(String.format("%.2f", bitacora.getTransitosFederal()));
        etOtros.setText(String.format("%.2f", bitacora.getOtros()));
        etSubtotalEfectivo.setText(String.format("%.2f", bitacora.getSubtotalEfectivo()));
        tvGranTotal.setText(String.format("$%.2f", bitacora.getGranTotal()));

        // Control de botones según color
        if ("amarillo".equals(bitacora.getColor())) {
            btnModificar.setVisibility(View.GONE);
            btnEnviar.setVisibility(View.GONE);
            btnEliminar.setVisibility(View.GONE);
        } else {
            btnModificar.setVisibility(View.VISIBLE);
            btnEnviar.setVisibility(View.VISIBLE);
            btnEliminar.setVisibility(View.VISIBLE);
        }
    }
    private void enviarBitacora(Bitacora bitacora) {
        BitacoraApi api = ApiClient.getBitacoraApi(requireContext());

        api.updateConfirmacion(bitacora.getId(), 2).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // Actualizar el objeto en memoria
                    bitacora.setConfirmacion(2);

                    Toast.makeText(requireContext(),
                            "Bitácora enviada correctamente (confirmacion=2)", Toast.LENGTH_SHORT).show();

                    // Volver al fragmento anterior
                    getParentFragmentManager().popBackStack();
                } else {
                    Toast.makeText(requireContext(),
                            "Error enviando bitácora: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(requireContext(),
                        "Fallo de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void eliminarBitacora(Bitacora bitacora) {
        String url = getString(R.string.base_url) + "bitacora/" + bitacora.getId();

        RequestQueue queue = Volley.newRequestQueue(requireContext());

        StringRequest request = new StringRequest(Request.Method.DELETE, url,
                response -> Toast.makeText(requireContext(), "Bitácora eliminada", Toast.LENGTH_SHORT).show(),
                error -> Toast.makeText(requireContext(), "Error eliminando bitácora", Toast.LENGTH_SHORT).show()
        );

        queue.add(request);
    }



    private void configurarBotones() {
        btnModificar.setOnClickListener(v -> {
            if (listener != null && bitacora != null) {
                int confirmacion = bitacora.getConfirmacion();
                if (confirmacion == 2) {
                    Toast.makeText(requireContext(), "No se puede modificar una bitácora enviada", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (confirmacion == 3) {
                    Toast.makeText(requireContext(), "No se puede modificar una bitácora aceptada", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Solo si confirmación es 0,1,4
                listener.onModificar(bitacora);
            }
        });

        btnEnviar.setOnClickListener(v -> {
            if (bitacora != null) {
                int confirmacion = bitacora.getConfirmacion();
                if (confirmacion == 2) {
                    Toast.makeText(requireContext(), "No se puede enviar una bitácora ya enviada", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (confirmacion == 3) {
                    Toast.makeText(requireContext(), "No se puede enviar una bitácora aceptada", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Solo si confirmación es 0,1,4
                enviarBitacora(bitacora);
            }
        });

        btnEliminar.setOnClickListener(v -> {
            if (bitacora != null) {
                int confirmacion = bitacora.getConfirmacion();
                if (confirmacion == 2) {
                    Toast.makeText(requireContext(), "No se puede eliminar una bitácora enviada", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (confirmacion == 3) {
                    Toast.makeText(requireContext(), "No se puede eliminar una bitácora aceptada", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Solo si confirmación es 0,1,4
                eliminarBitacora(bitacora);
            }
        });

        btnVolver.setOnClickListener(v -> {
            if (listener != null) {
                listener.onVolver();
            }
        });
    }
}
