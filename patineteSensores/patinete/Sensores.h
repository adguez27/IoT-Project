#include "HX711.h"
#include "soc/rtc.h";  //Libreria para poder bajar la frecuencia

#define LED_ROJO 27
#define PINENCENDIDO 4
#define PINAPAGADO 0
#define ECHO_PIN 19
#define TRIGGER_PIN 18
#define DOUT  2 //peso
#define CLK  5 //peso

HX711 balanza;

/*============================================================================
   SENSOR DISTANCIA
  ============================================================================*/

int distancia() {

  long duracion, distanciaCm;
  digitalWrite(TRIGGER_PIN, LOW); //nos aseguramos señal baja al principio
  delayMicroseconds(4);
  digitalWrite(TRIGGER_PIN, HIGH); //generamos pulso de 10us
  delayMicroseconds(10);
  digitalWrite(TRIGGER_PIN, LOW);
  duracion = pulseIn(ECHO_PIN, HIGH); //medimos el tiempo del pulso
  /*
    Serial.print("duracion: ");
    Serial.println(duracion);
  */

  distanciaCm = duracion * 10 / 292 / 2;
  //convertimos a distancia
  return distanciaCm;
} // ()

bool estaCerca(float distanciaCm) {
  float distanciaAlerta = 50;

  if (distanciaCm < distanciaAlerta) {
    return true;
  } // if()
  else {
    return false;

  } //else()

} // ()

void avisarColision() {
  int dis = distancia();
  if (estaCerca(dis)) {
    digitalWrite(LED_ROJO, HIGH);
    Serial.println(dis);
  } else {
    digitalWrite(LED_ROJO, LOW);
    Serial.println(dis);
  } // ifelse()

}




/*============================================================================
   DORMIR ARDUINO
  ============================================================================*/

void dormir() {
  boolean track = digitalRead(PINAPAGADO);

  if (track == LOW) {
    Serial.println("Me he DORMIDO");
    esp_deep_sleep_start();
  } //if()
} //()
/*============================================================================
   CONTROL DE PESO
  ============================================================================*/

void controldepeso() {
  float pesoFinal = balanza.get_units(20);
  Serial.print("Peso: ");
  Serial.print(pesoFinal,3);
  Serial.println(" kg");

  if (pesoFinal >= 20) {
    Serial.println("Demasiado peso para el patinete!");
    digitalWrite(LED_ROJO, HIGH);
  }
  else {
    digitalWrite(LED_ROJO, LOW);
  }
}
