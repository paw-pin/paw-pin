package com.example.pawpin_v2.screens.requests

import android.app.TimePickerDialog
import androidx.compose.foundation.Image
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.Icons
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pawpin_v2.R
import java.text.SimpleDateFormat
import java.util.*

import android.app.DatePickerDialog
import androidx.compose.foundation.border
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.ui.draw.clip


data class WalkRequest(
    val walkerName: String,
    val walkerRating: Double,
    val dogName: String,
    val dogBreed: String,
    val time: String,
    val date: Date,
    val location: String
)

@Composable
fun RequestListTab() {
    val requests = listOf(
        WalkRequest("Emily", 4.8, "Max", "Labrador Retriever", "2:00 PM", Date(2025 - 1900, 4, 10), "Central Park, NY"),
        WalkRequest("Sarah", 4.2, "Luna", "Beagle", "11:00 AM", Date(2025 - 1900, 4, 11), "Prospect Park, NY"),
        WalkRequest("Olivia", 4.7, "Bella", "Border Collie", "3:30 PM", Date(2025 - 1900, 4, 12), "Brooklyn Heights, NY")
    ).sortedBy { it.date }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        items(requests) { request ->
            val size = listOf("Small", "Medium", "Large").random()
            val walkType = listOf("Solo", "Group").random()
            val weekly = request.dogName == "Bella"
            val buddy = request.dogName == "Max"

            RequestCard(request, size, walkType, weekly, buddy)
        }
    }
}


@Composable
fun RequestsScreen(isOwner: Boolean = false) {

    if (isOwner) {
        var selectedTabIndex by remember { mutableStateOf(0) }
        val tabs = listOf("Offers", "Smart Picks", "Schedule")

        Scaffold(
            containerColor = Color.White,
            topBar = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(top = 3.dp, bottom = 3.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                }
            }
        ) { innerPadding ->
            Column(modifier = Modifier.padding(innerPadding)) {
                TabRow(selectedTabIndex = selectedTabIndex) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index }) {
                            Text(title, modifier = Modifier.padding(16.dp))
                        }
                    }
                }

                when (selectedTabIndex) {
                    0 -> StandardRequestTab()
                    1 -> PremiumRequestTab()
                    2 -> CreateRequestTab()
                }
            }
        }
    } else {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("My Requests", "Premium AI Match")

    Scaffold(
        containerColor = Color.White,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(top = 3.dp, bottom = 3.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
//                Image(
//                    painter = painterResource(id = R.drawable.logo_new),
//                    contentDescription = "Logo",
//                    modifier = Modifier
//                        .height(70.dp)
//                        .padding(5.dp),
//                    contentScale = ContentScale.Fit
//                )
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            TabRow(selectedTabIndex = selectedTabIndex) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index }) {
                        Text(title, modifier = Modifier.padding(16.dp))
                    }
                }
            }

            when (selectedTabIndex) {
                0 -> RequestListTab()
                1 -> PremiumAiMatchTab()
            }
        }
    }
}
}

@Composable
fun PremiumAiMatchTab() {
    val suggestions = listOf(
        WalkRequest("Emma", 4.9, "Cooper", "Golden Retriever", "10:00 AM", Date(2025 - 1900, 4, 15), "Greenwood Park"),
        WalkRequest("Liam", 4.8, "Daisy", "Cocker Spaniel", "2:30 PM", Date(2025 - 1900, 4, 16), "Liberty Lake"),
        WalkRequest("Mia", 4.7, "Rocky", "Shih Tzu", "5:00 PM", Date(2025 - 1900, 4, 17), "Harbor Trail")
    )

    val weeklyMap = mapOf("Cooper" to false, "Daisy" to true, "Rocky" to false)

    // UI filter state
    var startDate by remember { mutableStateOf("Apr 15, 2025") }
    var endDate by remember { mutableStateOf("Apr 17, 2025") }
    var startTime by remember { mutableStateOf("08:00") }
    var endTime by remember { mutableStateOf("18:00") }
    var sortOption by remember { mutableStateOf("Rating (High to Low)") }

    // 🔓 Premium Label
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
            .background(Color(0xFF4A148C), shape = MaterialTheme.shapes.medium)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = "🔓 Premium Feature",
            color = Color.White,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold
        )
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            "Personalized Matches",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4A148C),
                fontSize = MaterialTheme.typography.titleLarge.fontSize
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 🔍 Mock Filters UI
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(value = startDate, onValueChange = { startDate = it }, label = { Text("Start Date") }, modifier = Modifier.weight(1f))
            OutlinedTextField(value = endDate, onValueChange = { endDate = it }, label = { Text("End Date") }, modifier = Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(value = startTime, onValueChange = { startTime = it }, label = { Text("Start Time") }, modifier = Modifier.weight(1f))
            OutlinedTextField(value = endTime, onValueChange = { endTime = it }, label = { Text("End Time") }, modifier = Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(8.dp))

        // 🔃 Mock Sort Dropdown
        var expanded by remember { mutableStateOf(false) }
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
                Text(sortOption)
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                DropdownMenuItem(text = { Text("Rating (High to Low)") }, onClick = {
                    sortOption = "Rating (High to Low)"
                    expanded = false
                })
                DropdownMenuItem(text = { Text("Time (Soonest)") }, onClick = {
                    sortOption = "Time (Soonest)"
                    expanded = false
                })
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(suggestions) { suggestion ->
                RequestCard(
                    request = suggestion,
                    size = listOf("Small", "Medium").random(),
                    walkType = listOf("Solo", "Group").random(),
                    weekly = weeklyMap[suggestion.dogName] ?: false,
                    isBuddy = false
                )
            }
        }
    }
}




@Composable
fun MessageDialog(
    onDismiss: () -> Unit,
    dogName: String
) {
    var message by remember { mutableStateOf("") }

    // Predefined questions (as checkboxes)
    val predefinedQuestions = listOf(
        "Does $dogName get along with other dogs?",
        "Any allergies I should know about?",
        "Can I give treats during walks?"
    )
    val checkedStates = remember { mutableStateMapOf<String, Boolean>() }

    // Initialize all as unchecked
    predefinedQuestions.forEach { question ->
        if (question !in checkedStates) {
            checkedStates[question] = false
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                // You can collect and log/send this if needed
                val selectedQuestions = checkedStates.filterValues { it }.keys
                val finalMessage = buildString {
                    if (selectedQuestions.isNotEmpty()) {
                        appendLine("Questions:")
                        selectedQuestions.forEach { appendLine("• $it") }
                        appendLine()
                    }
                    if (message.isNotBlank()) {
                        appendLine("Custom Message:")
                        appendLine(message)
                    }
                }
                println("Sending:\n$finalMessage") // Or handle submission
                onDismiss()
            }) {
                Text("Send")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        title = { Text("Message Owner of $dogName") },
        text = {
            Column {
                Text("Select questions to ask:")
                Spacer(modifier = Modifier.height(8.dp))
                predefinedQuestions.forEach { question ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    ) {
                        Checkbox(
                            checked = checkedStates[question] == true,
                            onCheckedChange = { checkedStates[question] = it }
                        )
                        Text(text = question, modifier = Modifier.padding(start = 8.dp))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text("Write an additional message:")
                OutlinedTextField(
                    value = message,
                    onValueChange = { message = it },
                    label = { Text("Write your message...") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    )
}



@Composable
fun RequestCard(
    request: WalkRequest,
    size: String,
    walkType: String,
    weekly: Boolean,
    isBuddy: Boolean
) {
    var showMessageDialog by remember { mutableStateOf(false) } // ✅ MUST be here

    val cardColor = when {
        isBuddy -> Color(0xFFE3F2FD)
        weekly -> Color(0xFFF3E5F5)
        else -> Color.White
    }

    Spacer(modifier = Modifier.height(8.dp))

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Face,
                    contentDescription = "Dog",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text("${request.walkerName} (${String.format("%.1f★", request.walkerRating)})", style = MaterialTheme.typography.titleMedium)
                    Text("${request.dogName} (${request.dogBreed})", style = MaterialTheme.typography.bodyMedium)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text("🕒${request.location}", style = MaterialTheme.typography.bodySmall)
            //Text("📅 ${SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(request.date)}", style = MaterialTheme.typography.bodySmall)

            val formatter = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
            val isGroupWalk = walkType == "Group"

            Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                Text(
                    "📅 ${formatter.format(request.date)} at ${request.time}",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
//                Text(
//                    "📍 Location: ${request.location}",
//                    style = MaterialTheme.typography.bodyMedium,
//                    modifier = Modifier.padding(top = 4.dp)
//                )
                Text(
                    "Estimated Distance: ${listOf(0.9, 1.4, 2.1).random()} km • Duration: ${listOf(20, 30, 45).random()} mins",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.DarkGray
                )
            }

            Divider(modifier = Modifier.padding(vertical = 8.dp))

//            Column(modifier = Modifier.fillMaxWidth()) {
//                Text("🐶 Dog Details:", fontWeight = FontWeight.SemiBold)
//                Text("• Breed: ${request.dogBreed}")
//                Text("• Size: $size")
//                Text("• Preferred Walk: $walkType")
//            }

            val aiInsight = when (request.dogName) {
                "Cooper" -> "Cooper is calm and obedient, ideal for group walks. This time slot aligns well with your 10AM park route — efficient and compatible."
                "Daisy" -> "Daisy thrives on mid-distance walks and is very social. This match works great with your 2PM group walks and ensures easy pairing."
                "Rocky" -> "Rocky prefers quieter walks and is perfect for your solo afternoon slots. His pace and size are a natural fit for your recent preferences."
                else -> null
            }

            aiInsight?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF1F8E9), shape = MaterialTheme.shapes.medium)
                        .padding(12.dp)
                ) {
                    Text(
                        "🧠 Smart AI Insight",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF558B2F),
                        fontSize = MaterialTheme.typography.labelLarge.fontSize
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        it,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF33691E)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "🐾 Powered by behavior patterns, location proximity & walk success scores.",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                }
            }



            if (weekly) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "📆 This walk is part of a weekly schedule.",
                    color = Color(0xFF6A1B9A),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold
                )
            }

            if (isGroupWalk) {
                Spacer(modifier = Modifier.height(6.dp))
//                Text(
//                    text = "✨ Great fit! This can be grouped with your existing walk at ${request.time}.",
//                    color = Color(0xFF2E7D32),
//                    style = MaterialTheme.typography.bodySmall,
//                    fontWeight = FontWeight.Medium
//                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("📏 Size: $size", style = MaterialTheme.typography.bodySmall)
                Text("👣 Walk: $walkType", style = MaterialTheme.typography.bodySmall)
            }

//            if (weekly) {
//                Text("📆 Weekly Walk Request", color = Color(0xFF6A1B9A), fontWeight = FontWeight.SemiBold)
//            }
            if (isBuddy) {
                Text("🐾 Already a Buddy", color = Color(0xFF1565C0), fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {

                var isEnabled = true;
                if (request.dogName == "Cooper" || request.dogName == "Daisy" || request.dogName == "Rocky" ) {
                    isEnabled = false;
                }
                Button(
                    onClick = { /* Accept action */ },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ,enabled = isEnabled
                ) {
                    Text("Accept")
                }
                Button(
                    onClick = { /* Deny action */ },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ,enabled = isEnabled
                ) {
                    Text("Deny")
                }
                // ✅ New Message button with ❓ icon
                Button(
                    onClick = { showMessageDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xE94CAF50))
                    ,enabled = isEnabled
                ) {
                    Icon(
                        imageVector = Icons.Default.HelpOutline,
                        contentDescription = "Message Owner",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Message",
                        color = Color.White,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))



            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                //var showMessageDialog by remember { mutableStateOf(false) }

                if (showMessageDialog) {
                    MessageDialog(
                        onDismiss = { showMessageDialog = false },
                        dogName = request.dogName
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))


            }
        }
    }
}


@Composable
fun AcceptedWalkCard(walk: WalkRequest) {

    var showMessageDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFE8F5E9)  // soft green background for accepted
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Face,
                    contentDescription = "Dog",
                    tint = Color(0xFF4CAF50), // Green
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "${walk.dogName} (${walk.dogBreed})",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = { /* Accept action */ },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Accept")
                }
                Button(
                    onClick = { /* Deny action */ },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Deny")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

// "Message Owner" button
            Spacer(modifier = Modifier.height(8.dp))

// "Message Owner" button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = { showMessageDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A1B9A))
                ) {
                    Text("Message Owner")
                }
            }

// ✅ Dialog must be outside of Row/Column
            if (showMessageDialog) {
                MessageDialog(
                    onDismiss = { showMessageDialog = false },
                    dogName = walk.dogName
                )
            }



            Text("Date: ${SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(walk.date)}", style = MaterialTheme.typography.bodySmall)
            Text("Time: ${walk.time}", style = MaterialTheme.typography.bodySmall)
            Text("Location: ${walk.location}", style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun StandardRequestTab() {
    val format = SimpleDateFormat("MMM d, yyyy h:mm a", Locale.getDefault())
    val requests = listOf(
        WalkRequest("Alex", 4.5, "Luna", "Golden Retriever", "2:00 PM", Date(), "Central Park"),
        WalkRequest("Sam", 4.3, "Luna", "Golden Retriever", "11:00 AM", Date(), "Prospect Park")
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(requests) { request ->
            val hasPastWalks = request.walkerName == "Alex"
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "👤 ${request.walkerName}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "⭐ ${request.walkerRating}",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text("📍 ${request.location}")
                    Text("🕒 ${request.time}")
                    Text("📅 ${format.format(request.date)}")
                    Text("🐶 ${request.dogName} (${request.dogBreed})")
                    Text("⏱️ 30 min  •  👣 Solo Walk")

                    if (request.walkerName == "Alex") {
                        Box(
                            modifier = Modifier
                                .padding(vertical = 4.dp)
                                .background(Color(0xFFE1BEE7), shape = RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "📆 Weekly Request • 3x/week",
                                color = Color(0xFF6A1B9A),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    if (hasPastWalks) {
                        Box(
                            modifier = Modifier
                                .padding(top = 4.dp, bottom = 4.dp)
                                .background(Color(0xFFDCEDC8), shape = RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Already Walked Luna",
                                color = Color(0xFF33691E),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { /* Accept logic */ },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5E35B1))
                    ) {
                        Text("Accept Walk", color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun PremiumRequestTab() {
    val format = SimpleDateFormat("MMM d, yyyy h:mm a", Locale.getDefault())
    val requests = listOf(
        WalkRequest("Jamie", 4.9, "Luna", "Golden Retriever", "10:00 AM", Date(), "Hudson Trail"),
        WalkRequest("Taylor", 5.0, "Luna", "Golden Retriever", "4:00 PM", Date(), "Liberty Field")
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(requests) { request ->
            val hasPastWalks = request.walkerName == "Jamie"
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE9DCEB)),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "✨ ${request.walkerName}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "⭐ ${request.walkerRating}",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text("📍 ${request.location}")
                    Text("🕒 ${request.time}")
                    Text("📅 ${format.format(request.date)}")
                    Text("🐶 ${request.dogName} (${request.dogBreed})")
                    Text("⏱️ 45 min  •  👣 Group Walk")
                    Spacer(modifier = Modifier.height(6.dp))


                    if (request.walkerName == "Jamie") {
                        Box(
                            modifier = Modifier
                                .padding(vertical = 4.dp)
                                .background(Color(0xFFE1BEE7), shape = RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "📆 Weekly Request • 3x/week",
                                color = Color(0xFF6A1B9A),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    if (hasPastWalks) {
                        Box(
                            modifier = Modifier
                                .padding(top = 4.dp, bottom = 4.dp)
                                .background(Color(0xFFDCEDC8), shape = RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Already Walked Luna",
                                color = Color(0xFF33691E),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }



                    Spacer(modifier = Modifier.height(8.dp))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFAF97B2), shape = RoundedCornerShape(10.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            "🔓 Premium AI Insight",
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF9C27B0),
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        val insight = if (request.walkerName.contains("Jamie")) {
                            "Jamie is ideal for calm morning group walks. Luna tends to behave best between 10–11AM, and this walk aligns with her historical preferences and pace."
                        } else {
                            "Taylor adjusts walk intensity in real time. Afternoon walks offer Luna structured exercise with recovery, ensuring balance between energy use and focus."
                        }
                        Text(insight, fontSize = 13.sp)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { /* Accept logic */ },
                        modifier = Modifier.fillMaxWidth(), enabled = false,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9C27B0))
                    ) {
                        Text("Accept Premium Walk", color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun CreateRequestTab() {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val dateFormat = SimpleDateFormat("EEE, MMM d, yyyy", Locale.getDefault())
    val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())

    var time by remember { mutableStateOf(timeFormat.format(calendar.time)) }
    var duration by remember { mutableFloatStateOf(30f) }
    var useKm by remember { mutableStateOf(false) }
    var walkType by remember { mutableStateOf("Solo") }
    var location by remember { mutableStateOf("Central Park") }
    var date by remember { mutableStateOf(calendar.time) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var asap by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Box(
                modifier = Modifier
                    .size(84.dp)
                    .background(Color.White, shape = CircleShape)
                    .border(3.dp, Color(0xFF9C27B0), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo_new),
                    contentDescription = "PawPin Logo",
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                )
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            //Text("📅 Date", fontWeight = FontWeight.Medium)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePicker = true }
            ) {
                Icon(Icons.Default.CalendarToday, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(8.dp))
                Text(dateFormat.format(date), fontSize = 16.sp)
            }

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(onClick = { showTimePicker = true }) {
                    Text("Select Time: $time")
                }
                Spacer(Modifier.width(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = asap, onCheckedChange = { asap = it })
                    Text("ASAP", fontSize = 12.sp)
                }
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("📍 Pickup Location", fontWeight = FontWeight.Medium)
            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                placeholder = { Text("Enter address or park") }
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("⏱️ Duration Type", fontWeight = FontWeight.Medium)
            Row(verticalAlignment = Alignment.CenterVertically) {
                FilterChip(selected = !useKm, onClick = { useKm = false }, label = { Text("Minutes") })
                Spacer(Modifier.width(8.dp))
                FilterChip(selected = useKm, onClick = { useKm = true }, label = { Text("Kilometers") })
            }
            Text(if (useKm) "Distance: ${duration.toInt()} km" else "Duration: ${duration.toInt()} min")
            Slider(
                value = duration,
                onValueChange = { duration = it },
                valueRange = if (useKm) 0.5f..5f else 10f..90f,
                steps = if (useKm) 9 else 8,
                modifier = Modifier.fillMaxWidth(0.92f)
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("👣 Walk Type", fontWeight = FontWeight.Medium)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                listOf("Solo", "Group", "Both").forEach { option ->
                    AssistChip(
                        onClick = { walkType = option },
                        label = { Text(option) },
                        colors = if (walkType == option)
                            AssistChipDefaults.assistChipColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                        else AssistChipDefaults.assistChipColors()
                    )
                }
            }
        }

        //Spacer(modifier = Modifier.height(8.dp))

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {

                //Spacer(Modifier.width(6.dp))
                Text("\uD83D\uDD01 Weekly Schedule", fontWeight = FontWeight.Medium)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                listOf("M", "T", "W", "T", "F", "S", "S").forEach { day ->
                    var selected by remember { mutableStateOf(false) }
                    AssistChip(
                        onClick = { selected = !selected },
                        label = { Text(day) },
                        colors = if (selected)
                            AssistChipDefaults.assistChipColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                        else AssistChipDefaults.assistChipColors()
                    )
                }
            }
            //Text("Choose days you'd like this request to repeat weekly.", fontSize = 12.sp, color = Color.Gray)
        }

        Button(
            onClick = { /* TODO: Submit logic */ },
            modifier = Modifier.align(Alignment.End),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text("Submit Request", color = Color.White)
        }
    }

    LaunchedEffect(showDatePicker) {
        if (showDatePicker) {
            val current = Calendar.getInstance()
            DatePickerDialog(
                context,
                { _, year, month, dayOfMonth ->
                    calendar.set(year, month, dayOfMonth)
                    date = calendar.time
                    showDatePicker = false
                },
                current.get(Calendar.YEAR),
                current.get(Calendar.MONTH),
                current.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    LaunchedEffect(showTimePicker) {
        if (showTimePicker) {
            val now = Calendar.getInstance()
            TimePickerDialog(
                context,
                { _, hourOfDay, minute ->
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    calendar.set(Calendar.MINUTE, minute)
                    time = timeFormat.format(calendar.time)
                    showTimePicker = false
                },
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                false
            ).show()
        }
    }
}
