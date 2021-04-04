#include <timer-api.h>
#include <timer_setup.h>

//char rows[] = {0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};

const int r1=A0, r2=A1, r3=A2, r4=A3, r5=A4, r6=A5, r7=2, r8=3;
const int c1=4, c2=5, c3=6, c4=7, c5=8, c6=9, c7=10, c8=11;
int global_r=0, global_c=0;
char input[]="$0_0\n";
int index_input=0;
int data[8][8]={{0,0,0,0,0,0,0,0},{0,1,1,0,0,0,0,0},{0,0,1,1,0,0,0,0},{0,0,0,1,1,0,0,0},{0,0,0,1,1,0,0,0},{0,0,0,0,0,1,1,0},{0,0,0,0,0,0,1,0},{0,0,0,0,0,0,0,0}};
void light(int r,int c){
  switch(r){
    case 1:
      digitalWrite(r1,LOW);
      break;
    case 2:
      digitalWrite(r2,LOW);
      break;
    case 3:
      digitalWrite(r3,LOW);
      break;
    case 4:
      digitalWrite(r4,LOW);
      break;
    case 5:
      digitalWrite(r5,LOW);
      break;
    case 6:
      digitalWrite(r6,LOW);
      break;
    case 7:
      digitalWrite(r7,LOW);
      break;
    case 8:
      digitalWrite(r8,LOW);
      break;
    }

    switch(c){
    case 1:
      digitalWrite(c1,HIGH);
      break;
    case 2:
      digitalWrite(c2,HIGH);
      break;
    case 3:
      digitalWrite(c3,HIGH);
      break;
    case 4:
      digitalWrite(c4,HIGH);
      break;
    case 5:
      digitalWrite(c5,HIGH);
      break;
    case 6:
      digitalWrite(c6,HIGH);
      break;
    case 7:
      digitalWrite(c7,HIGH);
      break;
    case 8:
      digitalWrite(c8,HIGH);
      break;
    }
  }

void clear_all(){
  digitalWrite(r1,HIGH);
  digitalWrite(r2,HIGH);
  digitalWrite(r3,HIGH);
  digitalWrite(r4,HIGH);
  digitalWrite(r5,HIGH);
  digitalWrite(r6,HIGH);
  digitalWrite(r7,HIGH);
  digitalWrite(r8,HIGH);
  
  digitalWrite(c1,LOW);
  digitalWrite(c2,LOW);
  digitalWrite(c3,LOW);
  digitalWrite(c4,LOW);
  digitalWrite(c5,LOW);
  digitalWrite(c6,LOW);
  digitalWrite(c7,LOW);
  digitalWrite(c8,LOW);
  }

void setup() {
  pinMode(A0, OUTPUT);
  pinMode(A1, OUTPUT);
  pinMode(A2, OUTPUT);
  pinMode(A3, OUTPUT);
  pinMode(A4, OUTPUT);
  pinMode(A5, OUTPUT);
  pinMode(2, OUTPUT);
  pinMode(3, OUTPUT);
  pinMode(4, OUTPUT);
  pinMode(5, OUTPUT);
  pinMode(6, OUTPUT);
  pinMode(7, OUTPUT);
  pinMode(8, OUTPUT);
  pinMode(9, OUTPUT);
  pinMode(10, OUTPUT);
  pinMode(11, OUTPUT);
  // put your setup code here, to run once:
  Serial.begin(57600);
  timer_init_ISR_5KHz(TIMER_DEFAULT);
  while (!Serial)
   {
     ; // wait for serial port to connect. Needed for Leonardo only
   }
  Serial.println("Ready to receive");
  clear_all();

//  digitalWrite(2, LOW);
//  //digitalWrite(3, LOW);
//  digitalWrite(c1,HIGH);
//  digitalWrite(c2,HIGH);
//  digitalWrite(c3,HIGH);
//  digitalWrite(c4,HIGH);
//  digitalWrite(c5,HIGH);
//  digitalWrite(c6,HIGH);
//  digitalWrite(c7,HIGH);
//  digitalWrite(c8,HIGH);
}

void loop() {
  // Scan incoming packets
  // Clear all when an empty message received
  
  //Serial.println("Alive !");
  delay(200);
  while (Serial.available() > 0) {
    int inChar = Serial.read();
    
    if (inChar=='$') {
      index_input = 1;
    }
    
    if(isDigit(inChar)){
      input[index_input] = inChar;  
      index_input++;
    }
    
    if(inChar=='_'){
      index_input=3;  
    }
      
    // if you get a newline, print the string, then the string's value:
    if (inChar == '\n') {
      if(index_input == 1){
        clear_all_data();
        index_input = 0;
        Serial.println("Erase");
      }else{
        index_input = 0;
        data[input[1]-48][input[3]-48] = 1;
        Serial.println(input);
      }
    }
  }
}
void clear_all_data(){
  for(int i=0; i<8; i++)
    for(int j=0; j<8; j++)
      data[i][j] = 0;
  }
//
void timer_handle_interrupts(int timer) {
   // Clear all rows
   // Set col
   clear_all();
   
   //global_r = data[][global_c];
   for(int i=0; i<8; i++)
     if(data[i][global_c]>0){
       light(i+1, global_c+1);
       //Serial.print(i+1, global_c+1);
     }
   
  
   global_c += 1;
   if (global_c > 7){
     global_c = 0;
   }
  }
