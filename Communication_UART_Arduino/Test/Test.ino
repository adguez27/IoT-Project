const int EchoPin = 19;
const int TriggerPin = 18;


void setup() {
  // put your setup code here, to run once:
Serial.begin(115200);
pinMode(TriggerPin, OUTPUT);
pinMode(EchoPin, INPUT);
}

void loop() {
  // put your main code here, to run repeatedly:
Serial.print("Distancia: ");
Serial.println(distancia(TriggerPin, EchoPin));
delay(1000);
}

int distancia(int TriggerPin, int EchoPin) {
long duracion, distanciaCm;
digitalWrite(TriggerPin, LOW); //nos aseguramos señal baja al principio
delayMicroseconds(4);
digitalWrite(TriggerPin, HIGH); //generamos pulso de 10us
delayMicroseconds(10);
digitalWrite(TriggerPin, LOW);
duracion = pulseIn(EchoPin, HIGH); //medimos el tiempo del pulso
distanciaCm = duracion * 10 / 292 / 2;
 //convertimos a distancia
return distanciaCm;
}
