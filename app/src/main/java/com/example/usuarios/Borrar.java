package com.example.usuarios;

import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Borrar extends AppCompatActivity {

    private String jugadorId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        jugadorId = getIntent().getStringExtra("id");

        mostrarConfirmacion();
    }

    private void mostrarConfirmacion() {
        new AlertDialog.Builder(this)
                .setTitle("Confirmar borrado")
                .setMessage("¿Seguro que quieres borrar este jugador?")
                .setPositiveButton("Sí", (dialog, which) -> borrarJugador())
                .setNegativeButton("No", (dialog, which) -> finish()) // cierra Activity
                .setCancelable(false)
                .show();
    }

    private void borrarJugador() {
        new Thread(() -> {
            HttpURLConnection conn = null;
            try {
                URL url = new URL(Config.SERVER_IP + "/borrar?database=Userfifa&collection=jugadores&id=" + jugadorId);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("DELETE");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);

                int responseCode = conn.getResponseCode();

                BufferedReader reader;
                if (responseCode >= 200 && responseCode < 400) {
                    reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                } else {
                    reader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                }

                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                final String responseText = response.toString();

                runOnUiThread(() -> {
                    Log.d("DEBUG_BORRAR", "Código: " + responseCode + " | Respuesta: " + responseText);

                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        Toast.makeText(Borrar.this, "Jugador borrado correctamente", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(Borrar.this, "Error al borrar: " + responseText, Toast.LENGTH_LONG).show();
                    }
                    finish();
                });

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(Borrar.this, "Error de conexión: " + e.getMessage(), Toast.LENGTH_LONG).show());
            } finally {
                if (conn != null) conn.disconnect();
            }
        }).start();
    }
}
