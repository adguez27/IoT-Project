const int EchoPin = 19;
const int TriggerPin = 18;


void setup() {
  // put your setup code here, to run once:
    Serial.begin(115200, SERIAL_8N1);

  pinMode(TriggerPin, OUTPUT);
  pinMode(EchoPin, INPUT);
}

void loop() {
  // put your main code here, to run repeatedly:
  if (Serial.available() > 0) {
     char command = (char) Serial.read();
 //    Serial.println(command);
     switch (command) {
        case 'H':
           Serial.println("Kola Mundo XXX");
           break;
        case 'D':
           Serial.println(distancia(TriggerPin, EchoPin));
           break;
    }
  }
//     Serial1.flush();
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
