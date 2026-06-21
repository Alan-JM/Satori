package com.example.satori_by_aristo.Anticipos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.satori_by_aristo.Bitacoras.Operador.SesionUsuario;
import com.example.satori_by_aristo.R;
import com.example.satori_by_aristo.SesionActual;
import com.google.android.material.button.MaterialButton;

public class AccionesAnticipoFragment extends Fragment {

    private TextView tvFolio, tvFecha, tvUnidadTrans, tvOperador;
    private TextView tvImporte, tvConcepto, tvObservaciones;
    private MaterialButton btnModificar, btnEnviar, btnEliminar, btnVolver;

    private OnAccionAnticipoListener listener;
    private Anticipo anticipo;

    public interface OnAccionAnticipoListener {
        void onModificar(Anticipo anticipo);
        void onEnviar(Anticipo anticipo);
        void onEliminar(Anticipo anticipo);
        void onVolver();
    }

    public void setOnAccionAnticipoListener(OnAccionAnticipoListener listener) {
        this.listener = listener;
    }

    public void setAnticipo(Anticipo anticipo) {
        this.anticipo = anticipo;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_acciones_anticipo, container, false);

        inicializarVistas(view);
        cargarDatos();
        configurarBotones();

        return view;
    }

    private void inicializarVistas(View view) {
        tvFolio = view.findViewById(R.id.tvFolioDetalleAnticipo);
        tvFecha = view.findViewById(R.id.tvFechaDetalleAnticipo);
        tvUnidadTrans = view.findViewById(R.id.tvOperadorDetalleAnticipo);
        tvOperador = view.findViewById(R.id.tvNombreOperadorDetalleAnticipo);
        tvImporte = view.findViewById(R.id.tvImporteDetalleAnticipo);
        tvConcepto = view.findViewById(R.id.tvConceptoDetalleAnticipo);
        tvObservaciones = view.findViewById(R.id.tvObservacionesDetalleAnticipo);

        btnModificar = view.findViewById(R.id.btnModificarAnticipo);
        btnEnviar = view.findViewById(R.id.btnEnviarAnticipo);
        btnEliminar = view.findViewById(R.id.btnEliminarAnticipo);
        btnVolver = view.findViewById(R.id.btnVolverAnticipo);
    }

    private void cargarDatos() {
        if (anticipo == null) return;

        tvFolio.setText("Folio: " + anticipo.getIdFolio());
        tvFecha.setText("Fecha: " + anticipo.getFecha());
        tvUnidadTrans.setText("Unidad: " + anticipo.getUnidadTrans());
        tvOperador.setText("Operador: " + anticipo.getOperador());
        tvImporte.setText(String.format("Importe: $%.2f", anticipo.getImporte()));
        tvConcepto.setText("Concepto: " + anticipo.getConcepto());
        tvObservaciones.setText("Observaciones: " + anticipo.getObservaciones());

        if (anticipo.getConfirmacion() != null && anticipo.getConfirmacion() == 1) {
            btnModificar.setVisibility(View.GONE);
            btnEnviar.setVisibility(View.GONE);
            btnEliminar.setVisibility(View.GONE);
        } else {
            btnModificar.setVisibility(View.VISIBLE);
            btnEnviar.setVisibility(View.VISIBLE);
            btnEliminar.setVisibility(View.VISIBLE);
        }
    }

    private void configurarBotones() {
        btnModificar.setOnClickListener(v -> {
            if (listener != null && anticipo != null && anticipo.getConfirmacion() == 0) {
                listener.onModificar(anticipo);
            }
        });

        btnEnviar.setOnClickListener(v -> {
            if (listener != null && anticipo != null && anticipo.getConfirmacion() == 0) {
                anticipo.setTelefonoAdmin(SesionUsuario.getTelefonoAdmin());
                anticipo.setTelefono(SesionUsuario.getTelefonoAdmin());
                anticipo.setTelefonop(SesionActual.obtenerInstancia().getTelefono());
                listener.onEnviar(anticipo);
            }
        });

        btnEliminar.setOnClickListener(v -> {
            if (listener != null && anticipo != null && anticipo.getConfirmacion() == 0) {
                listener.onEliminar(anticipo);
            }
        });

        btnVolver.setOnClickListener(v -> {
            if (listener != null) {
                listener.onVolver();
            }
        });
    }
}
