package com.example.satori_by_aristo.Bitacoras.Administrador;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.satori_by_aristo.Bitacoras.Operador.Bitacora;
import com.example.satori_by_aristo.Bitacoras.Administrador.RechazarBitacoraActivity;
import com.example.satori_by_aristo.R;
import com.example.satori_by_aristo.retrofit.ApiClient;
import com.example.satori_by_aristo.retrofit.BitacoraApi;
import com.example.satori_by_aristo.retrofit.BitacoraDto;
import com.google.android.material.button.MaterialButton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccionesBitacoraAdminFragment extends Fragment {

    // Datos generales
    private EditText etFecha, etOperador, etEco, etCliente, etDestino, etAyudantes;

    // Kilometraje
    private EditText etOdometroInicial, etOdometroFinal, etDistanciaTotal, etCombustibleConsumido;


    // Gastos
    private EditText etCombustibleTarjeta, etCasetasTarjeta, etSubtotalTarjeta;
    private EditText etCombustibleEfectivo, etCasetasEfectivo, etComida;
    private EditText etReparaciones, etManiobras, etTransitosFederal, etOtros, etSubtotalEfectivo;
    private TextView tvGranTotal;

    // Botones
    private MaterialButton btnAutorizar, btnRechazar, btnDesautorizar, btnVolver;

    private OnAccionBitacoraAdminListener listener;
    private Bitacora bitacora;

    public interface OnAccionBitacoraAdminListener {
        void onAutorizar(Bitacora bitacora);
        void onRechazar(Bitacora bitacora, String motivo);
        void onDesautorizar(Bitacora bitacora);
        void onVolver();
    }

    public void setOnAccionBitacoraAdminListener(OnAccionBitacoraAdminListener listener) {
        this.listener = listener;
    }

    public void setBitacora(Bitacora bitacora) {
        this.bitacora = bitacora;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_acciones_bitacora_admin, container, false);

        inicializarVistas(view);
        cargarDatos();
        configurarBotones();

        return view;
    }

    private void inicializarVistas(View view) {
        // Datos generales
        etFecha = view.findViewById(R.id.fechaBitacoraDetalleAdmin);
        etOperador = view.findViewById(R.id.operadorBitacoraDetalleAdmin);
        etEco = view.findViewById(R.id.ecoBitacoraDetalleAdmin);
        etCliente = view.findViewById(R.id.clienteBitacoraDetalleAdmin);
        etDestino = view.findViewById(R.id.destinoBitacoraDetalleAdmin);
        etAyudantes = view.findViewById(R.id.ayudantesBitacoraDetalleAdmin);

        // Kilometraje
        etOdometroInicial = view.findViewById(R.id.odometroInicialBitacoraDetalleAdmin);
        etOdometroFinal = view.findViewById(R.id.odometroFinalBitacoraDetalleAdmin);
        etDistanciaTotal = view.findViewById(R.id.distanciaTotalBitacoraDetalleAdmin);
        etCombustibleConsumido = view.findViewById(R.id.combustibleConsumidoBitacoraDetalleAdmin);

        // Gastos
        etCombustibleTarjeta = view.findViewById(R.id.combustibleBitacoraDetalleAdmin);
        etCasetasTarjeta = view.findViewById(R.id.casetasBitacoraDetalleAdmin);
        etSubtotalTarjeta = view.findViewById(R.id.subtotalGastosTarjetaBitacoraDetalleAdmin);
        etCombustibleEfectivo = view.findViewById(R.id.combustible2BitacoraDetalleAdmin);
        etCasetasEfectivo = view.findViewById(R.id.casetas2BitacoraDetalleAdmin);
        etComida = view.findViewById(R.id.comidaBitacoraDetalleAdmin);
        etReparaciones = view.findViewById(R.id.reparacionesBitacoraDetalleAdmin);
        etManiobras = view.findViewById(R.id.maniobrasBitacoraDetalleAdmin);
        etTransitosFederal = view.findViewById(R.id.transitosFederalBitacoraDetalleAdmin);
        etOtros = view.findViewById(R.id.otrosBitacoraDetalleAdmin);
        etSubtotalEfectivo = view.findViewById(R.id.subtotalGastosBitacoraDetalleAdmin);
        tvGranTotal = view.findViewById(R.id.granTotalBitacoraDetalleAdmin);

        // Botones
        btnAutorizar = view.findViewById(R.id.btnAutorizarBitacoraAdmin);
        btnRechazar = view.findViewById(R.id.btnRechazarBitacoraAdmin);
        btnDesautorizar = view.findViewById(R.id.btnDesautorizarBitacoraAdmin);
        btnVolver = view.findViewById(R.id.btnVolverBitacoraAdmin);
    }
    private void cargarDatosDesdeServidor(int bitacoraId) {
        BitacoraApi api = ApiClient.getBitacoraApi(requireContext());

        api.getBitacoraById(bitacoraId).enqueue(new Callback<BitacoraDto>() {
            @Override
            public void onResponse(Call<BitacoraDto> call, Response<BitacoraDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    BitacoraDto dto = response.body();

                     Bitacora b = new Bitacora();
                    b.setId(dto.getIdFolio());
                    b.setFecha(dto.getFecha() != null ? dto.getFecha().toString() : "");
                    b.setOperador(dto.getOperador());
                    b.setEco(dto.getUnidadEco());
                    b.setCliente(dto.getCliente());
                    b.setDestino(dto.getDestino());
                    b.setAyudantes(dto.getAyudantes());
                    b.setOdometroInicial(dto.getOdometroInicial());
                    b.setOdometroFinal(dto.getOdometroFinal());
                    b.setDistanciaTotal(dto.getDistanciaTotal());

                    // 🔹 Conversión segura de BigDecimal a double
                    b.setCombustibleConsumido(dto.getCombustibleConsumido() != null ? dto.getCombustibleConsumido().doubleValue() : 0.0);
                    b.setCombustibleTarjeta(dto.getGastoTCombustible() != null ? dto.getGastoTCombustible().doubleValue() : 0.0);
                    b.setCasetasTarjeta(dto.getGastoTCasetas() != null ? dto.getGastoTCasetas().doubleValue() : 0.0);
                    b.setSubtotalTarjeta(dto.getSubTotalT() != null ? dto.getSubTotalT().doubleValue() : 0.0);
                    b.setCombustibleEfectivo(dto.getGastoECombustible() != null ? dto.getGastoECombustible().doubleValue() : 0.0);
                    b.setCasetasEfectivo(dto.getGastoECasetas() != null ? dto.getGastoECasetas().doubleValue() : 0.0);
                    b.setComida(dto.getGastoEComida() != null ? dto.getGastoEComida().doubleValue() : 0.0);
                    b.setReparaciones(dto.getGastoEReparaciones() != null ? dto.getGastoEReparaciones().doubleValue() : 0.0);
                    b.setManiobras(dto.getGastoEManiobras() != null ? dto.getGastoEManiobras().doubleValue() : 0.0);
                    b.setTransitosFederal(dto.getGastoETransito() != null ? dto.getGastoETransito().doubleValue() : 0.0);
                    b.setOtros(dto.getGastoEOtros() != null ? dto.getGastoEOtros().doubleValue() : 0.0);
                    b.setSubtotalEfectivo(dto.getSubTotalE() != null ? dto.getSubTotalE().doubleValue() : 0.0);
                    b.setGranTotal(dto.getGranTotal() != null ? dto.getGranTotal().doubleValue() : 0.0);
                    b.setTelefono(dto.getTelefono());
                    b.setConfirmacion(dto.getConfirmacion());

                    // 🔹 Guardar en variable local y mostrar en pantalla
                    bitacora = b;
                    cargarDatos();
                } else {
                    Toast.makeText(requireContext(),
                            "Error cargando bitácora: " + response.code(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BitacoraDto> call, Throwable t) {
                Toast.makeText(requireContext(),
                        "Error de red: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void cargarDatos() {
        if (bitacora == null) return;

        // Datos generales
        etFecha.setText(bitacora.getFecha());
        etOperador.setText(bitacora.getOperador());
        etEco.setText(bitacora.getEco());
        etCliente.setText(bitacora.getCliente());
        etDestino.setText(bitacora.getDestino());
        etAyudantes.setText(bitacora.getAyudantes());

        // Kilometraje
        etOdometroInicial.setText(String.valueOf(bitacora.getOdometroInicial()));
        etOdometroFinal.setText(String.valueOf(bitacora.getOdometroFinal()));
        etDistanciaTotal.setText(String.valueOf(bitacora.getDistanciaTotal()));
        etCombustibleConsumido.setText(String.valueOf(bitacora.getCombustibleConsumido()));

        // Conceptos (NO están en BD → comentados)
        // etConcepto1.setText(bitacora.getConcepto1());
        // etFecha1.setText(bitacora.getFecha1());
        // etImporte1.setText(String.valueOf(bitacora.getImporte1()));
        // etConcepto2.setText(bitacora.getConcepto2());
        // etFecha2.setText(bitacora.getFecha2());
        // etImporte2.setText(String.valueOf(bitacora.getImporte2()));
        // etConcepto3.setText(bitacora.getConcepto3());
        // etFecha3.setText(bitacora.getFecha3());
        // etImporte3.setText(String.valueOf(bitacora.getImporte3()));
        // double totalConceptos = bitacora.getImporte1() + bitacora.getImporte2() + bitacora.getImporte3();
        // tvTotal.setText(String.format("$%.2f", totalConceptos));

        // Gastos
        etCombustibleTarjeta.setText(String.valueOf(bitacora.getCombustibleTarjeta()));
        etCasetasTarjeta.setText(String.valueOf(bitacora.getCasetasTarjeta()));
        etSubtotalTarjeta.setText(String.valueOf(bitacora.getSubtotalTarjeta()));
        etCombustibleEfectivo.setText(String.valueOf(bitacora.getCombustibleEfectivo()));
        etCasetasEfectivo.setText(String.valueOf(bitacora.getCasetasEfectivo()));
        etComida.setText(String.valueOf(bitacora.getComida()));
        etReparaciones.setText(String.valueOf(bitacora.getReparaciones()));
        etManiobras.setText(String.valueOf(bitacora.getManiobras()));
        etTransitosFederal.setText(String.valueOf(bitacora.getTransitosFederal()));
        etOtros.setText(String.valueOf(bitacora.getOtros()));
        etSubtotalEfectivo.setText(String.valueOf(bitacora.getSubtotalEfectivo()));
        tvGranTotal.setText(String.format("$%.2f", bitacora.getGranTotal()));

        // Control de botones según confirmación
        if (bitacora.getConfirmacion() == 2) { // pendiente admin
            btnAutorizar.setVisibility(View.VISIBLE);
            btnRechazar.setVisibility(View.VISIBLE);
            btnDesautorizar.setVisibility(View.GONE);
        } else if (bitacora.getConfirmacion() == 3) { // autorizado
            btnAutorizar.setVisibility(View.GONE);
            btnRechazar.setVisibility(View.GONE);
            btnDesautorizar.setVisibility(View.VISIBLE);
        } else if (bitacora.getConfirmacion() == 4) { // rechazado
            btnAutorizar.setVisibility(View.GONE);
            btnRechazar.setVisibility(View.GONE);
            btnDesautorizar.setVisibility(View.GONE);
        }
    }
    private void autorizarBitacora(Bitacora bitacora) {
        BitacoraApi api = ApiClient.getBitacoraApi(requireContext());

        api.updateConfirmacion(bitacora.getId(), 3).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    bitacora.setConfirmacion(3);
                    Toast.makeText(requireContext(),
                            "Bitácora autorizada correctamente (confirmacion=3)", Toast.LENGTH_SHORT).show();
                    getParentFragmentManager().popBackStack();
                } else {
                    Toast.makeText(requireContext(),
                            "Error autorizando bitácora: " + response.code(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(requireContext(),
                        "Error de red: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void configurarBotones() {
        btnAutorizar.setOnClickListener(v -> {
            if (bitacora != null) {
                autorizarBitacora(bitacora);
            }
        });

        // 🔹 Rechazar lo dejamos pendiente porque abre otro fragmento/actividad
        btnRechazar.setOnClickListener(v -> {
            if (bitacora != null) {
                // 🔹 Crear Intent para abrir la actividad de rechazo
                Intent intent = new Intent(getActivity(), RechazarBitacoraActivity.class);

                // 🔹 Pasar el ID de la bitácora y otros datos si los necesitas
                intent.putExtra("bitacora_id", bitacora.getId());
                intent.putExtra("operador", bitacora.getOperador());
                intent.putExtra("cliente", bitacora.getCliente());

                // 🔹 Pasar el teléfono del operador desde la bitácora
                intent.putExtra("telefono", bitacora.getTelefono());

                // 🔹 Lanzar la actividad
                startActivity(intent);
            }
        });

        btnDesautorizar.setOnClickListener(v -> {
            if (listener != null && bitacora != null) {
                // 🔹 Volver a estado pendiente (2)
                bitacora.setConfirmacion(2);
                listener.onDesautorizar(bitacora);
            }
        });

        btnVolver.setOnClickListener(v -> {
            if (listener != null) {
                listener.onVolver();
            }
        });
    }
    @Override
    public void onResume() {
        super.onResume();
        if (bitacora != null) {
            cargarDatosDesdeServidor(bitacora.getId());
        }
    }
    }