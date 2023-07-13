package com.samediscare.printerserverapp

import androidx.lifecycle.LiveData

import androidx.lifecycle.MutableLiveData

import androidx.lifecycle.ViewModel


class MyViewModel : ViewModel() {
    // TODO: Implement the ViewModel
    private var stringMutableLiveDataUrl: MutableLiveData<String>? = null
    private var stringMutableLiveDataId: MutableLiveData<String>? = null
    private var arrayMutableLiveDataDeep: MutableLiveData<List<String>>? = null
    private var stringMutableLiveStatus: MutableLiveData<String>? = null
    fun init() {
        stringMutableLiveDataUrl = MutableLiveData()
        stringMutableLiveDataId = MutableLiveData()
        arrayMutableLiveDataDeep = MutableLiveData()
        stringMutableLiveStatus = MutableLiveData()
    }

    fun sendData(url: String, id: String) {
        val newFavs: ArrayList<String> = ArrayList()
        newFavs.add(url)
        newFavs.add(id)
        arrayMutableLiveDataDeep?.value=newFavs
        stringMutableLiveDataUrl!!.value = url
        stringMutableLiveDataId!!.value = id
    }
    fun sendStatus(status: String) {

        stringMutableLiveStatus!!.value = status
    }

    val URL: LiveData<String>?
        get() = stringMutableLiveDataUrl

    val message: MutableLiveData<List<String>>?
        get() = arrayMutableLiveDataDeep

    val status: LiveData<String>?
        get() = stringMutableLiveStatus



}