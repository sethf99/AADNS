package hacksquad.carcrash;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by s160439 on 21/2/2016.
 */
public class PersonInteraction {

    /*
         * Loads the last timestamp from the file described in this object's fileName parameter into its lastTimestamp parameter
         */
    static public String loadPeople(File file) throws IOException {
        return getFileReader(file).readLine();

    }

    /**
     * Sets the last timestamp
     *
     * @param timestamp The string to which the most recent timestamp should be set
     */
    static public void setPeople(String timestamp, FileOutputStream FOS) throws IOException {
        savePeople(FOS, timestamp);
    }

    /*
     * Saves the timestamp to file
     */
    static private void savePeople(FileOutputStream FOS, String timestamp) throws IOException {

        FOS.write(timestamp.getBytes());
        FOS.close();
    }

    /*
     * Gets a BufferedReader object that wraps the file described in this SMSTextManager object's fileName parameter
     * @return A BufferedReader object wrapping the file described in this SMSTextManager object's fileName parameter
     */
    static private BufferedReader getFileReader(File file) throws FileNotFoundException {

        return new BufferedReader(new FileReader(file));
    }
}
