package com.example.funparkapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.example.funparkapp.data.AppDatabase
import com.example.funparkapp.data.UserType
import com.example.funparkapp.ui.theme.FunParkAccessApp
import com.example.funparkapp.ui.theme.FunParkAppTheme
import com.google.firebase.FirebaseApp
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        FirebaseApp.initializeApp(this)
        setContent {
            FunParkAppTheme {
               FunParkAccessApp()
            }
        }

//        lifecycleScope.launch {
//            val appDatabase = AppDatabase.getDatabase(this@MainActivity)
//            val userDao = appDatabase.userDao
//
//            val adminUser = UserType(
//                username = "admin",
//                email = "admin@example.com",
//                password = "123",
//                points = 0,
//                role = "Admin"
//            )
//
//            userDao.insert(adminUser)
//        }
    }
}


