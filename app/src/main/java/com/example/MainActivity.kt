package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.MenuBook
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.data.local.AppDatabase
import com.example.data.local.SettingsDataStore
import com.example.data.local.SyllabusAssetLoader
import com.example.data.repository.JeeTrackerRepository
import com.example.ui.JeeTrackerViewModel
import com.example.ui.components.MotionTokens
import com.example.ui.screens.RevisionScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SubjectDetailScreen
import com.example.ui.screens.SyllabusScreen
import com.example.ui.screens.TimeCounterScreen
import com.example.ui.theme.JeeTrackerTheme

object AppRoutes {
    const val SYLLABUS = "syllabus"
    const val REVISION = "revision"
    const val TIME_COUNTER = "time_counter"
    const val SUBJECT_DETAIL = "subject_detail/{subjectId}"
    const val SETTINGS = "settings"

    fun subjectDetail(subjectId: String) = "subject_detail/$subjectId"
}

private data class TopLevelTab(
    val route: String,
    val labelRes: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

private val TOP_LEVEL_TABS = listOf(
    TopLevelTab(
        route = AppRoutes.SYLLABUS,
        labelRes = R.string.nav_syllabus,
        selectedIcon = Icons.Rounded.MenuBook,
        unselectedIcon = Icons.Outlined.MenuBook
    ),
    TopLevelTab(
        route = AppRoutes.REVISION,
        labelRes = R.string.nav_revision,
        selectedIcon = Icons.Rounded.AutoAwesome,
        unselectedIcon = Icons.Outlined.AutoAwesome
    ),
    TopLevelTab(
        route = AppRoutes.TIME_COUNTER,
        labelRes = R.string.nav_time_counter,
        selectedIcon = Icons.Rounded.Timer,
        unselectedIcon = Icons.Outlined.Timer
    )
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = AppDatabase.getInstance(applicationContext)
        val repository = JeeTrackerRepository(
            syllabusAssetLoader = SyllabusAssetLoader(applicationContext),
            progressDao = database.chapterProgressDao(),
            settingsDataStore = SettingsDataStore(applicationContext)
        )

        setContent {
            val viewModel: JeeTrackerViewModel = viewModel(
                factory = JeeTrackerViewModel.Factory(repository)
            )
            JeeTrackerApp(viewModel = viewModel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun JeeTrackerApp(viewModel: JeeTrackerViewModel) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val chapters by viewModel.chapters.collectAsStateWithLifecycle()
    val subjectSummaries by viewModel.subjectSummaries.collectAsStateWithLifecycle()
    val overallSummary by viewModel.overallSummary.collectAsStateWithLifecycle()
    val timeCounterState by viewModel.timeCounterState.collectAsStateWithLifecycle()

    JeeTrackerTheme(settings = settings) {
        val navController = rememberNavController()
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination
        val currentRoute = currentDestination?.route

        val isTopLevel = currentRoute in setOf(
            AppRoutes.SYLLABUS,
            AppRoutes.REVISION,
            AppRoutes.TIME_COUNTER
        )

        val topBarTitle = remember(currentRoute, navBackStackEntry) {
            when {
                currentRoute == AppRoutes.SETTINGS -> "Settings"
                currentRoute == AppRoutes.REVISION -> "Revision Queue"
                currentRoute == AppRoutes.TIME_COUNTER -> "Time Counter"
                currentRoute == AppRoutes.SUBJECT_DETAIL -> {
                    when (navBackStackEntry?.arguments?.getString("subjectId")) {
                        "physics" -> "Physics"
                        "chemistry" -> "Chemistry"
                        "maths" -> "Maths"
                        else -> "Subject"
                    }
                }
                else -> "JEE Tracker"
            }
        }

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            topBar = {
                TopAppBar(
                    title = {
                        AnimatedContent(
                            targetState = topBarTitle,
                            transitionSpec = {
                                fadeIn(tween(200, easing = MotionTokens.EmphasizedDecelerate)) togetherWith
                                    fadeOut(tween(150))
                            },
                            label = "topbar_title"
                        ) { title ->
                            Text(
                                text = title,
                                style = MaterialTheme.typography.titleLarge
                            )
                        }
                    },
                    navigationIcon = {
                        if (!isTopLevel) {
                            IconButton(
                                onClick = { navController.popBackStack() },
                                modifier = Modifier.testTag("back_button")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                    contentDescription = stringResource(R.string.cd_back)
                                )
                            }
                        }
                    },
                    actions = {
                        if (currentRoute != AppRoutes.SETTINGS) {
                            IconButton(
                                onClick = { navController.navigate(AppRoutes.SETTINGS) },
                                modifier = Modifier.testTag("settings_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Settings,
                                    contentDescription = stringResource(R.string.cd_settings)
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.onSurface
                    )
                )
            },
            bottomBar = {
                if (isTopLevel) {
                    NavigationBar {
                        TOP_LEVEL_TABS.forEach { tab ->
                            val selected = currentDestination?.hierarchy?.any {
                                it.route == tab.route
                            } == true
                            val label = stringResource(tab.labelRes)
                            val iconScale by animateFloatAsState(
                                targetValue = if (selected) 1.12f else 1f,
                                animationSpec = spring(
                                    dampingRatio = 0.55f,
                                    stiffness = Spring.StiffnessMedium
                                ),
                                label = "tab_icon_scale_${tab.route}"
                            )
                            NavigationBarItem(
                                selected = selected,
                                onClick = {
                                    navController.navigate(tab.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                modifier = Modifier.testTag("nav_tab_${tab.route}"),
                                icon = {
                                    Icon(
                                        imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                                        contentDescription = label,
                                        modifier = Modifier.graphicsLayer {
                                            scaleX = iconScale
                                            scaleY = iconScale
                                        }
                                    )
                                },
                                label = { Text(label) }
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            SharedTransitionLayout(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                NavHost(
                    navController = navController,
                    startDestination = AppRoutes.SYLLABUS,
                    modifier = Modifier.fillMaxSize(),
                    enterTransition = {
                        fadeIn(tween(240, easing = MotionTokens.EmphasizedDecelerate)) +
                            scaleIn(initialScale = 0.97f, animationSpec = tween(240, easing = MotionTokens.EmphasizedDecelerate))
                    },
                    exitTransition = {
                        fadeOut(tween(180, easing = MotionTokens.EmphasizedAccelerate))
                    }
                ) {
                    composable(AppRoutes.SYLLABUS) {
                        SyllabusScreen(
                            overallSummary = overallSummary,
                            subjectSummaries = subjectSummaries,
                            syllabusYear = viewModel.syllabusData.syllabusYear,
                            sharedTransitionScope = this@SharedTransitionLayout,
                            animatedVisibilityScope = this,
                            onSubjectClick = { subjectId ->
                                navController.navigate(AppRoutes.subjectDetail(subjectId))
                            }
                        )
                    }

                    composable(
                        route = AppRoutes.SUBJECT_DETAIL,
                        arguments = listOf(navArgument("subjectId") { type = NavType.StringType }),
                        enterTransition = {
                            fadeIn(tween(280, easing = MotionTokens.EmphasizedDecelerate)) +
                                slideIntoContainer(
                                    towards = AnimatedContentTransitionScope.SlideDirection.Start,
                                    animationSpec = tween(300, easing = MotionTokens.EmphasizedDecelerate),
                                    initialOffset = { it / 6 }
                                )
                        },
                        exitTransition = {
                            fadeOut(tween(200, easing = MotionTokens.EmphasizedAccelerate))
                        },
                        popEnterTransition = {
                            fadeIn(tween(240, easing = MotionTokens.EmphasizedDecelerate))
                        },
                        popExitTransition = {
                            fadeOut(tween(220, easing = MotionTokens.EmphasizedAccelerate)) +
                                slideOutOfContainer(
                                    towards = AnimatedContentTransitionScope.SlideDirection.End,
                                    animationSpec = tween(240, easing = MotionTokens.EmphasizedAccelerate),
                                    targetOffset = { it / 6 }
                                )
                        }
                    ) { backStackEntry ->
                        val subjectId = backStackEntry.arguments?.getString("subjectId") ?: "physics"
                        SubjectDetailScreen(
                            subjectId = subjectId,
                            allChapters = chapters,
                            sharedTransitionScope = this@SharedTransitionLayout,
                            animatedVisibilityScope = this,
                            onToggleStage = { chapter, stage ->
                                viewModel.toggleChapterStage(chapter, stage)
                            }
                        )
                    }

                    composable(AppRoutes.REVISION) {
                        RevisionScreen(
                            chapters = chapters,
                            subjectSummaries = subjectSummaries,
                            onMarkRevisionDone = { chapter, done ->
                                viewModel.setRevisionDone(chapter, done)
                            },
                            onToggleStage = { chapter, stage ->
                                viewModel.toggleChapterStage(chapter, stage)
                            }
                        )
                    }

                    composable(AppRoutes.TIME_COUNTER) {
                        TimeCounterScreen(uiState = timeCounterState)
                    }

                    composable(
                        route = AppRoutes.SETTINGS,
                        enterTransition = {
                            slideIntoContainer(
                                towards = AnimatedContentTransitionScope.SlideDirection.Start,
                                animationSpec = tween(280, easing = MotionTokens.EmphasizedDecelerate)
                            ) + fadeIn(tween(220))
                        },
                        popExitTransition = {
                            slideOutOfContainer(
                                towards = AnimatedContentTransitionScope.SlideDirection.End,
                                animationSpec = tween(240, easing = MotionTokens.EmphasizedAccelerate)
                            ) + fadeOut(tween(180)) + scaleOut(targetScale = 0.96f, animationSpec = tween(200))
                        }
                    ) {
                        SettingsScreen(
                            settings = settings,
                            syllabusData = viewModel.syllabusData,
                            onThemeModeChange = viewModel::setThemeMode,
                            onAmoledBlackChange = viewModel::setAmoledBlack,
                            onDynamicColorChange = viewModel::setDynamicColor,
                            onAccentPresetChange = viewModel::setAccentPreset,
                            onCornerRadiusChange = viewModel::setCardCornerRadius,
                            onFontScaleChange = viewModel::setFontScale,
                            onCardDensityChange = viewModel::setCardDensity,
                            onChapterLayoutChange = viewModel::setChapterLayout,
                            onExamDateEpochDayChange = viewModel::setExamDateEpochDay,
                            onBufferDaysChange = viewModel::setBufferDays,
                            onResetProgress = viewModel::resetAllProgress
                        )
                    }
                }
            }
        }
    }
}
