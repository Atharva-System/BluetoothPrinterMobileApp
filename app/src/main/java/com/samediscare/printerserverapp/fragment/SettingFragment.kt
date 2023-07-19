package com.samediscare.printerserverapp.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothClass
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.CompoundButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.bong.brothersetup.adapter.PrinterListAdapter
import com.bong.brothersetup.data.MyItem
import com.bong.brothersetup.utils.common.Constants
import com.bong.brothersetup.utils.common.UserPreferences
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.samediscare.printerserverapp.Brother
import com.samediscare.printerserverapp.R
import com.samediscare.printerserverapp.adapter.PrinterPaperAdapter
import com.samediscare.printerserverapp.data.BtModel
import com.samediscare.printerserverapp.data.CommonSpinnerModel
import com.samediscare.printerserverapp.data.repository.LabelPrinterRepository
import com.samediscare.printerserverapp.databinding.FragmentSettingBinding
import com.samediscare.printerserverapp.utils.common.RunTimePermission
import java.util.HashMap
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class SettingFragment : Fragment(), View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private var bAdapter: BluetoothAdapter? = null

    lateinit var mySharedPreferences: UserPreferences
    private lateinit var repository: LabelPrinterRepository
    private var printerFormatList: ArrayList<CommonSpinnerModel> = ArrayList()
    private lateinit var _binding: FragmentSettingBinding
    private val binding get() = _binding!!
    var items= mutableListOf<MyItem>()
    var printerPos=-1
    var paperPos=-1
    private var bitmap: Bitmap? = null
    private lateinit var btModel: BtModel
    private val requireBtPermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                //check permission for android 12
                checkPermissionForAndroid12(it as MutableMap<String, Boolean>)
            } else {
                //check permission for android 11 and below version
                checkPermissionForBelowAndroid12(it as MutableMap<String, Boolean>)
            }
        }
    private val enableBluetoothLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {

        } else {
            setSwitchButton()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    @SuppressLint("MissingPermission")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingBinding.inflate(inflater,container, false)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        mySharedPreferences = context?.let { UserPreferences.getInstance(it) }!!
        val bluetoothManager: BluetoothManager =
            requireContext().getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bAdapter = bluetoothManager.adapter

        setUpView()
        setSwitchButton()
        if(checkBtPermissions()){
            getBluetoothDevices()
            getBluetoothPaper()
        }
        binding.save.setOnClickListener(this)
        binding.testPrint.setOnClickListener(this)

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        btModel = ViewModelProvider(requireActivity()).get(BtModel::class.java)
        btModel.startUsingBy(requireContext())
        btModel.qrPrintStatus.observe(requireActivity()){
            try {
                when (it) {
                    Constants.PrintStatus.NO_INTERNET -> {
                    }
                    Constants.PrintStatus.PRINTING ->{
                    }
                    Constants.PrintStatus.BLUETOOTH_OFF -> {

                        showToast(getString(R.string.printer_bluetooth_off))
                    }
                    Constants.PrintStatus.DEVICE_BLUETOOTH_OFF -> {

                        showToast(getString(R.string.device_bluetooth_off))

                    }
                    Constants.PrintStatus.COMMUNICATION_ERROR -> {

                        showToast(getString(R.string.communication_error))
                    }
                    Constants.PrintStatus.UNKNOWN_ERROR -> {

                        showToast(getString(R.string.unknown_error))
                    }
                    Constants.PrintStatus.SUCCESSFUL -> {

                        showToast(getString(R.string.success))
                    }
                    Constants.PrintStatus.SAVEPAIRDEVICE -> {

                        showToast(getString(R.string.pair_device))
                    }

                    else -> {
                           if(it.isNotEmpty()){
                               showToast(getString(R.string.unknown_error))
                           }
                       // showToast(getString(R.string.success))
                    }

                }

            } catch (e: Exception) {
                //btModel.qrPrintStatus.postValue("")
            }
        }
    }
    private fun setUpView(){
        repository= LabelPrinterRepository()
        printerFormatList = repository.getLanguageSelectionOptions(printerFormatList)


    }
 private fun setSwitchButton(){
     binding.switchButton.setOnCheckedChangeListener(null)
     if (bAdapter == null) {
         binding.switchButton.isChecked=false
     } else binding.switchButton.isChecked = bAdapter!!.isEnabled()
     binding.switchButton.setOnCheckedChangeListener(this)
 }
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.save -> {
                if(paperPos>=0 && printerPos>=0){
                    mySharedPreferences.saveData(getString(R.string.key_address),items[printerPos].macaddress)
                    mySharedPreferences.saveData(getString(R.string.key_paper_size),printerFormatList[paperPos].id)
                    printerFormatList[paperPos].spinnerText?.let {
                        mySharedPreferences.saveData(getString(R.string.key_paper_size_Name),
                            it
                        )
                    }
                    Toast.makeText(context,getString(R.string.data_saved),Toast.LENGTH_LONG).show()
                }

            }
            R.id.testPrint -> {
                binding.testPrint.text=(getString(R.string.printing))
               printQrCode()
            }

            else -> {
            }
        }
    }
    @SuppressLint("MissingPermission")
    private fun getBluetoothDevices() = if (bAdapter == null) {
        Toast.makeText(
            context,
            "Bluetooth Not Supported",
            Toast.LENGTH_SHORT
        ).show()
    } else {
        try{
            items.clear()
            val pairedDevices: Set<BluetoothDevice> = bAdapter!!.bondedDevices
            val customItem = MyItem("",  getString(R.string.select_printer))
            items.add(customItem)
            if (pairedDevices.isNotEmpty()) {
                for (device in pairedDevices) {
                    if (device.bluetoothClass.majorDeviceClass == BluetoothClass.Device.Major.IMAGING) {
                        val customItem = MyItem(device.address,  device.name)
                        items.add(customItem)
                    }

                }
            }
            binding.labelPrinterNote.isVisible = items.size<2
            val adapter = context?.let { PrinterListAdapter(it, items) }
            binding.spinner.adapter = adapter
            try{
                if(items.size<2){
                    binding.spinner.setSelection(0)
                }
                else if(mySharedPreferences.getInt(getString(R.string.key_pos),-1)>-1){
                    var savedMac=(mySharedPreferences.getData(getString(R.string.key_address),""))
                    val index = items.indexOfFirst { item ->
                        item.macaddress == savedMac
                    }
                    if(index!=-1){
                        binding.spinner.setSelection(index)
                    }else{
                        binding.spinner.setSelection(0)
                    }

                }
                else {
                    binding.spinner.setSelection(0)
                }
            }catch (e: Exception){

            }

            binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                   printerPos=position
                    mySharedPreferences.saveInt(getString(R.string.key_pos),position)
                }
                override fun onNothingSelected(parent: AdapterView<*>) {
                    // Do something when nothing is selected
                }
            }
        } catch(e: Exception) {
            binding.labelPrinterNote.isVisible=true
        }
    }
    private fun getBluetoothPaper() {

        val adapter = context?.let { PrinterPaperAdapter(it, printerFormatList) }
        binding.paperSpinner.adapter = adapter
        if(mySharedPreferences.getInt(getString(R.string.key_paper_pos),-1)>-1){
            binding.paperSpinner.setSelection(mySharedPreferences.getInt("paper_pos",0))
        }
        binding.paperSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                paperPos=position
                mySharedPreferences.saveInt(getString(R.string.key_paper_pos),position)
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do something when nothing is selected
            }
        }

    }
    override fun onResume() {

        super.onResume()

    }
    private fun checkBtPermissions(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            fun checkPrm(p: String) =
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    p
                ) == PackageManager.PERMISSION_GRANTED
            return if (checkPrm(Manifest.permission.BLUETOOTH_SCAN) && checkPrm(Manifest.permission.BLUETOOTH_CONNECT)) {
                true
            } else {
                try{
                    requireBtPermissions.launch(
                        arrayOf(
                            Manifest.permission.BLUETOOTH_SCAN,
                            Manifest.permission.BLUETOOTH_CONNECT,
                        )
                    )
                }catch (e: Exception){
                    Log.e("--ded--",e.toString())
                }

                false
            }
        } else {
            fun checkPrm(p: String) =
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    p
                ) == PackageManager.PERMISSION_GRANTED
            return if (checkPrm(Manifest.permission.ACCESS_FINE_LOCATION)) {
                true
            } else {
                requireBtPermissions.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                    )
                )
                false
            }
        }

    }


    private fun checkPermissionForAndroid12(it: MutableMap<String, Boolean>) {
        try {
            if (it[Manifest.permission.BLUETOOTH_SCAN]!! && it[Manifest.permission.BLUETOOTH_CONNECT]!!) {
                getBluetoothDevices()
                getBluetoothPaper()
            } else {
                try{
                    val listOfPermission =
                        listOf(
                            Manifest.permission.BLUETOOTH_SCAN,
                            Manifest.permission.BLUETOOTH_CONNECT,
                        )
                    RunTimePermission.checkPermission(
                        requireActivity(), listOfPermission, "Allow Permission"
                    ) {

                    }
                }catch (e: Exception){
                    Log.e("--err--",e.toString())
                }

            }
        } catch (e: Exception) {
        }
    }


    private fun checkPermissionForBelowAndroid12(status: MutableMap<String, Boolean>) {
        try {
            if (status[Manifest.permission.ACCESS_FINE_LOCATION]!!) {
                getBluetoothDevices()
                getBluetoothPaper()
            } else {

                val listOfPermission =
                    listOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                    )
                RunTimePermission.checkPermission(
                    requireActivity(), listOfPermission, "Allow permission"
                ) {

                }
            }
        } catch (e: Exception) {
        }
    }

    override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {
        if (bAdapter!!.isEnabled) {
            val disableIntent =  Intent(Settings.ACTION_BLUETOOTH_SETTINGS)
            enableBluetoothLauncher.launch(disableIntent)

        } else {
            val enableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            enableBluetoothLauncher.launch(enableIntent)
        }
    }
    private fun printQrCode() {
        val printerAdapter = Brother()
        bitmap = generateQRCode("Hello samediscare")
        printerAdapter.sendFile(bitmap,null, context,mySharedPreferences.getData(getString(R.string.key_address),""), mySharedPreferences.getData("paper_size","").toInt(),false,btModel.qrPrintStatus)

    }
    private fun generateQRCode(content:String): Bitmap? {
        var returnBitmap: Bitmap? = null
        try {
            val hintMap: MutableMap<EncodeHintType, Any?> = HashMap()
            hintMap[EncodeHintType.MARGIN] = 1
            val matrix = MultiFormatWriter().encode(
                content, BarcodeFormat.QR_CODE, 500, 500, hintMap
            )
            val width = matrix.height
            val height = matrix.height
            val pixels = IntArray(width * height)
            for (y in 0 until height) {
                for (x in 0 until width) {
                    val grey = if (matrix[x, y]) 0x00 else 0xff
                    pixels[y * width + x] = -0x1000000 or 0x00010101 * grey
                }
            }
            returnBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            returnBitmap.setPixels(pixels, 0, width, 0, 0, width, height)
            //returnBitmap.set
        } catch (e: java.lang.Exception) {
        }
        return returnBitmap
    }
    private  fun showToast(message: String){
        val isVisible = isFragmentVisible()
        if(isVisible){
            Toast.makeText(context,message,Toast.LENGTH_LONG).show()
            btModel.qrPrintStatus.postValue("")
            binding.testPrint.text=(getString(R.string.test_print))
        }


    }
    private fun isFragmentVisible(): Boolean {
        return isVisible && !isHidden
    }
}

