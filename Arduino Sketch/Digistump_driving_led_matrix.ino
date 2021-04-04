#include <DigiCDC.h>
#include <LedControl.h>

int DIN = 0; //11;
int CS =  1;  //7;
int CLK = 2; //13;

char input[]="$0_0\r\n";
int index_input=0;
int loop_count = 0;

LedControl lc=LedControl(DIN, CLK, CS,0);
int inChar = '$';

byte data [8]={ B10000000,
                B01000000,
                B00100000,
                B00010000,
                B00001000,
                B00000100,
                B00000010,
                B00000001};
                
void clear_all(){
    lc.clearDisplay(0);
    for(int i =0; i<8; i++)
        data[i] = 0;
}


void setup() {                
  // initialize the digital pin as an output.
  SerialUSB.begin(); 
  lc.shutdown(0,false);
  lc.setIntensity(0,0);
  lc.clearDisplay(0);
  //SerialUSB.println("HaHa");
}

// the loop routine runs over and over again forever:
void loop() {
  
  if (SerialUSB.available()) {
    inChar = SerialUSB.read();
    //SerialUSB.write(inChar);

    if (inChar=='$') {
      index_input = 1;
      //SerialUSB.write('&');
    }
    
    if(isDigit(inChar)){
      input[index_input] = inChar;  
      index_input++;
      //SerialUSB.write('^');
    }
    
    if(inChar=='_'){
      index_input=3;  
      //SerialUSB.write('#');
    }
      
    // if you get a newline, print the string, then the string's value:
    //if ((inChar == '#')||(inChar == '\r')||(inChar == '\n')) {
    
    
    if ((inChar == '#')) {
      
      //SerialUSB.write('Z');
      
      if(index_input == 1){
        clear_all();
        index_input = 0;
        //Serial.println("Erase");
      }else{
        index_input = 0;
        data[input[1]-48] |= (1<<(input[3]-48));
        //data[input[1]-48][input[3]-48] = 1;
        //SerialUSB.println(input);
      }
    }
  }

  loop_count +=1;  
  if (loop_count>10) {
    loop_count = 0;

    for(int i=0;i<8;i++) 
        lc.setRow(0,i,data[i]);
    }
  
   //SerialUSB.delay(10);
   /*
   if you don't call a SerialUSB function (write, print, read, available, etc) 
   every 10ms or less then you must throw in some SerialUSB.refresh(); 
   for the USB to keep alive - also replace your delays - ie. delay(100); 
   with SerialUSB.delays ie. SerialUSB.delay(100);
   */
}
