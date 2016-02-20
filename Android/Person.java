package thing;

public class Person {
  private String name;
  private String phoneNumber;
  private String msg;
  /**
   * Default constructor
   */
  Person(){
	  name="";
	  phoneNumber="";
	  msg="";
  }
  /**
   * Constructor
   * @param nameI The name of this person
   * @param phoneNumberI This person's phonenumber
   * @param msgI The message to send to this person
   */
  Person(String nameI,String phoneNumberI,String msgI){
	  name=nameI;
	  phoneNumber=phoneNumberI;
	  msg=msgI;
  }
  /**
   * Sets this person's name
   * @param nameI The name to set it to
   */
  public void setName(String nameI){
	  name=nameI;
  }
  /**
   * Sets this person's phone number
   * @param phoneNumberI The phone number to set it to
   */
  public void setNumber(String phoneNumberI){
	  phoneNumber=phoneNumberI;
  }
  /**
   * Sets this person's message
   * @param msgI The message to set it to
   */
  public void setMsg(String msgI){
	  msg=msgI;
  }
  /**
   * Gets this person's name
   * @return This person's name
   */
  public String getName(){
	  return name;
  }
  /**
   * Gets this person's phone number
   * @return This person's phone number
   */
  public String getNumber(){
	  return phoneNumber;
  }
  /**
   * Gets this person's message
   * @return This person's message
   */
  public String getMsg(){
	  return msg;
  }
}
