package com.example.runningassistant.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentContainerView
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.runningassistant.R
import com.example.runningassistant.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

       val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        setupActionBarWithNavController(navHostFragment.navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        return findViewById<FragmentContainerView>(R.id.nav_host_fragment).findNavController()
            .navigateUp() || super.onSupportNavigateUp()
    }

    fun checkPermission(permission: String, requestCode: Int): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    permission
                ) == PackageManager.PERMISSION_GRANTED -> {
                    return true
                }
                shouldShowRequestPermissionRationale(permission) -> {
                    val builder: AlertDialog.Builder = AlertDialog.Builder(this)

                    builder.setMessage("To track your location during training app needs this permission\n Without it you can still browse your old trainings")
                        .setTitle("Requesting " + permission + "permission")
                        .setPositiveButton(getString(R.string.confirm_text)) { dialog, _ ->
                            requestPermissions(arrayOf(permission), requestCode)
                            dialog.cancel()
                        }.setNegativeButton(R.string.cancel_text) { dialog, _ ->
                            dialog.cancel()
                        }
                    return false
                }
                else -> {
                    requestPermissions(arrayOf(permission), requestCode)
                    return false
                }
            }
        } else {
            return true
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == COARSE_LOCATION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    val intent = Intent(this, TrainingActivity::class.java)
                    startActivity(intent)
                }
            }
        } else {
            if (requestCode == FINE_LOCATION_REQUEST_CODE) {
                if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    val intent = Intent(this, TrainingActivity::class.java)
                    startActivity(intent)

                }
            }
        }
    }

    companion object {
        const val COARSE_LOCATION_REQUEST_CODE = 100
        const val FINE_LOCATION_REQUEST_CODE = 101
    }
}