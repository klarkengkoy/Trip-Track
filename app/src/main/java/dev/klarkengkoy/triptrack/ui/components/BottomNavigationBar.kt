package dev.klarkengkoy.triptrack.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation3.runtime.NavKey
import dev.klarkengkoy.triptrack.ui.navigation.Navigator

data class BottomNavItem(val route: NavKey, val icon: Int, val label: String, val isEnabled: Boolean = true)

@Composable
fun BottomNavigationBar(
    navigator: Navigator,
    currentRoute: NavKey?,
    items: List<BottomNavItem>,
    modifier: Modifier = Modifier
) {
    NavigationBar(modifier = modifier) {
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(painterResource(id = item.icon), contentDescription = item.label) },
                label = { Text(item.label) },
                selected = item.route == currentRoute,
                enabled = item.isEnabled,
                onClick = {
                    if (item.route != currentRoute) {
                        navigator.navigate(item.route)
                    }
                }
            )
        }
    }
}
