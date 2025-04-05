package com.example.mdmpoc

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    private lateinit var devicePolicyManager: DevicePolicyManager
    private lateinit var compName: ComponentName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        devicePolicyManager = getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        compName = ComponentName(this, MyDeviceAdminReceiver::class.java)

        val activateButton = findViewById<Button>(R.id.activateButton)
        activateButton.setOnClickListener {
            if (!devicePolicyManager.isAdminActive(compName)) {
                val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
                intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, compName)
                intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "We need this permission to lock or wipe your device.")
                startActivity(intent)
            } else {
                Toast.makeText(this, "Already activated", Toast.LENGTH_SHORT).show()
            }
        }

        val lockButton = findViewById<Button>(R.id.lockButton)
        lockButton.setOnClickListener {
            if (devicePolicyManager.isAdminActive(compName)) {
                devicePolicyManager.lockNow()
            } else {
                Toast.makeText(this, "Please activate device admin first.", Toast.LENGTH_SHORT).show()
            }
        }

        val passwordButton = findViewById<Button>(R.id.setPasswordPolicyButton)
        passwordButton.setOnClickListener {
            if (devicePolicyManager.isAdminActive(compName)) {
                devicePolicyManager.setPasswordQuality(compName, DevicePolicyManager.PASSWORD_QUALITY_NUMERIC)
                devicePolicyManager.setPasswordMinimumLength(compName, 6)
                Toast.makeText(this, "Password policy applied", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Device Admin not active", Toast.LENGTH_SHORT).show()
            }
        }

        val kioskButton = findViewById<Button>(R.id.kioskButton)
        kioskButton.setOnClickListener {
            if (devicePolicyManager.isAdminActive(compName)) {
                setLockTaskPackage()
                if (devicePolicyManager.isLockTaskPermitted(packageName)) {
                    startLockTask()
                    Toast.makeText(this, "Kiosk Mode Started", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Kiosk mode not permitted", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Activate Device Admin first", Toast.LENGTH_SHORT).show()
            }
        }

        val exitKioskButton = findViewById<Button>(R.id.exitKioskButton)
        exitKioskButton.setOnClickListener {
            stopLockTask()
            Toast.makeText(this, "Kiosk Mode Exited", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setLockTaskPackage() {
        val packages = arrayOf(packageName)
        devicePolicyManager.setLockTaskPackages(compName, packages)
    }

    private fun exitKioskMode() {
        stopLockTask()
        Toast.makeText(this, "Exited Kiosk Mode", Toast.LENGTH_SHORT).show()
    }
}
