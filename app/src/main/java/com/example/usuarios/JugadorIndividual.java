package com.example.usuarios;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Button;
import android.widget.TextView;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;

public class JugadorIndividual extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jugadorindividual);

        ImageView imgJugador = findViewById(R.id.imgJugador);
        TextView tvNombre = findViewById(R.id.tvNombre);
        TextView tvPosicion = findViewById(R.id.tvPosicion);
        TextView tvEdad = findViewById(R.id.tvEdad);
        TextView tvPeso = findViewById(R.id.tvPeso);
        TextView tvAltura = findViewById(R.id.tvAltura);
        TextView tvNumero = findViewById(R.id.tvNumero);

        Button btnBorrar = findViewById(R.id.btnBorrar);
        Button btnEditar = findViewById(R.id.btnEditar);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            tvNombre.setText(extras.getString("nombre", "--"));
            tvPosicion.setText("Posición: " + extras.getString("posicion", "--"));
            tvEdad.setText("Edad: " + extras.getString("edad", "--"));
            tvPeso.setText("Peso: " + extras.getString("peso", "--") + " kg");
            tvAltura.setText("Altura: " + extras.getString("altura", "--") + " m");
            tvNumero.setText("Número: " + extras.getString("numero", "--"));
            imgJugador.setImageResource(R.drawable.mingueza);
        }

        btnBorrar.setOnClickListener(v -> {
            Intent intent = new Intent(this, Borrar.class);
            intent.putExtra("id", getIntent().getStringExtra("id"));
            startActivity(intent);
        });


    }
}
