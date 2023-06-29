package com.samediscare.printerserverapp

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.bong.brothersetup.adapter.PrinterListAdapter
import com.bong.brothersetup.data.MyItem
import com.bong.brothersetup.utils.common.UserPreferences
import com.samediscare.printerserverapp.adapter.PrinterPaperAdapter
import com.samediscare.printerserverapp.data.CommonSpinnerModel
import com.samediscare.printerserverapp.data.repository.LabelPrinterRepository
import com.samediscare.printerserverapp.databinding.ActivityPrinterSettingBinding


class PrinterSetting : AppCompatActivity() {
    private var bAdapter: BluetoothAdapter? = null

    lateinit var mySharedPreferences:UserPreferences
    private lateinit var repository: LabelPrinterRepository
    private var printerFormatList: ArrayList<CommonSpinnerModel> = ArrayList()
    private lateinit var binding: ActivityPrinterSettingBinding
    private val BLUETOOTH_PERMISSION_REQUEST_CODE = 1
   var items= mutableListOf<MyItem>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrinterSettingBinding.inflate(layoutInflater)
        setContentView(binding.root)
         mySharedPreferences = UserPreferences.getInstance(applicationContext)
//        bAdapter = BluetoothAdapter.getDefaultAdapter()
        val bluetoothManager: BluetoothManager =
            applicationContext!!.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bAdapter = bluetoothManager.adapter
        supportActionBar?.title = "Setting"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        setUpView()

    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
               finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
    private fun setUpView(){
        repository=LabelPrinterRepository()
        printerFormatList = repository.getLanguageSelectionOptions(printerFormatList)
        checkBluetoothPermissions()

 }
    @SuppressLint("MissingPermission")
    private fun getBluetoothDevices() = if (bAdapter == null) {
        Toast.makeText(
            applicationContext,
            "Bluetooth Not Supported",
            Toast.LENGTH_SHORT
        ).show()
    } else {
        try{
            val pairedDevices: Set<BluetoothDevice> = bAdapter!!.bondedDevices
            val customItem = MyItem("",  "Select Printer")
            items.add(customItem)
            if (pairedDevices.isNotEmpty()) {
                for (device in pairedDevices) {
                    val customItem = MyItem(device.address,  device.name)
                    items.add(customItem)
                }
            }
            if(items.size<2){
                binding.labelPrinterNote.isVisible=true
            }
            val adapter = PrinterListAdapter(applicationContext, items)
            binding.spinner.adapter = adapter
            if(mySharedPreferences.getInt("pos",-1)>-1)
                binding.spinner.setSelection(mySharedPreferences.getInt("pos",0))
            else
                binding.spinner.setSelection(mySharedPreferences.getInt("pos",0))
            binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                    mySharedPreferences.saveData("address",items[position].macaddress)
                    mySharedPreferences.saveInt("pos",position)
                }
                override fun onNothingSelected(parent: AdapterView<*>) {
                    // Do something when nothing is selected
                }
            }
        } catch(e: Exception) {
            Log.e("----",e.toString())
            binding.labelPrinterNote.isVisible=true
        }
    }
    private fun getBluetoothPaper() {

            val adapter = PrinterPaperAdapter(applicationContext, printerFormatList)
            binding.paperSpinner.adapter = adapter
            if(mySharedPreferences.getInt("paper_pos",-1)>-1){
                binding.paperSpinner.setSelection(mySharedPreferences.getInt("paper_pos",0))
            }
            binding.paperSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                    mySharedPreferences.saveData("paper_size",printerFormatList[position].id)
                    mySharedPreferences.saveInt("paper_pos",position)
                }
                override fun onNothingSelected(parent: AdapterView<*>) {
                    // Do something when nothing is selected
                }
            }

    }
    private fun checkBluetoothPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT)
            != PackageManager.PERMISSION_GRANTED
        ) {
            Log.e("----","Calle 1")
            // Permission not granted, request it
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.BLUETOOTH_CONNECT),
                BLUETOOTH_PERMISSION_REQUEST_CODE
            )
        } else {
            Log.e("----","Calle 2")
            getBluetoothDevices()
            getBluetoothPaper()
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == BLUETOOTH_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getBluetoothDevices()
                getBluetoothPaper()
            } else {

            }
        }
    }

}