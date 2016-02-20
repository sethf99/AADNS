package hacksquad.carcrash;

import android.location.Location;
import android.os.AsyncTask;
import android.telephony.SmsManager;

/**
 * Created by s160439 on 20/2/2016.
 */
public class SMSTask extends AsyncTask<Location, Void, Void> {

    @Override
    protected Void doInBackground(Location... params) {
        SmsManager sms = SmsManager.getDefault();
        double lat = 0;
        double lon = 0;
        if(params[0] != null) {
             lat = params[0].getLatitude();
             lon= params[0].getLongitude();
        }
        else{
            lat = 22.2377285;
            lon = 114.22280090000004;
            System.out.println("Emulator had no last checked location - using demo coords");
        }


        sms.sendTextMessage("94327857", null, "The contact at this number has been involved in a car" +
                " crash at Lat: " + lat + " Lon: " + lon, null, null);
        return null;
    }
}
