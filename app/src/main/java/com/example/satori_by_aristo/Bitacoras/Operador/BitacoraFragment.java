package com.example.satori_by_aristo.Bitacoras.Operador;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.satori_by_aristo.R;
import com.example.satori_by_aristo.retrofit.ApiClient;
import com.example.satori_by_aristo.retrofit.BitacoraApi;
import com.example.satori_by_aristo.retrofit.BitacoraDto;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BitacoraFragment extends Fragment
        implements AccionesBitacoraFragment.OnAccionBitacoraListener {

    private ListView listView;
    private MaterialButton btnRegistrar;
    private BitacoraAdapter adapter;

    public static List<Bitacora> bitacorasRegistradas = new ArrayList<>();
    public static int nextId = 1;

    private Switch switchVerBitacorasOperador;
    private boolean mostrarAceptadas = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bitacora, container, false);

        listView = view.findViewById(R.id.bitacorasRegistradas);
        btnRegistrar = view.findViewById(R.id.RegistrarBitacora);
        switchVerBitacorasOperador = view.findViewById(R.id.switchVerBitacorasOperador);

        adapter = new BitacoraAdapter(requireContext(), bitacorasRegistradas);
        listView.setAdapter(adapter);

        btnRegistrar.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ContrasenaViaje.class);
            startActivity(intent);
        });

        listView.setOnItemClickListener((parent, itemView, position, id) -> {
            if (position >= 0 && position < bitacorasRegistradas.size()) {
                Bitacora bitacora = bitacorasRegistradas.get(position);
                mostrarFragmentAcciones(bitacora);
            }
        });

        // Configuración del switch
        switchVerBitacorasOperador.setOnCheckedChangeListener((buttonView, isChecked) -> {
            mostrarAceptadas = isChecked;
            if (isChecked) {
                switchVerBitacorasOperador.setThumbResource(R.drawable.ic_ve);   // ojo abierto
            } else {
                switchVerBitacorasOperador.setThumbResource(R.drawable.ic_nove); // ojo tachado
            }
            cargarBitacorasDesdeServidor();
        });

        return view;
    }

    private void mostrarFragmentAcciones(Bitacora bitacora) {
        AccionesBitacoraFragment fragment = new AccionesBitacoraFragment();

        Bundle args = new Bundle();
        args.putInt("bitacora_id", bitacora.getId());
        fragment.setArguments(args);

        fragment.setOnAccionBitacoraListener(this);

        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.content_frame, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onModificar(Bitacora bitacora) {
        int confirmacion = bitacora.getConfirmacion();

        if (confirmacion == 0 || confirmacion == 1 || confirmacion == 4) {
            Intent intent = new Intent(getActivity(), NuevaBitacora1.class);
            intent.putExtra("bitacora_id", bitacora.getId());
            startActivity(intent);
        } else if (confirmacion == 2) {
            Toast.makeText(requireContext(),
                    "No se puede modificar una bitácora enviada",
                    Toast.LENGTH_SHORT).show();
        } else if (confirmacion == 3) {
            Toast.makeText(requireContext(),
                    "No se puede modificar una bitácora aceptada",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onEnviar(Bitacora bitacora) {
        int confirmacion = bitacora.getConfirmacion();

        if ((confirmacion == 0 || confirmacion == 1 || confirmacion == 4) && "gris".equals(bitacora.getColor())) {
            BitacoraApi api = ApiClient.getBitacoraApi(requireContext());

            BitacoraDto dto = new BitacoraDto();
            dto.setIdFolio(bitacora.getId());
            dto.setConfirmacion(2);

            api.updateBitacora(bitacora.getId(), dto).enqueue(new Callback<BitacoraDto>() {
                @Override
                public void onResponse(Call<BitacoraDto> call, Response<BitacoraDto> response) {
                    if (response.isSuccessful()) {
                        bitacora.setColor("amarillo"); // marcar como enviada
                        adapter.notifyDataSetChanged();

                        Toast.makeText(requireContext(), "Bitácora enviada correctamente", Toast.LENGTH_SHORT).show();
                        getParentFragmentManager().popBackStack();
                    } else {
                        Toast.makeText(requireContext(), "Error enviando bitácora: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<BitacoraDto> call, Throwable t) {
                    Toast.makeText(requireContext(), "Fallo de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else if (confirmacion == 2) {
            Toast.makeText(requireContext(), "No se puede enviar una bitácora ya enviada", Toast.LENGTH_SHORT).show();
        } else if (confirmacion == 3) {
            Toast.makeText(requireContext(), "No se puede enviar una bitácora aceptada", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(requireContext(), "Esta bitácora no está en estado válido para enviar", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onEliminar(Bitacora bitacora) {
        if ("gris".equals(bitacora.getColor())) {
            bitacorasRegistradas.remove(bitacora);
            adapter.notifyDataSetChanged();
            Toast.makeText(requireContext(),
                    "Bitácora eliminada",
                    Toast.LENGTH_SHORT).show();
            getParentFragmentManager().popBackStack();
        } else {
            Toast.makeText(requireContext(),
                    "No se puede eliminar una bitácora enviada",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void cargarBitacorasDesdeServidor() {
        BitacoraApi api = ApiClient.getBitacoraApi(requireContext());

        api.getAllBitacoras().enqueue(new Callback<List<BitacoraDto>>() {
            @Override
            public void onResponse(Call<List<BitacoraDto>> call, Response<List<BitacoraDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    bitacorasRegistradas.clear();

                    String telefonoSesion = SesionUsuario.getTelefonoP();
                    Toast.makeText(requireContext(), "TelefonoP en sesión: " + telefonoSesion, Toast.LENGTH_LONG).show();

                    for (BitacoraDto dto : response.body()) {
                        if (dto.getTelefono() != null && dto.getTelefono().equals(telefonoSesion)) {
                            int confirmacion = dto.getConfirmacion() != null ? dto.getConfirmacion() : 0;

                            // Switch apagado → mostrar confirmacion 1, 2 o 4
                            if (!mostrarAceptadas && (confirmacion == 1 || confirmacion == 2 || confirmacion == 4)) {
                                bitacorasRegistradas.add(convertirDto(dto));
                            }
                            // Switch encendido → mostrar solo confirmacion 3
                            else if (mostrarAceptadas && confirmacion == 3) {
                                bitacorasRegistradas.add(convertirDto(dto));
                            }
                        }
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(requireContext(), "Error cargando bitácoras: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<BitacoraDto>> call, Throwable t) {
                Toast.makeText(requireContext(), "Fallo de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Bitacora convertirDto(BitacoraDto dto) {
        Bitacora b = new Bitacora();
        b.setId(dto.getIdFolio());
        b.setFecha(dto.getFecha().toString());
        b.setOperador(dto.getOperador());
        b.setEco(dto.getUnidadEco());
        b.setCliente(dto.getCliente());
        b.setDestino(dto.getDestino());
        b.setAyudantes(dto.getAyudantes());
        b.setOdometroInicial(dto.getOdometroInicial());
        b.setOdometroFinal(dto.getOdometroFinal());
        b.setDistanciaTotal(dto.getDistanciaTotal());

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
        b.setConfirmacion(dto.getConfirmacion() != null ? dto.getConfirmacion() : 0);

        return b;
    }

    @Override
    public void onVolver() {
        getParentFragmentManager().popBackStack();
    }

    @Override
    public void onResume() {
        super.onResume();
        cargarBitacorasDesdeServidor();
    }
}
