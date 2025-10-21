package com.example.usuarios;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class VerJugadores extends AppCompatActivity {

    private RecyclerView recyclerView;
    private JugadorAdapter adapter;
    private ArrayList<Jugador> listaJugadores;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jugadores);

        recyclerView = findViewById(R.id.recyclerJugadores);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        listaJugadores = new ArrayList<>();
        adapter = new JugadorAdapter(listaJugadores);
        recyclerView.setAdapter(adapter);

        obtenerJugadores();
    }

    private void obtenerJugadores() {
        new Thread(() -> {
            try {
                URL url = new URL(Config.SERVER_IP + "/listar?database=Userfifa&collection=jugadores");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                int responseCode = conn.getResponseCode();
                Log.d("DEBUG_API", "Código de respuesta: " + responseCode);

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    Log.d("DEBUG_API", "Respuesta del servidor: " + response.toString());

                    JSONArray jsonArray = new JSONArray(response.toString());
                    ArrayList<Jugador> nuevaLista = new ArrayList<>();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);

                        String id = "";
                        if (obj.has("_id")) {
                            try {
                                id = obj.getJSONObject("_id").optString("$oid", "");
                            } catch (Exception e) {
                                id = obj.optString("_id", "");
                            }
                        }

                        String nombre = obj.optString("nombre", "Sin nombre");
                        String posicion = obj.optString("posicion", "Sin posición");
                        String edad = String.valueOf(obj.optInt("edad", 0));
                        String peso = obj.optString("peso", "0");
                        String altura = obj.optString("altura", "0");
                        String numero = obj.optString("numero", "0");

                        nuevaLista.add(new Jugador(id, nombre, posicion, edad, peso, altura, numero));
                    }

                    Log.d("DEBUG_API", "Jugadores leídos: " + nuevaLista.size());

                    runOnUiThread(() -> {
                        listaJugadores.clear();
                        listaJugadores.addAll(nuevaLista);
                        adapter.notifyDataSetChanged();
                        Toast.makeText(this, "Jugadores cargados: " + listaJugadores.size(), Toast.LENGTH_SHORT).show();
                    });

                } else {
                    runOnUiThread(() ->
                            Toast.makeText(this, "Error al obtener jugadores", Toast.LENGTH_SHORT).show());
                }

                conn.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("DEBUG_API", "Error: ", e);
                runOnUiThread(() ->
                        Toast.makeText(this, "Error de conexión", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }


    private static class Jugador {
        private final String id, nombre, posicion, edad, peso, altura, numero;

        public Jugador(String id, String nombre, String posicion, String edad, String peso, String altura, String numero) {
            this.id = id;
            this.nombre = nombre;
            this.posicion = posicion;
            this.edad = edad;
            this.peso = peso;
            this.altura = altura;
            this.numero = numero;
        }

        public String getId() { return id; }
        public String getNombre() { return nombre; }
        public String getPosicion() { return posicion; }
        public String getEdad() { return edad; }
        public String getPeso() { return peso; }
        public String getAltura() { return altura; }
        public String getNumero() { return numero; }
    }


    private class JugadorAdapter extends RecyclerView.Adapter<JugadorAdapter.JugadorViewHolder> {

        private final ArrayList<Jugador> jugadores;

        public JugadorAdapter(ArrayList<Jugador> jugadores) {
            this.jugadores = jugadores;
        }

        @NonNull
        @Override
        public JugadorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.itemjugador, parent, false);
            return new JugadorViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull JugadorViewHolder holder, int position) {
            Jugador jugador = jugadores.get(position);
            holder.tvNombre.setText(jugador.getNombre());
            holder.tvPosicion.setText(jugador.getPosicion());
            holder.imgJugador.setImageResource(R.drawable.mingueza);

            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), JugadorIndividual.class);
                intent.putExtra("id", jugador.getId());
                intent.putExtra("nombre", jugador.getNombre());
                intent.putExtra("posicion", jugador.getPosicion());
                intent.putExtra("edad", jugador.getEdad());
                intent.putExtra("peso", jugador.getPeso());
                intent.putExtra("altura", jugador.getAltura());
                intent.putExtra("numero", jugador.getNumero());
                v.getContext().startActivity(intent);
            });
        }

        @Override
        public int getItemCount() {
            return jugadores.size();
        }

        class JugadorViewHolder extends RecyclerView.ViewHolder {
            ImageView imgJugador;
            TextView tvNombre, tvPosicion;

            public JugadorViewHolder(@NonNull View itemView) {
                super(itemView);
                imgJugador = itemView.findViewById(R.id.imgJugador);
                tvNombre = itemView.findViewById(R.id.tvNombre);
                tvPosicion = itemView.findViewById(R.id.tvPosicion);
            }
        }
    }
}
