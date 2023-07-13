package com.samediscare.printerserverapp.utils.common

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentActivity
import com.permissionx.guolindev.PermissionX
import com.samediscare.printerserverapp.R

object RunTimePermission {
    fun checkPermission(
        context: FragmentActivity,
        listOfPermission: List<String>,
        message:String,
        onSuccess: () -> Unit
    ) {
        PermissionX.init(context)
            .permissions(
                listOfPermission
            )
            .setDialogTintColor(Color.parseColor("#1972e8"), Color.parseColor("#8ab6f5"))
            .onExplainRequestReason { scope, deniedList, beforeRequest ->
                scope.showRequestReasonDialog(
                    deniedList,
                    message,
                    context.getString(R.string.app_common_accept),
                    context.getString(R.string.app_common_deny)
                )
            }
            .onForwardToSettings { scope, deniedList ->
                showAlertDialog(context, deniedList.toString()) { _, _ ->
                    val intent = Intent()
                    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    val uri = Uri.fromParts("package", context.packageName, null)
                    intent.data = uri
                    context.startActivity(intent)
                }

            }
            .request { allGranted, grantedList, deniedList ->
                if (allGranted) {
                    // openSomeActivityForResult()
                    onSuccess()
                    //  onSuccess()
                } else {
                    //context.toast(context.getString(R.string.app_permission_deny) + deniedList)
                }
            }

    }

    private fun showAlertDialog(
        context: Context,
        msg: String,
        positiveListener: DialogInterface.OnClickListener
    ) {
        val alertDialogBuilder: AlertDialog.Builder = AlertDialog.Builder(context)
        alertDialogBuilder.setTitle(context.getString(R.string.app_permission_allow_in_settings))
        alertDialogBuilder.setMessage(msg)
        alertDialogBuilder.setPositiveButton(
            context.getString(R.string.app_common_yes),
            positiveListener
        )
        alertDialogBuilder.setNegativeButton(context.getString(R.string.app_common_no), null)
        val alertDialog: AlertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }
}