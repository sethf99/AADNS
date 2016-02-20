package thing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;

public class LastTimestampInteractions {
	private String lastTimestamp;
	private String fileName;
	/**
	 * Constructor
	 * @param fileNameI The name of the file which this object will look for the timestamp in
	 */
	LastTimestampInteractions(String fileNameI){
		fileName=fileNameI;
		loadLastTimestamp();
	}
	/*
	 * Loads the last timestamp from the file described in this object's fileName parameter into its lastTimestamp parameter
	 */
	private void loadLastTimestamp(){
		try{
		  lastTimestamp=getFileReader().readLine();
		} catch (Exception e){
	      e.printStackTrace();
		}
	}
	/**
	 * Get the most recent timestamp from the file described in this object's fileName parameter
	 * @return The most recent timestamp, as a string
	 */
	public String getLastTimestamp(){
		return lastTimestamp;
	}
	/**
	 * Sets the last timestamp
	 * @param timestamp The string to which the most recent timestamp should be set
	 */
	public void setLastTimestamp(String timestamp){
		lastTimestamp=timestamp;
		saveTimestamp();
	}
	/*
	 * Saves the timestamp to file
	 */
	private void saveTimestamp(){
		File myFile = new File(getFilesDir().getAbsolutePath());
		FileWriter fw = new FileWriter(myFile+"/"+fileName, false);
		fw.write(lastTimestamp);
	}
	/*
	 * Gets a BufferedReader object that wraps the file described in this SMSTextManager object's fileName parameter
	 * @return A BufferedReader object wrapping the file described in this SMSTextManager object's fileName parameter
	 */
	private BufferedReader getFileReader(){
		try{
			return new BufferedReader(new FileReader(new File(getFilesDir().getAbsolutePath())));
		} catch (Exception e){
			e.printStackTrace();
		}
	}
}
