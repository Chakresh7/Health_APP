package com.calai.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.calai.CalAiApplication
import com.calai.data.local.AppDatabase
import com.calai.ui.auth.AuthViewModel
import com.calai.ui.auth.LoginScreen
import com.calai.data.local.ChatRepository
import com.calai.data.local.MealEntity
import com.calai.data.local.MealRepository
import com.calai.data.local.UserPreferences
import com.calai.data.remote.NutritionAnalysis
import com.calai.domain.model.MealTypes
import com.calai.ui.chat.ChatScreen
import com.calai.ui.chat.ChatViewModel
import com.calai.ui.diary.DiaryScreen
import com.calai.ui.diary.DiaryViewModel
import com.calai.ui.home.HomeScreen
import com.calai.ui.home.HomeViewModel
import com.calai.ui.notifications.NotificationsScreen
import com.calai.ui.onboarding.OnboardingScreen
import com.calai.ui.result.FoodResultScreen
import com.calai.ui.scanner.ScannerScreen
import com.calai.ui.scanner.ScannerViewModel
import com.calai.ui.settings.SettingsScreen
import com.calai.ui.settings.SettingsViewModel
import kotlinx.coroutines.launch

private object Routes {
    const val Login = "login"
    const val Onboarding = "onboarding"
    const val Home = "home"
    const val Scan = "scan"
    const val Ask = "ask"
    const val Diary = "diary"
    const val Result = "result"
    const val AddMeal = "add_meal"
    const val Settings = "settings"
    const val Notifications = "notifications"
}

private data class NavigationItem(
    val route: String,
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

@Composable
fun AppNavigation() {
    val context = LocalContext.current
    val preferences = remember { UserPreferences(context.applicationContext) }
    val authRepository = (context.applicationContext as CalAiApplication).authRepository
    val authViewModel: AuthViewModel = viewModel(factory = AuthViewModel.factory(authRepository))
    val authState by authViewModel.uiState.collectAsStateWithLifecycle()
    var startReady by remember { mutableStateOf(false) }
    var sessionReady by remember { mutableStateOf(false) }
    var onboarded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        preferences.goals.collect { onboarded = it.onboardingComplete }
    }

    LaunchedEffect(startReady, authState.isInitializing) {
        if (!authState.isInitializing) sessionReady = true
    }

    LaunchedEffect(Unit) {
        startReady = true
    }

    if (!startReady || !sessionReady) {
        Box(
            modifier = Modifier.fillMaxSize().background(Color(0xFFF8F8FA)),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Color(0xFF17171B))
        }
        return
    }

    CalAiNavHost(
        preferences = preferences,
        authViewModel = authViewModel,
        startOnboarded = onboarded,
        startSignedIn = authState.isSignedIn
    )
}

@Composable
private fun CalAiNavHost(
    preferences: UserPreferences,
    authViewModel: AuthViewModel,
    startOnboarded: Boolean,
    startSignedIn: Boolean
) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val database = remember { AppDatabase.create(context.applicationContext) }
    val mealRepository = remember { MealRepository(database.mealDao()) }
    val chatRepository = remember { ChatRepository(database.chatDao()) }
    val scannerViewModel: ScannerViewModel = viewModel(factory = ScannerViewModel.factory(preferences))
    val homeViewModel: HomeViewModel = viewModel(factory = HomeViewModel.factory(mealRepository, preferences))
    val diaryViewModel: DiaryViewModel = viewModel(factory = DiaryViewModel.factory(mealRepository))
    val chatViewModel: ChatViewModel = viewModel(factory = ChatViewModel.factory(mealRepository, preferences, chatRepository))
    val settingsViewModel: SettingsViewModel = viewModel(factory = SettingsViewModel.factory(preferences))
    var analysis by remember { mutableStateOf<NutritionAnalysis?>(null) }
    var analysisImageUri by remember { mutableStateOf<String?>(null) }
    var editingMeal by remember { mutableStateOf<MealEntity?>(null) }
    val navigationItems = listOf(
        NavigationItem(Routes.Home, "Home", Icons.Outlined.Home),
        NavigationItem(Routes.Ask, "Ask", Icons.Outlined.ChatBubbleOutline),
        NavigationItem(Routes.Diary, "Diary", Icons.AutoMirrored.Outlined.MenuBook)
    )
    val currentEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentEntry?.destination?.route
    val showBar = currentRoute in setOf(Routes.Home, Routes.Ask, Routes.Diary, Routes.Scan)

    Scaffold(
        bottomBar = {
            if (showBar) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    shape = RoundedCornerShape(36.dp),
                    color = Color.White,
                    shadowElevation = 8.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(68.dp)
                            .padding(start = 8.dp, end = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(modifier = Modifier.weight(1f).fillMaxHeight()) {
                            navigationItems.forEach { item ->
                                val selected = currentRoute == item.route
                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .clickable { navController.goToTab(item.route) },
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        item.icon,
                                        contentDescription = item.label,
                                        tint = if (selected) Color(0xFF17171B) else Color(0xFF8A8A90),
                                        modifier = Modifier.size(22.dp)
                                    )
                                    Text(
                                        item.label,
                                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (selected) Color(0xFF17171B) else Color(0xFF8A8A90)
                                    )
                                }
                            }
                        }
                        Box(
                            modifier = Modifier
                                .padding(start = 10.dp)
                                .size(52.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF17171B))
                                .clickable {
                                    navController.navigate(Routes.Scan) { launchSingleTop = true }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Filled.Add,
                                contentDescription = "Scan food",
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = when {
                !startOnboarded -> Routes.Onboarding
                !startSignedIn -> Routes.Login
                else -> Routes.Home
            },
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Routes.Onboarding) {
                OnboardingScreen(authViewModel = authViewModel) { answers ->
                    scope.launch {
                        preferences.completeOnboarding(
                            displayName = answers.name,
                            goalType = answers.goal,
                            calorieTarget = answers.calorieTarget,
                            proteinTarget = answers.proteinTarget,
                            carbsTarget = answers.carbsTarget,
                            fatTarget = answers.fatTarget
                        )
                        navController.navigate(Routes.Home) {
                            popUpTo(0) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                }
            }
            composable(Routes.Login) {
                LoginScreen(viewModel = authViewModel) {
                    navController.navigate(Routes.Home) {
                        popUpTo(Routes.Login) { inclusive = true }
                    }
                }
            }
            composable(Routes.Home) {
                HomeScreen(
                    viewModel = homeViewModel,
                    onScanFood = { navController.navigate(Routes.Scan) },
                    onOpenSettings = { navController.navigate(Routes.Settings) },
                    onOpenNotifications = { navController.navigate(Routes.Notifications) },
                    onAddManually = {
                        editingMeal = null
                        navController.navigate(Routes.AddMeal)
                    }
                )
            }
            composable(Routes.Notifications) {
                val homeState by homeViewModel.uiState.collectAsStateWithLifecycle()
                NotificationsScreen(
                    notifications = homeState.notifications,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Routes.Scan) {
                ScannerScreen(
                    contentResolver = context.contentResolver,
                    viewModel = scannerViewModel,
                    onAnalysisReady = { result, imageUri ->
                        analysis = result
                        analysisImageUri = imageUri?.toString()
                        editingMeal = null
                        navController.navigate(Routes.Result)
                    },
                    onAddManually = {
                        editingMeal = null
                        navController.navigate(Routes.AddMeal)
                    }
                )
            }
            composable(Routes.Ask) { ChatScreen(viewModel = chatViewModel) }
            composable(Routes.Diary) {
                DiaryScreen(
                    viewModel = diaryViewModel,
                    onEditMeal = { meal ->
                        editingMeal = meal
                        analysis = meal.toNutritionAnalysis()
                        analysisImageUri = meal.imageUri
                        navController.navigate(Routes.Result)
                    },
                    onAddManually = {
                        editingMeal = null
                        navController.navigate(Routes.AddMeal)
                    }
                )
            }
            composable(Routes.Result) {
                val current = analysis
                val mealBeingEdited = editingMeal
                if (current != null) {
                    FoodResultScreen(
                        analysis = current,
                        initialMealType = mealBeingEdited?.mealType ?: MealTypes.defaultForNow(),
                        title = if (mealBeingEdited != null) "Edit meal" else "Review estimate",
                        primaryActionLabel = if (mealBeingEdited != null) "Save changes" else "Add to diary",
                        showRetake = mealBeingEdited == null,
                        onSave = { approved, mealType ->
                            if (mealBeingEdited != null) {
                                diaryViewModel.updateMeal(
                                    mealBeingEdited.copy(
                                        foodName = approved.food_name,
                                        calories = approved.estimated_calories,
                                        proteinG = approved.protein_g,
                                        carbsG = approved.carbs_g,
                                        fatG = approved.fat_g,
                                        mealType = mealType
                                    )
                                )
                            } else {
                                homeViewModel.saveAnalysis(approved, mealType, analysisImageUri)
                            }
                            editingMeal = null
                            navController.popBackStack(Routes.Home, false)
                        },
                        onRetake = {
                            scannerViewModel.reset()
                            navController.popBackStack(Routes.Scan, false)
                        }
                    )
                }
            }
            composable(Routes.AddMeal) {
                FoodResultScreen(
                    analysis = blankAnalysis(),
                    initialMealType = MealTypes.defaultForNow(),
                    title = "Add meal",
                    primaryActionLabel = "Save meal",
                    showRetake = false,
                    onSave = { approved, mealType ->
                        homeViewModel.saveAnalysis(approved, mealType)
                        navController.popBackStack(Routes.Home, false)
                    },
                    onCancel = { navController.popBackStack() }
                )
            }
            composable(Routes.Settings) {
                SettingsScreen(
                    viewModel = settingsViewModel,
                    authViewModel = authViewModel,
                    onBack = { navController.popBackStack() },
                    onLoggedOut = {
                        navController.navigate(Routes.Login) {
                            popUpTo(0) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
    }
}

private fun NavHostController.goToTab(route: String) {
    navigate(route) {
        popUpTo(Routes.Home) {
            saveState = true
            inclusive = false
        }
        launchSingleTop = true
        restoreState = true
    }
}

private fun blankAnalysis() = NutritionAnalysis(
    food_name = "",
    portion_estimate = "1 serving",
    estimated_calories = 0,
    protein_g = 0.0,
    carbs_g = 0.0,
    fat_g = 0.0,
    confidence = 0.0
)

private fun MealEntity.toNutritionAnalysis() = NutritionAnalysis(
    food_name = foodName,
    portion_estimate = "1 serving",
    estimated_calories = calories,
    protein_g = proteinG,
    carbs_g = carbsG,
    fat_g = fatG,
    confidence = 0.0
)
