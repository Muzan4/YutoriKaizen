package com.example.yutorikaizen.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.yutorikaizen.presentation.screens.duckbreak.DuckBreakScreen
import com.example.yutorikaizen.presentation.screens.focus.FocusTimerScreen
import com.example.yutorikaizen.presentation.screens.home.HomeScreen
import com.example.yutorikaizen.presentation.screens.log.TriggerLogScreen
import com.example.yutorikaizen.presentation.screens.tasks.TaskScreen
import com.example.yutorikaizen.presentation.theme.CalmTeal
import com.example.yutorikaizen.presentation.theme.MoonGray
import com.example.yutorikaizen.presentation.theme.NightSurface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Favorite

/**
 * App-level navigation graph.
 *
 * WHY NavHost over manual back-stack management?
 * NavHost is declarative — you describe destinations, not transitions.
 * The back stack, deep links, and state restoration are handled by the
 * Navigation component. Each screen stays stateless (just a route string).
 *
 * WHY bottom nav lives here and not in each screen?
 * The bottom nav is shared across all top-level screens. Putting it in
 * AppNavGraph (inside a single Scaffold) means it's drawn once and never
 * re-animated during navigation. If each screen owned its own nav bar,
 * you'd see it flash/flicker on every tab switch.
 */
@Composable
fun AppNavGraph() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            YutoriBottomNav(
                currentRoute = navController
                    .currentBackStackEntryAsState().value
                    ?.destination,
                onNavigate = { route ->
                    navController.navigate(route) {
                        // Pop up to the start destination to avoid building
                        // a huge back-stack when tapping tabs repeatedly
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState    = true
                    }
                }
            )
        }
    ) { innerPadding ->
        NavHost(
            navController    = navController,
            startDestination = NavRoute.Home.route,
            modifier         = Modifier.padding(innerPadding)
        ) {
            composable(NavRoute.Home.route)      { HomeScreen() }
            composable(NavRoute.Focus.route)     { FocusTimerScreen() }
            composable(NavRoute.Log.route)       { TriggerLogScreen() }
            composable(NavRoute.DuckBreak.route) { DuckBreakScreen() }
            composable(NavRoute.Tasks.route)     { TaskScreen() }
        }
    }
}

// ---------------------------------------------------------------------------
// Route definitions — sealed class keeps all route strings in one place.
// If a route string changes, you only update it here.
// ---------------------------------------------------------------------------
sealed class NavRoute(val route: String, val label: String, val icon: ImageVector) {
    object Home      : NavRoute("home",      "Home",  Icons.Rounded.Home)
    object Focus     : NavRoute("focus",     "Focus", Icons.Rounded.PlayArrow)
    object Log       : NavRoute("log",       "Log",   Icons.Rounded.Edit)
    object DuckBreak : NavRoute("duckbreak", "Break", Icons.Rounded.Favorite)
    object Tasks     : NavRoute("tasks",     "Tasks", Icons.Rounded.CheckCircle)
}

private val bottomNavItems = listOf(
    NavRoute.Home,
    NavRoute.Focus,
    NavRoute.Log,
    NavRoute.DuckBreak,
    NavRoute.Tasks
)

@Composable
private fun YutoriBottomNav(
    currentRoute: androidx.navigation.NavDestination?,
    onNavigate: (String) -> Unit
) {
    NavigationBar(
        containerColor = NightSurface
    ) {
        bottomNavItems.forEach { screen ->
            val selected = currentRoute?.hierarchy?.any { it.route == screen.route } == true
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector        = screen.icon,
                        contentDescription = screen.label
                    )
                },
                label   = { Text(screen.label) },
                selected = selected,
                onClick  = { onNavigate(screen.route) },
                colors  = NavigationBarItemDefaults.colors(
                    selectedIconColor   = CalmTeal,
                    selectedTextColor   = CalmTeal,
                    unselectedIconColor = MoonGray,
                    unselectedTextColor = MoonGray,
                    indicatorColor      = NightSurface
                )
            )
        }
    }
}
