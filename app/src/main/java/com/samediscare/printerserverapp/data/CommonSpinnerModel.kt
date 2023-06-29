package com.samediscare.printerserverapp.data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class CommonSpinnerModel(
    @Expose
    @SerializedName("id")
    val id: String,

    @Expose
    @SerializedName("spinnerText")
    val spinnerText: String?,

    @Expose
    @SerializedName("spinnerImage")
    val spinnerImage: String?
)
