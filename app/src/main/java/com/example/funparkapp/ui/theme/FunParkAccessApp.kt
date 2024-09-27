package com.example.funparkapp.ui.theme


import TicketViewModel
import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.funparkapp.R
import com.example.funparkapp.data.AppDatabase
import com.example.funparkapp.data.CartItemRepository
import com.example.funparkapp.data.CartItemViewModel
import com.example.funparkapp.data.CartItemViewModelFactory
import com.example.funparkapp.data.PaymentMethodRepository
import com.example.funparkapp.data.PaymentMethodViewModel
import com.example.funparkapp.data.PaymentMethodViewModelFactory
import com.example.funparkapp.data.PurchaseHistoryRepository
import com.example.funparkapp.data.PurchaseHistoryViewModel
import com.example.funparkapp.data.PurchaseHistoryViewModelFactory
import com.example.funparkapp.data.SharedViewModel
import com.example.funparkapp.data.TicketRepository
import com.example.funparkapp.data.TicketViewModelFactory
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


enum class FunParkScreen(@StringRes val title: Int){
    MainMenu(title = R.string.app_name),
    TicketMenu(title = R.string.ticket),
    TicketType(title = R.string.ticket_type),
    ShoppingCart(title = R.string.shoppingCart),
    Checkout(title = R.string.checkOut),
    PaySuccess(title = R.string.none),
    Receipt(title = R.string.ticket_receipt),
    TicketHistory(title = R.string.ticket_history),
    AdminTicket(title = R.string.admin_ticket_screen_title)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(
    currentScreen: FunParkScreen,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    onMenuClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    CenterAlignedTopAppBar(
        title = { Text(stringResource(currentScreen.title), fontSize = 30.sp, fontWeight = FontWeight.Bold) },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color(0xFFFFA500),
            titleContentColor = Color.White
        ),
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = { navigateUp() }) {
                    Icon(
                        painter = painterResource(R.drawable.back),
                        contentDescription = stringResource(R.string.back_button)
                    )
                }
            }
        },
        actions = {
            IconButton(onClick = { onMenuClick() }) {
                Icon(
                    painter = painterResource(R.drawable.menu),
                    contentDescription = "Menu Bar",
                    modifier = Modifier.size(35.dp)
                )
            }
        }
    )
}

@Composable
fun FunParkAccessApp(
    navController: NavHostController = rememberNavController(),
    context: Context = LocalContext.current
) {
    // Initialize coroutine scope
    val coroutineScope = rememberCoroutineScope()

    // Initialize firebase reference
    val firebaseDatabase: DatabaseReference = FirebaseDatabase.getInstance().getReference("tickets")
    val firebaseDatabase2: DatabaseReference = FirebaseDatabase.getInstance().getReference("ticketPurchased")

    // Initialize the database and repository
    val appDatabase = remember { AppDatabase.getDatabase(context) }
    val ticketRepository = remember { TicketRepository(appDatabase.ticketDao, firebaseDatabase, coroutineScope) }
    val cartRepository = remember { CartItemRepository(appDatabase.cartDao) }
    val purchaseHistoryRepository = remember { PurchaseHistoryRepository(appDatabase.ticketPurchasedDao, firebaseDatabase2) }
    val paymentMethodRepository = remember { PaymentMethodRepository(appDatabase.paymentMethodDao) }

    // Create ViewModelFactory instances
    val ticketViewModelFactory = remember { TicketViewModelFactory(ticketRepository) }
    val cartItemViewModelFactory = remember { CartItemViewModelFactory(cartRepository) }
    val purchaseHistoryViewModelFactory = remember { PurchaseHistoryViewModelFactory(purchaseHistoryRepository) }
    val paymentMethodViewModelFactory = remember {PaymentMethodViewModelFactory(paymentMethodRepository)}

    // Get ViewModel instances using the factory
    val ticketViewModel: TicketViewModel = viewModel(factory = ticketViewModelFactory)
    val cartItemViewModel: CartItemViewModel = viewModel(factory = cartItemViewModelFactory)
    val purchaseHistoryViewModel: PurchaseHistoryViewModel = viewModel(factory = purchaseHistoryViewModelFactory)
    val paymentMethodViewModel: PaymentMethodViewModel = viewModel(factory = paymentMethodViewModelFactory)
    val sharedViewModel: SharedViewModel = viewModel()


    val backStackEntry by navController.currentBackStackEntryAsState()

    val currentRoute = backStackEntry?.destination?.route ?: FunParkScreen.MainMenu.name
    val currentScreen = when {
        currentRoute.startsWith(FunParkScreen.TicketType.name) -> FunParkScreen.TicketType
        currentRoute == FunParkScreen.MainMenu.name -> FunParkScreen.MainMenu
        currentRoute == FunParkScreen.TicketMenu.name -> FunParkScreen.TicketMenu
        currentRoute == FunParkScreen.ShoppingCart.name -> FunParkScreen.ShoppingCart
        currentRoute == FunParkScreen.Checkout.name -> FunParkScreen.Checkout
        currentRoute == FunParkScreen.PaySuccess.name -> FunParkScreen.PaySuccess
        currentRoute == FunParkScreen.Receipt.name -> FunParkScreen.Receipt
        else -> FunParkScreen.MainMenu
    }

    Scaffold(
        topBar = {
            AppBar(
                currentScreen = currentScreen,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() },
                onMenuClick = {navController.navigate(FunParkScreen.MainMenu.name)}
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = FunParkScreen.MainMenu.name,
            modifier = Modifier.padding(innerPadding)
        ){
            //MainMenu
            composable(route = FunParkScreen.MainMenu.name){
                MainMenuScreen(
                    onTicketClick = {navController.navigate(FunParkScreen.TicketMenu.name)},
                    onReserveClick = {}
                )
            }

            //Admin Ticket
            composable(route = FunParkScreen.AdminTicket.name){
                AdminTicketScreen(ticketViewModel = ticketViewModel)
            }

            //Ticket Menu
            composable(route = FunParkScreen.TicketMenu.name) {
                SelectTicketScreen(
                    ticketViewModel = ticketViewModel,
                    goToShoppingCart = { navController.navigate(FunParkScreen.ShoppingCart.name) },
                    goToSpecificTicketPlan = { ticketPlan ->
                        navController.navigate("${FunParkScreen.TicketType.name}/$ticketPlan")
                    }
                )
            }

            //Ticket Type
            composable(route = "${FunParkScreen.TicketType.name}/{ticketPlan}") { backStackEntry ->
                val ticketPlan = backStackEntry.arguments?.getString("ticketPlan") ?: ""
                // Pass the ticketPlan to SelectTicketType
                SelectTicketType(
                    ticketPlan = ticketPlan,
                    ticketViewModel = ticketViewModel,
                    cartItemViewModel = cartItemViewModel,
                    goToShoppingCart = {navController.navigate(FunParkScreen.ShoppingCart.name)}
                )
            }

            // Shopping Cart
            composable(route = FunParkScreen.ShoppingCart.name) {
                ShoppingCartScreen(
                    cartItemViewModel = cartItemViewModel,
                    checkout = { navController.navigate(FunParkScreen.Checkout.name) },
                    continueShopping = { navController.navigate(FunParkScreen.TicketMenu.name) }
                )
            }

            // Checkout
            composable(route = FunParkScreen.Checkout.name) {
                CheckoutScreen(
                    paySuccess = {navController.navigate(FunParkScreen.PaySuccess.name) },
                    cartItemViewModel = cartItemViewModel,
                    purchaseHistoryViewModel = purchaseHistoryViewModel,
                    paymentMethodViewModel = paymentMethodViewModel,
                    sharedViewModel= sharedViewModel
                )
            }

            //Pay Success
            composable(route = FunParkScreen.PaySuccess.name) {
                PaySuccessScreen(
                    viewReceipt = { navController.navigate(FunParkScreen.Receipt.name) }
                )
            }

            //View Receipt
            composable(route = FunParkScreen.Receipt.name) {
                TicketReceiptScreen(
                    purchaseHistoryViewModel = purchaseHistoryViewModel,
                    sharedViewModel = sharedViewModel,
                    homePageClick = { navController.navigate(FunParkScreen.MainMenu.name) }
                )
            }

            //Ticket Purchased History
            composable(route = FunParkScreen.TicketHistory.name){
                TicketPurchasedHistoryScreen(
                    purchaseHistoryViewModel = purchaseHistoryViewModel,
                    sharedViewModel = sharedViewModel,
                    viewTicket = { navController.navigate(FunParkScreen.Receipt.name) }
                )
            }

        }
    }
}
