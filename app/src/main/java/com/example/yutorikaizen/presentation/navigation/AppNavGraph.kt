package com.example.yutorikaizen.presentation.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.example.yutorikaizen.MainActivity
import com.example.yutorikaizen.presentation.components.AppTourOverlay
import com.example.yutorikaizen.presentation.components.APP_TOUR_STEPS
import com.example.yutorikaizen.presentation.screens.account.AccountScreen
import com.example.yutorikaizen.presentation.screens.analytics.AnalyticsDashboardScreen
import com.example.yutorikaizen.presentation.screens.dailygoal.DailyGoalScreen
import com.example.yutorikaizen.presentation.screens.duckbreak.FrogBreakScreen
import com.example.yutorikaizen.presentation.screens.expenses.ExpenseTrackerScreen
import com.example.yutorikaizen.presentation.screens.focus.FocusTimerScreen
import com.example.yutorikaizen.presentation.screens.habits.HabitTrackerScreen
import com.example.yutorikaizen.presentation.screens.home.HomeScreen
import com.example.yutorikaizen.presentation.screens.home.HomeViewModel
import com.example.yutorikaizen.presentation.screens.intercept.InterceptSettingsScreen
import com.example.yutorikaizen.presentation.screens.journey.JourneyScreen
import com.example.yutorikaizen.presentation.screens.onboarding.OnboardingScreen
import com.example.yutorikaizen.presentation.screens.surge.SurgeBridgeScreen
import com.example.yutorikaizen.presentation.screens.wardrobe.WardrobeScreen
import com.example.yutorikaizen.presentation.theme.*

/**
 * App navigation graph.
 * The onboarding gate reads UserProfile from HomeViewModel —
 * if onboardingComplete is false, we redirect to the onboarding flow.
 */
@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    val homeViewModel: HomeViewModel = hiltViewModel()
    val homeUiState by homeViewModel.uiState.collectAsStateWithLifecycle()

    // Routes that do NOT show the bottom nav
    val fullScreenRoutes = setOf(
        NavRoute.Onboarding.route,
        NavRoute.Surge.route,
        NavRoute.FrogBreak.route,
        NavRoute.InterceptSettings.route,
        NavRoute.Expenses.route,
        NavRoute.Wardrobe.route,
        NavRoute.Analytics.route
    )
    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStack?.destination?.route
    val showBottomBar = currentRoute !in fullScreenRoutes

    var tourStep by remember { mutableIntStateOf(0) }
    val isTourActive = homeUiState.onboardingComplete && !homeUiState.hasSeenRibiIntro

    // Deep Link Navigation (e.g. from Cat Quick Open or 7am/9pm notification)
    val pendingNav by MainActivity.pendingNavigation.collectAsStateWithLifecycle()
    LaunchedEffect(pendingNav) {
        val dest = pendingNav ?: return@LaunchedEffect
        val bottomTabs = setOf(
            NavRoute.Home.route,
            NavRoute.Habits.route,
            NavRoute.DailyGoal.route,
            NavRoute.Journey.route,
            NavRoute.Account.route
        )
        try {
            if (dest in bottomTabs) {
                navController.navigate(dest) {
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            } else {
                navController.navigate(dest) {
                    launchSingleTop = true
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            MainActivity.pendingNavigation.value = null
        }
    }

    // Onboarding gate: after loading, redirect if not complete
    LaunchedEffect(homeUiState.isLoading, homeUiState.onboardingComplete) {
        if (!homeUiState.isLoading && !homeUiState.onboardingComplete) {
            navController.navigate(NavRoute.Onboarding.route) {
                popUpTo(navController.graph.id) { inclusive = true }
            }
        }
    }

    // Interactive App Tour: automatically open each page as the tutorial progresses!
    LaunchedEffect(isTourActive, tourStep) {
        if (isTourActive) {
            val step = APP_TOUR_STEPS.getOrElse(tourStep) { APP_TOUR_STEPS.first() }
            if (currentRoute != step.targetRoute) {
                navController.navigate(step.targetRoute) {
                    launchSingleTop = true
                }
            }
        }
    }

    // ── System Back Button Handling ──────────────────────────────────────────
    // 1. During Tour: pressing phone back button moves to previous tour step & opens its page!
    BackHandler(enabled = isTourActive) {
        if (tourStep > 0) {
            tourStep--
        } else {
            homeViewModel.dismissRibiIntro()
            navController.navigate(NavRoute.Home.route) {
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                }
                launchSingleTop = true
            }
        }
    }

    // ── Safe Back Navigation ────────────────────────────────────────────────
    // Pops to the actual previous screen in history. If backstack is empty, returns smoothly to Home.
    val navigateBack: () -> Unit = {
        if (!navController.popBackStack()) {
            navController.navigate(NavRoute.Home.route) {
                popUpTo(navController.graph.findStartDestination().id) {
                    inclusive = false
                }
                launchSingleTop = true
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = androidx.compose.ui.graphics.Color.Transparent,
            bottomBar = {
                if (showBottomBar) {
                    YutoriBottomNav(
                        currentRoute = currentBackStack?.destination,
                        onNavigate = { route ->
                            if (currentRoute != route) {
                                if (route == NavRoute.Home.route) {
                                    navController.popBackStack(NavRoute.Home.route, inclusive = false)
                                } else {
                                    navController.navigate(route) {
                                        launchSingleTop = true
                                    }
                                }
                            }
                        }
                    )
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = NavRoute.Home.route,
                modifier = Modifier.padding(innerPadding),
                enterTransition = { fadeIn() },
                exitTransition = { fadeOut() }
            ) {
                composable(NavRoute.Onboarding.route) {
                    OnboardingScreen(
                        onFinished = {
                            navController.navigate(NavRoute.Home.route) {
                                popUpTo(navController.graph.id) { inclusive = true }
                            }
                        }
                    )
                }

                composable(NavRoute.Home.route) {
                    HomeScreen(
                        viewModel = homeViewModel,
                        onStartFocusSession = { navController.navigate(NavRoute.Focus.route) },
                        onDuckBreak = { navController.navigate(NavRoute.FrogBreak.route) },
                        onAccountClick = {
                            navController.navigate(NavRoute.Account.route) {
                                launchSingleTop = true
                            }
                        },
                        onSurgeClick = { navController.navigate(NavRoute.Surge.route) },
                        onDailyInsightClick = {
                            navController.navigate(NavRoute.Analytics.route) {
                                launchSingleTop = true
                            }
                        }
                    )
                }

                composable(NavRoute.Habits.route) {
                    HabitTrackerScreen(onBack = navigateBack)
                }

                composable(NavRoute.DailyGoal.route) {
                    DailyGoalScreen(onBack = navigateBack)
                }

                composable(NavRoute.Journey.route) {
                    JourneyScreen(
                        onSurge = { navController.navigate(NavRoute.Surge.route) },
                        onBack = navigateBack
                    )
                }

                composable(NavRoute.Surge.route) {
                    SurgeBridgeScreen(
                        onFinished = navigateBack
                    )
                }

                composable(NavRoute.Analytics.route) {
                    AnalyticsDashboardScreen(
                        onBack = navigateBack
                    )
                }

                composable(NavRoute.Focus.route) {
                    FocusTimerScreen(onBack = navigateBack)
                }

                composable(NavRoute.FrogBreak.route) {
                    FrogBreakScreen(onBack = navigateBack)
                }

                composable(NavRoute.Account.route) {
                    AccountScreen(
                        onBack = navigateBack,
                        onInterceptSettings = {
                            navController.navigate(NavRoute.InterceptSettings.route) {
                                launchSingleTop = true
                            }
                        },
                        onWardrobeClick = {
                            navController.navigate(NavRoute.Wardrobe.route) {
                                launchSingleTop = true
                            }
                        },
                        onExpensesClick = {
                            navController.navigate(NavRoute.Expenses.route) {
                                launchSingleTop = true
                            }
                        },
                        onAnalyticsClick = {
                            navController.navigate(NavRoute.Analytics.route) {
                                launchSingleTop = true
                            }
                        }
                    )
                }

                composable(NavRoute.Wardrobe.route) {
                    WardrobeScreen(onBack = navigateBack)
                }

                composable(NavRoute.Expenses.route) {
                    ExpenseTrackerScreen(onBack = navigateBack)
                }

                composable(NavRoute.InterceptSettings.route) {
                    InterceptSettingsScreen(onBack = navigateBack)
                }
            }
        }

        // Global Tour Overlay floating on top of all pages!
        if (isTourActive) {
            AppTourOverlay(
                currentStep = tourStep,
                onNext = {
                    if (tourStep < APP_TOUR_STEPS.size - 1) {
                        tourStep++
                    } else {
                        homeViewModel.dismissRibiIntro()
                        navController.navigate(NavRoute.Home.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                        }
                    }
                },
                onPrev = {
                    if (tourStep > 0) {
                        tourStep--
                    }
                },
                onSkip = {
                    homeViewModel.dismissRibiIntro()
                    navController.navigate(NavRoute.Home.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Route definitions
// ─────────────────────────────────────────────────────────────────────────────
sealed class NavRoute(val route: String, val label: String, val icon: ImageVector) {
    object Home : NavRoute("home", "Home", Icons.Rounded.Home)
    object Habits : NavRoute("habits", "Habits", Icons.Rounded.Checklist)
    object DailyGoal : NavRoute("daily_goal", "Goal", Icons.Rounded.WbSunny)
    object Journey : NavRoute("journey", "Journey", Icons.Rounded.Favorite)
    object Account : NavRoute("account", "Profile", Icons.Rounded.AccountCircle)

    // Sub-screens
    object Analytics : NavRoute("analytics", "Insights", Icons.Rounded.BarChart)
    object Focus : NavRoute("focus", "Focus", Icons.Rounded.PlayArrow)
    object FrogBreak : NavRoute("frogbreak", "Break", Icons.Rounded.SelfImprovement)
    object Surge : NavRoute("surge", "Surge", Icons.Rounded.Warning)
    object Onboarding : NavRoute("onboarding", "Start", Icons.Rounded.Star)
    object InterceptSettings : NavRoute("intercept_settings", "App Pause", Icons.Rounded.Shield)
    object Expenses : NavRoute("expenses", "Expenses", Icons.Rounded.AttachMoney)
    object Wardrobe : NavRoute("wardrobe", "Wardrobe", Icons.Rounded.Face)
}

private val bottomNavItems = listOf(
    NavRoute.Home,
    NavRoute.Habits,
    NavRoute.DailyGoal,
    NavRoute.Journey,
    NavRoute.Account
)

@Composable
private fun YutoriBottomNav(
    currentRoute: androidx.navigation.NavDestination?,
    onNavigate: (String) -> Unit
) {
    NavigationBar(
        containerColor = CozyBlack3,
        tonalElevation = 0.dp
    ) {
        bottomNavItems.forEach { screen ->
            val selected = currentRoute?.hierarchy?.any { it.route == screen.route } == true
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = screen.icon,
                        contentDescription = screen.label,
                        modifier = Modifier.size(22.dp)
                    )
                },
                label = {
                    Text(
                        screen.label,
                        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                        fontSize = 11.sp
                    )
                },
                selected = selected,
                onClick = { onNavigate(screen.route) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MossGreen,
                    selectedTextColor = MossGreen,
                    unselectedIconColor = SoftTan,
                    unselectedTextColor = SoftTan,
                    indicatorColor = MossGreenDim
                )
            )
        }
    }
}
