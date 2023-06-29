package com.samediscare.printerserverapp

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.MutableLiveData
import com.bong.brothersetup.utils.common.UserPreferences
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.samediscare.printerserverapp.databinding.ActivityMainBinding
private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    private  var printUrl=""
    private  var printInventoryId=""
    private lateinit var binding: ActivityMainBinding
    private lateinit var mySharedPreferences: UserPreferences
    private var printerMacAddress=""
    private var bitmap: Bitmap? = null
    private val mData: MutableLiveData<String> = MutableLiveData()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        mySharedPreferences = applicationContext?.let { UserPreferences.getInstance(it) }!!
        printerMacAddress=mySharedPreferences.getData("address","")
        //binding.b1?.isEnabled = false
        binding.b1.setOnClickListener{
            if(printerMacAddress.isNotEmpty()){
                bitmap?.let { printQrCode(it) }
            }else{
                Toast.makeText(applicationContext,"Please select printer from settings.",Toast.LENGTH_LONG).show()
            }


        }
        binding.imgsetting.setOnClickListener{
            val intent = Intent(applicationContext, PrinterSetting::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

        val mainIntent = intent
        if (mainIntent?.data != null) {
            val data: Uri = mainIntent.data!!
            printUrl= data.getQueryParameter("url").toString()
            printInventoryId =data.getQueryParameter("inventoryid").toString()
            if (printUrl.isNotEmpty()){
                binding.b1.isEnabled = true
                bitmap = generateQRCode(printUrl)
                binding.i1.setImageBitmap(bitmap)
                binding.txtId.text=printInventoryId
            }

        }
        else{
//            bitmap = generateQRCode("http://hcdhdd.com")
//            binding.i1.setImageBitmap(bitmap)
        }

    }

    private fun generateQRCode(text: String): Bitmap {
        val width = 500
        val height = 500
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val codeWriter = MultiFormatWriter()
        try {
            val bitMatrix =
                codeWriter.encode(text, BarcodeFormat.QR_CODE, width, height)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    val color = if (bitMatrix[x, y]) Color.BLACK else Color.WHITE
                    bitmap.setPixel(x, y, color)
                }
            }
        } catch (e: WriterException) {

            Log.d(TAG, "generateQRCode: ${e.message}")

        }
        return bitmap
    }
    private fun printQrCode(qrCodeBitmap: Bitmap) {
          val printerAdapter = Brother()
            printerAdapter.sendFile(qrCodeBitmap,null, applicationContext,mySharedPreferences.getData("address",""), mySharedPreferences.getData("paper_size","").toInt(),false,mData)

    }
}