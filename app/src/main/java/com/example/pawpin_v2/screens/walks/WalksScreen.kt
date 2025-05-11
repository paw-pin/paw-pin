package com.example.pawpin_v2.screens.walks

import com.google.accompanist.flowlayout.FlowRow
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pawpin_v2.screens.requests.WalkRequest
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

data class MockDog(
    val name: String,
    val breed: String,
    val owner: String,
    val distanceKm: Float,
    val requestedMin: Int,
    val actualMin: Int
)

data class MockGroupWalk(
    val date: Date,
    val location: String,
    val dogs: List<MockDog>
)

@Composable
fun WalksScreen(isOwner: Boolean = false) {

    if (isOwner) {


        var selectedTabIndex by remember { mutableStateOf(0) }
        val tabs = listOf("Upcoming", "History")

        Column(modifier = Modifier.fillMaxSize()) {
            TabRow(selectedTabIndex = selectedTabIndex) {
                tabs.forEachIndexed { index, title ->
                    Tab(selected = selectedTabIndex == index, onClick = { selectedTabIndex = index }) {
                        Text(title, modifier = Modifier.padding(16.dp))
                    }
                }
            }

            when (selectedTabIndex) {
                0 -> OwnerUpcomingWalks()
                1 -> OwnerHistoryWalks()
            }
        }


    } else {


    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Upcoming", "History")

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = selectedTabIndex) {
            tabs.forEachIndexed { index, title ->
                Tab(selected = selectedTabIndex == index, onClick = { selectedTabIndex = index }) {
                    Text(title, modifier = Modifier.padding(16.dp))
                }
            }
        }

        when (selectedTabIndex) {
            0 -> UpcomingWalksTab()
            1 -> HistoryWalksTab()
        }
    }
}
}

@Composable
fun UpcomingWalksTab() {
    val now = Calendar.getInstance().time
    val requests = listOf(
        WalkRequest("Emily", 4.8, "Max", "Labrador Retriever", "", Date(now.time + TimeUnit.MINUTES.toMillis(30)), "Central Park, NY"),
        WalkRequest("Sarah", 4.2, "Luna", "Beagle", "", Date(now.time + TimeUnit.MINUTES.toMillis(55)), "Prospect Park, NY"),
        WalkRequest("Olivia", 4.7, "Bella", "Border Collie", "", Date(now.time + TimeUnit.MINUTES.toMillis(75)), "Brooklyn Heights, NY")
    )

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        items(requests) { request ->
            val sex = listOf("Male", "Female").random()
            val size = listOf("Small", "Medium", "Large").random()
            val walkType = listOf("Solo", "Group").random()
            val estimatedDistance = (1..3).random() + listOf(0.0f, 0.5f).random()
            val isBuddy = request.dogName == "Max"
            val weekly = request.dogName == "Luna"

            UpcomingWalkCard(
                request = request,
                isBuddy = isBuddy,
                sex = sex,
                size = size,
                walkType = walkType,
                weekly = weekly,
                estimatedDistance = estimatedDistance
            )
        }
    }
}

@Composable
fun UpcomingWalkCard(request: WalkRequest, isBuddy: Boolean, sex: String, size: String, walkType: String, weekly: Boolean, estimatedDistance: Float) {
    val timeRemaining = ((request.date.time - System.currentTimeMillis()) / 60000).coerceAtLeast(0).toInt()
    val cardColor = when {
        isBuddy -> Color(0xFFE3F2FD)
        weekly -> Color(0xFFF3E5F5)
        else -> Color.White
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Face, contentDescription = "Dog", tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text("${request.walkerName} (${request.dogName})", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text("📍 ${request.location}")
                    Text("🕒 In $timeRemaining min", color = MaterialTheme.colorScheme.primary)
                    Text("📏 Est. Distance: %.1f km".format(estimatedDistance))
                    if (isBuddy) Text("🐾 Already your buddy!", color = Color.Blue, fontWeight = FontWeight.SemiBold)
                    else if (weekly) Text("📆 Weekly Appointment", color = Color(0xFF6A1B9A), fontWeight = FontWeight.SemiBold)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Divider()
            Spacer(modifier = Modifier.height(12.dp))
            FlowRow(mainAxisSpacing = 8.dp, crossAxisSpacing = 8.dp) {
                Chip(label = "🐶 Breed: ${request.dogBreed}", color = Color(0xFFFAFAFA))
                Chip(label = "🚻 Sex: $sex", color = Color(0xFFFAFAFA))
                Chip(label = "📏 Breed Size: $size", color = Color(0xFFFAFAFA))
                Chip(label = "👣 Walk Type: $walkType", color = Color(0xFFFAFAFA))
            }
        }
    }
}

@Composable
fun Chip(label: String, color: Color) {
    Box(
        modifier = Modifier
            .background(color = color, shape = MaterialTheme.shapes.small)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(label, fontSize = 13.sp)
    }
}

@Composable
fun HistoryWalksTab() {
    val mockWalks = listOf(
        MockGroupWalk(
            date = Calendar.getInstance().apply { add(Calendar.HOUR_OF_DAY, -2) }.time,
            location = "Central Park, NY",
            dogs = listOf(
                MockDog("Max", "Labrador", "Emily", 2.2f, 30, 32),
                MockDog("Bella", "Beagle", "Sarah", 2.2f, 30, 30)
            )
        ),
        MockGroupWalk(
            date = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }.time,
            location = "Prospect Park, NY",
            dogs = listOf(
                MockDog("Luna", "Poodle", "Olivia", 1.5f, 20, 18)
            )
        ),
        MockGroupWalk(
            date = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -2) }.time,
            location = "Riverside Park, NY",
            dogs = listOf(
                MockDog("Charlie", "Golden Retriever", "James", 3.0f, 45, 42),
                MockDog("Daisy", "Boxer", "Anna", 3.0f, 45, 46),
                MockDog("Rocky", "Corgi", "Mike", 3.0f, 45, 44)
            )
        )
    ).sortedByDescending { it.date }

    LazyColumn(modifier = Modifier.fillMaxSize().padding(8.dp)) {
        items(mockWalks) { walk ->
            Column(modifier = Modifier.padding(12.dp)) {
                HistoryGroupWalk(walk)
                Spacer(modifier = Modifier.height(12.dp))
                Divider()
            }
        }
    }
}

@Composable
fun HistoryGroupWalk(walk: MockGroupWalk) {
    val dateFormat = remember { SimpleDateFormat("EEE, MMM d, yyyy h:mm a", Locale.getDefault()) }
    val dateStr = dateFormat.format(walk.date)
    val isSolo = walk.dogs.size == 1

    Text(text = dateStr, style = MaterialTheme.typography.titleMedium)
    Spacer(modifier = Modifier.height(6.dp))
    Text("📍 Location: ${walk.location}", style = MaterialTheme.typography.bodyMedium)
    Spacer(modifier = Modifier.height(4.dp))
    Text("👣 Walk Type: ${if (isSolo) "Solo" else "Group"}", style = MaterialTheme.typography.bodyMedium)

    walk.dogs.forEach { dog ->
        Spacer(modifier = Modifier.height(12.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(Modifier.padding(12.dp)) {
                Text("🐶 ${dog.name} (${dog.breed})", fontWeight = FontWeight.SemiBold)
                Text("👤 Owner: ${dog.owner}")
                Text("📏 Distance: %.2f km".format(dog.distanceKm))
                Text("⏳ Requested: ${dog.requestedMin} min")
                Text("✅ Actual: ${dog.actualMin} min")
            }
        }
    }

}

@Composable
fun OwnerUpcomingWalks() {
    val now = System.currentTimeMillis()
    val today = Calendar.getInstance().apply { timeInMillis = now }.get(Calendar.DAY_OF_YEAR)
    val urgentWalk = WalkRequest("Urgent Dan", 4.5, "Luna", "Golden Retriever", SimpleDateFormat("h:mm a").format(Date(now + 20 * 60000)), Date(now + 20 * 60000), "Union Square")
    val laterWalks = listOf(
        WalkRequest("Alex Walker", 4.9, "Luna", "Golden Retriever", "3:00 PM", Date(now + 86400000), "Central Park"),
        WalkRequest("Sophie Steps", 4.7, "Luna", "Golden Retriever", "11:00 AM", Date(now + 2 * 86400000), "Prospect Park")
    )
    val mockUpcoming = listOf(urgentWalk) + laterWalks

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        items(mockUpcoming) { walk ->
            val minutesUntil = ((walk.date.time - now) / 60000).toInt()
            val isUrgent = minutesUntil <= 30
            val walkDay = Calendar.getInstance().apply { time = walk.date }.get(Calendar.DAY_OF_YEAR)
            val canCancel = walkDay != today
            val isWeekly = walk.walkerName == "Alex Walker"

            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                elevation = CardDefaults.cardElevation(4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("🦮 ${walk.walkerName} (${walk.dogName})", fontWeight = FontWeight.Bold, fontSize = 17.sp)

                    Text(
                        "⭐ ${walk.walkerRating} • ${SimpleDateFormat("EEE, MMM d").format(walk.date)} at ${walk.time} • ${walk.location}",
                        fontSize = 13.sp,
                        color = Color.DarkGray
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 6.dp)) {
                        if (isUrgent) {
                            Text(
                                "⏳ Pickup in $minutesUntil min",
                                color = Color.Red,
                                fontSize = 12.sp,
                                modifier = Modifier
                                    .background(Color(0xFFFFCDD2), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                        if (isWeekly) {
                            Text(
                                "📆 Weekly • 3x",
                                color = Color(0xFF6A1B9A),
                                fontSize = 12.sp,
                                modifier = Modifier
                                    .background(Color(0xFFF3E5F5), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    FlowRow(mainAxisSpacing = 8.dp, crossAxisSpacing = 6.dp) {
//                        Text("🐶 ${walk.dogBreed}", fontSize = 12.sp, modifier = Modifier
//                            .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
//                            .padding(horizontal = 10.dp, vertical = 4.dp))
                        Text("👣 Solo Walk", fontSize = 12.sp, modifier = Modifier
                            .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp))
                        Text("⏳ Requested: 45 min", fontSize = 12.sp, modifier = Modifier
                            .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp))
                        Text("✅ Estimated: 42 min", fontSize = 12.sp, modifier = Modifier
                            .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp))
                        Text("📏 2.4 km", fontSize = 12.sp, modifier = Modifier
                            .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp))
                    }

                    Spacer(Modifier.height(12.dp))

                    if (canCancel) {
                        Button(
                            onClick = { /* TODO: cancel logic */ },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                            modifier = Modifier.align(Alignment.Start)
                        ) {
                            Text("Cancel Walk", color = Color.White)
                        }
                    }

                }
            }
        }
    }
}

@Composable
fun OwnerHistoryWalks() {
    val mockHistory = listOf(
        WalkRequest("Chris Canine", 4.6, "Luna", "Golden Retriever", "2:00 PM", Date(System.currentTimeMillis() - 86400000 * 2), "Liberty Trail"),
        WalkRequest("Taylor Paws", 4.8, "Luna", "Golden Retriever", "10:30 AM", Date(System.currentTimeMillis() - 86400000 * 3), "Liberty Trail")
    )

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        items(mockHistory) { walk ->
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                elevation = CardDefaults.cardElevation(2.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("✅ ${walk.walkerName} (${walk.dogName})", fontWeight = FontWeight.Bold, fontSize = 17.sp)

                    Text(
                        "⭐ ${walk.walkerRating} • ${SimpleDateFormat("EEE, MMM d").format(walk.date)} at ${walk.time} • ${walk.location}",
                        fontSize = 13.sp,
                        color = Color.DarkGray
                    )

                    Spacer(Modifier.height(8.dp))

                    FlowRow(mainAxisSpacing = 8.dp, crossAxisSpacing = 6.dp) {
                        Text("🐶 ${walk.dogBreed}", fontSize = 12.sp, modifier = Modifier
                            .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp))
                        Text("👣 Solo Walk", fontSize = 12.sp, modifier = Modifier
                            .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp))
                        Text("⏳ Requested: ${walk.time}", fontSize = 12.sp, modifier = Modifier
                            .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp))
                        Text("✅ Completed: 48 min", fontSize = 12.sp, modifier = Modifier
                            .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp))
                        Text("📏 2.4 km", fontSize = 12.sp, modifier = Modifier
                            .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp))
                    }

                    Spacer(Modifier.height(12.dp))

                    Text("📝 Summary:", fontWeight = FontWeight.SemiBold)
                    Text("Walk completed successfully. Your dog was happy and playful.")
                }
            }
        }
    }
}
