#include "Sensores.h"

//RTC_DATA_ATTR definicion de variable en memoria del RTC
RTC_DATA_ATTR int contador = -1;



void setup() {
  Serial.begin(115200);

  //Configuracion de pines para distancia.
  pinMode(ECHO_PIN, INPUT);
  pinMode(TRIGGER_PIN, OUTPUT);
  pinMode(LED_ROJO, OUTPUT);

  //Configuracion de pines para dormir/despertar.
  pinMode(PINENCENDIDO, INPUT_PULLUP); //pin PIN entrada con resistencia de puldown
  pinMode(PINAPAGADO, INPUT); //pin PIN entrada con resistencia de puldown
  contador++;
  delay(1000);
  esp_sleep_enable_ext0_wakeup(GPIO_NUM_4, 0); //1 = High, 0 = Low
  if (contador < 1 ) {
    esp_deep_sleep_start(); //duerme al ESP32 (modo SLEEP)
  }//if()
  //Configuiracion de pines para calculo de peso.
  pinMode(PIN_GALGA1, INPUT);
  pinMode(PIN_GALGA2, INPUT);
  pinMode(PIN_GALGA3, INPUT);
  pinMode(PIN_GALGA4, INPUT);
} // ()

void loop() {
  avisarColision();

  dormir();

  controldepeso();

  delay(500);
} // ()
