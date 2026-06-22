package com.example.satori_by_aristo.Bitacoras.Operador;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.satori_by_aristo.R;

import org.json.JSONObject;

import java.util.ArrayList;

public class DetalleOperadorFragment extends Fragment {

    private String nombreOperador;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("DetalleOperador", "onCreateView: inflando layout");
        View view = inflater.inflate(R.layout.fragment_detalle_operador, container, false);

        if (getArguments() != null) {
            nombreOperador = getArguments().getString("nombreOperador", "");
            Log.d("DetalleOperador", "Argumento recibido: nombre/identificador=" + nombreOperador);
        }

        // CORRECCIÓN 1: Se corrigió el cruce de IDs para que cada chart apunte a su vista correcta
        BarChart chartGasto = view.findViewById(R.id.chartGastoPorViaje);
        BarChart chartKm = view.findViewById(R.id.chartKmPorViaje);
        PieChart chartPie = view.findViewById(R.id.chartViajesPorTipo);

        if (nombreOperador != null && !nombreOperador.isEmpty()) {
            cargarDatos(chartGasto, chartKm, chartPie);
        } else {
            Log.e("DetalleOperador", "El nombre/identificador del operador está vacío.");
        }

        return view;
    }

    private void cargarDatos(BarChart chartGasto, BarChart chartKm, PieChart chartPie) {
        // CORRECCIÓN 2: Uri.encode evita que la petición falle si 'nombreOperador' tiene espacios
        String url = getString(R.string.base_url) + "bitacoras/operador/" + Uri.encode(nombreOperador);
        Log.d("DetalleOperador", "URL llamada: " + url);

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    Log.d("DetalleOperador", "Registros recibidos: " + response.length());

                    if (response.length() == 0) {
                        Toast.makeText(getContext(), "No hay viajes registrados para este operador", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    ArrayList<BarEntry> gastoEntries = new ArrayList<>();
                    ArrayList<BarEntry> kmEntries = new ArrayList<>();
                    ArrayList<String> labels = new ArrayList<>();

                    int menos50 = 0, entre50y150 = 0, mas150 = 0;

                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject obj = response.getJSONObject(i);
                            String destino = obj.optString("destino", "Destino");
                            String folio = obj.optString("idFolio", String.valueOf(i));
                            String label = destino + " (" + folio + ")";
                            labels.add(label);

                            float gasto = (float) obj.optDouble("granTotal", 0);
                            float km = (float) obj.optDouble("distanciaTotal", 0);

                            gastoEntries.add(new BarEntry(i, gasto));
                            kmEntries.add(new BarEntry(i, km));

                            // Clasificación para el pie chart
                            if (km < 50) menos50++;
                            else if (km <= 150) entre50y150++;
                            else mas150++;

                        } catch (Exception e) {
                            Log.e("DetalleOperador", "Error parseando bitácora", e);
                        }
                    }

                    // Gráfico de barras: gasto por viaje
                    BarDataSet gastoDataSet = new BarDataSet(gastoEntries, "Gasto por viaje");
                    gastoDataSet.setColor(getResources().getColor(android.R.color.holo_orange_light));
                    BarData gastoData = new BarData(gastoDataSet);
                    chartGasto.setData(gastoData);
                    chartGasto.getXAxis().setGranularity(1f);
                    // Ocultamos la malla de fondo para que se vea más limpio
                    chartGasto.getXAxis().setDrawGridLines(false);
                    chartGasto.getXAxis().setValueFormatter(new ValueFormatter() {
                        @Override
                        public String getAxisLabel(float value, AxisBase axis) {
                            return (value >= 0 && value < labels.size()) ? labels.get((int) value) : "";
                        }
                    });
                    chartGasto.getDescription().setEnabled(false);
                    chartGasto.invalidate();

                    // Gráfico de barras: km por viaje
                    BarDataSet kmDataSet = new BarDataSet(kmEntries, "Km por viaje");
                    kmDataSet.setColor(getResources().getColor(android.R.color.holo_blue_light));
                    BarData kmData = new BarData(kmDataSet);
                    chartKm.setData(kmData);
                    chartKm.getXAxis().setGranularity(1f);
                    chartKm.getXAxis().setDrawGridLines(false);
                    chartKm.getXAxis().setValueFormatter(new ValueFormatter() {
                        @Override
                        public String getAxisLabel(float value, AxisBase axis) {
                            return (value >= 0 && value < labels.size()) ? labels.get((int) value) : "";
                        }
                    });
                    chartKm.getDescription().setEnabled(false);
                    chartKm.invalidate();

                    // Gráfico de pastel: clasificación de viajes
                    ArrayList<PieEntry> pieEntries = new ArrayList<>();
                    if (menos50 > 0) pieEntries.add(new PieEntry(menos50, "<50 km"));
                    if (entre50y150 > 0) pieEntries.add(new PieEntry(entre50y150, "50-150 km"));
                    if (mas150 > 0) pieEntries.add(new PieEntry(mas150, ">150 km"));

                    PieDataSet pieDataSet = new PieDataSet(pieEntries, "Distribución de viajes");
                    ArrayList<Integer> colors = new ArrayList<>();
                    colors.add(getResources().getColor(android.R.color.holo_green_light));
                    colors.add(getResources().getColor(android.R.color.holo_orange_light));
                    colors.add(getResources().getColor(android.R.color.holo_red_light));
                    pieDataSet.setColors(colors);

                    chartPie.setData(new PieData(pieDataSet));
                    chartPie.getDescription().setEnabled(false);
                    chartPie.invalidate();

                    Log.d("DetalleOperador", "Gráficas actualizadas correctamente");
                },
                error -> {
                    Log.e("DetalleOperador", "Error en la petición: " + error.getMessage(), error);
                    Toast.makeText(getContext(), "Error al cargar datos del servidor", Toast.LENGTH_SHORT).show();
                }
        );

        Volley.newRequestQueue(requireContext()).add(request);
    }
}