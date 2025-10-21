package dev.klarkengkoy.triptrack

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.klarkengkoy.triptrack.ui.theme.TripTrackTheme

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            var useDarkTheme by remember { mutableStateOf(false) }
            TripTrackTheme(useDarkTheme = useDarkTheme) {
                val navController = rememberNavController()
                val items = listOf(
                    "Home" to R.drawable.ic_home_black_24dp,
                    "Dashboard" to R.drawable.ic_dashboard_black_24dp,
                    "Notifications" to R.drawable.ic_notifications_black_24dp,
                    "Settings" to R.drawable.ic_settings_black_24dp
                )

                Scaffold(
                    bottomBar = {
                        NavigationBar {
                            val navBackStackEntry by navController.currentBackStackEntryAsState()
                            val currentDestination = navBackStackEntry?.destination
                            items.forEach { (screen, icon) ->
                                NavigationBarItem(
                                    icon = { Icon(painterResource(id = icon), contentDescription = null) },
                                    label = { Text(screen) },
                                    selected = currentDestination?.route == screen.lowercase(),
                                    onClick = { navController.navigate(screen.lowercase()) }
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    MobileNavigation(
                        navController = navController,
                        onToggleTheme = { useDarkTheme = !useDarkTheme },
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
