package com.example.satori_by_aristo.Bitacoras.Operador;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.components.AxisBase;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.satori_by_aristo.R;
import com.example.satori_by_aristo.SesionActual;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GeneralFragment extends Fragment {

    private String[] telefonos;
    private float[] kmTotales;
    private float[] gastoTotales;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("GeneralFragment", "onCreateView: inflando layout");
        View view = inflater.inflate(R.layout.fragment_general, container, false);

        BarChart chartKm = view.findViewById(R.id.chartKm);
        BarChart chartGasto = view.findViewById(R.id.chartGasto);
        PieChart chartPieGeneral = view.findViewById(R.id.chartPieGeneral);

        cargarDatos(chartKm, chartGasto, chartPieGeneral);

        return view;
    }

    private void cargarDatos(BarChart chartKm, BarChart chartGasto, PieChart chartPieGeneral) {
        String telAdminSesion = SesionActual.obtenerInstancia().getTelefono();
        Log.d("GeneralFragment", "Teléfono del admin en sesión: " + telAdminSesion);

        String url = getString(R.string.base_url) + "bitacoras/admin/" + telAdminSesion;
        Log.d("GeneralFragment", "URL llamada (admin): " + url);

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    Log.d("GeneralFragment", "Respuesta recibida. Registros: " + response.length());

                    if (response.length() == 0) {
                        Log.w("GeneralFragment", "No hay bitácoras confirmadas para este administrador");
                        return;
                    }

                    // Agrupar por teléfono
                    Map<String, Float> kmPorTelefono = new HashMap<>();
                    Map<String, Float> gastoPorTelefono = new HashMap<>();

                    int viajesMenores50 = 0;
                    int viajes50a150 = 0;
                    int viajesMayores150 = 0;

                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject obj = response.getJSONObject(i);
                            String telefono = obj.optString("telefono"); // clave para agrupar
                            float km = (float) obj.optDouble("distanciaTotal", 0);
                            float gasto = (float) obj.optDouble("granTotal", 0);

                            // Sumar por teléfono
                            kmPorTelefono.put(telefono, kmPorTelefono.getOrDefault(telefono, 0f) + km);
                            gastoPorTelefono.put(telefono, gastoPorTelefono.getOrDefault(telefono, 0f) + gasto);

                            // Clasificación de viajes por distancia
                            if (km < 50) {
                                viajesMenores50++;
                            } else if (km <= 150) {
                                viajes50a150++;
                            } else {
                                viajesMayores150++;
                            }

                        } catch (Exception e) {
                            Log.e("GeneralFragment", "Error parseando bitácora", e);
                        }
                    }

                    // Convertir a arrays
                    telefonos = kmPorTelefono.keySet().toArray(new String[0]);
                    kmTotales = new float[telefonos.length];
                    gastoTotales = new float[telefonos.length];

                    for (int i = 0; i < telefonos.length; i++) {
                        kmTotales[i] = kmPorTelefono.get(telefonos[i]);
                        gastoTotales[i] = gastoPorTelefono.get(telefonos[i]);
                    }

                    // Variables efectivamente finales para usarlas en el siguiente callback
                    final int finalViajesMenores50 = viajesMenores50;
                    final int finalViajes50a150 = viajes50a150;
                    final int finalViajesMayores150 = viajesMayores150;

                    // --- INICIO DE BÚSQUEDA EN TABLA PERFIL ---
                    String urlPerfiles = getString(R.string.base_url) + "perfil";
                    JsonArrayRequest requestPerfiles = new JsonArrayRequest(
                            Request.Method.GET,
                            urlPerfiles,
                            null,
                            responsePerfiles -> {
                                Map<String, String> mapaNombres = new HashMap<>();
                                for (int j = 0; j < responsePerfiles.length(); j++) {
                                    try {
                                        JSONObject objPerfil = responsePerfiles.getJSONObject(j);
                                        // Guardamos la relación teléfono -> nombre
                                        mapaNombres.put(objPerfil.optString("telefono"), objPerfil.optString("nombre"));
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                                // Creamos un arreglo paralelo a 'telefonos' pero con los nombres
                                final String[] nombresX = new String[telefonos.length];
                                for (int i = 0; i < telefonos.length; i++) {
                                    // Si por algo no se encuentra el perfil, deja el número de teléfono por defecto
                                    nombresX[i] = mapaNombres.containsKey(telefonos[i]) ? mapaNombres.get(telefonos[i]) : telefonos[i];
                                }

                                // === TU CÓDIGO INTACTO PARA DIBUJAR GRÁFICAS (Solo usando nombresX) ===

                                // Gráfico de kilómetros
                                ArrayList<BarEntry> kmEntries = new ArrayList<>();
                                for (int i = 0; i < telefonos.length; i++) {
                                    kmEntries.add(new BarEntry(i, kmTotales[i]));
                                }
                                BarDataSet kmDataSet = new BarDataSet(kmEntries, "Km");
                                chartKm.getDescription().setEnabled(false);

                                kmDataSet.setColor(getResources().getColor(android.R.color.holo_blue_light));
                                chartKm.setData(new BarData(kmDataSet));
                                chartKm.getXAxis().setGranularity(1f);
                                chartKm.getXAxis().setValueFormatter(new ValueFormatter() {
                                    @Override
                                    public String getAxisLabel(float value, AxisBase axis) {
                                        return (value >= 0 && value < nombresX.length) ? nombresX[(int) value] : "";
                                    }
                                });
                                chartKm.invalidate();

                                // Gráfico de gasto total
                                ArrayList<BarEntry> gastoEntries = new ArrayList<>();
                                for (int i = 0; i < telefonos.length; i++) {
                                    gastoEntries.add(new BarEntry(i, gastoTotales[i]));
                                }
                                BarDataSet gastoDataSet = new BarDataSet(gastoEntries, "Gasto");
                                chartGasto.getDescription().setEnabled(false);

                                gastoDataSet.setColor(getResources().getColor(android.R.color.holo_red_light));
                                chartGasto.setData(new BarData(gastoDataSet));
                                chartGasto.getXAxis().setGranularity(1f);
                                chartGasto.getXAxis().setValueFormatter(new ValueFormatter() {
                                    @Override
                                    public String getAxisLabel(float value, AxisBase axis) {
                                        return (value >= 0 && value < nombresX.length) ? nombresX[(int) value] : "";
                                    }
                                });
                                chartGasto.invalidate();

                                // PieChart dinámico
                                ArrayList<PieEntry> pieEntries = new ArrayList<>();
                                pieEntries.add(new PieEntry(finalViajesMenores50, "<50 km"));
                                pieEntries.add(new PieEntry(finalViajes50a150, "50-150 km"));
                                pieEntries.add(new PieEntry(finalViajesMayores150, ">150 km"));

                                PieDataSet pieDataSet = new PieDataSet(pieEntries, "Distribución de viajes");
                                chartPieGeneral.getDescription().setEnabled(false);

                                ArrayList<Integer> colors = new ArrayList<>();
                                colors.add(getResources().getColor(android.R.color.holo_green_light));
                                colors.add(getResources().getColor(android.R.color.holo_orange_light));
                                colors.add(getResources().getColor(android.R.color.holo_purple));
                                pieDataSet.setColors(colors);

                                chartPieGeneral.setData(new PieData(pieDataSet));
                                chartPieGeneral.invalidate();

                                Log.d("GeneralFragment", "Gráficas actualizadas con nombres correctamente");
                            },
                            errorPerfiles -> Log.e("GeneralFragment", "Error al cargar los perfiles para los nombres", errorPerfiles)
                    );

                    // Agregamos la petición de perfiles a la cola
                    Volley.newRequestQueue(requireContext()).add(requestPerfiles);
                    // --- FIN DE BÚSQUEDA EN TABLA PERFIL ---

                },
                error -> Log.e("GeneralFragment", "Error en la petición de bitácoras", error)
        );

        Volley.newRequestQueue(requireContext()).add(request);
    }
}