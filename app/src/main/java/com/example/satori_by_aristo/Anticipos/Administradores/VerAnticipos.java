        package com.example.satori_by_aristo.Anticipos.Administradores;

        import android.os.Bundle;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.ListView;
        import android.widget.Toast;

        import androidx.fragment.app.Fragment;
        import androidx.fragment.app.FragmentTransaction;

        import com.android.volley.Request;
        import com.android.volley.RequestQueue;
        import com.android.volley.toolbox.JsonArrayRequest;
        import com.android.volley.toolbox.Volley;
        import com.example.satori_by_aristo.Anticipos.Anticipo;
        import com.example.satori_by_aristo.R;
        import com.example.satori_by_aristo.SesionActual;

        import org.json.JSONException;
        import org.json.JSONObject;

        import java.util.ArrayList;
        import java.util.List;

        public class VerAnticipos extends Fragment {

            private ListView listView;
            private AnticipoAdapter adapter;
            private final List<Anticipo> anticiposRegistrados = new ArrayList<>();
            private String BASE_URL;

            @Override
            public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                     Bundle savedInstanceState) {
                View view = inflater.inflate(R.layout.fragment_ver_anticipos, container, false);

                BASE_URL = getString(R.string.base_url);

                listView = view.findViewById(R.id.anticiposRegistrados);

                adapter = new AnticipoAdapter(requireContext(), anticiposRegistrados);
                listView.setAdapter(adapter);

                // Al hacer clic en un anticipo, abrir el fragmento de acciones admin
                listView.setOnItemClickListener((parent, itemView, position, id) -> {
                    Anticipo anticipo = anticiposRegistrados.get(position);
                    mostrarAccionesAnticipo(anticipo);
                });

                return view;
            }

            private void mostrarAccionesAnticipo(Anticipo anticipo) {
                AccionesAnticipoAdminFragment fragment = new AccionesAnticipoAdminFragment();
                fragment.setAnticipo(anticipo);

                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.content_frame, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }

            @Override
            public void onResume() {
                super.onResume();
                cargarAnticiposDesdeServidor();
            }

            private void cargarAnticiposDesdeServidor() {
                String url = BASE_URL + "anticipo";

                RequestQueue queue = Volley.newRequestQueue(requireContext());

                JsonArrayRequest request = new JsonArrayRequest(
                        Request.Method.GET,
                        url,
                        null,
                        response -> {
                            anticiposRegistrados.clear();
                            String telefonoSesion = SesionActual.obtenerInstancia().getTelefono();

                            for (int i = 0; i < response.length(); i++) {
                                try {
                                    JSONObject obj = response.getJSONObject(i);

                                    // 🔹 Obtener los tres campos de teléfono
                                    String telefonoAdmin = obj.optString("telefonoAdmin", "");
                                    String telefono = obj.optString("telefono", "");
                                    String telefonop = obj.optString("telefonop", "");

                                     if (telefonoSesion != null &&
                                            (telefonoSesion.equals(telefonoAdmin) || telefonoSesion.equals(telefonop))) {

                                         int idFolio = obj.optInt("idFolio", -1);

                                         Anticipo anticipo = new Anticipo(
                                                 idFolio,
                                                 obj.optString("fecha", null),
                                                 obj.optString("unidadTrans", null),
                                                 obj.optString("operador", null),
                                                 null,
                                                 obj.optDouble("importe", 0.0),
                                                 obj.optString("concepto", null),
                                                 obj.optString("observaciones", null),
                                                 obj.optInt("confirmacion", 0),
                                                 telefonoAdmin,
                                                 telefono,
                                                 telefonop
                                         );


                                         anticiposRegistrados.add(anticipo);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            adapter.notifyDataSetChanged();
                        },
                        error -> Toast.makeText(getContext(), "Error cargando anticipos", Toast.LENGTH_SHORT).show()
                );

                queue.add(request);
            }
        }
