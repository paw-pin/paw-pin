package com.example.pawpin_v2.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items

import androidx.compose.foundation.Image
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pawpin_v2.R
//import com.example.pawpin_v2.screens.ratings.RatingItem
import kotlin.random.Random

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp


data class RatingItem(
    val userName: String,
    val dogBreed: String,
    val ratingStars: String,        // Their rating for you
    val comment: String,            // Their comment
    val ratedBackStars: String?,    // Your rating back to them (null = not rated yet)
    val ratedBackComment: String?   // Your optional comment back to them
)

@Composable
fun CombinedProfileScreen(isOwner: Boolean = false) {

    if (isOwner) {
        var selectedTabIndex by remember { mutableStateOf(0) }
        val tabs = listOf("General", "Inbox", "Friends")


        Column(modifier = Modifier.fillMaxSize()) {
            TabRow(selectedTabIndex = selectedTabIndex) {
                tabs.forEachIndexed { index, title ->
                    Tab(selected = selectedTabIndex == index, onClick = { selectedTabIndex = index }) {
                        Text(title, modifier = Modifier.padding(16.dp))
                    }
                }
            }
            when (selectedTabIndex) {
                0 -> OwnerHeader()
                1 -> OwnerMessagesTab()
                2 -> OwnerFriendsTab()
            }
        }
    } else {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("General", "Ratings", "Inbox", "Friends")


    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = selectedTabIndex) {
            tabs.forEachIndexed { index, title ->
                Tab(selected = selectedTabIndex == index, onClick = { selectedTabIndex = index }) {
                    Text(title, modifier = Modifier.padding(16.dp))
                }
            }
        }
        when (selectedTabIndex) {
            0 -> WalkerProfileTab()
            1 -> RatingsTab()
            2 -> MessagesTab()
            3 -> FriendsTab()
        }
    }
}
}

data class Message(
    val from: String,
    val dogName: String,
    val text: String,
    val isRead: Boolean,
    val reply: String? = null
)

@Composable
fun MessagesTab() {
    val messages = remember {
        listOf(
            Message("Alice", "Luna", "Hey, thank you for the great walk today! 🐶", isRead = true, reply = "You're very welcome! Luna was great."),
            Message("Bob", "Rex", "Hi, Rex seemed really tired after the walk, was everything okay?", isRead = true, reply = null),
            Message("Charlie", "Poppy", "Just wanted to say thanks!", isRead = false),
            Message("David", "Max", "Did Max enjoy his group walk today?", isRead = false)
        )
    }

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        items(messages) { msg ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = if (!msg.isRead) CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)) else CardDefaults.cardColors()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("📨 From: ${msg.from} (${msg.dogName})", fontWeight = FontWeight.Bold)
                    Text(msg.text, modifier = Modifier.padding(top = 4.dp))

                    msg.reply?.let {
                        Spacer(modifier = Modifier.height(8.dp))
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFD0F0C0))
                                .padding(12.dp)
                        ) {
                            Text("💬 Your reply:", fontWeight = FontWeight.Medium)
                            Text(it)
                        }
                    }

                    if (msg.isRead && msg.reply == null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "✅ Read • No reply",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray
                        )
                    } else if (!msg.isRead) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "🔴 Unread",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Red,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun WalkerProfileTab() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val context = LocalContext.current

        Box(
            modifier = Modifier.size(180.dp),
            contentAlignment = Alignment.Center
        ) {
            // Profile image
            Image(
                painter = painterResource(id = R.drawable.logo_new),
                contentDescription = "Company Logo",
                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape)
                    .border(width = 2.dp, color = Color.LightGray, shape = CircleShape)
            )

            // Verified badge with icon and toast
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = 6.dp, y = 6.dp)
                    .clickable {
                        Toast
                            .makeText(context, "Verified by liveness test", Toast.LENGTH_LONG)
                            .show()
                    }
                    .background(Color(0xFF777777), shape = RoundedCornerShape(10.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.VerifiedUser,
                        contentDescription = "Verified Icon",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Premium",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }



        Spacer(Modifier.height(12.dp))
        Text("John Doe", fontWeight = FontWeight.Bold, fontSize = 22.sp)
        Text("john.doe@example.com", fontSize = 14.sp)

        Spacer(Modifier.height(24.dp))
        Divider(thickness = 1.dp)
        Spacer(Modifier.height(24.dp))

        ProfileInfoItem("Member Since", "📅 January 2023")
        ProfileInfoItem("Walks Completed", "👟 128")
        ProfileInfoItem("Average Rating", "⭐ 4.8")
        EditableProfileInfoItem("Preferred Dog Breeds", "Labrador, Beagle, Poodle")
        EditableProfileInfoItem("Preferred Breed Sizes", "Medium")
        EditableProfileInfoItem("Preferred Walk Type", "Group Walks")
        EditableProfileInfoItem("Recurring Walks with Clients", "Yes")

        Spacer(Modifier.height(32.dp))
        Button(
            onClick = {},
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Text("Logout", color = Color.White)
        }
    }
}

@Composable
fun RatingsTab() {
    val ratings = remember {
        listOf(
            RatingItem("Alice", "Labrador Retriever", "★★★★☆", "Very professional and friendly!", "★★★☆☆", "Very playful and friendly dog!"),
            RatingItem("Bob", "German Shepherd", "★★★★★", "Took great care of Max!", null, null),
            RatingItem("Charlie", "Poodle", "★★★☆☆", "Good, but arrived 10 minutes late.", "★★☆☆☆", "Could be more punctual."),
            RatingItem("David", "Beagle", "★★★★☆", "Wonderful and caring walker.", null, null),
            RatingItem("Eve", "Golden Retriever", "★★★★★", "Couldn't have asked for better!", "★★★★★", "Super well-behaved during the walk."),
            RatingItem("Frank", "Chihuahua", "★★★☆☆", "Friendly but forgot the leash.", null, null),
        )
    }
    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        items(ratings.size) { index ->
            val rating = ratings[index]
            val groupWalk = if (index % 2 == 0) "🐕 Group Walk" else "👤 Single Walk"
            val expectedTime = 30 + index * 2
            val actualTime = expectedTime + Random.nextInt(-2, 3)
            Card(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("👤 ${rating.userName} (${rating.dogBreed})", fontWeight = FontWeight.Bold)
                    Text("Booked: ${expectedTime} min | Walked: ${actualTime} min | $groupWalk")
                    Text(rating.ratingStars)
                    Text(rating.comment)
                    Spacer(modifier = Modifier.height(8.dp))
                    if (rating.ratedBackStars != null) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFD0F0C0))
                                .padding(12.dp)
                        ) {
                            Text("📝 You rated back: ${rating.ratedBackStars}", fontWeight = FontWeight.Medium)
                            rating.ratedBackComment?.let {
                                Text("\"$it\"", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFFFF59D))
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                                .clickable { /* Show popup */ },
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🖊️ Rate Back", color = Color.Black, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileInfoItem(label: String, value: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(text = label, style = MaterialTheme.typography.labelMedium, color = Color.Gray)
        Text(text = value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
    }
}

data class Friend(
    val name: String,
    val dogName: String,
    val rating: Float,
    val phone: String
)

@Composable
fun FriendsTab() {
    val friends = remember {
        listOf(
            Friend("Alice Smith", "Luna", 4.8f, "+123456789"),
            Friend("Bob Johnson", "Rex", 4.6f, "+198765432"),
            Friend("Charlie White", "Bella", 5.0f, "+102938475"),
        )
    }


    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        items(friends) { friend ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("${friend.name} & 🐶 ${friend.dogName}", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(Modifier.height(4.dp))
                    Text("⭐ ${friend.rating}", color = Color.Gray)

                    Spacer(Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {

                        Button(
                            onClick = { /* simulate text */ },
                            modifier = Modifier
                                .weight(1f)
                                .height(36.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFA5D6A7)),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(Icons.Default.Chat, contentDescription = "Text", tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Text", color = Color.White, fontSize = 12.sp)
                        }

                        Button(
                            onClick = { /* navigate to profile */ },
                            modifier = Modifier
                                .weight(1f)
                                .height(36.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFCE93D8)),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(Icons.Default.Person, contentDescription = "Profile", tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Profile", color = Color.White, fontSize = 12.sp)
                        }
                    }

                }
            }
        }
    }
}

@Composable
fun EditableProfileInfoItem(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = label, style = MaterialTheme.typography.labelMedium, color = Color.Gray)
            Text(text = value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
        }
        IconButton(onClick = { /* TODO: Edit functionality */ }) {
            Icon(Icons.Default.Edit, contentDescription = "Edit")
        }
    }
}


@Composable
fun OwnerMessagesTab() {
    data class Message(val from: String, val dog: String, val text: String, val isRead: Boolean, val reply: String?)

    val messages = listOf(
        Message("Alex Walker", "Luna", "Thanks for confirming the walk tomorrow!", isRead = true, reply = "You're welcome! See you then."),
        Message("Sophie Steps", "Rex", "Can we switch to the afternoon slot tomorrow?", isRead = true, reply = null),
        Message("Charlie Woof", "Bella", "It was great walking Bella today. She's such a sweetheart!", isRead = false, reply = null)
    )

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        items(messages) { msg ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = if (!msg.isRead) CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)) else CardDefaults.cardColors()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("📨 From: ${msg.from} (${msg.dog})", fontWeight = FontWeight.Bold)
                    Text(msg.text, modifier = Modifier.padding(top = 4.dp))

                    msg.reply?.let {
                        Spacer(modifier = Modifier.height(8.dp))
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFD0F0C0))
                                .padding(12.dp)
                        ) {
                            Text("💬 Your reply:", fontWeight = FontWeight.Medium)
                            Text(it)
                        }
                    }

                    if (msg.isRead && msg.reply == null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "✅ Read • No reply",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray
                        )
                    } else if (!msg.isRead) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "🔴 Unread",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Red,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun OwnerFriendsTab() {
    val friends = listOf(
        Triple("Alex Walker", "Luna", "+123456789"),
        Triple("Sophie Steps", "Rex", "+198765432"),
        Triple("Charlie Woof", "Bella", "+102938475")
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(friends) { (name, walksDone) ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(Modifier.height(4.dp))
                    Text("⭐ 4.7", color = Color.Gray)
                    Spacer(Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Button(
                            onClick = { /* simulate text */ },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFA5D6A7))
                        ) {
                            Icon(Icons.Default.Chat, contentDescription = "Text", tint = Color.White)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Text", color = Color.White)
                        }
                        Button(
                            onClick = { /* view profile */ },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFCE93D8))
                        ) {
                            Icon(Icons.Default.Person, contentDescription = "Profile", tint = Color.White)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Profile", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OwnerHeader() {
    var showContent by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        showContent = true
    }

    AnimatedVisibility(
        visible = showContent,
        enter = fadeIn() + slideInVertically(initialOffsetY = { -40 }),
        exit = fadeOut() + slideOutVertically(targetOffsetY = { 40 })
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .background(Color.White)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.size(180.dp),
                contentAlignment = Alignment.Center
            ) {
                // Profile image
                Image(
                    painter = painterResource(id = R.drawable.logo_new),
                    contentDescription = "Company Logo",
                    modifier = Modifier
                        .size(150.dp)
                        .clip(CircleShape)
                        .border(width = 2.dp, color = Color.LightGray, shape = CircleShape)
                )
                val context = LocalContext.current
                // Verified badge with icon and toast
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = 6.dp, y = 6.dp)
                        .clickable {
                            Toast
                                .makeText(context, "Verified by liveness test", Toast.LENGTH_LONG)
                                .show()
                        }
                        .background(Color(0xFF9C27B0), shape = RoundedCornerShape(10.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.VerifiedUser,
                            contentDescription = "Verified Icon",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Premium",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }


            Spacer(modifier = Modifier.height(12.dp))
            Text("John Doe", fontWeight = FontWeight.Bold, fontSize = 22.sp)
            Text("john.doe@example.com", fontSize = 14.sp)
            //Text("Golden Retriever • Female • 3 yrs", fontSize = 14.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(24.dp))
            Divider(thickness = 1.dp)
            Spacer(modifier = Modifier.height(16.dp))

            ProfileInfoItem("Member Since", "📅 January 2023")
            ProfileInfoItem("Dog Name", "🐶 Luna")
            ProfileInfoItem("Average Rating", "⭐ 4.9")
            ProfileInfoItem("Walks Completed", "👟 128")
            ProfileInfoItem("Average Distance Walked", "3.1km")
            ProfileInfoItem("Average Walk Duration", "35min")
            EditableProfileInfoItem("Walk Type", "Solo")
            EditableProfileInfoItem("Dog Breed", "Golden Retriever")
            EditableProfileInfoItem("Dog Size", "Large")

            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = { /* TODO: handle logout */ },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                modifier = Modifier.fillMaxWidth(0.8f)
            ) {
                Text("Logout", color = Color.White)
            }
        }
    }
}
