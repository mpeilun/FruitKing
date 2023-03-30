package hsj.niu.edu.tw.fruit;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class MainActivity2 extends AppCompatActivity {
    ImageView imageView;
    TextView mTextView1, mTextView2;
    HashMap<String, String> type = new HashMap<>();
    HashMap<String, String> code = new HashMap<>();
    ProgressDialog pd;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        imageView = findViewById(R.id.imageView3);
        mTextView1 = findViewById(R.id.textView2);
        mTextView2 = findViewById(R.id.textView3);
        button = findViewById(R.id.button2);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        type.put("apple", "蘋果");
        type.put("banana", "香蕉");
        type.put("guava", "芭樂");
        type.put("mango", "芒果");
        type.put("wax_apple", "蓮霧");
        type.put("other", "其他");

        code.put("apple", "X69");
        code.put("banana", "A1");
        code.put("guava", "P1");
        code.put("mango", "R1");
        code.put("wax_apple", "Q1");

        Bundle bundle = getIntent().getExtras();
        String key = bundle.getString("key");

        if(key.equals("other")){
            Toast.makeText(this, "無法辨識的目標！", Toast.LENGTH_LONG).show();
            this.finish();
        }else {
            int resID = getResources().getIdentifier(key , "drawable", getPackageName());
            imageView.setImageResource(resID);
            System.out.println(getNowDate());
            new JsonTask().execute("https://data.coa.gov.tw/api/v1/AgriProductsTransType/?Start_time=" + getNowDate() + "&End_time=" + getNowDate() + "&CropCode=" + Uri.encode(code.get(key)) + "&MarketName=%E5%8F%B0%E5%8C%97%E4%B8%80");
        }

        mTextView1.setText(type.get(key));
    }

    private class JsonTask extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();

            pd = new ProgressDialog(MainActivity2.this);
            pd.setMessage("Please wait");
            pd.setCancelable(false);
            pd.show();
        }

        protected String doInBackground(String... params) {


            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();


                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                    Log.d("Response: ", "> " + line);   //here u ll get whole response...... :-)

                }

                return buffer.toString();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (pd.isShowing()) {
                pd.dismiss();
            }
            mTextView2.setText(result.split("Avg_Price\": ")[1].split(",")[0]);
        }
    }

    static String getNowDate() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, -1);
        int year = c.get(Calendar.YEAR) - 1911;
        int month = c.get(Calendar.MONTH) + 1;
        int day = c.get(Calendar.DAY_OF_MONTH);

        return year + "."  + String.format("%02d", month) + "." + String.format("%02d", day);
    }

}

