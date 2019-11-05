#include "Sensores.h"

#define PINENCENDIDO 4
#define PINAPAGADO 0
#define ECHO_PIN 19
#define TRIGGER_PIN 18

//RTC_DATA_ATTR definicion de variable en memoria del RTC
RTC_DATA_ATTR int contador = -1;



void setup() {
  Serial.begin(115200);

  //Configuracion de pines para distancia.
  pinMode(ECHO_PIN, INPUT);
  pinMode(TRIGGER_PIN, OUTPUT);
  
  //Configuracion de pines para dormir/despertar.
  pinMode(PINENCENDIDO, INPUT_PULLUP); //pin PIN entrada con resistencia de puldown
  pinMode(PINAPAGADO, INPUT); //pin PIN entrada con resistencia de puldown
  contador++;
  delay(1000);
  esp_sleep_enable_ext0_wakeup(GPIO_NUM_4, 0); //1 = High, 0 = Low
  if (contador < 1 ) {
    esp_deep_sleep_start(); //duerme al ESP32 (modo SLEEP)
  }//if()

} // ()

void loop() {
  avisarColision(TRIGGER_PIN, ECHO_PIN);

  dormir(PINAPAGADO);

  delay(500);
} // ()
