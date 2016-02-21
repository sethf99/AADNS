package hacksquad.carcrash;

import android.os.AsyncTask;
import android.telephony.SmsManager;

/**
 * Created by s160439 on 20/2/2016.
 */
public class SMSTask extends AsyncTask<SMSData, Void, Void> {



    @Override
    protected Void doInBackground(SMSData... params) {
        SmsManager sms = SmsManager.getDefault();
        double lat = 0;
        double lon = 0;
        if(params[0].location != null) {
             lat = params[0].location.getLatitude();
             lon= params[0].location.getLongitude();
        }
        else{
            lat = 22.2377285;
            lon = 114.22280090000004;
            //Emulator does not have lat and lon.
        }

        for(String number : params[0].numbers)
        sms.sendTextMessage(number, null, "The contact at this number has been involved in a car" +
                " crash at Latitude: " + lat + " Longitude: " + lon, null, null);
        return null;
    }
}
