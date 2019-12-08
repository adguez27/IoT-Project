float bat = 100;
int contBat = 0;
#include "M5Stack.h"
#include "SensoresM5.h"
RTC_DATA_ATTR int contador = -1;




void setup() {
  // put your setup code here, to run once:
  M5.begin();
  M5.Lcd.setTextSize(2);
  Serial.begin(115200);
  //Distancia.
  pinMode(ECHO_PIN, INPUT);
  pinMode(TRIGGER_PIN, OUTPUT);
  //DOrmir M5
  pinMode(PINDORMIR, INPUT);

  contador++;
  delay(1000);
  esp_sleep_enable_ext0_wakeup(GPIO_NUM_26, 1); //1 = High, 0 = Low
  /*if (contador < 1 ) {
    esp_deep_sleep_start(); //duerme al ESP32 (modo SLEEP)
    }//if()*/

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
}

void loop() {
  M5.Lcd.setCursor(140, 0);
  M5.Lcd.print("Bateria: ");
  if (bat < 50 && bat > 30) {
    M5.Lcd.setTextColor(0xFFE0);
  }
  else if (bat < 30) {
    M5.Lcd.setTextColor(0xF800);
  }
  else {
    M5.Lcd.setTextColor(0x07E0);
  }
  M5.Lcd.println(bat);
  M5.Lcd.setTextColor(0xFFFF);
  M5.Lcd.setCursor(0, 50);
  // put your main code here, to run repeatedly:
  contBat += 1;
  avisarColision();
  controldepeso();
  if (contBat % 2 == 0) {
    bat -= 0.1;
  }
  if (Serial.available() > 0) {
    char command = (char) Serial.read();
    switch (command) {
      case 'H':
        Serial.println(bat);
        break;
    }
  }
  dormir();
  delay(500);
  M5.Lcd.clear();
}
