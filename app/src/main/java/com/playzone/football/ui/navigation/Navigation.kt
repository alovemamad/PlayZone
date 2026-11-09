package com.playzone.football.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.playzone.football.data.model.MatchStatus
import com.playzone.football.ui.components.PlayZoneTopBar
import com.playzone.football.ui.components.VirtualPredictionDialog
import com.playzone.football.ui.screens.*
import com.playzone.football.ui.theme.*
import com.playzone.football.viewmodel.MainViewModel

sealed class Screen(val route: String, val title: String, val icon: ImageVector? = null) {
    data object Home : Screen("home", "خانه", Icons.Default.Home)
    data object Matches : Screen("matches", "مسابقات", Icons.Default.SportsSoccer)
    data object Predictions : Screen("predictions", "پیش‌بینی‌ها", Icons.Default.History)
    data object Wallet : Screen("wallet", "کیف پول", Icons.Default.AccountBalanceWallet)
    data object Profile : Screen("profile", "پروفایل", Icons.Default.Person)

    data object Leagues : Screen("leagues", "لیگ‌ها")
    data object MatchDetail : Screen("match_detail/{matchId}", "جزئیات مسابقه") {
        fun createRoute(matchId: String) = "match_detail/$matchId"
    }
    data object TeamDetail : Screen("team_detail/{teamId}", "جزئیات تیم") {
        fun createRoute(teamId: String) = "team_detail/$teamId"
    }
    data object Responsible : Screen("responsible", "مدیریت زمان")
    data object Settings : Screen("settings", "تنظیمات")
    data object Privacy : Screen("privacy", "حریم خصوصی")
}

@Composable
fun MainScreen(viewModel: MainViewModel) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val wallet by viewModel.wallet.collectAsStateWithLifecycle()
    val matches by viewModel.filteredMatches.collectAsStateWithLifecycle()
    val rawMatches by viewModel.rawMatches.collectAsStateWithLifecycle()
    val selectedLeagueId by viewModel.selectedLeagueFilter.collectAsStateWithLifecycle()
    val selectedStatus by viewModel.matchStatusFilter.collectAsStateWithLifecycle()
    val predictions by viewModel.predictions.collectAsStateWithLifecycle()
    val responsibleSettings by viewModel.responsibleSettings.collectAsStateWithLifecycle()

    val activePredictionMatch by viewModel.activePredictionMatch.collectAsStateWithLifecycle()
    val activePredictionOutcome by viewModel.activePredictionOutcome.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    // Listen to messages from ViewModel
    LaunchedEffect(Unit) {
        viewModel.userMessage.collect { msg ->
            snackbarHostState.showSnackbar(msg)
        }
    }

    val isLiveApi by viewModel.isLiveApiActive.collectAsStateWithLifecycle()

    val bottomNavItems = listOf(
        Screen.Home,
        Screen.Matches,
        Screen.Predictions,
        Screen.Wallet,
        Screen.Profile
    )

    val showBottomBar = currentRoute in bottomNavItems.map { it.route }

    Scaffold(
        topBar = {
            if (showBottomBar) {
                PlayZoneTopBar(
                    virtualBalance = wallet.balance,
                    isLiveApi = isLiveApi,
                    onWalletClick = { navController.navigate(Screen.Wallet.route) },
                    onResponsibleClick = { navController.navigate(Screen.Responsible.route) }
                )
            }
        },
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = NavyDarkest,
                    contentColor = TextWhite
                ) {
                    bottomNavItems.forEach { item ->
                        val selected = currentRoute == item.route
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                item.icon?.let {
                                    Icon(
                                        imageVector = it,
                                        contentDescription = item.title,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            },
                            label = { Text(item.title, style = MaterialTheme.typography.labelSmall) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = GreenAccent,
                                selectedTextColor = GreenAccent,
                                unselectedIconColor = TextGrayMuted,
                                unselectedTextColor = TextGrayMuted,
                                indicatorColor = NavyCardElevated
                            ),
                            modifier = Modifier.testTag("nav_item_${item.route}")
                        )
                    }
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = NavyBackground
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    viewModel = viewModel,
                    wallet = wallet,
                    matches = matches,
                    selectedLeagueId = selectedLeagueId,
                    onNavigateToMatch = { matchId ->
                        navController.navigate(Screen.MatchDetail.createRoute(matchId))
                    },
                    onNavigateToMatchesList = { status ->
                        viewModel.selectStatusFilter(status)
                        navController.navigate(Screen.Matches.route)
                    },
                    onNavigateToWallet = {
                        navController.navigate(Screen.Wallet.route)
                    },
                    onNavigateToLeagues = {
                        navController.navigate(Screen.Leagues.route)
                    },
                    onPredictClick = { match, outcome ->
                        viewModel.openPredictionModal(match, outcome)
                    }
                )
            }

            composable(Screen.Matches.route) {
                MatchesScreen(
                    viewModel = viewModel,
                    matches = matches,
                    selectedStatus = selectedStatus,
                    selectedLeagueId = selectedLeagueId,
                    onNavigateToMatch = { matchId ->
                        navController.navigate(Screen.MatchDetail.createRoute(matchId))
                    },
                    onPredictClick = { match, outcome ->
                        viewModel.openPredictionModal(match, outcome)
                    }
                )
            }

            composable(Screen.Predictions.route) {
                PredictionsScreen(
                    viewModel = viewModel,
                    predictions = predictions,
                    onExploreMatchesClick = {
                        navController.navigate(Screen.Matches.route)
                    }
                )
            }

            composable(Screen.Wallet.route) {
                WalletScreen(
                    viewModel = viewModel,
                    wallet = wallet
                )
            }

            composable(Screen.Profile.route) {
                ProfileScreen(
                    viewModel = viewModel,
                    wallet = wallet,
                    onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                    onNavigateToResponsible = { navController.navigate(Screen.Responsible.route) },
                    onNavigateToPrivacy = { navController.navigate(Screen.Privacy.route) }
                )
            }

            composable(Screen.Leagues.route) {
                LeaguesScreen(
                    viewModel = viewModel,
                    onTeamClick = { teamId ->
                        navController.navigate(Screen.TeamDetail.createRoute(teamId))
                    }
                )
            }

            composable(
                route = Screen.MatchDetail.route,
                arguments = listOf(navArgument("matchId") { type = NavType.StringType })
            ) { backStackEntry ->
                val matchId = backStackEntry.arguments?.getString("matchId")
                val match = rawMatches.find { it.id == matchId }
                MatchDetailScreen(
                    match = match,
                    onBackClick = { navController.popBackStack() },
                    onPredictClick = { m, outcome ->
                        viewModel.openPredictionModal(m, outcome)
                    }
                )
            }

            composable(
                route = Screen.TeamDetail.route,
                arguments = listOf(navArgument("teamId") { type = NavType.StringType })
            ) { backStackEntry ->
                val teamId = backStackEntry.arguments?.getString("teamId")
                val team = viewModel.teams.find { it.id == teamId }
                TeamDetailScreen(
                    team = team,
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(Screen.Responsible.route) {
                ResponsibleGamingScreen(
                    viewModel = viewModel,
                    settings = responsibleSettings,
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(Screen.Settings.route) {
                SettingsScreen(
                    viewModel = viewModel,
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(Screen.Privacy.route) {
                PrivacyPolicyScreen(
                    onBackClick = { navController.popBackStack() }
                )
            }
        }

        // Active prediction modal dialog
        if (activePredictionMatch != null && activePredictionOutcome != null) {
            VirtualPredictionDialog(
                match = activePredictionMatch!!,
                outcome = activePredictionOutcome!!,
                userBalance = wallet.balance,
                onDismiss = { viewModel.closePredictionModal() },
                onConfirm = { stake -> viewModel.submitPrediction(stake) }
            )
        }
    }
}
