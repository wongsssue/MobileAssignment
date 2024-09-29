package com.example.funparkapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import com.example.funparkapp.data.CartSouvenirViewModel
import com.example.funparkapp.data.DataInitializer
import com.example.funparkapp.data.MapViewModel
import com.example.funparkapp.data.SouvenirViewModel
import com.example.funparkapp.data.ThemeViewModel
import com.example.funparkapp.data.saveLocationsToFirebase
import androidx.lifecycle.lifecycleScope
import com.example.funparkapp.data.AppDatabase
import com.example.funparkapp.data.UserType
import com.example.funparkapp.ui.theme.FunParkAccessApp
import com.example.funparkapp.ui.theme.FunParkAppTheme
import com.google.firebase.FirebaseApp
import kotlinx.coroutines.launch



class MainActivity : ComponentActivity() {

    private lateinit var souvenirViewModel: SouvenirViewModel
    private lateinit var cartSouvenirViewModel: CartSouvenirViewModel
    private lateinit var themeViewModel: ThemeViewModel
    private lateinit var mapViewModel: MapViewModel // Declare MapViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        DataInitializer.addSouvenirsToFirestore()
        saveLocationsToFirebase()

        souvenirViewModel = ViewModelProvider(this).get(SouvenirViewModel::class.java)
        cartSouvenirViewModel = ViewModelProvider(this).get(CartSouvenirViewModel::class.java)
        themeViewModel = ViewModelProvider(this).get(ThemeViewModel::class.java)
        mapViewModel = ViewModelProvider(this).get(MapViewModel::class.java)

        enableEdgeToEdge()
        FirebaseApp.initializeApp(this)
        setContent {
            FunParkAppTheme {
               FunParkAccessApp(
                   souvenirViewModel = souvenirViewModel,
                   cartSouvenirViewModel = cartSouvenirViewModel,
                   themeViewModel = themeViewModel,
                   mapViewModel = mapViewModel,
               )
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
//            val testUser = UserType(
//                username = "tester",
//                email = "tester@example.com",
//                password = "123",
//                points = 10000,
//                role = "tester"
//            )
//
//            userDao.insert(adminUser)
//            userDao.insert(testUser)
//        }
    }
}


