package anandgaur.com.barchartusingvolley;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ProgressDialog pd;
    private DecimalFormat mFormat;
    ArrayList yAxis;
    ArrayList pValues;
    ArrayList dValues;
    ArrayList xAxis1;
    BarEntry pvalues, dvalues;
    BarChart chart;
    BarData data;
    StringRequest stringRequest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pd = new ProgressDialog(MainActivity.this);
        pd.setMessage("loading");
        // Log.d("array",Arrays.toString(fullData));
        chart = (BarChart) findViewById(R.id.chart);
        load_data_from_server();


    }


    public void load_data_from_server() {
        pd.show();
        String url = "https://rawgit.com/anandgaur22/Anand-json/master/GrapJsonFormat.json";
        xAxis1 = new ArrayList<>();
        yAxis = null;
        pValues = new ArrayList<>();
        dValues = new ArrayList<>();

        stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    JSONArray jsonarray = new JSONArray(response);

                    for (int i = 0; i < jsonarray.length(); i++) {

                        JSONObject jsonobject = jsonarray.getJSONObject(i);


                        String petrol = jsonobject.getString("petrol").trim();
                        String date = jsonobject.getString("date").trim();
                        String diesel = jsonobject.getString("diesel").trim();

                        xAxis1.add(date);

                        pvalues = new BarEntry(Float.valueOf(petrol), i);
                        dvalues = new BarEntry(Float.valueOf(diesel), i);

                        pValues.add(pvalues);
                        dValues.add(dvalues);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();


                }

                BarDataSet petrolbarData = new BarDataSet(pValues, "Petrol Price");
                petrolbarData.setColor(Color.rgb(255, 153, 0));

                BarDataSet dieselbarData = new BarDataSet(dValues, "Diesel Price");
                dieselbarData.setColor(Color.rgb(51, 153, 255));

                yAxis = new ArrayList<>();
                yAxis.add(petrolbarData);
                yAxis.add(dieselbarData);
                //dieselbarData.setValueFormatter((ValueFormatter) new GraphActivity());

                String names[] = (String[]) xAxis1.toArray(new String[xAxis1.size()]);
                data = new BarData(names, yAxis);

                chart.setData(data);
                chart.setDescription("");
                chart.animateXY(4000, 4000);


                petrolbarData.setValueFormatter(new MyValueFormatter());
                dieselbarData.setValueFormatter(new MyValueFormatter());
                //chart.invalidate();
                pd.hide();
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error != null) {

                            Toast.makeText(getApplicationContext(), "No Internet Conection ", Toast.LENGTH_LONG).show();
                            pd.hide();
                        }
                    }
                }

        );
        MySingletonBarGraph.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    public class MyValueFormatter implements ValueFormatter {

        private DecimalFormat mFormat;

        public MyValueFormatter() {
            mFormat = new DecimalFormat("###,###,##0.00"); // use one decimal
        }

        @Override

        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            // write your logic here
            return mFormat.format(value) + ""; // e.g. append a dollar-sign
        }
    }

}
