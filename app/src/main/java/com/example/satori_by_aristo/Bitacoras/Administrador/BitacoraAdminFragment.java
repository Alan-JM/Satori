package com.example.satori_by_aristo.Bitacoras.Administrador;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.satori_by_aristo.Bitacoras.Operador.Bitacora;
import com.example.satori_by_aristo.Bitacoras.Operador.BitacoraAdapter;
import com.example.satori_by_aristo.R;
import com.example.satori_by_aristo.retrofit.ApiClient;
import com.example.satori_by_aristo.retrofit.BitacoraApi;
import com.example.satori_by_aristo.retrofit.BitacoraDto;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BitacoraAdminFragment extends Fragment
        implements AccionesBitacoraAdminFragment.OnAccionBitacoraAdminListener {

    private ListView listView;
    private BitacoraAdapter adapter;
    private List<Bitacora> bitacorasFiltradas = new ArrayList<>();

    private Switch switchVerBitacoras;
    private boolean mostrarProcesadas = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bitacora_admin, container, false);

        listView = view.findViewById(R.id.bitacorasAdminRegistradas);
        adapter = new BitacoraAdapter(getContext(), bitacorasFiltradas);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, itemView, position, id) -> {
            Bitacora bitacora = bitacorasFiltradas.get(position);
            mostrarFragmentAcciones(bitacora);
        });

        switchVerBitacoras = view.findViewById(R.id.switchVerBitacoras);
        switchVerBitacoras.setOnCheckedChangeListener((buttonView, isChecked) -> {
            mostrarProcesadas = isChecked;
            if (isChecked) {
                switchVerBitacoras.setThumbResource(R.drawable.ic_ve);   // ojo abierto
            } else {
                switchVerBitacoras.setThumbResource(R.drawable.ic_nove); // ojo tachado
            }
            cargarBitacorasDesdeServidor();
        });

        String telefonoSesionAdmin = SesionAdmin.getTelefonoAdmin();
        Toast.makeText(requireContext(),
                "Teléfono admin en sesión: " + telefonoSesionAdmin,
                Toast.LENGTH_LONG).show();

        return view;
    }

    private void cargarBitacorasDesdeServidor() {
        BitacoraApi api = ApiClient.getBitacoraApi(requireContext());
        String telefonoSesionAdmin = SesionAdmin.getTelefonoAdmin();

        api.getAllBitacoras().enqueue(new Callback<List<BitacoraDto>>() {
            @Override
            public void onResponse(Call<List<BitacoraDto>> call, Response<List<BitacoraDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    bitacorasFiltradas.clear();

                    for (BitacoraDto dto : response.body()) {
                        if (dto.getTelefonoAdmin() != null &&
                                dto.getTelefonoAdmin().equals(telefonoSesionAdmin)) {

                            Integer confirmacion = dto.getConfirmacion();

                            // Switch apagado → pendientes (confirmacion == 2)
                            if (!mostrarProcesadas && confirmacion != null && confirmacion == 2) {
                                bitacorasFiltradas.add(convertirDto(dto));
                            }
                            // Switch encendido → autorizadas (3) y rechazadas (4)
                            else if (mostrarProcesadas && confirmacion != null &&
                                    (confirmacion == 3 || confirmacion == 4)) {
                                bitacorasFiltradas.add(convertirDto(dto));
                            }
                        }
                    }
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<BitacoraDto>> call, Throwable t) {
                Toast.makeText(requireContext(),
                        "Error de red: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Bitacora convertirDto(BitacoraDto dto) {
        Bitacora b = new Bitacora();
        b.setId(dto.getIdFolio());
        b.setFecha(dto.getFecha() != null ? dto.getFecha().toString() : "");
        b.setOperador(dto.getOperador());
        b.setEco(dto.getUnidadEco());
        b.setCliente(dto.getCliente());
        b.setDestino(dto.getDestino());
        b.setConfirmacion(dto.getConfirmacion());
        return b;
    }

    private void mostrarFragmentAcciones(Bitacora bitacora) {
        AccionesBitacoraAdminFragment fragment = new AccionesBitacoraAdminFragment();
        fragment.setBitacora(bitacora);
        fragment.setOnAccionBitacoraAdminListener(this);

        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.content_frame, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onAutorizar(Bitacora bitacora) {
        bitacora.setConfirmacion(3); // autorizado
        adapter.notifyDataSetChanged();
        getParentFragmentManager().popBackStack();
    }

    @Override
    public void onRechazar(Bitacora bitacora, String motivo) {
        bitacora.setConfirmacion(4); // rechazado
        bitacora.setMotivoRechazo(motivo);
        adapter.notifyDataSetChanged();
        getParentFragmentManager().popBackStack();
    }

    @Override
    public void onDesautorizar(Bitacora bitacora) {
        bitacora.setConfirmacion(2); // vuelve a pendiente
        adapter.notifyDataSetChanged();
        getParentFragmentManager().popBackStack();
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
