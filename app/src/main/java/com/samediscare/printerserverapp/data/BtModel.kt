package com.samediscare.printerserverapp.data


import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothHeadset
import android.bluetooth.BluetoothProfile
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bong.brothersetup.utils.common.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BtModel() : ViewModel() {

    data class DeviceInfo(val device: BluetoothDevice, val bound: Boolean,val bluetoothClass:Int)

    var btAvailable = false
        set(v: Boolean) {
            field = v
            start()
        }

    val isBluetoothOn: MutableLiveData<Boolean> = MutableLiveData(false)

    private var discovering: Boolean = false

    private val adapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()

    val devices: MutableLiveData<List<DeviceInfo>> by lazy { MutableLiveData(emptyList()) }
    val pairDevices: MutableLiveData<List<DeviceInfo>> by lazy { MutableLiveData(emptyList()) }

    private var selectedDevice: BluetoothDevice? = null

    private val profileProxy: MutableSet<BluetoothProfile> =
        emptySet<BluetoothProfile>().toMutableSet()

    private val connected: MutableLiveData<Boolean> = MutableLiveData(true)
    val deviceConnectionStatus: MutableLiveData<Int> = MutableLiveData(-1)

    val selectedPrinterAddress: MutableLiveData<String> = MutableLiveData("")
    val selectedPrinterFormat: MutableLiveData<Int> = MutableLiveData(-1)
    var qrPrintStatus:MutableLiveData<String> = MutableLiveData(Constants.STRING_BLANK)
    @SuppressLint("MissingPermission")
    fun selectDevice(address: String) {
        selectedDevice = devices.value!!.first {
            it.device.address == address
        }.device
        selectedDevice?.apply {
            if (createBond())
                Log.i("MY_BT", "start binding with ${this.address}")
            else
                Log.i("MY_BT", "unable to start binding with ${this.address}")
        }
    }
    fun selectDeviceForRemove(address:String){
        selectedDevice = devices.value!!.first {
            it.device.address == address
        }.device
    }

    @SuppressLint("MissingPermission")
    fun start() {
        if (btAvailable && adapter != null && discovering) {
            this.devices.value = adapter.bondedDevices.map { DeviceInfo(it, true,it.bluetoothClass.deviceClass) }
            this.pairDevices.value=adapter.bondedDevices.map { DeviceInfo(it, true,it.bluetoothClass.deviceClass) }
            adapter.startDiscovery()
        }
    }

    @SuppressLint("MissingPermission")
    fun startDiscovering() {
        if (!discovering) {
            discovering = true
            start()
        }
        if (btAvailable && adapter != null && !discovering) {
            this.discovering = true
            this.devices.value = adapter.bondedDevices.map { DeviceInfo(it, true,it.bluetoothClass.deviceClass) }
            adapter.startDiscovery()
        }
    }

    @SuppressLint("MissingPermission")
    fun stopDiscovering() {
        if (btAvailable && adapter != null && discovering) {
            adapter.cancelDiscovery()
            this.discovering = false
        }
    }

    fun startUsingBy(context: Context) {
        context.registerReceiver(br, intentFilter)
    }

    fun stopUsingBy(context: Context) {
        context.unregisterReceiver(br)
    }

    private val intentFilter = IntentFilter().apply {
        addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED)
        addAction(BluetoothDevice.ACTION_ACL_CONNECTED)
        addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED)
        addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED)
        addAction(BluetoothDevice.ACTION_FOUND)
        addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
    }

    private val br = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                BluetoothDevice.ACTION_BOND_STATE_CHANGED ->
                    onBondStateChanged(
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE),
                        context
                    )
                BluetoothDevice.ACTION_ACL_CONNECTED ->
                    onAclConnected()
                BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED ->
                    onAclDisconnectRequested()
                BluetoothDevice.ACTION_ACL_DISCONNECTED ->
                    onAclDisconnected()
                BluetoothDevice.ACTION_FOUND ->
                    onFound(intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE))
                BluetoothAdapter.ACTION_DISCOVERY_FINISHED ->
                    onDiscoveryFinished()
            }
        }
    }

    private val pl = object : BluetoothProfile.ServiceListener {

        override fun onServiceConnected(profile: Int, proxy: BluetoothProfile) {
            if (profile == BluetoothProfile.HEADSET) {
                profileProxy.add(proxy)
                Log.i("MY_BT", "profile connected: HEADSET [${profileProxy.size}]")
            } else {
                Log.i("MY_BT", "profile connected: $profile")
            }
        }

        override fun onServiceDisconnected(profile: Int) {
            if (profile == BluetoothProfile.HEADSET) {
                Log.i("MY_BT", "profile disconnected: HEADSET")
            } else {
                Log.i("MY_BT", "profile disconnected: $profile")
            }
            profileProxy.clear()
        }
    }

    //private val phoneAddress = "3482C5176355"
    private val phoneAddress = "DCDCE2AA4B49"

    @SuppressLint("MissingPermission")
    fun connect() {
        if (profileProxy.isNotEmpty()) {
            val proxy = profileProxy.elementAt(0) as BluetoothHeadset
            Log.i("MY_BT", "Connecting to $selectedDevice...")
            viewModelScope.launch(Dispatchers.IO) {
                launch {
                    proxy.myConnect(selectedDevice!!)
                }
                delay(15000)
                withContext(Dispatchers.Main) {
                    connected.value =
                        proxy.getConnectionState(selectedDevice!!) == BluetoothHeadset.STATE_CONNECTED
                }
            }
        } else {
            Log.w("MY_BT", "No active BT proxy")
        }
    }


    fun unpairSelectedDevice() {
        fun BluetoothDevice.removeBond() {
            try {
                javaClass.getMethod("removeBond").invoke(this)
            } catch (e: Exception) {
                Log.e("MY_BT", "Removing bond has been failed. ${e.message}")
            }
        }
        selectedDevice?.removeBond()
    }

    @SuppressLint("MissingPermission")
    private fun onBondStateChanged(device: BluetoothDevice?, context: Context) {
        fun bs(s: Int) = when (s) {
            10 -> "BOND NONE"
            11 -> "BONDING"
            12 -> "BONDED"
            else -> "UNKNOWN???"
        }
        if (device != null) {
            Log.i("MY_BT", "bound changed: ${bs(device.bondState)}")
            if (device.bondState == 12) openProxy(context)
            if (device.bondState == 10) {
                val devs = devices.value
                if (devs != null) {
                    devices.value = devs.filterNot { it.device.address == device.address }
                }
            }
        }

    }

    private fun openProxy(context: Context) {
        if (!adapter!!.getProfileProxy(context, pl, BluetoothProfile.HEADSET)) {
            Log.e("MY_BT", "Error opening bt profile")
        }
    }

    private fun onAclConnected() {
        Log.i("MY_BT", "ACL connected")
        deviceConnectionStatus.postValue(1)
    }

    private fun onAclDisconnectRequested() {
        Log.i("MY_BT", "ACL disconnect requested")
        deviceConnectionStatus.postValue(2)

    }

    private fun onAclDisconnected() {
        Log.i("MY_BT", "ACL disconnected")
        deviceConnectionStatus.postValue(3)
    }

    @SuppressLint("MissingPermission")
    private fun onFound(device: BluetoothDevice?) {
        val l = devices.value
        if (device != null && l != null && l.none { it.device.address == device.address }) {
            devices.value = l + DeviceInfo(device, false,device.bluetoothClass.deviceClass)
        }
    }

    @SuppressLint("MissingPermission")
    private fun onDiscoveryFinished() {
        if (discovering && adapter != null) adapter.startDiscovery()
    }



    private fun BluetoothHeadset.myConnect(d: BluetoothDevice) {
        try {
            javaClass.getMethod("connect", BluetoothDevice::class.java).invoke(this, d)
        } catch (e: Exception) {
            Log.e("MY_BT", "Creating bond has been failed. ${e.message}")
        }
    }

    private fun BluetoothHeadset.myDisconnect(d: BluetoothDevice) {
        try {
            javaClass.getMethod("disconnect", BluetoothDevice::class.java).invoke(this, d)
        } catch (e: Exception) {
            Log.e("MY_BT", "Removing bond has been failed. ${e.message}")
        }
    }

}