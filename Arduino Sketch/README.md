### This directory contains Arduino sketch for running the Led Matrix

Leonardo sketch does the following  
----------------------------------  
  * Serial to communicate with CDC/ACM Uart connnection with Android Phone (or PC)
  * Accepts the data encoded in **$M_N\r** format. Example: To light the (0,0) led you need to send the string `$0_0\r`
  * Timer ISR to periodically scan the columns of 8x8 Led Matrix. And lightens up the corresponding led that has high value in the 8x8 int array named "data"
   
DigiStump sketch does the following  
-----------------------------------  
   * MAX7219 based Led Matrix driver is used (ensures 2 things, evenly lit Led's and no need for timer ISR) 
   * Uses CDC/ACM from VUSB  (Limitation : 64 Byte BulkTransfer Buffer). Program becomes unresponsive when more that 64 Byte of data is transferred. This uses a lot of resources and BAUD of only 9600 is possible 
   * Digistump has a VID/PID of 0x16D0 / 0x087E and the Android code for using the Digistump has to use a manual Probe object to get the corresponding USB driver
   * Caveat: The default BulkTransfer buffer size for Digispark is 8 Byte. This needs to be overwritten to 64 inside digiCDC.h (library file)
