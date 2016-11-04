#include <Chrono.h>
#include <LightChrono.h>

#include <ESP8266WiFi.h>
  

const char* ssid = "lol";
Chrono watch;
int counter = 0;

WiFiServer server(80);
IPAddress staticIP(192,168,0,101);
IPAddress gateway(192,168,0,1);
IPAddress subnet(255,255,255,0);

#define TIME_MSG_LEN  11   // time sync to PC is HEADER followed by Unix time_t as ten ASCII digits
#define TIME_HEADER  'T'   // Header tag for serial time sync message
#define TIME_REQUEST  7    // ASCII bell character requests a time sync message 


void setup()
{
  Serial.begin(115200);
  Serial.println();
  WiFi.config(staticIP, gateway, subnet);
  Serial.printf("Connecting to %s ", ssid);
  WiFi.begin(ssid);
  while (WiFi.status() != WL_CONNECTED)
  {
    delay(500);
    Serial.print(".");
  }
  Serial.println(" connected");

  server.begin();
  Serial.printf("Web server started, open %s in a web browser\n", WiFi.localIP().toString().c_str());
  watch.restart(0);
}


// prepare a web page to be send to a client (web browser)
String prepareHtmlPage()
{
  counter = counter + 1;
  String htmlPage = 
     String("HTTP/1.1 200 OK\r\n") +
            "Content-Type: text/html\r\n" +
            "Connection: close\r\n" +  // the connection will be closed after completion of the response
            "\r\n" +
            "<!DOCTYPE HTML>" +
            "<html>" + "Counter: " + counter + " Seconds elapsed: " + watch.seconds() +
            "</html>" +
            "\r\n";
  return htmlPage;
}


void loop()
{
  WiFiClient client = server.available(); 
  // wait for a client (web browser) to connect
  if (client)
  {
    Serial.println("\n[Client connected]");
    while (client.connected())
    {
      // read line by line what the client (web browser) is requesting
      if (client.available())
      {
        String line = client.readStringUntil('\r');
        Serial.print(line);
        // wait for end of client's request, that is marked with an empty line
        if (line.length() == 1 && line[0] == '\n')
        {
          client.println(prepareHtmlPage());
          break;
        }
      }
    }
    delay(1); // give the web browser time to receive the data

    // close the connection:
    client.stop();
    Serial.println("[Client disonnected]");
  }
}
