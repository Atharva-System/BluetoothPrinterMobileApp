package com.samediscare.printerserverapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.lifecycle.MutableLiveData;

import com.bong.brothersetup.utils.common.Constants;
import com.brother.ptouch.sdk.Printer;
import com.brother.ptouch.sdk.PrinterInfo;
import com.brother.ptouch.sdk.PrinterStatus;

import java.util.Set;


public class Brother {
    private final String TAG = Brother.class.getSimpleName();
    private int labelFormat = -1;


    public void sendFile(Bitmap bitmap,
                         String filePath,
                         Context context,
                         String addressOfSelectedPrinter,
                         int labelFormat,
                         boolean isPrintPdf,
                         MutableLiveData<String> printStatus) {
        Log.v("app", "Prepping to send file to printer");

        Log.v("app", "Instantiating Brother Printer");

        this.labelFormat = labelFormat;
        // Setup the printer
        Printer printer = new Printer();
        try {
            BluetoothAdapter adapter1;
            BluetoothManager adapter = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
            adapter1 = adapter.getAdapter();
            @SuppressLint("MissingPermission") Set<BluetoothDevice> listofDevice = adapter1.getBondedDevices();
            Log.e("data", "" + listofDevice.toString());
            if (listofDevice.size() == 0) {
                // Alert.show(context, "Please connect bluetooth from setting menu");
                //  String msg = context.getResources().getString(R.string.app_common_alert);
                // String description = context.getResources().getString(R.string.app_dashboard_settings_label_printer_setting_bluetooth_off);
                // showAlert(context, msg, description, "");
                Log.e(TAG, "Please pair bluetooth device or start Bluetooth");

                //Toast.makeText(context,"Please pair bluetooth device or start Bluetooth",Toast.LENGTH_LONG).show();
                printStatus.postValue(Constants.PrintStatus.SAVE_PAIR_DEVICE); // Shashwat
            }
            printer.setBluetooth(adapter1);
            for (BluetoothDevice s : listofDevice) {
                //   printTOIFormBitMap(bitmap,s,context);
                if (addressOfSelectedPrinter.equals(s.toString())) {
                    Log.e("both are equal", "" + s);
                    if (isPrintPdf) {
                        printTOIFormBitMap(null, s, context, filePath, isPrintPdf, printStatus);
                    } else {
                        printTOIFormBitMap(bitmap, s, context, null, isPrintPdf, printStatus);
                    }
                    // printTOIFormBitMap(bitmap, s, context);
                }
            }

        } catch (Exception e) {
            printStatus.postValue(Constants.PrintStatus.UNKNOWN_ERROR);
            Log.e("Some tag", Log.getStackTraceString(e.getCause().getCause()));
        }
    }


    public void printTOIFormBitMap(Bitmap bitmap, BluetoothDevice btDevice, Context context, String filePath, boolean isPrintPdf, MutableLiveData<String> printStatus) {
        // Specify printer
        final Printer printer = new Printer();
        PrinterInfo settings = printer.getPrinterInfo();


        @SuppressLint("MissingPermission") final String deviceName = btDevice.getName();
        if (deviceName == null) {

        }
        PrinterInfo.Model model = null;

        model = PrinterInfo.Model.PT_P910BT;

        if (model == null) {

        }

        settings.printerModel = model;
        /***************************************/

        // For Bluetooth:
        printer.setBluetooth(BluetoothAdapter.getDefaultAdapter());
        settings.port = PrinterInfo.Port.BLUETOOTH;
        settings.macAddress = btDevice.getAddress();
        // Print Settings
        settings.printMode = PrinterInfo.PrintMode.FIT_TO_PAPER;
        settings.labelNameIndex = labelFormat;
        settings.isAutoCut = true;
        settings.isCutAtEnd = true;
        settings.workPath = context.getCacheDir().getAbsolutePath();
        printer.setPrinterInfo(settings);
        Log.e(TAG, "settings: " + settings);
        Log.e(TAG, "labelFormat: " + labelFormat);
        try {
            new Thread(() -> {
                PrinterStatus result = null;
                boolean startCommunicationSuccess = printer.startCommunication();
                Log.e(TAG, "startCommunicationSuccess: " + startCommunicationSuccess);
                if (startCommunicationSuccess) {
                    if (isPrintPdf) {
                        result = printer.printPdfFile(filePath, 1);
                    } else {
                        result = printer.printImage(bitmap);
                    }
                    Log.e("result", "" + result.toString());
                    Log.e("errorCode", "" + result.errorCode);
                    if (result.errorCode.toString().equals("ERROR_NONE")) {
                        Log.e(TAG, "Successfully printed");
                        printStatus.postValue(Constants.PrintStatus.SUCCESSFUL);
                    } else {
                        Log.e(TAG, "Got some error to print pdf");
                        printStatus.postValue(Constants.PrintStatus.COMMUNICATION_ERROR);
                        // printStatus.postValue("Got some error to print pdf");
                    }
                    Log.e("statusBytes", "" + result.statusBytes);
                    //  RxBus.getInstance().publish(new PrintDoneEvent(result));
                    printer.endCommunication();
                } else {
                    //Toast.makeText(context,"Please connect with bluetooth device.",Toast.LENGTH_LONG).show();
                    // showAlert(context, msg, description, "");
                    printStatus.postValue(Constants.PrintStatus.DEVICE_BLUETOOTH_OFF);
                    // printStatus.postValue("startCommunicationSuccess: Please connect with bluetooth device.");
                    Log.e(TAG, "startCommunicationSuccess: Please connect with bluetooth device.");
                }
            }).start();
        } catch (Exception e) {
            printStatus.postValue(Constants.PrintStatus.UNKNOWN_ERROR);
            e.printStackTrace();
        }
    }
}
