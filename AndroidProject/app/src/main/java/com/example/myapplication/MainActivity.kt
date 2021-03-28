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
import android.widget.TextView
import com.hoho.android.usbserial.driver.UsbSerialPort
import com.hoho.android.usbserial.driver.UsbSerialProber


class MainActivity : AppCompatActivity() {
    lateinit var wvGui : WebView
    lateinit var tvTitle : TextView
    lateinit var usbMan: UsbManager
    lateinit var uPort: UsbSerialPort
    lateinit var usbConnection: UsbDeviceConnection
    lateinit var usbDevice : UsbDevice
    var foundDriver =false
    var connected = false

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

        usbMan = getSystemService(Context.USB_SERVICE) as UsbManager
        val allDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(usbMan)
        for(driver in allDrivers){
            val ports = driver.ports
            for(port in ports){
                Log.d("MA:", port.toString())
                foundDriver = true
                uPort = port
                //break
            }
            //break
        }
        if(foundDriver){
            usbDevice = uPort.driver.device
            if(!usbMan.hasPermission(usbDevice))
            {
                usbMan.requestPermission(usbDevice, PendingIntent.getBroadcast(this, 0, Intent(ACTION), 0))

            }
            usbConnection = usbMan.openDevice(usbDevice)

            uPort.open(usbConnection)
            uPort.setParameters(57600, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE)
            connected=true
        }
        //send_usb_str("Hello World first \r\n")
    }

    fun send_usb_str(s: String){
        if(connected){

            uPort.write(s.toByteArray(Charsets.UTF_8), 100)
            Log.d("MA:","Sending: "+s.toByteArray(Charsets.UTF_8).contentToString()+" : "+s)
            //uPort.close()
        }else{
            Log.d("MA:", "Received send request but Connection closed!")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("MA:", "Destroy called now()")
        if(connected){
            connected = false
            uPort.close()
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