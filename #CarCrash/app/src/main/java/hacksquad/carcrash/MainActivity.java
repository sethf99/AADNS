package hacksquad.carcrash;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    boolean on = false;
    String newLastTimestamp = "LOOOLZ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LastTimestampInteractions timestampInteractions = null;

        final Button button = (Button) findViewById(R.id.ON_OFF);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (on) {
                    button.setText(R.string.TurnsOn);
                    on = !on;
                } else {
                    button.setText(R.string.TurnsOff);
                    new CrashCheckTask().execute((newLastTimestamp));
                    on = !on;

                }
            }
        });
        new CrashCheckTask().execute(newLastTimestamp);
    }


    public void onTaskComplete(CrashData crashData) {
        newLastTimestamp = crashData.getTimestamp();
        if (on && crashData.isCrashed() == false) {
            new CrashCheckTask().execute(crashData.getTimestamp());
        } else {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            new SMSTask().execute(locationManager.getLastKnownLocation(locationManager.getBestProvider(new Criteria(),false)));
        }


    }

    public class CrashCheckTask extends AsyncTask<String, Void, CrashData> {

        @Override
        protected CrashData doInBackground(String... params) {

            String oldTimeStamp = params[0];

            URL myURL = null;
            try {
                myURL = new URL(
                        "http://api-m2x.att.com/v2/devices/bfd20b378bff7e258de2be669c49ca65/streams/impact");
                HttpURLConnection urlConnection = (HttpURLConnection) myURL
                        .openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("X-M2X-KEY",
                        "943a63dee51d1266771e2e44ea8e3efd");
                urlConnection.getInputStream();


                BufferedReader bs = new BufferedReader(new InputStreamReader(
                        urlConnection.getInputStream()));

                String text = "";
                String temp = "";
                while ((temp = bs.readLine()) != null) {
                    text += temp;
                }

                JSONObject jsonObject = new JSONObject(text);
                String newTimestamp = jsonObject.getString("latest_value_at");

                if (!newTimestamp.equals(oldTimeStamp)) {

                    return new CrashData(newTimestamp, true);
                }
                return new CrashData(newTimestamp, false);


            } catch (Exception e) {
                e.printStackTrace();
            }
            return new CrashData(oldTimeStamp, false);
        }

        @Override
        protected void onPostExecute(CrashData data) {

            onTaskComplete(data);
        }
    }

}
