
/*********
 * Trabalho Final
 *          SSC0147 - Tópicos Especiais em Sistemas de Computação I
 * 
 * Docente: 
 *          Jo Ueyama
 * 
 * Alunos:
 *          Marilene Andrade Garcia - 10276974
 *          Vinícius L. S. Genesio  - 10284688
 *          
 * Código baseado no código escrito por:
 * https://www.emqx.com/en/blog/esp8266-connects-to-the-public-mqtt-broker
*********/

 
#include <ESP8266WiFi.h>
#include <PubSubClient.h>
#include <Arduino.h>
#include <ESP8266WiFi.h>
#include <Hash.h>
#include <ESPAsyncTCP.h>
#include <ESPAsyncWebServer.h>
#include <Adafruit_Sensor.h>
#include <DHT.h>

// Definicao dos pinos
#define DHTPIN  4  // pino conectado ao sensor DTH
#define LEDGPIN 0  // pino conectado ao led verde
#define LEDRPIN 5  // pino conectado ao led vermelho

// Instanciando o sensor
#define DHTTYPE    DHT11 
DHT dht(DHTPIN, DHTTYPE);

// Valores de temperatura e pressão
float t = 0.0;
float h = 0.0;

// Tempo de atualização das medidas
unsigned long previousMillis = 0; 

// Leitura do sensor a cada 10 segundos
const long interval = 5000;  


// WiFi
const char *ssid = "netUSP"; // Enter your WiFi name
const char *password = "SejaBemVindo69";  // Enter WiFi password

// MQTT Broker
const char *mqtt_broker = "broker.emqx.io";
const char *topic = "ssc0147/trab_final";
const char *mqtt_username = "emqx";
const char *mqtt_password = "public";
const int mqtt_port = 1883;

WiFiClient espClient;
PubSubClient client(espClient);

void setup() {
  pinMode(LEDGPIN, OUTPUT); 
  pinMode(LEDRPIN, OUTPUT); 
  // Set software serial baud to 115200;
  Serial.begin(115200);
  // connecting to a WiFi network
  WiFi.begin(ssid, password);
  while (WiFi.status() != WL_CONNECTED) {
      delay(500);
      Serial.println("Connecting to WiFi..");
  }
  
  Serial.println("Connected to the WiFi network");
  //connecting to a mqtt broker
  client.setServer(mqtt_broker, mqtt_port);
  while (!client.connected()) {
      String client_id = "esp8266-client-";
      client_id += String(WiFi.macAddress());
      Serial.printf("The client %s connects to the public mqtt broker\n", client_id.c_str());
      if (client.connect(client_id.c_str(), mqtt_username, mqtt_password)) {
          Serial.println("Public emqx mqtt broker connected");
      } else {
          Serial.print("failed with state ");
          Serial.print(client.state());
          delay(2000);
      }
  }
}

void loop() {
  unsigned long currentMillis = millis();
  if (currentMillis - previousMillis >= interval) {
    digitalWrite(LEDRPIN, LOW);
    digitalWrite(LEDGPIN, HIGH);
    client.loop();
    
    // Salvar a ultima vez que o tempo foi atualizado
    previousMillis = currentMillis;
    // Ler a temperatura
    float newT = dht.readTemperature();
    
    // Se a leitura falhar o valor não é atualizado
    if (isnan(newT)) {
      Serial.println("Failed to read from DHT sensor!");
    }
    else {
      t = newT;
      Serial.println(t);
    }
    
    // Ler a humidade
    float newH = dht.readHumidity();
    
    // Se a leitura falhar o valor não é atualizado
    if (isnan(newH)) {
      Serial.println("Failed to read from DHT sensor!");
    }
    else {
      h = newH;
      Serial.println(h);
    }
    
    String stringUm = String(newT, 2);
    String stringDois = String(newH, 2);
    String stringTres = stringUm + " " + stringDois;
    char mensagem[15];
    stringTres.toCharArray(mensagem, 13);
    client.publish(topic, mensagem);
  }
  digitalWrite(LEDGPIN, LOW);
  digitalWrite(LEDRPIN, HIGH);
}
