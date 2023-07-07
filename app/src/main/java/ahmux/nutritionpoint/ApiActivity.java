package ahmux.nutritionpoint;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import android.util.Log;

public class ApiActivity extends AppCompatActivity {

    String food;
    EditText et;
    TextView tv1, tv2,tv3, tv4, tv5;
    View v1, v2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_api);

        et = (EditText)findViewById(R.id.editText);
        tv1 = (TextView)findViewById(R.id.textView13);
        tv2 = (TextView)findViewById(R.id.textView16);
        tv3 = (TextView)findViewById(R.id.textView17);
        tv4 = (TextView)findViewById(R.id.textView18);
        tv5 = (TextView)findViewById(R.id.textView19);

    }


    public void calculateClk(View view) {
        food = et.getText().toString();
        Toast.makeText(this, "Searching...", Toast.LENGTH_SHORT).show();
        new MyAsyncTask().execute();
    }


    /* #####AsyncTask Subclass################################################################### */
    private class MyAsyncTask extends AsyncTask<String, String, String>{

        @Override
        protected String doInBackground(String... strings) {

            String allStrings;
            try{

                String requestBody = "{\"query\":\"" + food + "\"}";
                URL myUrl = new URL("https://trackapi.nutritionix.com/v2/natural/nutrients");
                HttpURLConnection connection = (HttpURLConnection) myUrl.openConnection();

                // Set the connection properties
                connection.setRequestMethod("POST");
                connection.setConnectTimeout(700);
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setRequestProperty("Content-Type", "application/json");

                // Set additional headers
                connection.setRequestProperty("x-app-key", "1c6fecc7eea74ae3fd66350746f41dc3");
                connection.setRequestProperty("x-app-id", "20386995");
                connection.setRequestProperty("x-remote-user-id", "Venu");

                // Write the request body to the conn ection's output stream
                OutputStream outputStream = connection .getOutputStream();
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
                outputStreamWriter.write(requestBody);
                outputStreamWriter.flush();
                connection.setConnectTimeout(700);
                connection.connect();

                //Create a new InputStreamReader
                InputStreamReader streamReader = new InputStreamReader(connection.getInputStream());
                //Create a new buffered reader and String Builder
                BufferedReader reader = new BufferedReader(streamReader);

                String inputLine;
                StringBuilder stringBuilder = new StringBuilder();
                //Check if the line we are reading is not null
                while((inputLine = reader.readLine()) != null){
                    Log.e("api", inputLine);
                    stringBuilder.append(inputLine);
                }
                reader.close();
                streamReader.close();
                allStrings = stringBuilder.toString();
                publishProgress(allStrings);

            }catch(Exception e){}
            return "";
        }

        @Override
        protected void onProgressUpdate(String... values) {
            Log.d("hi", "onProgressUpdate: ");
            try {
                JSONObject jsonObject = new JSONObject(values[0]);

                JSONArray foodsArray = jsonObject.getJSONArray("foods");

                for (int i = 0; i < foodsArray.length(); i++) {
                    JSONObject foodObject = foodsArray.getJSONObject(i);

                    String name = foodObject.getString("food_name");
                    double calories = foodObject.getDouble("nf_calories");
                    double fat = foodObject.getDouble("nf_total_fat");
                    // Retrieve other desired values in a similar manner

                    // Do something with the retrieved values
                    // For example, you can log them or display them in a TextView

                    // Update the TextViews with the retrieved data
                    tv2.setText("Nutrition Facts");
                    tv3.setText("Amount: " + name);
                    tv4.setText("Calories: " + calories);
                    tv5.setText("Total Fat: " + fat);

                    // Show the views
                    v1 = findViewById(R.id.view);
                    v1.setVisibility(View.VISIBLE);
                    v2 = findViewById(R.id.view);
                    v2.setVisibility(View.VISIBLE);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
