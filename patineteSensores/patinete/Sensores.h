/*============================================================================
   SENSOR DISTANCIA
  ============================================================================*/

int distancia(int TriggerPin, int EchoPin) {

  long duracion, distanciaCm;
  digitalWrite(TriggerPin, LOW); //nos aseguramos señal baja al principio
  delayMicroseconds(4);
  digitalWrite(TriggerPin, HIGH); //generamos pulso de 10us
  delayMicroseconds(10);
  digitalWrite(TriggerPin, LOW);
  duracion = pulseIn(EchoPin, HIGH); //medimos el tiempo del pulso
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

void avisarColision(int T, int E){
  int dis = distancia(T, E);
  if (estaCerca(dis)) {
    Serial.println("objeto proximo");
    Serial.println(dis);
  } else {
    Serial.println("ningun objeto proximo uwu");
    Serial.println(dis);
  } // ifelse()

} 




/*============================================================================
   DORMIR ARDUINO
  ============================================================================*/

void dormir(int pinApagado) {
  boolean track = digitalRead(pinApagado);

  if (track == LOW) {
    Serial.println("Me he DORMIDO");
    esp_deep_sleep_start();
  } //if()
} //()
