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
    import com.example.funparkapp.data.UserViewModel
    import com.example.funparkapp.data.UserDao
    import com.example.funparkapp.data.UserRepository
    import com.example.funparkapp.data.UserType
    import com.example.funparkapp.data.UserViewModelFactory
    import com.google.firebase.database.DatabaseReference
    import com.google.firebase.database.FirebaseDatabase


    enum class FunParkScreen1(@StringRes val title: Int){
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
        Redeem(title = R.string.redeem)
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun AppBar1(
        currentScreen1: FunParkScreen1,
        canNavigateBack: Boolean,
        navigateUp: () -> Unit,
        onMenuClick: () -> Unit,
        modifier: Modifier = Modifier
    ) {
        CenterAlignedTopAppBar(
            title = { Text(stringResource(currentScreen1.title), fontSize = 30.sp, fontWeight = FontWeight.Bold) },
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
    fun FunParkAccessAppWithUserModule(
        navController: NavHostController = rememberNavController(),
        context: Context = LocalContext.current
    ) {

        // Initialize coroutine scope
        val coroutineScope = rememberCoroutineScope()

        val firebaseDatabase: DatabaseReference = FirebaseDatabase.getInstance().getReference("tickets")
        val firebaseDatabase2: DatabaseReference = FirebaseDatabase.getInstance().getReference("ticketPurchased")

        Log.i("FunParkAccessAppWithUserModule","Started")
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

        //User
        val userRepository = remember { UserRepository(appDatabase.userDao) }
        val userViewModelFactory = remember { UserViewModelFactory(userRepository) }
        val userViewModel: UserViewModel = viewModel(factory = userViewModelFactory)


        val backStackEntry by navController.currentBackStackEntryAsState()

        val currentRoute = backStackEntry?.destination?.route ?: FunParkScreen1.MainMenu.name
        val currentScreen1 = when {
            currentRoute.startsWith(FunParkScreen1.TicketType.name) -> FunParkScreen1.TicketType
            currentRoute == FunParkScreen1.MainMenu.name -> FunParkScreen1.MainMenu
            currentRoute == FunParkScreen1.TicketMenu.name -> FunParkScreen1.TicketMenu
            currentRoute == FunParkScreen1.ShoppingCart.name -> FunParkScreen1.ShoppingCart
            currentRoute == FunParkScreen1.Checkout.name -> FunParkScreen1.Checkout
            currentRoute == FunParkScreen1.PaySuccess.name -> FunParkScreen1.PaySuccess
            currentRoute == FunParkScreen1.Receipt.name -> FunParkScreen1.Receipt
            currentRoute == FunParkScreen1.GetStarted.name -> FunParkScreen1.GetStarted
            currentRoute == FunParkScreen1.Login.name -> FunParkScreen1.Login
            currentRoute == FunParkScreen1.Register.name -> FunParkScreen1.Register
            currentRoute == FunParkScreen1.Menu.name -> FunParkScreen1.Menu
            currentRoute == FunParkScreen1.Account.name -> FunParkScreen1.Account
            currentRoute == FunParkScreen1.Redeem.name -> FunParkScreen1.Redeem
            else -> FunParkScreen1.MainMenu
        }

        Scaffold(
            topBar = {
                AppBar1(
                    currentScreen1 = currentScreen1,
                    canNavigateBack = navController.previousBackStackEntry != null,
                    navigateUp = { navController.navigateUp() },
                    onMenuClick = { navController.navigate(FunParkScreen1.Menu.name) }
                )
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = FunParkScreen1.GetStarted.name,
                modifier = Modifier.padding(innerPadding)
            ){

                composable(route = FunParkScreen1.Redeem.name) {
                    // Here, you'll pass the required parameters
                    RedeemScreen(
                        ticketViewModel = ticketViewModel,
                        onClaimTicket = { ticket ->
                            // Handle the ticket claiming logic here
                            // For example, you can navigate to a confirmation screen
                            // navController.navigate(FunParkScreen1.Confirmation.name)
                        }
                    )

                }

                composable(route = FunParkScreen1.Account.name) {
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

                composable(route = FunParkScreen1.GetStarted.name) {
                    GettingStarted(navController)
                }

                composable(route = FunParkScreen1.Login.name) {
                    LoginScreen(navController, userViewModel)
                }

                composable(route = FunParkScreen1.Register.name) {
                    RegisterScreen(navController, userViewModel)
                }

                composable(route = FunParkScreen1.Menu.name) {
                    MenuScreen(
                        onMenuItemClick = { item ->
                            navController.navigate(item) // Navigate to the clicked item's route
                        },
                        onSignOutClick = {
                            navController.navigate(FunParkScreen1.GetStarted.name) // Handle sign out and navigate to GetStarted
                        },
                        navController = navController
                    )
                }

                composable(route = FunParkScreen1.MainMenu.name) {
                    MainMenuScreen(
                        onTicketClick = { navController.navigate(FunParkScreen1.TicketMenu.name) },
                        onReserveClick = { /* Handle reservation click */ }
                    )
                }


                //Admin Ticket
                composable(route = FunParkScreen1.AdminTicket.name){
                    AdminTicketScreen(ticketViewModel = ticketViewModel)
                }

                //Ticket Menu
                composable(route = FunParkScreen1.TicketMenu.name) {
                    SelectTicketScreen(
                        ticketViewModel = ticketViewModel,
                        goToShoppingCart = { navController.navigate(FunParkScreen1.ShoppingCart.name) },
                        goToSpecificTicketPlan = { ticketPlan ->
                            navController.navigate("${FunParkScreen1.TicketType.name}/$ticketPlan")
                        }
                    )
                }

                //Ticket Type
                composable(route = "${FunParkScreen1.TicketType.name}/{ticketPlan}") { backStackEntry ->
                    val ticketPlan = backStackEntry.arguments?.getString("ticketPlan") ?: ""
                    // Pass the ticketPlan to SelectTicketType
                    SelectTicketType(
                        ticketPlan = ticketPlan,
                        ticketViewModel = ticketViewModel,
                        cartItemViewModel = cartItemViewModel,
                        goToShoppingCart = {navController.navigate(FunParkScreen1.ShoppingCart.name)}
                    )
                }

                // Shopping Cart
                composable(route = FunParkScreen1.ShoppingCart.name) {
                    ShoppingCartScreen(
                        cartItemViewModel = cartItemViewModel,
                        checkout = { navController.navigate(FunParkScreen1.Checkout.name) },
                        continueShopping = { navController.navigate(FunParkScreen1.TicketMenu.name) }
                    )
                }

                // Checkout
                composable(route = FunParkScreen1.Checkout.name) {
                    CheckoutScreen(
                        paySuccess = {navController.navigate(FunParkScreen1.PaySuccess.name) },
                        cartItemViewModel = cartItemViewModel,
                        purchaseHistoryViewModel = purchaseHistoryViewModel,
                        paymentMethodViewModel = paymentMethodViewModel,
                        sharedViewModel= sharedViewModel
                    )
                }

                //Pay Success
                composable(route = FunParkScreen1.PaySuccess.name) {
                    PaySuccessScreen(
                        viewReceipt = { navController.navigate(FunParkScreen1.Receipt.name) }
                    )
                }

                //View Receipt
                composable(route = FunParkScreen1.Receipt.name) {
                    TicketReceiptScreen(
                        purchaseHistoryViewModel = purchaseHistoryViewModel,
                        sharedViewModel = sharedViewModel,
                        homePageClick = { navController.navigate(FunParkScreen1.MainMenu.name) }
                    )
                }

                //Ticket Purchased History
                composable(route = FunParkScreen1.TicketHistory.name){
                    TicketPurchasedHistoryScreen(
                        purchaseHistoryViewModel = purchaseHistoryViewModel,
                        sharedViewModel = sharedViewModel,
                        viewTicket = { navController.navigate(FunParkScreen1.Receipt.name) }
                    )
                }

            }
        }
    }
