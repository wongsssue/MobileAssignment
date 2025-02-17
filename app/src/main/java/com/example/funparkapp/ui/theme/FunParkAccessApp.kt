package com.example.funparkapp.ui.theme

import TicketViewModel
import android.content.Context
import android.util.Log
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.funparkapp.R
import com.example.funparkapp.data.AppDatabase
import com.example.funparkapp.data.CartSouvenirViewModel
import com.example.funparkapp.data.CartItemRepository
import com.example.funparkapp.data.CartItemViewModel
import com.example.funparkapp.data.CartItemViewModelFactory
import com.example.funparkapp.data.CheckoutViewModel
import com.example.funparkapp.data.FacilityRepository
import com.example.funparkapp.data.FacilityViewModel
import com.example.funparkapp.data.FacilityViewModelFactory
import com.example.funparkapp.data.MapViewModel
import com.example.funparkapp.data.PaymentMethodRepository
import com.example.funparkapp.data.PaymentMethodViewModel
import com.example.funparkapp.data.PaymentMethodViewModelFactory
import com.example.funparkapp.data.PurchaseHistoryRepository
import com.example.funparkapp.data.PurchaseHistoryViewModel
import com.example.funparkapp.data.PurchaseHistoryViewModelFactory
import com.example.funparkapp.data.RedeemHistoryRepository
import com.example.funparkapp.data.ReservationRepository
import com.example.funparkapp.data.ReservationViewModel
import com.example.funparkapp.data.ReservationViewModelFactory
import com.example.funparkapp.data.SharedViewModel
import com.example.funparkapp.data.TicketRepository
import com.example.funparkapp.data.TicketViewModelFactory
import com.example.funparkapp.data.UserViewModel
import com.example.funparkapp.data.UserDao
import com.example.funparkapp.data.UserRepository
import com.example.funparkapp.data.UserType
import com.example.funparkapp.data.UserViewModelFactory
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.example.funparkapp.data.RedeemHistoryDao
import com.example.funparkapp.data.RedeemHistoryViewModel
import com.example.funparkapp.data.RedeemHistoryViewModelFactory
import com.example.funparkapp.data.Souvenir
import com.example.funparkapp.data.SouvenirViewModel
import com.example.funparkapp.data.ThemeViewModel
import com.google.gson.Gson


enum class FunParkScreen(@StringRes val title: Int){
    MainMenu(title = R.string.app_name),
    TicketMenu(title = R.string.ticket),
    TicketType(title = R.string.ticket_type),
    ShoppingCart(title = R.string.shoppingCart),
    Checkout(title = R.string.checkOut),
    PaySuccess(title = R.string.none),
    Receipt(title = R.string.ticket_receipt),
    TicketHistory(title = R.string.ticket_history),
    AdminTicket(title = R.string.admin_ticket_screen_title),
    GetStarted(title = R.string.get_started), // Add GetStarted
    Login(title = R.string.login),          // Add Login
    Register(title = R.string.register),
    Menu(title = R.string.menu),
    Account(title = R.string.account),
    Redeem(title = R.string.redeem),
    AdminDashboard(title = R.string.admin_dashboard),
    AdminManageUser(title = R.string.admin_manage_user),
    AdminManageRedeem(title = R.string.admin_manage_redeem),
    RVManagementMainScreen(title = R.string.reservation_management),
    RVViewScreen(title = R.string.reservation_view_screen),
    RVTicketConfirmationScreen(title = R.string.reservation_ticket_confirmation_screen),
    RVMainScreen(title = R.string.reservation_main_page),
    RVSelectionScreen(title = R.string.reservation_selection_screen),
    RVSummaryScreen(title = R.string.reservation_summary_screen),
    RVDoneScreen(title = R.string.reservation_done_screen),
    RVQRScreen(title = R.string.reservation_qr_screen),
    RedeemHistory(title = R.string.redeem_history),
    AdminManageSouvenir(title = R.string.admin_manage_souvenir),
    CartScreen(title = R.string.cart_screen),
    CheckoutSouvenirScreen(title = R.string.checkout_souvenir_screen),
    MapScreen(title = R.string.map_screen),
    PaymentSuccessScreen(title = R.string.payment_success_screen),
    PurchaseSouvenirHistoryScreen(title = R.string.purchase_souvenir_history_screen),
    SouvenirScreen(title = R.string.souvenir_screen),
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(
    currentScreen: FunParkScreen,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    onMenuClick: () -> Unit,
    modifier: Modifier = Modifier,
    userViewModel: UserViewModel
) {

    val loggedInUser by userViewModel.loggedInUser.collectAsState()

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
            if (loggedInUser?.role != "Admin") {
                IconButton(onClick = { onMenuClick() }) {
                    Icon(
                        painter = painterResource(R.drawable.menu),
                        contentDescription = "Menu Bar",
                        modifier = Modifier.size(35.dp)
                    )
                }
            }
        }
    )
}

@Composable
fun FunParkAccessApp(
    souvenirViewModel: SouvenirViewModel,
    cartSouvenirViewModel: CartSouvenirViewModel,
    themeViewModel: ThemeViewModel,
    mapViewModel: MapViewModel,
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
    val facilityRepository = remember { FacilityRepository(appDatabase.facilityDao) }
    val reservationRepository = remember { ReservationRepository(appDatabase.reservationDao) }
    val redeemHistoryRepository = remember { RedeemHistoryRepository(appDatabase.redeemHistoryDao)}

    // Create ViewModelFactory instances
    val ticketViewModelFactory = remember { TicketViewModelFactory(ticketRepository) }
    val cartItemViewModelFactory = remember { CartItemViewModelFactory(cartRepository) }
    val purchaseHistoryViewModelFactory = remember { PurchaseHistoryViewModelFactory(purchaseHistoryRepository) }
    val paymentMethodViewModelFactory = remember {PaymentMethodViewModelFactory(paymentMethodRepository)}
    val facilityViewModelFactory = remember { FacilityViewModelFactory(facilityRepository) }
    val reservationViewModelFactory = remember { ReservationViewModelFactory(reservationRepository) }
    val redeemHistoryViewModelFactory = remember { RedeemHistoryViewModelFactory(redeemHistoryRepository)}

    // Get ViewModel instances using the factory
    val ticketViewModel: TicketViewModel = viewModel(factory = ticketViewModelFactory)
    val cartItemViewModel: CartItemViewModel = viewModel(factory = cartItemViewModelFactory)
    val purchaseHistoryViewModel: PurchaseHistoryViewModel = viewModel(factory = purchaseHistoryViewModelFactory)
    val paymentMethodViewModel: PaymentMethodViewModel = viewModel(factory = paymentMethodViewModelFactory)
    val sharedViewModel: SharedViewModel = viewModel()
    val facilityViewModel: FacilityViewModel = viewModel(factory = facilityViewModelFactory)
    val reservationViewModel: ReservationViewModel = viewModel(factory = reservationViewModelFactory)
    val redeemHistoryViewModel: RedeemHistoryViewModel = viewModel(factory = redeemHistoryViewModelFactory)

    //User
    val userRepository = remember { UserRepository(appDatabase.userDao) }
    val userViewModelFactory = remember { UserViewModelFactory(userRepository) }
    val userViewModel: UserViewModel = viewModel(factory = userViewModelFactory)


    val backStackEntry by navController.currentBackStackEntryAsState()

    val currentRoute = backStackEntry?.destination?.route ?: FunParkScreen.MainMenu.name
    val currentScreen = when {
        currentRoute.startsWith(FunParkScreen.TicketType.name) -> FunParkScreen.TicketType
        currentRoute == FunParkScreen.MainMenu.name -> FunParkScreen.MainMenu
        currentRoute == FunParkScreen.TicketMenu.name -> FunParkScreen.TicketMenu
        currentRoute == FunParkScreen.TicketHistory.name -> FunParkScreen.TicketHistory
        currentRoute == FunParkScreen.ShoppingCart.name -> FunParkScreen.ShoppingCart
        currentRoute == FunParkScreen.Checkout.name -> FunParkScreen.Checkout
        currentRoute == FunParkScreen.PaySuccess.name -> FunParkScreen.PaySuccess
        currentRoute == FunParkScreen.Receipt.name -> FunParkScreen.Receipt
        currentRoute == FunParkScreen.GetStarted.name -> FunParkScreen.GetStarted
        currentRoute == FunParkScreen.Login.name -> FunParkScreen.Login
        currentRoute == FunParkScreen.Register.name -> FunParkScreen.Register
        currentRoute == FunParkScreen.Menu.name -> FunParkScreen.Menu
        currentRoute == FunParkScreen.Account.name -> FunParkScreen.Account
        currentRoute == FunParkScreen.Redeem.name -> FunParkScreen.Redeem
        currentRoute == FunParkScreen.AdminDashboard.name -> FunParkScreen.AdminDashboard
        currentRoute == FunParkScreen.AdminManageUser.name -> FunParkScreen.AdminManageUser
        currentRoute == FunParkScreen.AdminManageRedeem.name -> FunParkScreen.AdminManageRedeem
        currentRoute == FunParkScreen.RVMainScreen.name -> FunParkScreen.RVMainScreen
        currentRoute == FunParkScreen.RVSelectionScreen.name -> FunParkScreen.RVSelectionScreen
        currentRoute == FunParkScreen.RVSummaryScreen.name -> FunParkScreen.RVSummaryScreen
        currentRoute == FunParkScreen.RVDoneScreen.name -> FunParkScreen.RVDoneScreen
        currentRoute == FunParkScreen.RVQRScreen.name -> FunParkScreen.RVQRScreen
        currentRoute == FunParkScreen.RVManagementMainScreen.name -> FunParkScreen.RVManagementMainScreen
        currentRoute == FunParkScreen.RVTicketConfirmationScreen.name -> FunParkScreen.RVTicketConfirmationScreen
        currentRoute == FunParkScreen.RedeemHistory.name -> FunParkScreen.RedeemHistory
        currentRoute == FunParkScreen.AdminManageSouvenir.name -> FunParkScreen.AdminManageSouvenir
        currentRoute == FunParkScreen.CartScreen.name -> FunParkScreen.CartScreen
        currentRoute == FunParkScreen.CheckoutSouvenirScreen.name -> FunParkScreen.CheckoutSouvenirScreen
        currentRoute == FunParkScreen.MapScreen.name -> FunParkScreen.MapScreen
        currentRoute == FunParkScreen.PaymentSuccessScreen.name -> FunParkScreen.PaymentSuccessScreen
        currentRoute == FunParkScreen.PurchaseSouvenirHistoryScreen.name -> FunParkScreen.PurchaseSouvenirHistoryScreen
        currentRoute == FunParkScreen.SouvenirScreen.name -> FunParkScreen.SouvenirScreen

        else -> FunParkScreen.MainMenu
    }

    Scaffold(
        topBar = {
            AppBar(
                currentScreen = currentScreen,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() },
                onMenuClick = {navController.navigate(FunParkScreen.Menu.name)},
                userViewModel = userViewModel
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = FunParkScreen.GetStarted.name,
            modifier = Modifier.padding(innerPadding)
        ){

            composable("adminSouvenirs") {
                AdminScreen(souvenirViewModel = souvenirViewModel, navController = navController)
            }
            composable("add_souvenir") {
                AddSouvenirScreen(souvenirViewModel = souvenirViewModel, navController = navController)
            }
            composable("delete_souvenir") {
                DeleteSouvenirScreen(souvenirViewModel = souvenirViewModel, navController = navController)
            }
            composable("view_souvenir") {
                ViewSouvenirsScreen(souvenirViewModel = souvenirViewModel)
            }
            composable("modify_souvenir") {
                ModifySouvenirScreen(souvenirViewModel = souvenirViewModel, navController = navController)
            }

            composable("map") {
                MapScreen(navController = navController, viewModel = mapViewModel)
            }

            composable("souvenir") {
                SouvenirScreen(
                    souvenirViewModel = souvenirViewModel,
                    onAddToCart = { souvenir, quantity -> cartSouvenirViewModel.addCartItem(souvenir, quantity) },
                    navigateToCart = { navController.navigate("cart") },
                    themeViewModel = themeViewModel
                )
            }
            composable("cart") {
                val cartItems by cartSouvenirViewModel.cartItems.collectAsState()
                val allSouvenirs by cartSouvenirViewModel.allSouvenirs.collectAsState()

                LaunchedEffect(Unit) {
                    if (allSouvenirs.isEmpty()) {
                        cartSouvenirViewModel.fetchSouvenirs()
                    }
                }

                CartScreen(
                    cartItems = cartItems,
                    onRemove = { cartItem -> cartSouvenirViewModel.removeCartItem(cartItem) },
                    onIncreaseQuantity = { cartItem -> cartSouvenirViewModel.increaseQuantity(cartItem) },
                    onDecreaseQuantity = { cartItem -> cartSouvenirViewModel.decreaseQuantity(cartItem) },
                    onSelectAll = { selectAll -> cartSouvenirViewModel.selectAll(selectAll) },
                    onCheckout = {
                        navController.navigate("checkout")
                    },
                    allSouvenirs = allSouvenirs,
                    themeViewModel = themeViewModel)
            }

            composable("checkout") {
                // Collect state from the ViewModel
                val selectedItems by cartSouvenirViewModel.cartItems.collectAsState()
                val allSouvenirs by cartSouvenirViewModel.allSouvenirs.collectAsState()
                val filteredSelectedItems = selectedItems.filter { it.selected }

                // Create an instance of the CheckoutViewModel (if not already available)
                val checkoutViewModel: CheckoutViewModel = viewModel() // Use the appropriate method to get your ViewModel

                // CheckoutScreen with updated parameters
                CheckoutScreen(
                    selectedItems = filteredSelectedItems,
                    allSouvenirs = allSouvenirs,
                    onConfirmPayment = { paymentMethod ->
                        // TODO: Implement payment confirmation logic
                        checkoutViewModel.savePaymentMethod(paymentMethod) // Pass the payment method to your ViewModel
                    },
                    onViewHistory = {
                        // Navigate to purchase history screen
                        navController.navigate("purchase_history")
                    },
                    onBackToHome = {
                        // Navigate back to the map screen
                        navController.navigate("map")
                    },
                    themeViewModel = themeViewModel,
                    checkoutViewModel = checkoutViewModel
                )
            }

            composable("confirmation") {
                PaymentSuccessScreen(
                    onViewHistory = { navController.navigate("purchase_history") },
                    onBackToHome = { navController.popBackStack("map", false) } // Navigate back to the map
                )
            }

            composable("purchase_history") {
                PurchaseSouvenirHistoryScreen(

                )
            }

            composable(route = FunParkScreen.RVViewScreen.name){
                ReservationViewScreen(
                    reservationViewModel = reservationViewModel,
                    facilityViewModel = facilityViewModel,
                    navController = navController
                )
            }

            composable(
                route = "reservation_qr_screen/{viewcancel}/{reservationID}", // Include arguments in the route
                arguments =listOf(
                    navArgument("viewcancel") { type = NavType.StringType },
                    navArgument("reservationID") { type = NavType.StringType },
                )
            ) { backStackEntry ->
                val viewcancel = backStackEntry.arguments?.getString("viewcancel") ?: ""
                val reservationID = backStackEntry.arguments?.getString("reservationID") ?: ""

                ReservationQRScreen(
                    viewCancel = viewcancel,
                    reservationID = reservationID,
                    reservationViewModel = reservationViewModel,
                    navController = navController
                )
            }

            composable(
                route = "reservation_done_screen/{reservationID}", // Include reservationID in the route
                arguments = listOf(navArgument("reservationID") { type = NavType.StringType })
            ) { backStackEntry ->
                val reservationID = backStackEntry.arguments?.getString("reservationID") ?: ""

                ReservationDoneScreen(
                    reservationID = reservationID,
                    navController = navController
                )
            }

            composable(
                route = "reservation_summary_screen/{facilityImage}/{facilityName}/{reservationTime}/{reservationPax}",
                arguments = listOf(
                    navArgument("facilityImage") { type = NavType.IntType },
                    navArgument("facilityName") { type = NavType.StringType },
                    navArgument("reservationTime") { type = NavType.StringType },
                    navArgument("reservationPax") { type = NavType.StringType }
                )
            ){ backStackEntry ->
                val facilityImage = backStackEntry.arguments?.getInt("facilityImage") ?: 0
                val facilityName = backStackEntry.arguments?.getString("facilityName") ?: ""
                val reservationTime = backStackEntry.arguments?.getString("reservationTime") ?: ""
                val reservationPax = backStackEntry.arguments?.getString("reservationPax") ?: ""


                ReservationSummaryScreen(
                    facilityImage = facilityImage,
                    facilityName = facilityName,
                    reservationTime = reservationTime,
                    reservationPax = reservationPax,
                    reservationViewModel = reservationViewModel,
                    navController = navController,
                    facilityViewModel = facilityViewModel,
                    goToCancel = { navController.popBackStack() }
                )
            }

            composable (route = FunParkScreen.AdminManageRedeem.name) {
                ManageRedemptionsScreen(redeemHistoryViewModel)
            }

            composable(route = FunParkScreen.RedeemHistory.name) {
                RedemptionHistoryScreen(
                    userViewModel = userViewModel,
                    redeemHistoryViewModel = redeemHistoryViewModel
                )
            }

            composable(route = FunParkScreen.RVTicketConfirmationScreen.name) {
                ReservationTicketConfirmationScreen(
                    purchaseHistoryViewModel = purchaseHistoryViewModel,
                    navController = navController
                )
            }

            composable(route = FunParkScreen.RVManagementMainScreen.name) {
                ReservationManagementScreen(facilityViewModel)
            }

            composable (
                route = "${FunParkScreen.RVMainScreen.name}/{viewOnly}", // Include viewOnly in the route
                arguments = listOf(
                    navArgument("viewOnly") { type = NavType.StringType },
                )
            ) {backStackEntry ->
                val viewOnly = backStackEntry.arguments?.getString("viewOnly") ?: ""

                ReservationMainScreen(
                    viewOnly = viewOnly,
                    facilityViewModel = facilityViewModel,
                    navController = navController
                )
            }

            composable(
                route = "reservation_selection_screen/{viewOnly}/{facilityName}", // Include arguments in the route
                arguments = listOf(
                    navArgument("viewOnly") { type = NavType.StringType },
                    navArgument("facilityName") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val viewOnly = backStackEntry.arguments?.getString("viewOnly") ?: ""
                val facilityName = backStackEntry.arguments?.getString("facilityName") ?: ""

                ReservationSelectionScreen(
                    viewOnly = viewOnly,
                    facilityName = facilityName,
                    facilityViewModel = facilityViewModel,
                    purchaseHistoryViewModel = purchaseHistoryViewModel,
                    navController = navController
                )
            }

            composable (route = FunParkScreen.AdminManageUser.name) {
                ManageUsersScreen(userViewModel, navController)
            }

            composable(route = FunParkScreen.AdminDashboard.name) {
                AdminDashboardScreen(navController, userViewModel)
            }

            composable(route = FunParkScreen.Redeem.name) {RedeemScreen(
                ticketViewModel = ticketViewModel,
                navController = navController,
                userViewModel = userViewModel,
                redeemHistoryViewModel = redeemHistoryViewModel
            )
            }

            composable(route = FunParkScreen.Account.name) {
                val user = userViewModel.userState.collectAsState().value

                AccountScreen(
                    user = user ?: UserType("", "", "", points = 0), // Provide a default or handle null case
                    viewModel = userViewModel,
                    onUsernameChange = { newUsername ->
                        user?.let {
                            userViewModel.changeUsername(it.username, newUsername) { errorMessage ->
                                // Handle error (e.g., show a Toast)
                            }
                        }
                    },
                    onPasswordChange = { newPassword ->
                        user?.let {
                            userViewModel.changePassword(it.username, newPassword) { errorMessage ->
                                // Handle error (e.g., show a Toast)
                            }
                        }
                    },
                    onSignOut = {
                        userViewModel.signOut() // Sign out logic
                    }
                )
            }

            composable(route = FunParkScreen.GetStarted.name) {
                GettingStarted(navController)
            }

            composable(route = FunParkScreen.Login.name) {
                LoginScreen(navController, userViewModel)
            }

            composable(route = FunParkScreen.Register.name) {
                RegisterScreen(navController, userViewModel)
            }

            composable(route = FunParkScreen.Menu.name) {
                MenuScreen(
                    onMenuItemClick = { item ->
                        navController.navigate(item) // Navigate to the clicked item's route
                    },
                    onSignOutClick = {
                        navController.navigate(FunParkScreen.GetStarted.name) // Handle sign out and navigate to GetStarted
                    },
                    navController = navController
                )
            }

            //MainMenu
            composable(route = FunParkScreen.MainMenu.name){
                MainMenuScreen(
                    onTicketClick = {navController.navigate(FunParkScreen.TicketMenu.name)},
                    onReserveClick = {navController.navigate(FunParkScreen.RVTicketConfirmationScreen.name)}
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
                    sharedViewModel= sharedViewModel,
                    userViewModel = userViewModel
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
