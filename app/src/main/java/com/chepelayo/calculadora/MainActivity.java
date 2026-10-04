package com.chepelayo.calculadora;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.view.inputmethod.InputMethodManager;
import android.content.Context;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private EditText etOperando1, etOperando2;
    private TextView tvResultado;
    private ImageButton btnSumar, btnRestar, btnMultiplicar, btnDividir;
    private Button btnCambiar;
    private Button btnLimpiar;
    private boolean modoCambiado = false;
    private int botonSeleccionadoId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etOperando1 = findViewById(R.id.etOperando1);
        etOperando2 = findViewById(R.id.etOperando2);
        tvResultado = findViewById(R.id.tvResultado);
        btnSumar = findViewById(R.id.btnSumar);
        btnRestar = findViewById(R.id.btnRestar);
        btnDividir = findViewById(R.id.btnDividir);
        btnMultiplicar = findViewById(R.id.btnMultiplicar);
        btnCambiar = findViewById(R.id.btnCambiar);
        btnLimpiar = findViewById(R.id.btnLimpiar);

        // Cada botón calcula según el modo actual
        btnSumar.setOnClickListener(v -> {
            seleccionar(btnSumar);
            calcular(modoCambiado ? '/' : '+');
        });
        btnDividir.setOnClickListener(v -> {
            seleccionar(btnDividir);
            calcular(modoCambiado ? '+' : '/');
        });
        btnRestar.setOnClickListener(v -> {
            seleccionar(btnRestar);
            calcular(modoCambiado ? '*' : '-');
        });
        btnMultiplicar.setOnClickListener(v -> {
            seleccionar(btnMultiplicar);
            calcular(modoCambiado ? '-' : '*');
        });

        btnCambiar.setOnClickListener(v -> cambiarOperadores());
        btnLimpiar.setOnClickListener(v -> limpiar());
    }

    // Mantenemos los datos al rotar la pantalla
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("operando1", etOperando1.getText().toString());
        outState.putString("operando2", etOperando2.getText().toString());
        outState.putString("resultado", tvResultado.getText().toString());
        outState.putBoolean("modoCambiado", modoCambiado);
        outState.putInt("botonSeleccionado", botonSeleccionadoId);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        etOperando1.setText(savedInstanceState.getString("operando1"));
        etOperando2.setText(savedInstanceState.getString("operando2"));
        tvResultado.setText(savedInstanceState.getString("resultado"));
        modoCambiado = savedInstanceState.getBoolean("modoCambiado");
        botonSeleccionadoId = savedInstanceState.getInt("botonSeleccionado");

        // si en modo cambiado, repintar iconos correctos
        if (modoCambiado) {
            btnSumar.setImageResource(R.drawable.dividir_estados);
            btnDividir.setImageResource(R.drawable.sumar_estados);
            btnRestar.setImageResource(R.drawable.multiplicar_estados);
            btnMultiplicar.setImageResource(R.drawable.restar_estados);
        }
        if (botonSeleccionadoId != -1) {
            findViewById(botonSeleccionadoId).setSelected(true);
        }
    }

    private void calcular(char operacion) {
        ocultarTeclado();

        String texto1 = etOperando1.getText().toString();
        String texto2 = etOperando2.getText().toString();

        if (texto1.isEmpty() || texto2.isEmpty()){
            Toast.makeText(this, R.string.campos_vacios, Toast.LENGTH_SHORT).show();
            return;
        }

        double n1, n2;
        try {
            n1 = Double.parseDouble(texto1);
            n2 = Double.parseDouble(texto2);
        } catch (NumberFormatException e) {
            Toast.makeText(this, R.string.numero_no_valido, Toast.LENGTH_SHORT).show();
            return;
        }

        double resultado;
        switch (operacion) {
            case '+':
                resultado = n1 + n2;
                break;
            case '-':
                resultado = n1 - n2;
                break;
            case '/':
                if (n2 == 0) {
                    Toast.makeText(this, R.string.division_cero, Toast.LENGTH_SHORT).show();
                    return;
                }
                resultado = n1 / n2;
                break;
            case '*':
                resultado = n1 * n2;
                break;
            default:
                return;
        }

        tvResultado.setText(formatear(resultado));
    }

    // Muestra 5 en vez de 5.0 cuando el resultado es entero
    private String formatear(double valor) {
        if (valor == Math.floor(valor) && !Double.isInfinite(valor) && Math.abs(valor) < 1e15) {
            return String.valueOf((long) valor);
        }
        return String.valueOf(valor);
    }

    // Deja en azul solo el botón pulsado
    private void seleccionar(ImageButton elegido) {
        btnSumar.setSelected(false);
        btnRestar.setSelected(false);
        btnDividir.setSelected(false);
        btnMultiplicar.setSelected(false);
        elegido.setSelected(true);
        botonSeleccionadoId = elegido.getId();
    }

    // Fase III: mismos botones, distinta imagen y función
    private void cambiarOperadores() {
        modoCambiado = !modoCambiado;
        if (modoCambiado) {
            btnSumar.setImageResource(R.drawable.dividir_estados);
            btnDividir.setImageResource(R.drawable.sumar_estados);
            btnRestar.setImageResource(R.drawable.multiplicar_estados);
            btnMultiplicar.setImageResource(R.drawable.restar_estados);
        } else {
            btnSumar.setImageResource(R.drawable.sumar_estados);
            btnDividir.setImageResource(R.drawable.dividir_estados);
            btnRestar.setImageResource(R.drawable.restar_estados);
            btnMultiplicar.setImageResource(R.drawable.multiplicar_estados);
        }

        // Quitamos el azul y el resultado anterior para que no confunda
        seleccionar(btnSumar);
        btnSumar.setSelected(false);
        tvResultado.setText(R.string.resultado);
        botonSeleccionadoId = -1;
    }

    private void limpiar() {
        etOperando1.setText("");
        etOperando2.setText("");
        tvResultado.setText(R.string.resultado);
        btnSumar.setSelected(false);
        btnRestar.setSelected(false);
        btnMultiplicar.setSelected(false);
        btnDividir.setSelected(false);
        botonSeleccionadoId = -1;
    }

    private void ocultarTeclado() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}