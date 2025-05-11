package com.example.pawpin_v2.screens.map

import android.Manifest
import android.app.Application
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pawpin_v2.R
import com.example.pawpin_v2.viewmodel.MapViewModel
import com.example.pawpin_v2.data.DogInfo
import com.google.accompanist.permissions.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.maps.android.compose.*
import kotlinx.coroutines.launch
import kotlin.random.Random
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng


import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.LocationServices

import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(isOwner: Boolean = false) {

    if (isOwner) {
            OwnerMapScreen()
    } else {

        val context = LocalContext.current
        val viewModel: MapViewModel = viewModel(
            factory = object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return MapViewModel(context.applicationContext as Application) as T
                }
            }
        )

        LaunchedEffect(Unit) {
            viewModel.startLocationUpdates()
        }


        val tracking by viewModel.tracking.collectAsState()
        val routePoints by viewModel.routePoints.collectAsState()
        val distance by viewModel.distance.collectAsState()
        val cameraPositionState = rememberCameraPositionState()
        val userLocation by viewModel.currentLocation.collectAsState()
        val walkPath by viewModel.walkPathPoints.collectAsState()

        val snackbarHostState = remember { SnackbarHostState() }


        val permissionState = rememberMultiplePermissionsState(
            listOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )

        var selectedDogs by remember { mutableStateOf<List<DogInfo>>(emptyList()) }
        var showNearbyDogsDialog by remember { mutableStateOf(false) }
        val availableDogs = remember {
            mutableStateOf(
                listOf(
                    DogInfo("Luna", Random.nextInt(10, 40)),
                    DogInfo("Rocky", Random.nextInt(10, 40)),
                    DogInfo("Bella", Random.nextInt(10, 40))
                )
            )
        }

        val coroutineScope = rememberCoroutineScope()


        fun centerOnUser() {
            userLocation?.let {
                coroutineScope.launch {
                    cameraPositionState.animate(
                        update = CameraUpdateFactory.newLatLngZoom(it, 16f)
                    )
                }
            }
        }

        var hasCenteredMap by remember { mutableStateOf(false) }

        LaunchedEffect(userLocation) {
            if (userLocation != null && !hasCenteredMap) {
                centerOnUser()
                hasCenteredMap = true
            }
        }



        LaunchedEffect(tracking) {
            if (tracking && routePoints.isNotEmpty()) {
                cameraPositionState.animate(
                    update = CameraUpdateFactory.newLatLngZoom(routePoints.first(), 15f)
                )
            }
        }


        if (!permissionState.allPermissionsGranted) {
            Column(
                Modifier.fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Location permission is required.")
                Button(onClick = { permissionState.launchMultiplePermissionRequest() }) {
                    Text("Grant Permission")
                }
            }
        } else {
            Box(Modifier.fillMaxSize()) {


                GoogleMap(
                    modifier = Modifier.matchParentSize(),
                    cameraPositionState = cameraPositionState,
                    properties = MapProperties(isMyLocationEnabled = true),
                    uiSettings = MapUiSettings(compassEnabled = true, zoomControlsEnabled = false)
                ) {
                    viewModel.extraDogs.forEachIndexed { index, dog ->
                        Marker(
                            state = rememberMarkerState(position = dog.position),
                            title = "🐶 ${dog.name}",
                            snippet = "📍 Pickup in ${
                                Random.nextInt(
                                    10,
                                    40
                                )
                            } min\n👤 Owner: John Doe",
                            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)
                        )
                    }






                    if (routePoints.isNotEmpty()) {
                        Polyline(
                            points = routePoints,
                            color = Color(0xFF2196F3),
                            width = 8f
                        ) // Blue
                    }

                    if (tracking && walkPath.isNotEmpty()) {
                        Polyline(
                            points = walkPath,
                            color = Color.Red,
                            width = 8f
                        ) // 🔴 Live walk trail
                    }


                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .padding(12.dp)
                        .wrapContentSize()
                ) {
                    // 👤 Circular logo image with border and elevation-like shadow
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(80.dp)
                            .shadow(6.dp, CircleShape, clip = false)
                            .clip(CircleShape)
                            .background(Color.White)
                            .border(2.dp, Color(0xFF6A1B9A), CircleShape)
                            .clickable { showNearbyDogsDialog = true }
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.logo_new),
                            contentDescription = null,
                            modifier = Modifier.size(60.dp),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // ✨ Text badge with gradient background
                    Box(
                        modifier = Modifier
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(Color(0xFF8E24AA), Color(0xFFCE93D8))
                                ),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = "Walk all dogs!",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodySmall,
                            letterSpacing = 1.sp
                        )
                    }
                }



                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.align(Alignment.TopCenter)
                ) {
                    if (tracking || distance > 0f) {
                        Text(
                            text = String.format("Distance: %.2f m", distance),
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f))
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        )
                    }
                }

                Card(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(24.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Column(
                        Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        var expanded by remember { mutableStateOf(false) }

                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded }
                        ) {
                            OutlinedTextField(
                                readOnly = true,
                                value = if (selectedDogs.isNotEmpty()) "${selectedDogs.size} selected" else "",
                                onValueChange = {},
                                label = { Text("Select dogs") },
                                placeholder = { Text("None selected") },
                                trailingIcon = { Icon(Icons.Filled.ArrowDropDown, null) },
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth()
                            )
                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {

                                Column(
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    availableDogs.value.forEachIndexed { index, dog ->
                                        val isSelected = dog in selectedDogs
                                        val backgroundColor =
                                            if (index % 2 == 0) Color(0xFFFFF3E0) else Color(
                                                0xFFF5F5F5
                                            ) // light orange & gray

                                        DropdownMenuItem(
                                            text = {
                                                Column {
                                                    Row(
                                                        horizontalArrangement = Arrangement.SpaceBetween,
                                                        modifier = Modifier.fillMaxWidth()
                                                    ) {
                                                        Text(
                                                            "🐶 ${dog.name}",
                                                            fontWeight = FontWeight.Bold
                                                        )
                                                        if (isSelected) Text("✅")
                                                    }
                                                    Text(
                                                        "⏱ Pickup in ${dog.pickupInMinutes} min",
                                                        style = MaterialTheme.typography.bodySmall
                                                    )
                                                    Text(
                                                        "👤 Owner: John Doe",
                                                        style = MaterialTheme.typography.bodySmall
                                                    )
                                                }
                                            },
                                            onClick = {
                                                val myLocation = userLocation
                                                    ?: cameraPositionState.position.target
                                                if (!isSelected) {
                                                    selectedDogs += dog
                                                    viewModel.addDogMarker(dog.name, myLocation)
                                                } else {
                                                    selectedDogs -= dog
                                                    viewModel.removeDogMarker(dog.name)
                                                }
                                                expanded = false
                                            },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(color = backgroundColor)
                                                .padding(4.dp)
                                        )
                                    }
                                }

                            }
                        }

                        Spacer(Modifier.height(12.dp))

                        Button(
                            enabled = selectedDogs.isNotEmpty() && !tracking && routePoints.isEmpty(),
                            onClick = {
                                coroutineScope.launch {
                                    val myLocation =
                                        userLocation ?: cameraPositionState.position.target
                                    val points = selectedDogs.mapNotNull { dog ->
                                        viewModel.extraDogs.find { it.name.contains(dog.name) }?.position
                                    }
                                    if (points.isNotEmpty()) {
                                        val fullRoute =
                                            viewModel.getWalkingRoute(myLocation, points)
                                        viewModel.setRoutePoints(fullRoute)
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Generate Route")
                        }

                        Spacer(Modifier.height(12.dp))

                        Button(
                            onClick = {
                                if (!tracking) {
                                    viewModel.startWalk()
                                } else {
                                    viewModel.endWalk()
                                    viewModel.clearDogMarkers()
                                    //val walkedDogNames = selectedDogs.joinToString(", ") { it.name }
                                    coroutineScope.launch {
                                        val dogNames = selectedDogs.joinToString(", ") { it.name }
                                        snackbarHostState.showSnackbar(
                                            message = "🎉 Walk finished!\nDogs: $dogNames\nDistance: ${
                                                "%.0f".format(
                                                    distance
                                                )
                                            } meters 🐾",
                                            withDismissAction = true,
                                            duration = SnackbarDuration.Long
                                        )
                                        selectedDogs =
                                            emptyList() // ✅ happens after snackbar is shown
                                    }


//                                Toast.makeText(
//                                    context,
//                                    "🎉 Walk finished!\nDogs: $walkedDogNames\nDistance: ${"%.0f".format(distance)} meters 🐾",
//                                    Toast.LENGTH_LONG
//                                ).show()


                                }
                            },
                            enabled = selectedDogs.isNotEmpty() && routePoints.isNotEmpty(),
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (tracking) Color.Red else MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Text(
                                text = if (tracking) "End Walk" else "Start Walk",
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }

                    }
                }

                if (showNearbyDogsDialog) {
                    AlertDialog(
                        onDismissRequest = { showNearbyDogsDialog = false },
                        title = {
                            Text(
                                "Nearby Dogs 🐾",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        text = {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                availableDogs.value.forEach { dog ->
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(
                                            containerColor = Color(
                                                0xFFFFF3E0
                                            )
                                        ) // Light orange background
                                    ) {
                                        Column(Modifier.padding(12.dp)) {
                                            Text(
                                                text = "🐶 ${dog.name}",
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = "Pickup in ${dog.pickupInMinutes} min",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = Color.Gray
                                            )
                                            Text(
                                                text = "Owner: John Doe",  // 🔥 Replace this with dynamic owner if you have it
                                                style = MaterialTheme.typography.bodySmall,
                                                color = Color.DarkGray
                                            )
                                        }
                                    }
                                }
                            }
                        },
                        confirmButton = {
                            TextButton(onClick = {
                                //val myLocation = userLocation ?: cameraPositionState.position.target
                                val myLocation = userLocation ?: userLocation
                                ?: cameraPositionState.position.target

                                viewModel.clearDogMarkers()
                                availableDogs.value.forEach { dog ->
                                    viewModel.addDogMarker(
                                        "${dog.name} (${dog.pickupInMinutes} min)",
                                        myLocation
                                    )
                                }
                                selectedDogs = availableDogs.value
                                showNearbyDogsDialog = false
                            }) { Text("Pick All") }
                        },
                        dismissButton = {
                            TextButton(onClick = { showNearbyDogsDialog = false }) {
                                Text("Cancel")
                            }
                        }
                    )
                }

                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                )

            }
        }
    }


}


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun OwnerMapScreen() {
    val permissionState = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    if (!permissionState.allPermissionsGranted) {
        Column(
            Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Location permission is required.")
            Button(onClick = { permissionState.launchMultiplePermissionRequest() }) {
                Text("Grant Permission")
            }
        }
        return
    }

    val context = LocalContext.current
    val fusedClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    var userLocation by remember { mutableStateOf<LatLng?>(null) }

    LaunchedEffect(Unit) {
        val lastLocation = fusedClient.lastLocation.await()
        if (lastLocation != null) {
            userLocation = LatLng(lastLocation.latitude, lastLocation.longitude)
        }
    }

    if (userLocation == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(userLocation!!, 16f)
    }

    val walkerLocation = LatLng(userLocation!!.latitude + 0.001, userLocation!!.longitude + 0.001)
    var showInfo by remember { mutableStateOf(false) }

    Box(Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.matchParentSize(),
            cameraPositionState = cameraPositionState,
            uiSettings = MapUiSettings(compassEnabled = true, zoomControlsEnabled = false),
            properties = MapProperties(isMyLocationEnabled = true)
        ) {
            Marker(
                state = MarkerState(position = userLocation!!),
                title = "📍 Dog: Luna (Golden Retriever)",
                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
            )
            Marker(
                state = MarkerState(position = walkerLocation),
                title = "🐾 Walker approaching",
                snippet = "Pickup in 5 min",
                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE),
                onClick = {
                    showInfo = true
                    false
                }
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(12.dp)
                .wrapContentSize()
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(2.dp, Color(0xFF6A1B9A), CircleShape)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo_new),
                    contentDescription = null,
                    modifier = Modifier.size(60.dp),
                    contentScale = ContentScale.Crop
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(Color(0xFF8E24AA), Color(0xFFCE93D8))
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 5.dp)
            ) {
                Text(
                    text = "Pickup in 5 min",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodySmall,
                    letterSpacing = 1.sp
                )
            }
        }

        if (showInfo) {
            Card(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(20.dp),
                elevation = CardDefaults.cardElevation(6.dp)
            ) {
                Column(Modifier.padding(vertical = 16.dp, horizontal = 30.dp)) {
                    Text("Walker Info", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("👤Alex John - ⭐4.9")
                    Spacer(modifier = Modifier.height(2.dp))
                    Text("🐶Single Walk")
                    Spacer(modifier = Modifier.height(3.dp))
                    Text("⏳Walk Time: 45min")
                    Spacer(modifier = Modifier.height(4.dp))
                    Button(onClick = { showInfo = false } , modifier = Modifier.align(Alignment.CenterHorizontally)) {
                        Text("Close")
                    }
                }

            }
        }
    }
}