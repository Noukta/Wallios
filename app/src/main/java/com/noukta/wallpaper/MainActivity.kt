package com.noukta.wallpaper

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.noukta.wallpaper.ext.requestNotificationsPermission
import com.noukta.wallpaper.ui.MainContent
import com.noukta.wallpaper.ui.theme.WallpaperAppTheme
import com.noukta.wallpaper.util.PrefHelper
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val vm: MainViewModel by viewModels()
    private lateinit var auth: FirebaseAuth
    private var startTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase Auth
        auth = Firebase.auth

        // Request notification permission on Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestNotificationsPermission(this)
        }

        setContent {
            WallpaperAppTheme {
                // Handle back press in Compose
                BackHandler {
                    vm.toggleExitDialog()
                }
                MainContent(vm)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        startTime = System.currentTimeMillis()

        // Sign in anonymously to Firebase - ensure auth completes before data fetching
        if (auth.currentUser == null) {
            auth.signInAnonymously()
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Log.d("FirestoreAuth", "signInAnonymously:success")
                        vm.onAuthSuccess()
                    } else {
                        Log.w("FirestoreAuth", "signInAnonymously:failure", task.exception)
                        Toast.makeText(
                            this,
                            getString(R.string.auth_failed),
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
                }
        } else {
            // Already authenticated
            vm.onAuthSuccess()
        }
    }

    override fun onStop() {
        super.onStop()
        val timeSpent = System.currentTimeMillis() - startTime + PrefHelper.getTimeSpent()
        PrefHelper.setTimeSpent(timeSpent)
    }
}