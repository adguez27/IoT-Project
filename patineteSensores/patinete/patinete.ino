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
  esp_sleep_enable_ext0_wakeup(GPIO_NUM_5, 1); //1 = High, 0 = Low
  if (contador < 1 ) {
    esp_deep_sleep_start(); //duerme al ESP32 (modo SLEEP)
  }//if()

  //Configuracion sensor de peso
  rtc_clk_cpu_freq_set(RTC_CPU_FREQ_80M);   //bajo la frecuencia a 80MHz
  
  balanza.begin(DOUT, CLK);
  
  Serial.print("Lectura del valor del ADC:  ");
  Serial.println(balanza.read());
  Serial.println("No ponga ningun  objeto sobre la balanza");
  Serial.println("Destarando...");
  Serial.println("...");
  balanza.set_scale(24000.0); // Establecemos la escala
  balanza.tare(20);  //El peso actual es considerado Tara.
  
  Serial.println("Listo para pesar"); 
  
} // ()

void loop() {
  avisarColision();

  dormir();

  controldepeso();

  delay(500);
} // ()
