package com.example.pruebaancora

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import coil.compose.rememberAsyncImagePainter

sealed class ScreenState {
    object HomeScreen: ScreenState()
    data class UserScreen(val username: String): ScreenState()
    data class GalleryScreen(val username: String, val imageID: Int = 1): ScreenState()
}

sealed class ScreenEvent {
    data class GoToUserScreen(val username: String) : ScreenEvent()
    data class GoToGalleryScreen(val username: String): ScreenEvent()
    object GoBackToHomeScreen : ScreenEvent()
    object GoBackToUserScreen : ScreenEvent()
    object NextImage: ScreenEvent()
    object LastImage : ScreenEvent()
}

class MainViewModel: ViewModel() {

    private val _state = MutableStateFlow<ScreenState>(ScreenState.HomeScreen)
    val state: StateFlow<ScreenState> = _state


    fun processEvent(event: ScreenEvent) {
        when (event) {
            is ScreenEvent.GoToUserScreen -> {
                _state.value = ScreenState.UserScreen(username = event.username)
            }

            is ScreenEvent.GoToGalleryScreen -> {
                _state.value = ScreenState.GalleryScreen(username = event.username)
            }

            is ScreenEvent.NextImage -> {
                val currentImage = if (_state.value is ScreenState.GalleryScreen) {
                    (_state.value as ScreenState.GalleryScreen).imageID
                } else {
                    1
                }
                if (currentImage < 1083) {
                    _state.value = ScreenState.GalleryScreen(username = (_state.value as ScreenState.GalleryScreen).username, imageID = currentImage + 1)
                } else {
                    _state.value = ScreenState.GalleryScreen(username = (_state.value as ScreenState.GalleryScreen).username, imageID = currentImage)
                }
            }

            is ScreenEvent.LastImage -> {
                val currentImage = if (_state.value is ScreenState.GalleryScreen) {
                    (_state.value as ScreenState.GalleryScreen).imageID
                } else {
                    1
                }

                if (currentImage > 1) {
                    _state.value = ScreenState.GalleryScreen(username = (_state.value as ScreenState.GalleryScreen).username, imageID = currentImage - 1)
                } else {
                    _state.value = ScreenState.GalleryScreen(username = (_state.value as ScreenState.GalleryScreen).username, imageID = currentImage)
                }
            }

            is ScreenEvent.GoBackToHomeScreen -> {
                _state.value = ScreenState.HomeScreen
            }

            is ScreenEvent.GoBackToUserScreen -> {
                _state.value = ScreenState.UserScreen(username = (_state.value as ScreenState.GalleryScreen).username)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: MainViewModel
) {
    var username by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text("Inicio de sesion")
                },
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.primary,
            ) {
            }
        }

    ) { innerPadding ->
        Column (
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Usuario:") },
                modifier = Modifier.padding(30.dp)
            )
            Button(
                onClick = {
                    if (username.isBlank()) {
                        username = "Usuario"
                    }
                    viewModel.processEvent(ScreenEvent.GoToUserScreen(username))
                },
                colors = buttonColors(
                    MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Text(
                    text = "Aceptar",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 20.sp
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserScreen(
    username: String,
    viewModel: MainViewModel
) {
    Scaffold(
        topBar = {
            TopAppBar(
                colors = topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text(username)
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.processEvent(ScreenEvent.GoBackToHomeScreen) }) {
                        Icon(Icons.Filled.ArrowBack, "backIcon")
                    }
                },
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.primary,
            ) {
            }
        }

    ) { innerPadding ->
        Surface(
            onClick = { viewModel.processEvent(ScreenEvent.GoToGalleryScreen(username)) }
        ) {
            Column (
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                Box(
                    modifier = Modifier
                        .size(150.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray)
                        .padding(4.dp)
                ) {
                    Icon(
                        Icons.Rounded.Person,
                        "Right",
                        Modifier.fillMaxSize(),
                        Color.Gray
                    )
                }
                Text(
                    text = "Bienvenido, ${username}",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 30.sp,
                    modifier = Modifier.padding(top = 40.dp)
                )
                Text(
                    text = "Pulsa la pantalla para continuar",
                    fontSize = 18.sp
                )
            }
        }
    }


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GalleryScreen(
    username: String,
    viewModel: MainViewModel
) {
    val state = viewModel.state.collectAsState().value
    val currentImage = if (state is ScreenState.GalleryScreen) state.imageID else 1

    val baseURLImage = "https://picsum.photos/id/"
    val sizeImage = "/400/600"

    Scaffold(
        topBar = {
            TopAppBar(
                colors = topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text(username)
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.processEvent(ScreenEvent.GoBackToUserScreen) }) {
                        Icon(Icons.Filled.ArrowBack, "backIcon")
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.primary,
            ) {
                Row (
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Icon(
                        Icons.Rounded.ArrowBack,
                        "Left",
                        Modifier.size(80.dp).clickable { viewModel.processEvent(ScreenEvent.LastImage) }
                    )

                    Icon(
                        Icons.Rounded.ArrowForward,
                        "Right",
                        Modifier.size(80.dp).clickable { viewModel.processEvent(ScreenEvent.NextImage) }
                    )
                }
            }
        }

    ) { innerPadding ->
        Image(
            painter = rememberAsyncImagePainter(baseURLImage.plus(currentImage.toString()).plus(sizeImage)),
            contentDescription = "currentImage",
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.primaryContainer)
        )
    }
}



@Composable
fun AncoraTestApp() {

    val viewModel: MainViewModel = viewModel()
    val state by viewModel.state.collectAsState()
    val navController = rememberNavController()

    LaunchedEffect(state) {
        when (state) {
            is ScreenState.HomeScreen -> navController.navigate("homeScreen")
            is ScreenState.UserScreen -> {
                val username = (state as ScreenState.UserScreen).username
                navController.navigate("userScreen/$username")
            }
            is ScreenState.GalleryScreen -> {
                val username = (state as ScreenState.GalleryScreen).username
                navController.navigate("galleryScreen/$username")
            }
        }
    }

    NavHost(navController = navController, startDestination = "homeScreen") {
        composable("homeScreen") {
            HomeScreen(viewModel)
        }
        composable("userScreen/{username}", arguments = listOf(navArgument("username") { type = NavType.StringType })) { backStackEntry ->
            UserScreen(username = backStackEntry.arguments?.getString("username") ?: "", viewModel)
        }
        composable("galleryScreen/{username}", arguments = listOf(navArgument("username") { type = NavType.StringType })) { backStackEntry ->
            GalleryScreen(username = backStackEntry.arguments?.getString("username") ?: "", viewModel)
        }
    }
}


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AncoraTestApp()
        }
    }
}