package hacksquad.carcrash;

import android.Manifest;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class CrashService extends Service {
    static boolean on = false;File file;
    static MainActivity mainActivity;
    CountDownTimer countDownTimer;
    AlertDialog ad = null;
    String newLastTimestamp = "";

    public CrashService() {
        MainActivity.crashService = this;
    }

    public void swapOnOff() {
        on = !on;
        if (on) {
            new CrashCheckTask().execute(newLastTimestamp);

        }
    }

    @Override
    public void onCreate() {
        countDownTimer = new CountDownTimer(10000, 1000) {

            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                System.out.println("Finished");
                sendMessage();
            }

        };

        file = new File(getFilesDir(), "data.txt");

        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            if (LastTimestampInteractions.loadLastTimestamp(file) == null) {
                LastTimestampInteractions.setLastTimestamp("Loooolz", openFileOutput("data.txt", Context.MODE_PRIVATE));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void onTaskComplete(CrashData crashData) {
        try {
            newLastTimestamp = crashData.getTimestamp();
            LastTimestampInteractions.setLastTimestamp(crashData.getTimestamp(), openFileOutput("data.txt", Context.MODE_PRIVATE));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (on && crashData.isCrashed() == false) {
            newLastTimestamp = crashData.getTimestamp();
            new CrashCheckTask().execute(crashData.getTimestamp());
        } else if(on) {
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                    .setContentTitle("Crash Detected")
                    .setContentText("Turn off #CarCrash to cancel automatic messaging if this is a mistake.")
                    .setSmallIcon(android.R.drawable.ic_menu_rotate);

            Intent resultIntent = new Intent(this, MainActivity.class);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addParentStack(MainActivity.class);
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent =
                    stackBuilder.getPendingIntent(
                            0,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );
            mBuilder.setContentIntent(resultPendingIntent);
            NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            mNotificationManager.notify(0, mBuilder.build());



                AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
                builder.setMessage("Crash detected: Police will messaged in 30 seconds unless app is turned off");

                ad = builder.create();
                ad.show();
                countDownTimer.start();

        }

    }

    public void sendMessage() {
        ad.dismiss();
        if (on) {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            new SMSTask().execute(locationManager.getLastKnownLocation(locationManager.getBestProvider(new Criteria(), false)));
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
