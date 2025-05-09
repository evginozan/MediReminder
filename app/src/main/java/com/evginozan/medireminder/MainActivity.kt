package com.evginozan.medireminder

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import com.evginozan.medireminder.core.navigation.NavGraph
import com.evginozan.medireminder.core.theme.Component
import com.evginozan.medireminder.core.theme.MediReminderTheme
import com.evginozan.medireminder.presentation.components.BottomNavigationBar
import com.evginozan.medireminder.service.MedicineTrackerWorker
import com.google.accompanist.systemuicontroller.rememberSystemUiController

class MainActivity : ComponentActivity() {
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // İzin verildi, kullanıcıya bildirim verilebilir
            Toast.makeText(
                this,
                "İlaç hatırlatıcı bildirimleri aktif edildi",
                Toast.LENGTH_SHORT
            ).show()

            // WorkManager servisini başlat (izin verildikten sonra)
            MedicineTrackerWorker.scheduleWork(this)
        } else {
            // İzin reddedildi, kullanıcıya açıklama yapılmalı
            showNotificationPermissionExplanationDialog()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Bildirim izni kontrolü ve isteme
        checkNotificationPermission()

        setContent {
            MediReminderTheme {
                val systemUiController = rememberSystemUiController()
                val isDarkTheme = isSystemInDarkTheme()

                // Status bar rengini değiştirmek için daha kapsamlı bir yaklaşım:
                SideEffect {
                    // Tüm sistem barlarını değiştir - status bar dahil
                    systemUiController.setStatusBarColor(
                        color = Component,
                        darkIcons = false
                    )
                }

                val navController = rememberNavController()

                Scaffold(
                    bottomBar = {
                        BottomNavigationBar(navController = navController)
                    }
                ) { innerPadding ->
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        NavGraph(navController = navController)
                    }
                }
            }
        }
    }


    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permissionCheck = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            )

            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun showNotificationPermissionExplanationDialog() {
        android.app.AlertDialog.Builder(this)
            .setTitle("Bildirim İzni Gerekli")
            .setMessage("İlaç hatırlatıcıları için bildirim izni gereklidir. " +
                    "İzin vermezseniz, ilaç alma zamanlarınızı ve stok uyarılarını alamazsınız.")
            .setPositiveButton("Ayarlara Git") { _, _ ->
                // Kullanıcıyı uygulama ayarlarına yönlendir
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", packageName, null)
                }
                startActivity(intent)
            }
            .setNegativeButton("İptal") { dialog, _ ->
                dialog.dismiss()
                Toast.makeText(
                    this,
                    "Bildirimler kapalı. İlaç hatırlatmaları yapılamayacak.",
                    Toast.LENGTH_LONG
                ).show()
            }
            .setCancelable(false)
            .show()
    }
}