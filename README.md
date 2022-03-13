# Cat-O-Matic
This project was repurposing a basic automatic cat feeder into a more advanced one with Wifi & Mobile App capability using a ESP8266 MCU and a Mobile Application.

The ESP8266 MCU would connect to wifi and communicate to the app using a firebase database where the food level and feeding time/amounts would be stored.
An ultrasonic sensor was used to read the level of the cat food and report it within an app.
The ESP8266 would read the feeding times from the database and start a motor that would eject the food at the correct times.
