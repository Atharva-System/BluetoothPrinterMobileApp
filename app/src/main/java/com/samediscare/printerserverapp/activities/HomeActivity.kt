package com.samediscare.printerserverapp.activities

import android.net.Uri
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.samediscare.printerserverapp.MyViewModel
import com.samediscare.printerserverapp.R
import com.samediscare.printerserverapp.databinding.ActivityHomeBinding


class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var navController: NavController
    var myViewModel: MyViewModel? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        navController= Navigation.findNavController(this,R.id.activity_home_nav_host_fragment)
        setupWithNavController(binding.bottomNavigationView,navController)
        val mainIntent = intent
        if (mainIntent?.data != null) {
            val data: Uri = mainIntent.data!!
           var printUrl= data.getQueryParameter(getString(R.string.select_url)).toString()
            var printInventoryId =data.getQueryParameter(getString(R.string.select_id)).toString()
            if (printUrl.isNotEmpty()){
                myViewModel = ViewModelProviders.of(this)[MyViewModel::class.java]
                myViewModel!!.init()
                myViewModel!!.sendData(printUrl,printInventoryId)

            }

        }


    }
}