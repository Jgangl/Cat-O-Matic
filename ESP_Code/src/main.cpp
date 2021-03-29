#include <Arduino.h>

#include <ESP8266WiFi.h>
#include <string.h>
#include <FirebaseESP8266.h>
#include <WiFiUdp.h>
#include <NTPClient.h>
#include <TimeLib.h>
//#include <HCSR04.h>
//#include <RunningMedian.h>

struct Time{
  short hour;
  short minute;

  Time(){
    this->hour = 0;
    this->minute = 0;
  }

  Time(short hour, short minute){
    this->hour = hour;
    this->minute = minute;
  }
};

struct Meal{
  bool enabled;
  Time time;
  short amount;
  bool feeding;

  Meal(){
    enabled = false;
    time = {00, 00};
    amount = 0;
    feeding = false;
  }

  Meal(bool enabled, short amount, Time time){
    this->enabled = enabled;
    this->time = time;
    this->amount = amount;
    this->feeding = false;    
  }

  Meal(bool enabled, char amount, short hour, short minute){
    this->enabled = enabled;
    this->amount = amount;
    this->time = Time(hour, minute);
    this->feeding = false;
  }

  void SetTime(int hour, int minute){
    this->time = Time(hour, minute);
  }
};


void ManualFeed(char amount);
void streamCallback(StreamData data);
void streamTimeoutCallback(bool timeout);
void ConfigureFirebase();
void LoadDataFromDatabase();
void resetManualFeedTrigger();
void SortMeals();
void PrintSortedMeals();
void ManualFeedTriggered();
void getNextMeal();

const char* ssid = "99th Precinct";
const char* password = "You'reNotCheddar";

const long utcOffsetInSeconds = -5 * 60 * 60;
//char daysOfTheWeek[7][12] = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};

WiFiUDP ntpUDP;
NTPClient timeClient(ntpUDP, "pool.ntp.org", utcOffsetInSeconds, 86400000);//Update interval is once a day

Meal meals[5];
Meal sortedMeals[5];
bool disableAllMeals = false;
Meal nextMeal;

short triggerPin = 12;
short echoPin = 13;
short enA = 4;  //D2
short in1 = 0;  //D3
short in2 = 2;  //D4

int currHour = 0;
int currMinute = 0;

short numPtnSwPresses = 0;
bool isFeeding = false;

unsigned long currentMillis = 0;
unsigned long previousMillis = 0;
const unsigned long MEAL_CHECK_REFRESH = 1000;//ms

#define FIREBASE_HOST "cat-o-matic.firebaseio.com"
#define API_KEY "AIzaSyAEBR1xRGweFzS0vkMo2aX7HSLCLFcXHg4"
#define USER_EMAIL "joshua.gangl@gmail.com"
#define USER_PASSWORD "Jpg150034"

//Define the Firebase Data object
FirebaseData fbdo;
// Define the FirebaseAuth data for authentication data
FirebaseAuth auth;
// Define the FirebaseConfig data for config data
FirebaseConfig config;

//RunningMedian samples = RunningMedian(15);
//UltraSonicDistanceSensor distanceSensor(triggerPin, echoPin);

void setup()
{
  pinMode(enA, OUTPUT);
  pinMode(in1, OUTPUT);
  pinMode(in2, OUTPUT);

  Serial.begin(921600);
  Serial.println();
  
  Serial.printf("Connecting to %s ", ssid);
  WiFi.begin(ssid, password);
  while (WiFi.status() != WL_CONNECTED)
  {
    delay(500);
    Serial.print(".");
  }
  Serial.println(" connected");

  timeClient.begin();

  ConfigureFirebase();
  LoadDataFromDatabase();
  resetManualFeedTrigger();
  //PrintSortedMeals();

  timeClient.update();
  getNextMeal();
  Serial.printf("\nNext Meal   Hour: %d  Minute: %d\n\n", nextMeal.time.hour, nextMeal.time.minute);

  //setSyncProvider(getNtpTime);
}
bool timer = 0;

void loop()
{
  timeClient.update();
  //timeClient.getEpochTime();
  //ProcessMealTimes();

  currentMillis = millis();
  if(currentMillis - previousMillis > MEAL_CHECK_REFRESH){
    previousMillis = currentMillis;
    //ProcessMealTimes();
  }
}

void ConfigureFirebase(){
  //Assign the project host and api key 
  config.host = FIREBASE_HOST;
  config.api_key = API_KEY;

  auth.user.email = USER_EMAIL;
  auth.user.password = USER_PASSWORD;
  Firebase.begin(&config, &auth);
  Firebase.reconnectWiFi(true);
  Firebase.RTDB.setMaxRetry(&fbdo, 3);
  Firebase.RTDB.setMaxErrorQueue(&fbdo, 30);
  fbdo.setBSSLBufferSize(1024, 1024); //minimum size is 512 bytes, maximum size is 16384 bytes

  Firebase.setStreamCallback(fbdo, streamCallback, streamTimeoutCallback);
  
  if (!Firebase.beginStream(fbdo, "/"))
  {
    //Could not begin stream connection, then print out the error detail
    Serial.println(fbdo.errorReason());
  }
}

void LoadDataFromDatabase(){
  Serial.println("Loading From Database");
  if (Firebase.RTDB.get(&fbdo, "/Meals"))
  {
    FirebaseJsonArray &arr = fbdo.jsonArray();

    for (size_t i = 0; i < arr.size(); i++){
      
      FirebaseJsonData &jsonData = fbdo.jsonData();
      
      //Get the result data from FirebaseJsonArray object
      arr.get(jsonData, i);

      if(jsonData.typeNum != FirebaseJson::JSON_NULL){
        //Serial.println(jsonData.stringValue);

        FirebaseJson json;
        jsonData.getJSON(json);

        FirebaseJsonData mealJson;
        json.get(mealJson, "Amount");
        meals[i-1].amount = mealJson.intValue;
        //Serial.println("Amount: " + mealJson.stringValue);
        json.get(mealJson, "Enabled");
        meals[i-1].enabled = mealJson.boolValue;
        //Serial.println("Enabled: " + mealJson.stringValue);
        json.get(mealJson, "Time");

        char str[6];
        strcpy(str, mealJson.stringValue.c_str());
        char* token = strtok(str, ":");
        char hourTok[3];
        char minTok[3];
        strcpy(hourTok, token); 
        token = strtok(NULL, ":");
        strcpy(minTok, token);

        short hour = atoi(hourTok);
        short min = atoi(minTok);

        meals[i-1].SetTime(hour, min);

        //Serial.printf("Hour: %d   Minute: %d\n", hour, min);
        //Serial.println();
        //Serial.println();
      }
    }

    //Need to sort meals after loading meals from database
    SortMeals();
  }
  else
  {
    Serial.println("FAILED");
    Serial.println("REASON: " + fbdo.errorReason());
    Serial.println("------------------------------------");
    Serial.println();
  }

  if (Firebase.RTDB.getBool(&fbdo, "/DisableAllMeals")){
    disableAllMeals = fbdo.boolData();
    //Serial.println(disableAllMeals);
    //printf("Sucessfully retrieved DisabledAllMeals\n");
  }

  Serial.println("Done Loading from Database");
}

void resetManualFeedTrigger(){
  if (Firebase.RTDB.setBool(&fbdo, "/ManualFeedTrigger", false)){
    printf("Sucessfully reset ManualFeedTrigger\n");
  }
}

void streamCallback(StreamData data)
{
  Serial.println("\nStream Data...");
  Serial.println(data.dataPath());

  char* str = strdup(data.dataPath().c_str());
  char* token = strtok (str, "/");

  if(token != NULL){
    Serial.println(token);

    if(strcmp(token, "Meals") == 0){
      token = strtok (NULL, "/");
      char mealNum = atoi(token);
      Serial.println(mealNum);
      token = strtok (NULL, "/");
      if(strcmp(token, "Amount") == 0){
        Serial.printf("Amount: %d\n", data.intData());
        short amount = data.intData();
        meals[mealNum-1].amount = amount;
        //Amount
      }
      else if(strcmp(token, "Enabled") == 0){
        Serial.println("Enabled");
        bool enabled = data.boolData();
        meals[mealNum-1].enabled = enabled;
        //Enabled
      }
      else if(strcmp(token, "Time") == 0){
        Serial.println("Time");
        char time[6];
        strcpy(time, data.stringData().c_str());

        char* token = strtok(time, ":");
        char hourTok[3];
        char minTok[3];
        strcpy(hourTok, token); 
        token = strtok(NULL, ":");
        strcpy(minTok, token);

        short hour = atoi(hourTok);
        short min = atoi(minTok);

        meals[mealNum-1].SetTime(hour, min);
      }
      //Need to sort meals after updating meals from database
      SortMeals();
    }
    else if(strcmp(token, "DisableAllMeals") == 0){
      Serial.println("Disable All Meals");
      disableAllMeals = data.boolData();
    }
    else if(strcmp(token, "ManualFeedTrigger") == 0){
      Serial.println("Manual Feed Trigger");
      bool manualFeedTrigger = data.boolData();
      if(manualFeedTrigger){
        ManualFeedTriggered();
        //Reset manual trigger
        if (Firebase.RTDB.setBool(&fbdo, "/ManualFeedTrigger", false)){
          printf("Sucessfully reset ManualFeedTrigger\n");
        }
      }
    }
  }
}

void UpdateCurrentTime(){
  currHour = timeClient.getHours();
  currMinute = timeClient.getMinutes();

  Serial.printf("TIME ---  Hour: %d  Minute: %d\n\n", currHour, currMinute);
}

void ProcessMealTimes(){
  int currHour = timeClient.getHours();
  int currMin = timeClient.getMinutes();
  
  int numMeals = sizeof(meals) / sizeof(Meal);

  for(int i = 0; i < numMeals; i++){
    if(meals[i].enabled){
      if((meals[i].time.hour == currHour)  &&  (meals[i].time.minute == currMin)){
        if(!meals[i].feeding){//Check if meal was already done
          meals[i].feeding = true;

          //StartFeeding(meals[i].amount);
        }
      }
    }
  }
  
}

void ManualFeedTriggered(){
  int hour = timeClient.getHours();
  int minute = timeClient.getMinutes();
  int second = timeClient.getSeconds();

  Serial.printf("----TIME UPDATE----\n\n");
  Serial.printf("Hour: %d Minute: %d Second: %d\n\n", hour, minute, second);

}

void startFeedingMotor(){
  digitalWrite(in1, LOW);
  digitalWrite(in2, HIGH);
  analogWrite(enA, 512);
}

void stopFeedingMotor(){
  digitalWrite(in1, LOW);
  digitalWrite(in2, HIGH);
  digitalWrite(enA, LOW);
}

//Global function that notifies when stream connection lost
//The library will resume the stream connection automatically
void streamTimeoutCallback(bool timeout)
{
  if(timeout){
    //Stream timeout occurred
    Serial.println("Stream timeout, resume streaming...");
  }  
}

void getNextMeal(){
  //UpdateCurrentTime();

  int currentHour = 1;
  int currentMinute = 30;

  Serial.printf("FAKE TIME ---  Hour: %d  Minute: %d\n\n", currHour, currMinute);

  int numMeals = sizeof(meals) / sizeof(Meal);
  for(int i = 0; i < numMeals; i++){
    //Check if current time is before first meal
    if(i == 0 && (currentHour < sortedMeals[i].time.hour && currentMinute < sortedMeals[i].time.minute)){
      nextMeal = sortedMeals[0];
      Serial.println("Next Meal Set");
    }

    //Check if current time is after last meal
    if(i == numMeals-1 && (currentHour > sortedMeals[i].time.hour && currentMinute > sortedMeals[i].time.minute)){
      nextMeal = sortedMeals[0];
      Serial.println("Next Meal Set");
    }

    //Check if current time is between 2 meals
    if(currentHour > sortedMeals[i].time.hour && currentMinute > sortedMeals[i].time.minute){
      if(currentHour < sortedMeals[i+1].time.hour && currentMinute < sortedMeals[i+1].time.minute){
        //Between indeces
        nextMeal = sortedMeals[i+1];
        Serial.println("Next Meal Set");
      }
    }
  }
}

void SortMeals(){
  Serial.println("Sorting Meals");
  short numMeals = sizeof(meals) / sizeof(Meal);
  Meal tmpSortedMeals[numMeals];
  
  //Copy Meals array
  for(int i = 0; i < numMeals; i++){
    tmpSortedMeals[i] = meals[i];
  }

  //Sort meals
  for(int i = 1; i < numMeals; i++){
    
    Meal keyMeal = tmpSortedMeals[i];
    int j = i-1;

    while(j >= 0 && (tmpSortedMeals[j].time.hour > keyMeal.time.hour || (tmpSortedMeals[j].time.hour == keyMeal.time.hour && tmpSortedMeals[j].time.minute > keyMeal.time.minute))){

      tmpSortedMeals[j+1] = tmpSortedMeals[j];
      j = j-1;
    }
    tmpSortedMeals[j+1] = keyMeal;
  }

  for(int i = 0; i < numMeals; i++){
    sortedMeals[i] = tmpSortedMeals[i];
  }

  Serial.println("Done Sorting Meals");
}

void PrintSortedMeals(){
  short numMeals = sizeof(meals) / sizeof(Meal);

  for(int i = 0; i < numMeals; i++){
    Serial.printf("MEAL---Amount: %d  Enabled: %d  Time: %d:%d\n", sortedMeals[i].amount, sortedMeals[i].enabled, sortedMeals[i].time.hour, sortedMeals[i].time.minute);
  }
}