package dev.klarkengkoy.triptrack

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import dev.klarkengkoy.triptrack.ui.login.LoginViewModel
import dev.klarkengkoy.triptrack.ui.login.SignInEvent
import dev.klarkengkoy.triptrack.ui.theme.TripTrackTheme
import kotlinx.coroutines.launch

data class BottomNavItem(val route: String, val icon: Int, val label: String)

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val loginViewModel: LoginViewModel by viewModels()

    private val signInLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        FirebaseAuthUIActivityResultContract(),
    ) { res ->
        Log.d(TAG, "Sign-in result received")
        loginViewModel.onSignInResult(res)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(
                Color.TRANSPARENT,
                Color.TRANSPARENT,
            )
        )
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            loginViewModel.signInEvent.collect { event ->
                when (event) {
                    is SignInEvent.Launch -> launchSignIn(event.providers)
                    SignInEvent.Success -> {
                        val user = Firebase.auth.currentUser
                        if (user != null) {
                            val sharedPref = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                            with(sharedPref.edit()) {
                                putString("user_name", user.displayName)
                                putString("user_email", user.email)
                                apply()
                            }
                            Toast.makeText(this@MainActivity, "Welcome, ${user.displayName}", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this@MainActivity, R.string.sign_in_successful, Toast.LENGTH_SHORT).show()
                        }
                    }
                    SignInEvent.Error -> {
                        Toast.makeText(this@MainActivity, R.string.sign_in_failed, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        val bottomNavItems = listOf(
            BottomNavItem("trips", R.drawable.travel_luggage_and_bags_24px, "Trips"),
            BottomNavItem("dashboard", R.drawable.dashboard_24px, "Dashboard"),
            BottomNavItem("media", R.drawable.photo_album_24px, "Media"),
            BottomNavItem("maps", R.drawable.map_24px, "Maps"),
            BottomNavItem("settings", R.drawable.settings_24px, "Settings")
        )

        setContent {
            val isSignedIn by loginViewModel.isUserSignedIn.collectAsState()

            if (isSignedIn) {
                TripTrackTheme {
                    val navController = rememberNavController()

                    Scaffold(
                        bottomBar = {
                            BottomNavigationBar(navController, bottomNavItems)
                        }
                    ) { innerPadding ->
                        MobileNavigation(
                            navController = navController,
                            onToggleTheme = { /* TODO */ },
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            } else {
                LoginNavigation(viewModel = loginViewModel)
            }
        }
    }

    private fun launchSignIn(providers: List<AuthUI.IdpConfig>) {
        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .build()
        signInLauncher.launch(signInIntent)
    }

    @Composable
    private fun BottomNavigationBar(
        navController: NavController,
        items: List<BottomNavItem>
    ) {
        NavigationBar {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination
            items.forEach { item ->
                NavigationBarItem(
                    icon = { Icon(painterResource(id = item.icon), contentDescription = item.label) },
                    label = { Text(item.label) },
                    selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                    onClick = {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}
