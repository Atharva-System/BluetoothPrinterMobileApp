package com.samediscare.printerserverapp.fragment

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.bong.brothersetup.utils.common.Constants
import com.bong.brothersetup.utils.common.UserPreferences
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.samediscare.printerserverapp.Brother
import com.samediscare.printerserverapp.MyViewModel
import com.samediscare.printerserverapp.R
import com.samediscare.printerserverapp.data.BtModel
import com.samediscare.printerserverapp.databinding.FragmentPrintBinding
import java.util.*


class PrintFragment : Fragment() {
    private lateinit var mySharedPreferences: UserPreferences
    private var printerMacAddress=""
    private var bitmap: Bitmap? = null
    private val mData: MutableLiveData<String> = MutableLiveData()
    private var _binding: FragmentPrintBinding? = null
    private val binding get() = _binding!!
    private var bAdapter: BluetoothAdapter? = null
    var bluetoothManager: BluetoothManager? = null
    private var mViewModel: MyViewModel? = null
    private lateinit var btModel: BtModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPrintBinding.inflate(inflater,container, false)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        mySharedPreferences = context?.let { UserPreferences.getInstance(it) }!!
        printerMacAddress=mySharedPreferences.getData(getString(R.string.key_address),"")
        btModel = ViewModelProvider(requireActivity()).get(BtModel::class.java)
        btModel.startUsingBy(requireContext())
        binding.b1.setOnClickListener{
            if(printerMacAddress.isNotEmpty())
            {
                if (bitmap != null) {

                        printQrCode()
                        binding.statusTxt.text=getString(R.string.printing)
                    }
                else{
                    Toast.makeText(context,"Please select QR .",Toast.LENGTH_LONG).show()
                }


            }else{
                Toast.makeText(context,"Please select printer from settings.",Toast.LENGTH_LONG).show()
            }
        }

        return binding.root
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mViewModel = ViewModelProviders.of(this).get(MyViewModel::class.java)
        try{
            ViewModelProviders.of(requireActivity()).get(MyViewModel::class.java).message!!.observe(
                viewLifecycleOwner
            ) { message ->
                getDeppLink(message[0], message[1])
            }
        }catch (e: Exception){
            //getDeppLink("https:oioedbncjewehelwkjklewjlw", "89")
        }
        btModel.qrPrintStatus.observe(requireActivity()){
            try {
                when (it) {
                    Constants.PrintStatus.NO_INTERNET -> {
                        checkBlutoothStatus()
                    }
                    Constants.PrintStatus.PRINTING ->{
                     }
                Constants.PrintStatus.BLUETOOTH_OFF -> {
                    checkBlutoothStatus()
                    showToast(getString(R.string.printer_bluetooth_off))
                }
                Constants.PrintStatus.DEVICE_BLUETOOTH_OFF -> {
                    checkBlutoothStatus()
                    showToast(getString(R.string.device_bluetooth_off))

                }
                Constants.PrintStatus.COMMUNICATION_ERROR -> {
                    checkBlutoothStatus()
                    showToast(getString(R.string.communication_error))
                }
                Constants.PrintStatus.UNKNOWN_ERROR -> {
                    checkBlutoothStatus()
                    showToast(getString(R.string.unknown_error))
                    }
                Constants.PrintStatus.SUCCESSFUL -> {
                    checkBlutoothStatus()
                    showToast(getString(R.string.success))
                }
                else ->{

                    checkBlutoothStatus()
                }

            }

        } catch (e: Exception) {
                btModel.qrPrintStatus.postValue("")
               }
    }
    }

    fun  getDeppLink(printUrl:String, printInventoryId:String){
        binding.b1.isEnabled = true
        bitmap = generateQRCode(printUrl)
        binding.i1.setImageBitmap(bitmap)
        binding.txtId.text=printInventoryId
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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

    private fun printQrCode() {
        val printerAdapter = Brother()

        printerAdapter.sendFile(bitmap,null, context,mySharedPreferences.getData(getString(R.string.key_address),""), mySharedPreferences.getData("paper_size","").toInt(),false,btModel.qrPrintStatus)

    }
    override fun onResume() {
        setPrinterData()
        checkBlutoothStatus()
        super.onResume()

    }
   private fun checkBlutoothStatus(){
       val bluetoothManager: BluetoothManager =
           requireContext().getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
       bAdapter = bluetoothManager.adapter
        bAdapter = bluetoothManager?.adapter
        if (bAdapter == null) {
            binding.statusTxt.text=getString(R.string.offline)
        } else if (!bAdapter!!.isEnabled()) {
            binding.statusTxt.text=getString(R.string.offline)
        } else {
            binding.statusTxt.text=getString(R.string.online)
        }
    }
    private fun setPrinterData() {
        try{
            binding.modelTxt.text= mySharedPreferences.getData(getString(R.string.key_address),"Not Available")
            binding.paperTxt.text= mySharedPreferences.getData(getString(R.string.key_paper_size_Name),"Not Available")
            printerMacAddress=mySharedPreferences.getData(getString(R.string.key_address),"")
        }catch (e: Exception)
        {

        }
    }
    private  fun showToast(message: String){
//        val customToastLayout = layoutInflater.inflate(R.layout.custom_toast_layout,null)
//        val customToast = Toast(context)
//        val toastText = customToastLayout.findViewById<TextView>(R.id.custom_toast_text)
//        toastText.text = message
//        customToast.view = customToastLayout
//        customToast.setGravity(Gravity.FILL_HORIZONTAL or Gravity.TOP, 0, 0)
//        customToast.duration = Toast.LENGTH_LONG
//        customToast.show()
//        val toast = Toast.makeText(context, message, Toast.LENGTH_LONG)
//        val view = toast.view
//
//        view!!.background.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN)
//
//        val text = view!!.findViewById<TextView>(android.R.id.message)
//        text.setTextColor(Color.WHITE)
//        toast.setGravity(Gravity.FILL_HORIZONTAL or Gravity.TOP, 0, 0)
//        toast.show()
        Toast.makeText(context,message,Toast.LENGTH_LONG).show()
        btModel.qrPrintStatus.postValue("")


    }
}