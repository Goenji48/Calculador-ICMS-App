package com.goenji48.icmscalculator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class MainActivity extends AppCompatActivity {

    private EditText productInput;
    private Button btnStateSelect;
    private TextView currentICMS, currentProductValue, calcResultText;
    private SharedPreferences sharedPreferences;
    private String[] stateList, icmsValueList;
    private int statePosition;
    private float currentIValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnStateSelect = findViewById(R.id.btnStateSelect);
        btnStateSelect.setOnClickListener(v -> {
            selectState();
        });

        productInput = findViewById(R.id.product_input);
        productInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentProductValue.setText("R$ " + s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        currentICMS = findViewById(R.id.current_icms);
        currentProductValue = findViewById(R.id.current_product_value);

        calcResultText = findViewById(R.id.result_text);

        stateList = getResources().getStringArray(R.array.state_list_array);
        icmsValueList = getResources().getStringArray(R.array.icms_value_array);

        sharedPreferences = getSharedPreferences("current_state_save", Context.MODE_PRIVATE);

        statePosition = sharedPreferences.getInt("STATE_POSITION", 0);
        loadStateItem(statePosition);

        Button btnCalculate = findViewById(R.id.btnCalculate);
        btnCalculate.setOnClickListener(v -> {
            getProductValue();
        });
    }

    private void getProductValue() {
        if (!productInput.getText().toString().trim().isEmpty()) {
            float product = Float.parseFloat(productInput.getText().toString());
            currentProductValue.setText("R$ " + product);
            calculateICMS(product, currentIValue);
        } else {
            Toast.makeText(getApplicationContext(), "Insira o valor do produto no campo", Toast.LENGTH_SHORT).show();
        }
    }

    private void selectState() {
        AlertDialog dialog = new MaterialAlertDialogBuilder(new ContextThemeWrapper(this, R.style.DefaultAlertDialogStyle))
                .setTitle("Estado")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setSingleChoiceItems(stateList, statePosition, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                statePosition = which;
                sharedPreferences.edit().putInt("STATE_POSITION", statePosition).apply();
                loadStateItem(statePosition);
            }
        }).create();
        dialog.show();
    }

    private void loadStateItem(int pos) {
        btnStateSelect.setText(stateList[pos]);
        currentIValue = Float.parseFloat(icmsValueList[pos]);
        currentICMS.setText(currentIValue + " %");
    }

    private void calculateICMS(float product, float icms) {
        float finalResult = (product * (icms / 100)) + product;
        calcResultText.setVisibility(View.VISIBLE);
        calcResultText.setText("Valor total do produto com ICMS: R$ " +
                String.format("%.2f", finalResult));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.about) {
            aboutDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    private void aboutDialog() {
        AlertDialog dialog = new MaterialAlertDialogBuilder(new ContextThemeWrapper(this, R.style.DefaultAlertDialogStyle))
                .setTitle("Sobre")
                .setMessage("* O aplicativo não opera com o valor de Imposto de Importação" +
                        " (60% do valor do produto para acima de $50) para cálculo de ICMS *" + "\n\n\n\n" + "Criador: https://github.com/Goenji48")
                .setPositiveButton("Fechar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();

        dialog.show();
    }
}