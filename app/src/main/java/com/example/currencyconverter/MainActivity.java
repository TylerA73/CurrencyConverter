package com.example.currencyconverter;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import java.text.DecimalFormat;

import cz.msebera.android.httpclient.Header;

/**
 * Application Purpose:
 *      Custom Android application to calculate the value of a conversion from USD to CAD,
 *      and then increased by 20%
 *
 *      Uses the Exchange Rates API in order to determine the conversion rate from USD to CAD,
 *      using USD as the base.
 *
 *      The converted value is calculated using the retrieved rate.
 *
 *      It was requested that the final value be increased by 20% to account for shipping costs.
 */
public class MainActivity extends AppCompatActivity {

    final private String URL = "https://api.exchangeratesapi.io/latest?base=USD&symbols=CAD";
    private float rate = 1.0f;      // exchange rate, default is 1
    private float percent = 1.2f;   // Increase the final value by 20%

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Call the Exchange Rate API
        requestData(URL);
    }

    /**
     * calculate
     *
     * Calculates the result based on the conversion rate and the 20% increase
     *
     * @param v View
     */
    public void calculate(View v) {
        // valueField is the Decimal EditText
        EditText valueField = (EditText)findViewById(R.id.value);

        // Default value and result to 0
        float value = 0;    // Value is the value held in the EditText valueField
        float result = 0;   // Result will be the final result calculated

        // If there is a value in the valueField, parse it to a float
        // and set value to that new value
        if (!valueField.getText().toString().equals(null) && !valueField.getText().toString().equals("")) {
            value = Float.parseFloat(valueField.getText().toString());
        }

        // We want to format the float to only 2 decimal places
        // So we will round it to the nearest hundredth
        // Then set the text of valueField to the new formatted result
        result = value * rate * percent;
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        valueField.setText(df.format(result));
    }

    /**
     * requestData
     *
     * This function is responsible for fetching the exchange rates
     *
     * @param URL String
     */
    private void requestData(String URL) {
        // Asynchronous HTTP Client
        AsyncHttpClient client = new AsyncHttpClient();

        // Fetch the data from the URL as JSON data
        client.get(URL, new JsonHttpResponseHandler() {

            /**
             * onSuccess
             *
             * Function to be called when a successful API request has been made
             *
             * @param statusCode
             * @param headers
             * @param response
             */
            @Override
            public void onSuccess(int statusCode, Header[] headers,
                                  JSONObject response) {
                // called when response HTTP status is "200 OK"
                Log.d("DEBUG", "JSON: " + response.toString());


                try {

                    // Convert the rate from a String to a float
                    rate = Float.parseFloat(response.getJSONObject("rates").getString("CAD"));


                } catch (Exception e) {

                    // Log the error
                    Log.e("ERROR", e.toString());

                }

            }

            /**
             * onFailure
             *
             * Function to be called if the API request fails
             *
             * @param statusCode
             * @param headers
             * @param e
             * @param response
             */
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable e,
                                  JSONObject response) {

                // called when response HTTP status is "4XX" (eg. 401, 403, 404)

                Log.d("DEBUG", "Request fail! Status code: " + statusCode);
                Log.d("DEBUG", "Fail response: " + response);
                Log.e("ERROR", e.toString());

                Toast.makeText(MainActivity.this, "Request Failed",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
