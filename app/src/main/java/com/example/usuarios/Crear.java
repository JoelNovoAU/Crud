package com.example.usuarios;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Crear extends AppCompatActivity {

    private TextInputEditText etNombre, etPosicion, etEdad, etPeso, etAltura, etNumero;
    private Button btnGuardar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.crear);

        etNombre = findViewById(R.id.etNombre);
        etPosicion = findViewById(R.id.etPosicion);
        etEdad = findViewById(R.id.etEdad);
        etPeso = findViewById(R.id.etPeso);
        etAltura = findViewById(R.id.etaltura);
        etNumero = findViewById(R.id.etNumero);
        btnGuardar = findViewById(R.id.btnGuardar);

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                guardarJugador();
            }
        });
    }

    private void guardarJugador() {
        final String nombre = etNombre.getText().toString();
        final String posicion = etPosicion.getText().toString();
        final String edad = etEdad.getText().toString();
        final String peso = etPeso.getText().toString();
        final String altura = etAltura.getText().toString();
        final String numero = etNumero.getText().toString();

        if(nombre.isEmpty() || posicion.isEmpty()) {
            Toast.makeText(this, "Nombre y posición son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> {
            try {
                URL url = new URL("http://10.0.2.2:3000/crear");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setDoOutput(true);

                JSONObject document = new JSONObject();
                document.put("nombre", nombre);
                document.put("posicion", posicion);
                document.put("edad", edad);
                document.put("peso", peso);
                document.put("altura", altura);
                document.put("numero", numero);

                JSONObject body = new JSONObject();
                body.put("database", "Userfifa");
                body.put("collection", "jugadores");
                body.put("document", document);

                OutputStream os = conn.getOutputStream();
                os.write(body.toString().getBytes("UTF-8"));
                os.close();

                int responseCode = conn.getResponseCode();
                if(responseCode == HttpURLConnection.HTTP_CREATED) {
                    runOnUiThread(() -> Toast.makeText(this, "Jugador creado!", Toast.LENGTH_SHORT).show());
                } else {
                    runOnUiThread(() -> Toast.makeText(this, "Error al crear jugador", Toast.LENGTH_SHORT).show());
                }

                conn.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "Error de conexión", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }
}
