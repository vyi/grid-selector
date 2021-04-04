package com.example.myapplication

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbDeviceConnection
import android.hardware.usb.UsbManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract.Intents.Insert.ACTION
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.TextView
import com.hoho.android.usbserial.driver.CdcAcmSerialDriver
import com.hoho.android.usbserial.driver.ProbeTable

import com.hoho.android.usbserial.driver.UsbSerialPort
import com.hoho.android.usbserial.driver.UsbSerialProber


class MainActivity : AppCompatActivity() {
    lateinit var wvGui : WebView
    lateinit var tvTitle : TextView
    lateinit var usbMan: UsbManager
    lateinit var userial: UsbSerialPort
    lateinit var usbConnection: UsbDeviceConnection
    //lateinit var usbDevice : UsbDevice
    //lateinit var usbDriver : UsbSerialDriver
    var foundDriver =false
    var serialReady = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        wvGui = findViewById<WebView>(R.id.wvGui)
        wvGui.webViewClient = WebViewClient()
        wvGui.settings.javaScriptEnabled = true
        wvGui.loadUrl("file:///android_asset/index.html")
        wvGui.addJavascriptInterface( webInterface(), "Android")

        // Example of a call to a native method
        //.text = stringFromJNI()
        tvTitle = findViewById<TextView>(R.id.sample_text)
        tvTitle.text = "Grid Con"

        val dButt = findViewById<Button>(R.id.bDispatch)
        usbMan = getSystemService(Context.USB_SERVICE) as UsbManager

        dButt.setOnClickListener {
            Log.d("MA:", "Law")
            openSerialUsbConn()
        }
        //send_usb_str("Hello World first \r\n")
    }

    /************************************************************************************/
    //              USB Serial: PWM Dispatch code
    //
    //    IF a device is connected
    //          check if its our robot (vid, pid, hw_desc_string
    //              bind to the driver
    /************************************************************************************/
    fun openSerialUsbConn(){
        try{
            // Find all available drivers from attached devices.

            // Find all available drivers from attached devices.
            usbMan = getSystemService(Context.USB_SERVICE) as UsbManager

//            val usbPermissionIntent = PendingIntent.getBroadcast(this, 0, Intent(INTENT_ACTION_GRANT_USB), 0)
//            manager.requestPermission(driver.getDevice(), usbPermissionIntent)
//
            val customTable = ProbeTable()
            customTable.addProduct(0x16d0, 0x087e, CdcAcmSerialDriver::class.java)
            //customTable.addProduct(0x1234, 0x0002, CdcAcmSerialDriver::class.java)

            val prober = UsbSerialProber(customTable)
            val availableDrivers = prober.findAllDrivers(usbMan)

            //val availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(manager)
            if (availableDrivers.isEmpty()) {
                Log.d("MA:", "No drivers found")
                return
            }

            // Open a connection to the first available driver.

            // Open a connection to the first available driver.
            val driver = availableDrivers[0]
            val connection = usbMan.openDevice(driver.device)
            // add UsbManager.requestPermission(driver.getDevice(), ..) handling here
            if(connection == null){
                Log.d("MA", "Require permission")
                if(!usbMan.hasPermission(driver.device))
                {
                    usbMan.requestPermission(driver.device, PendingIntent.getBroadcast(this, 0, Intent(ACTION), 0))
                }
            }else{
                Log.d("MA:", "Permission not needed ?")
            }
            if(!usbMan.hasPermission(driver.device)){
                Log.d("MA:", "Still need permission!")
                return
            }else{

                userial = driver.ports[0] // Most devices have just one port (port 0)

                userial.open(connection)
                userial.setParameters(9600, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE)
                serialReady = true
                Log.d("MA:", driver.device.deviceName)
            }

        }catch (e: Exception){
            Log.d("MA:", "No Device? Please Check.")
            serialReady = false
            e.printStackTrace()
        }finally {

        }
    }

    fun send_usb_str(s: String){
        if(serialReady){

            userial.write(s.toByteArray(Charsets.UTF_8), 2000)
            Log.d("MA:","Sending: "+s.toByteArray(Charsets.UTF_8).contentToString()+" : "+s)
            //uPort.close()
        }else{
            Log.d("MA:", "Received send request but Connection closed!")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("MA:", "Destroy called now()")
        if(serialReady){
            serialReady = false
            userial.close()
        }
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    external fun stringFromJNI(): String

    companion object {
        // Used to load the 'native-lib' library on application startup.
        init {
            System.loadLibrary("native-lib")
        }
    }

    inner class webInterface{
        lateinit var myContext : Context
        //var myString = ""

        fun webInterface(m : Context){
            this.myContext = m
        }

        @JavascriptInterface
        fun sendData(data : String){
            //this.myString = data
            send_usb_str(data)
            Log.d("MA: Inside Inner class ",  data)
        }
    }
}