package com.yang.simpleplayer.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.yang.simpleplayer.R
import com.yang.simpleplayer.activities.list.ListActivity
import com.yang.simpleplayer.databinding.ActivityMainBinding

// TODO: permission 요청 거절 2번 초과시 intent 사용해서 사용자를 설정으로 안내하기
class MainActivity : AppCompatActivity() {
    private var _binding:ActivityMainBinding? = null
    private val binding:ActivityMainBinding get() = requireNotNull(_binding)

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        showListActivity()
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()
        ){ isGranted:Boolean ->
            if(isGranted) {
                startListActivity()
            } else {
                finish()
            }
        }

    private fun showListActivity() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                        startListActivity()}
        else requestReadExternalStoragePermission()
    }

    private fun requestReadExternalStoragePermission() {
        if(shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)){
            showPermissionAlertDialog()
        } else {
            showPermissionAlertDialog()
        }
    }

    private fun startListActivity() {
        val intent = Intent(this, ListActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun showPermissionAlertDialog() {
        val alertDialog:AlertDialog? = this.let {
            val builder = AlertDialog.Builder(it)
            builder.apply {
                setMessage(R.string.permission_message)
                setPositiveButton(R.string.ok) { _, _ ->
                    requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
                setNegativeButton(R.string.finish) { _, _ ->
                    finish()
                }
            }
            builder.create()
        }
        alertDialog?.show()
    }
}