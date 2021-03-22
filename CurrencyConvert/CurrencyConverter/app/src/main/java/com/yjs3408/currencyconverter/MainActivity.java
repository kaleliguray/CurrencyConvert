package com.yjs3408.currencyconverter;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getName();
    private static final String URL = "https://api.exchangeratesapi.io/latest?base=USD";

    private final Map<String, Double> currencies = new HashMap<>();

    private EditText amountEditText;
    private Spinner fromSpinner;
    private Spinner toSpinner;
    private TextView resultTextView;
    private Button convertButton;
    private TextView lastUpdateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        amountEditText = findViewById(R.id.amount_edit_text);
        fromSpinner = findViewById(R.id.from_spinner);
        toSpinner = findViewById(R.id.to_spinner);
        resultTextView = findViewById(R.id.result_text_view);
        convertButton = findViewById(R.id.convert_button);
        lastUpdateTextView = findViewById(R.id.last_updated_text_View);
    }

    private void setupViews() {
        final ArrayList<String> currenciesList = new ArrayList<>(currencies.keySet());
        Collections.sort(currenciesList);
        ArrayAdapter<String> currenciesArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, currenciesList);
        fromSpinner.setAdapter(currenciesArrayAdapter);
        toSpinner.setAdapter(currenciesArrayAdapter);
        convertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (amountEditText.getText().toString().trim().length() != 0) {
                    String fromKey = (String) fromSpinner.getSelectedItem();
                    String toKey = (String) toSpinner.getSelectedItem();
                    double amount = Double.parseDouble(amountEditText.getText().toString());
                    double dollarValue = amount / currencies.get(fromKey);
                    double result = dollarValue * currencies.get(toKey);
                    resultTextView.setText(String.format("%.2f", result));
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Volley.newRequestQueue(this).add(new JsonObjectRequest(Request.Method.GET, URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject rates = response.getJSONObject("rates");
                            Iterator<String> keys = rates.keys();
                            while (keys.hasNext()) {
                                String key = keys.next();
                                currencies.put(key, rates.getDouble(key));
                            }
                            setupViews();
                            lastUpdateTextView.setText(response.getString("date"));
                        } catch (JSONException e) {
                            Log.e(TAG, "onResponse: ", e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "onErrorResponse: ", error);
                    }
                }));
    }
}
