package com.yogo.transportapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
data class TransportRoute(
    val id: Int,
    val lineName: String, // e.g., "Line 14"
    val destination: String, // e.g., "Central Station"
    val arrivalTime: String, // e.g., "5 mins"
    val type: String, // e.g., "Bus" or "Train"
    var isFavourite: Boolean = false
)
data class ScheduleItem(
    val time: String,
    val status: String, // e.g., "On Time", "Delayed"
    val isDelayed: Boolean = false
)
fun RouteItem(route: TransportRoute, onFavoriteClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1.0f)) {
                Text(text = route.lineName, style = MaterialTheme.typography.titleLarge)
                Text(text = "To: ${route.destination}")
            }

            // The Favorite Star Button
            IconButton(onClick = onFavoriteClick) {
                Icon(
                    imageVector = if (route.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = if (route.isFavorite) Color.Red else Color.Gray
                )
            }
        }
    }
}


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                AppNavigation() // This is the starting point
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    // NavHost manages the screens
    NavHost(navController = navController, startDestination = "login") {
        // Route 1: Login
        composable("login") {
            LoginScreen(onLoginSuccess = {
                navController.navigate("route_search")
            })
        }
        // Route 2: Route Search
        composable("route_search") {
            RouteSearchScreen()
        }
    }
}

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Public Transport Login", fontSize = 24.sp)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") })
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation()
        )

        Button(onClick = {
            if (email.isNotBlank() && password.length >= 4) {
                onLoginSuccess() // Triggers the navigation to Route Search
            } else {
                Toast.makeText(context, "Enter credentials", Toast.LENGTH_SHORT).show()
            }
        }) {
            Text("Login")
        }
    }
}

@Composable
fun RouteSearchScreen() {
    var searchQuery by remember { mutableStateOf("") }

    // Some dummy data for Yogo's app
    val allRoutes = listOf(
        TransportRoute(1, "Bus 102", "Downtown Terminal", "3 mins", "Bus"),
        TransportRoute(2, "Express 5", "Airport", "12 mins", "Train"),
        TransportRoute(3, "Line 14", "University Square", "8 mins", "Tram"),
        TransportRoute(4, "Bus 201", "West Shopping Mall", "15 mins", "Bus"),
        TransportRoute(5, "Metro Blue", "East Harbor", "2 mins", "Subway")
    )

    // Filter the list based on what the user types
    val filteredRoutes = allRoutes.filter {
        it.destination.contains(searchQuery, ignoreCase = true) ||
                it.lineName.contains(searchQuery, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Where are you going?",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp, top = 24.dp)
        )

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Search Destination") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // The Scrolling List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(filteredRoutes) { route ->
                RouteItem(route = route)
            }
        }
    }
}
@Composable
fun FavoritesScreen(favoriteRoutes: List<TransportRoute>) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "My Favorite Routes",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        if (favoriteRoutes.isEmpty()) {
            Text("You haven't added any favorites yet!", modifier = Modifier.padding(top = 20.dp))
        } else {
            LazyColumn {
                items(favoriteRoutes) { route ->
                    // Reuse the RouteItem but without the click logic here for simplicity
                    RouteItem(route = route, onFavoriteClick = {})
                }
            }
        }
    }
}
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onLogout: () -> Unit) {
    var isDarkMode by remember { mutableStateOf(false) }
    var notificationsEnabled by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Settings") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            // Section 1: Appearance
            Text("Appearance", modifier = Modifier.padding(16.dp), color = Color.Gray)
            ListItem(
                headlineContent = { Text("Dark Mode") },
                leadingContent = { Icon(Icons.Default.Settings, contentDescription = null) },
                trailingContent = {
                    Switch(checked = isDarkMode, onCheckedChange = { isDarkMode = it })
                }
            )

            HorizontalDivider()

            // Section 2: Alerts
            Text("Alerts", modifier = Modifier.padding(16.dp), color = Color.Gray)
            ListItem(
                headlineContent = { Text("Bus Delay Notifications") },
                leadingContent = { Icon(Icons.Default.Notifications, contentDescription = null) },
                trailingContent = {
                    Switch(checked = notificationsEnabled, onCheckedChange = { notificationsEnabled = it })
                }
            )

            HorizontalDivider()

            // Section 3: Account
            Text("Account", modifier = Modifier.padding(16.dp), color = Color.Gray)
            ListItem(
                headlineContent = { Text("Logout", color = Color.Red) },
                leadingContent = { Icon(Icons.Default.ExitToApp, contentDescription = null, tint = Color.Red) },
                modifier = Modifier.clickable { onLogout() }
            )
        }
    }
}
@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    // This is the "Source of Truth" for all routes in the app
    val routesState = remember {
        mutableStateListOf(
            TransportRoute(1, "Bus 102", "Downtown Terminal", "3 mins", "Bus"),
            TransportRoute(2, "Express 5", "Airport", "12 mins", "Train")
        )
    }

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(onLoginSuccess = { navController.navigate("route_search") })
        }
        composable("route_search") {
            RouteSearchScreen(
                routes = routesState,
                onToggleFavorite = { route ->
                    val index = routesState.indexOf(route)
                    routesState[index] = route.copy(isFavorite = !route.isFavorite)
                },
                onGoToFavorites = { navController.navigate("favorites") }
            )
        }
        composable("favorites") {
            FavoritesScreen(favoriteRoutes = routesState.filter { it.isFavorite })
            composable("settings") {
                SettingsScreen(onLogout = {
                    // Pop the backstack so the user can't "Go Back" into the app after logging out
                    navController.navigate("login") {
                        popUpTo("login") { inclusive = true }
                        composable("route_search") {
                            RouteSearchScreen(
                                // When a user clicks a specific route card, navigate to its schedule
                                onRouteClick = { route ->
                                    navController.navigate("schedule/${route.lineName}")
                                }
                            )
                        }

                        // New Route: The Schedule Screen expects a routeName argument
                        composable("schedule/{routeName}") { backStackEntry ->
                            val routeName = backStackEntry.arguments?.getString("routeName") ?: "Unknown Route"

                            ScheduleViewScreen(
                                routeName = routeName,
                                onBackClick = { navController.popBackStack() } // Goes back to the search list
                            )
                        }
                    }
                })
            }
        }
    }
}
NavHost(navController = navController, startDestination = "login") {
    // ... login, route_search, schedule, favorites, settings ...

    // New Route: Map View
    composable("map_view") {
        MapViewScreen()
    }
}
@Composable
fun BottomNavigationBar(navController: NavController) {
    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Search, contentDescription = null) },
            label = { Text("Search") },
            selected = false, // You can add logic to track which one is selected
            onClick = { navController.navigate("route_search") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Favorite, contentDescription = null) },
            label = { Text("Favorites") },
            selected = false,
            onClick = { navController.navigate("favorites") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Settings, contentDescription = null) },
            label = { Text("Settings") },
            selected = false,
            onClick = { navController.navigate("settings") }
        )
    }
}

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleViewScreen(routeName: String, onBackClick: () -> Unit) {
    // Mock schedule data for this specific route
    val upcomingDepartures = listOf(
        ScheduleItem("08:00 AM", "Departed"),
        ScheduleItem("08:15 AM", "On Time"),
        ScheduleItem("08:30 AM", "Delayed 5 mins", isDelayed = true),
        ScheduleItem("08:45 AM", "On Time"),
        ScheduleItem("09:00 AM", "Scheduled")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "$routeName Schedule") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp)
        ) {
            items(upcomingDepartures) { departure ->
                ScheduleRow(item = departure)
            }
        }
    }
}

@Composable
fun ScheduleRow(item: ScheduleItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (item.isDelayed) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = item.time,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (item.isDelayed) {
                    Icon(
                        Icons.Default.Warning,
                        contentDescription = "Delayed",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(16.dp).padding(end = 4.dp)
                    )
                }
                Text(
                    text = item.status,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (item.isDelayed) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun MapViewScreen() {
    // 1. Define coordinates for the map focus (Yaoundé)
    val cityCenter = LatLng(3.8480, 11.5021)
    val busStopNorth = LatLng(3.8600, 11.5100)
    val trainStation = LatLng(3.8350, 11.4900)

    // 2. Set the initial camera position and zoom level
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(cityCenter, 13f)
    }

    // 3. Render the Google Map
    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState
    ) {
        // Add markers for transport hubs
        Marker(
            state = MarkerState(position = cityCenter),
            title = "Central Bus Terminal",
            snippet = "Lines: 102, Express 5"
        )

        Marker(
            state = MarkerState(position = busStopNorth),
            title = "North Transit Station",
            snippet = "Lines: Bus 201"
        )

        Marker(
            state = MarkerState(position = trainStation),
            title = "Yaoundé Train Station",
            snippet = "Intercity Rail Network"
        )
    }
}
