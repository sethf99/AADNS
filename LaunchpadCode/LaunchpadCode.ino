#include <WiFi.h>
#include <Wire.h>
#include <BMA222.h>
#include "SPI.h"

#include <aJSON.h>
#include "M2XStreamClient.h"

char ssid[] = "BitwiseHacks"; //  your network SSID (name)
char pass[] = "bwhackathon"; // your network password (use for WPA, or use as key for WEP)

int status = WL_IDLE_STATUS;

char deviceId[] = "bfd20b378bff7e258de2be669c49ca65"; // Feed you want to post to
char m2xKey[] = "943a63dee51d1266771e2e44ea8e3efd"; // Your M2X access key
char streamName[] = "impact"; // Stream you want to post to

BMA222 mySensor;

double x1 = 0.0;
double x2 = 0.0;
double xDiff = 0.0;

double yx1 = 0.0;
double y2 = 0.0;
double yDiff = 0.0;

double z1 = 0.0;
double z2 = 0.0;
double zDiff = 0.0;

double LIMIT = 100.0;


WiFiClient client;

M2XStreamClient m2xClient(&client, m2xKey);

void setup() {

  
  Serial.begin(9600);
  pinMode(RED_LED, OUTPUT); 

  mySensor.begin();
  uint8_t chipID = mySensor.chipID();
  Serial.print("chipID: ");
  Serial.println(chipID);

  delay(10);

  // attempt to connect to Wifi network:
  Serial.print("Attempting to connect to Network named: ");
  // print the network name (SSID);
  Serial.println(ssid); 
  WiFi.begin(ssid, pass); // Use this if your wifi network requires a password
  // WiFi.begin(ssid);    // Use this if your wifi network is unprotected.
  while ( WiFi.status() != WL_CONNECTED) {
    // print dots while we wait to connect
    Serial.print(".");
    delay(300);
  }
  Serial.println("\nConnect success!");
  Serial.println("Waiting for an ip address");

  while (WiFi.localIP() == INADDR_NONE) {
    // print dots while we wait for an ip addresss
    Serial.print(".");
    delay(300);
  }

  Serial.println("\nIP Address obtained");

  // you're connected now, so print out the status  
  printWifiStatus();

}

int max(int a, int b) {
  if(a > b){
    return a;
  }
  else{
    return b;
  }
}
 
void loop() {

  // Retrieve the x, y, and z accelerometer data
  float x = mySensor.readXData();
  float y = mySensor.readYData();
  float z = mySensor.readZData();
  
  // Find difference in accelerometer movement
  x1 = x;
  xDiff = x1 - x2;
  x2 = x1;
  
  yx1 = y;
  yDiff = yx1 - y2;
  y2 = yx1;
  
  z1 = z;
  zDiff = z1 - z2;
  z2 = z1;
  
  
  //Display values in serial monitor
  Serial.print("Accel Diff X: ");
  Serial.print(xDiff);
  Serial.print(", Y: ");
  Serial.print(yDiff);
  Serial.print(", Z: ");
  Serial.println(zDiff);

  if(xDiff >= LIMIT || yDiff >= LIMIT){
    int response = m2xClient.updateStreamValue(deviceId, streamName, 1);
    Serial.print("M2x client response code: ");
    Serial.println(response);
  }

}

void printWifiStatus() {
  // print the SSID of the network you're attached to:
  Serial.print("SSID: ");
  Serial.println(WiFi.SSID());

  // print your WiFi shield's IP address:
  IPAddress ip = WiFi.localIP();
  Serial.print("IP Address: ");
  Serial.println(ip);
}

