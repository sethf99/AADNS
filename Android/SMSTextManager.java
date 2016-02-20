package thing;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * @author s160274 - Chris
 * To use this class: Put a file with the line
Police &#& 999 &~& I've been in a car crash at \l. Please send help.
 * then call the addPersonToCallList method to fill the list
 * then call sendSMSToAll to send all the SMSes.
 */
public class SMSTextManager {
	private String fileName;
	private String location;
	/**
	 * Constructs a new SMSTextManager object.
	 * @param fileNameI File name to be accessed. Assumed to be in the assets directory.
	 */
	public SMSTextManager(String fileNameI){
		fileName=fileNameI;
	}
	/**
	 * Changes a person's name
	 * @param oldName The name which will be changed
	 * @param newName The name to be changed to
	 * @throws IllegalArgumentException if newName is a name that already exists
	 */
	public void modifyPersonName(String oldName, String newName) throws IllegalArgumentException{
		ArrayList<Person> data = new ArrayList<Person>();
		try{
			data=loadData();
		} catch (Exception e){
			e.printStackTrace();
		}
		for(Person i : data){
			if(i.getName().equals(newName)){
				throw new IllegalArgumentException(newName);//Throw exception if the new name already exists
			}
		}
		modifyPersonParameter(oldName,newName,0);
	}
	/**
	 * Changes a person's phone number
	 * @param name The name of the person whose phone number will be changed
	 * @param newPhoneNumber The new phone number
	 * @throws IllegalArgumentException if one of the parameters contains the illegal escape characters " &#& " or " &~& "
	 */
	public void modifyPersonNumber(String name, String newPhoneNumber) throws IllegalArgumentException{
		modifyPersonParameter(name,newPhoneNumber,1);
	}
	/**
	 * Changes the message to be sent to a person
	 * @param name The name of the person whose message will be changed
	 * @param newMessage The new message
	 * @throws IllegalArgumentException if one of the parameters contains the illegal escape characters " &#& " or " &~& "
	 */
	public void modifyPersonMessage(String name, String newMessage) throws IllegalArgumentException{
		modifyPersonParameter(name,newMessage,2);
	}
	/*
	 * Changes a single parameter for a single person
	 * @param personName The name of the person who will have a parameter changed
	 * @param newParameter The new parameter
	 * @param whichParameter Which parameter (name, phone number, message)
	 * @throws IllegalArgumentException if one of the parameters contains the illegal escape characters " &#& " or " &~& "
	 */
	private void modifyPersonParameter(String personName,String newParameter, int whichParameter) throws IllegalArgumentException{
		if(newParameter.contains(" &#& ") || newParameter.contains(" &~& ")){
			throw new IllegalArgumentException(newParameter);
		}
		try {
		  // input the file contents to the ArrayList
		  BufferedReader file = getFileReader();
		  String line;
		  ArrayList<String> lines = new ArrayList<String>();
		  while ((line = file.readLine()) != null){
		    lines.add(line);
		  }
		  file.close();
		  int index=-1;
		  for(int i=0; i<lines.size();i++){
		    if(lines.get(i).contains(personName+ " &#& ")){
		      index=i;
		    }
		  }
		  if(index!=-1){//If the target name was found
		    String newLine="";
		    switch(whichParameter){
		    case 0:
		    	newLine=newParameter + lines.get(index).substring(personName.length());
		    	break;
		    case 1:
		    	newLine=lines.get(index).substring(0, lines.get(index).indexOf(" &#& ")+5)+newParameter+lines.get(index).substring(lines.get(index).indexOf(" &~& "));
		    	break;
		    case 2:
		    	newLine=lines.get(index).substring(0, lines.get(index).indexOf(" &~& ")+5)+newParameter;
		    	break;
		    }
		    lines.set(index, newLine);
		  } else {
			  throw new IllegalArgumentException(personName);//Throw illegalArgException because the name to modify doesn't exist
		  }
		  String fullList="";//Compile our arraylist into a single string
		  for(String i : lines){
			  fullList=fullList+i+"\n";
		  }
		  File myFile = new File(getFilesDir().getAbsolutePath()); //Use filewriter to overwrite
		  FileWriter fw = new FileWriter(myFile+"/"+fileName, false);
		  fw.write(fullList);
		  fw.close();
		  } catch (Exception e) {
		        e.printStackTrace();
		  }
	}
	/**
	 * Adds a person, their phone number, and their message to the list of people to sms in event of a crash
	 * @param name The name of the person
	 * @param phoneNumber The person's phone number as a String
	 * @param message The message to send the person
	 * @throws IllegalArgumentException if one of the parameters contains the illegal escape characters " &#& " or " &~& "
	 */
	public void addPersonToCallList(String name, String phoneNumber,String message) throws IllegalArgumentException{
		if(name.contains(" &#& ") || name.contains(" &~& ")){
			throw new IllegalArgumentException(name);
		}
		if(phoneNumber.contains(" &#& ") || phoneNumber.contains(" &~& ")){
			throw new IllegalArgumentException(phoneNumber);
		}
		if(message.contains(" &#& ") || message.contains(" &~& ")){
			throw new IllegalArgumentException(message);
		}
		File myFile = new File(getFilesDir().getAbsolutePath());
		FileWriter fw = new FileWriter(myFile+"/"+fileName);
		fw.write(name+ " &#& "+ phoneNumber + " &~& " + message);
		fw.close();
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
	/*
	 * Receives input from the file and parses it to a list of Person objects
	 * @return The file's contents parsed to Person objects
	 * @throws IOException when the file described in this SMSTextManager object's fileName parameter could not be accessed
	 */
	private ArrayList<Person> loadData() throws IOException{
		BufferedReader bufferedReader = getFileReader();
		ArrayList<String> data = new ArrayList<String>();
		String temp="";
		while((temp=bufferedReader.readLine()) != null){
			data.add(temp);
		}
        return parseData(data);
	}
	/*
	 * Parses lines of Strings to Person objects
	 * @param data The contents of the file, line by line
	 * @return The file's contents parsed to Person objects
	 */
	private ArrayList<Person> parseData(ArrayList<String> data){
		ArrayList<Person> personData = new ArrayList<Person>();
		Pattern matchingPattern = Pattern.compile("^(.*) &#& (.*) &~& (.*)$");
		 for(String i : data){
			 Matcher matcher = matchingPattern.matcher(i);
			 if(matcher.find()){
				 personData.add(new Person(matcher.group(0),matcher.group(1),matcher.group(2)));
			 }
		 }
		 return personData;
	}

	/**
	 * Sends an SMS to all people in the file described in this SMSTextManager object's fileName parameter
	 * Requires the SEND_SMS permission
	 * @return {} if operation was successful, array of names of people to whom messages were not send if not all were sent, {Exception's stacktrace} if an exception occurred 
	 */
	public String[] sendSMStoAll(){
		ArrayList<Person> personData = new ArrayList<Person>();
		try{
			loadData();
		} catch (Exception e){
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
		    PrintStream ps = new PrintStream(baos);
		    e.printStackTrace(ps);
		    ps.close();
		    String[] toReturn = new String[1];
		    toReturn[0]=baos.toString();
		    return toReturn;
		}
		ArrayList<String> errorList = new ArrayList<String>();
		SmsManager sms = SmsManager.getDefault();
		setLocation();//Set the current location
		String messageWithLocation="";
		for(Person i : personData){
			messageWithLocation=i.getMsg();
			while(messageWithLocation.indexOf("\\l")!=-1){
				messageWithLocation=messageWithLocation.substring(0, messageWithLocation.indexOf("\\l"))+location+messageWithLocation.substring(messageWithLocation.indexOf("\\l")+location.length());
			}
			try{ //I don't think this pending intent line is neccesary. If it is, uncomment and replace the second to last null in the sendSMS line with 'pi'
	            //PendingIntent pi = PendingIntent.getActivity(this,0,new Intent (this, sms.class), 0);
		        sms.sendTextMessage(i.getNumber(), null, messageWithLocation, null, null);
			} catch (Exception e){
				errorList.add(i.getName());
			}
		}
		return errorList.toArray(new String[0]);
	}
	/**
	 * Gets the location from location services manager and returns it
	 */
	public String getLocation(){
		return "1 1";//TODO RUSSELL FILL THIS IN
	}
	/*
	 * Sets the location string in this class
	 */ 
	private void setLocation(){
		location=getLocation();
	}
	
}