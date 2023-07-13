package com.samediscare.printerserverapp.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.bong.brothersetup.adapter.PrinterListAdapter
import com.bong.brothersetup.data.MyItem
import com.bong.brothersetup.utils.common.UserPreferences
import com.samediscare.printerserverapp.R
import com.samediscare.printerserverapp.adapter.PrinterPaperAdapter
import com.samediscare.printerserverapp.data.CommonSpinnerModel
import com.samediscare.printerserverapp.data.repository.LabelPrinterRepository
import com.samediscare.printerserverapp.databinding.FragmentSettingBinding
import com.samediscare.printerserverapp.utils.common.RunTimePermission

class SettingFragment : Fragment(), View.OnClickListener {
    private var bAdapter: BluetoothAdapter? = null

    lateinit var mySharedPreferences: UserPreferences
    private lateinit var repository: LabelPrinterRepository
    private var printerFormatList: ArrayList<CommonSpinnerModel> = ArrayList()
    private lateinit var _binding: FragmentSettingBinding
    private val binding get() = _binding!!
    var items= mutableListOf<MyItem>()
    var printerPos=-1
    var paperPos=-1
     val SWITCH_COMPAT_IGNORE_TAG = "SWITCH_COMPAT_IGNORE_TAG"

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
        if (bAdapter == null) {
            binding.switchButton.isChecked=isCheckedWithIgnoreTag(false)
        } else if (!bAdapter!!.isEnabled()) {
            binding.switchButton.isChecked=isCheckedWithIgnoreTag(false)
        } else {
            binding.switchButton.isChecked=isCheckedWithIgnoreTag(true)
        }

        binding.save.setOnClickListener(this)



        binding.switchButton.setOnCheckedChangeListener { buttonView, isChecked ->
            if (buttonView.tag != SWITCH_COMPAT_IGNORE_TAG) {
                if (bAdapter!!.isEnabled) {
                    bAdapter!!.disable()
                } else {
                    bAdapter!!.enable()
                    val handler = Handler()

                    handler.postDelayed({
                        getBluetoothDevices()
                    }, 2000)

                }
            }

        }
        return binding.root
    }

    private fun isCheckedWithIgnoreTag(isChecked: Boolean): Boolean {
        binding.switchButton.tag = SWITCH_COMPAT_IGNORE_TAG
        binding.switchButton.tag = null
        return isChecked
    }
    private fun setUpView(){
        repository= LabelPrinterRepository()
        printerFormatList = repository.getLanguageSelectionOptions(printerFormatList)


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
               Toast.makeText(context,"ON Progress",Toast.LENGTH_LONG).show()
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
                    val customItem = MyItem(device.address,  device.name)
                    items.add(customItem)
                }
            }
            binding.labelPrinterNote.isVisible = items.size<2
            val adapter = context?.let { PrinterListAdapter(it, items) }
            binding.spinner.adapter = adapter
            if(items.size<2){
                binding.spinner.setSelection(0)
            }
            else if(mySharedPreferences.getInt(getString(R.string.key_pos),-1)>-1)
                binding.spinner.setSelection(mySharedPreferences.getInt(getString(R.string.key_pos),0))
            else
                binding.spinner.setSelection(mySharedPreferences.getInt(getString(R.string.key_pos),0))
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
                mySharedPreferences.saveInt("paper_pos",position)
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do something when nothing is selected
            }
        }

    }
    override fun onResume() {
        if(checkBtPermissions()){
            getBluetoothDevices()
            getBluetoothPaper()
        }
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
                requireBtPermissions.launch(
                    arrayOf(
                        Manifest.permission.BLUETOOTH_SCAN,
                        Manifest.permission.BLUETOOTH_CONNECT,
                    )
                )
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

                val listOfPermission =
                    listOf(
                        Manifest.permission.BLUETOOTH_SCAN,
                        Manifest.permission.BLUETOOTH_CONNECT,
                    )
                RunTimePermission.checkPermission(
                    requireActivity(), listOfPermission, "Allow Permission"
                ) {
                   Log.e("-----","cdkdjcvkldjcle")
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
                    Log.e("-----","cdkdjcvkcceceeldjcle")
                }
            }
        } catch (e: Exception) {
        }
    }

}