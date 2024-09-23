package com.example.funparkapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.funparkapp.ui.theme.FunParkAccessApp
import com.example.funparkapp.ui.theme.FunParkAppTheme


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FunParkAppTheme {
               FunParkAccessApp()
            }
        }
    }
}


