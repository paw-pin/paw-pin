package com.example.pawpin_v2.screens.community

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class Event(val title: String, val date: String, val location: String, val speaker: String, val description: String, val duration: String)
data class BlogCategory(val title: String, val posts: List<BlogPost>)
data class BlogPost(val title: String, val author: String, val excerpt: String, val content: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityScreen(isOwner: Boolean = false) {

    if (isOwner) {
        var selectedTabIndex by remember { mutableStateOf(0) }
        val tabs = listOf("🎉 Events", "🟣 Deals")

        Scaffold { padding ->
            Column(modifier = Modifier.padding(padding)) {
                TabRow(selectedTabIndex = selectedTabIndex) {
                    tabs.forEachIndexed { index, tab ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index }) {
                            Text(
                                tab,
                                modifier = Modifier.padding(16.dp),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
                when (selectedTabIndex) {
                    0 -> OwnerEventsTab()
                    1 -> SpecialDealsTab()
                }
            }
        }
    } else {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("🎉 Events", "📚 Blog")

    Scaffold { padding ->
        Column(modifier = Modifier.padding(padding)) {
            TabRow(selectedTabIndex = selectedTabIndex) {
                tabs.forEachIndexed { index, tab ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index }) {
                        Text(
                            tab,
                            modifier = Modifier.padding(16.dp),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
            when (selectedTabIndex) {
                0 -> EventsTab()
                1 -> BlogTab()
            }
        }
    }
}
}


@Composable
fun OwnerEventsTab() {
    data class DogEvent(
        val title: String,
        val date: String,
        val location: String,
        val isPremium: Boolean,
        val description: String,
        val startTime: String,
        val duration: String
    )

    val mockEvents = listOf(
        DogEvent("Paw Park Picnic", "July 22", "Central Park", false, "Bring your pup and a blanket for food and fun in the park!", "10:00 AM", "2 hours"),
        DogEvent("Downtown Doggy Dash", "July 29", "5th Avenue", true, "Join other energetic dogs for a 3K group run. Includes bandanas!", "9:30 AM", "1 hour"),
        DogEvent("Pet & Owner Yoga", "August 5", "Liberty Lawn", false, "Stretch and pose alongside your furry friend in this relaxing outdoor session.", "5:00 PM", "45 min"),
        DogEvent("Sunset Sniffs Soirée", "August 12", "Riverbank Dog Zone", true, "An exclusive premium social hour with treats, sniff zones, and drinks for owners.", "6:00 PM", "90 min")
    )

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        items(mockEvents) { event ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(3.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (event.isPremium) Color(0xFFF3E5F5) else Color.White
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = event.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            modifier = Modifier.weight(1f)
                        )
                        if (event.isPremium) {
                            Text(
                                text = "🔒 Premium",
                                color = Color(0xFF8E24AA),
                                fontSize = 12.sp,
                                modifier = Modifier
                                    .background(Color(0xFFE1BEE7), shape = RoundedCornerShape(8.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("📅 ${event.date} • ${event.startTime} • ${event.duration}  •  📍 ${event.location}", fontSize = 13.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(event.description, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    if (event.isPremium) {
                        Button(onClick = { /* RSVP logic */ }, modifier = Modifier.align(Alignment.End), enabled = false) {
                            Text("RSVP")
                        }
                    } else {
                        Button(onClick = { /* RSVP logic */ }, modifier = Modifier.align(Alignment.End)) {
                            Text("RSVP")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EventsTab() {
    val events = listOf(
        Event(
            "Dog Nutrition Deep Dive",
            "July 22, 2025",
            "Online",
            "Dr. Woofstein",
            "Exclusive breakdown of feeding strategies + free PDF guide.",
            "1h 15min"
        ),
        Event(
            "Premium Giveaway: Year of Free Walks",
            "August 5, 2025",
            "Members Only",
            "Team PawPin",
            "Enter to win a full year of free dog walks. Premium users only!",
            "Instant Entry"
        ),
        Event(
            "How to Communicate with Dog Owners",
            "June 20, 2025",
            "Online Webinar",
            "Jane Walker",
            "Practical guidance on improving communication with clients.",
            "1h 30min"
        ),
        Event(
            "Safety Protocols During Walks",
            "July 5, 2025",
            "Central Park, NY",
            "Dr. Emily Canine",
            "Learn emergency responses and leash safety standards.",
            "2h"
        ),
        Event(
            "Advanced Dog Handling Tips",
            "August 12, 2025",
            "SoHo Club, NY",
            "Marko Tails",
            "Pro strategies for handling excited or aggressive dogs.",
            "1h 45min"
        )
    )

    val rsvpStatus = remember { mutableStateMapOf<String, Boolean>() }

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        items(events) { event ->

            val isPremium = event.title.contains("Premium", ignoreCase = true) || event.title.contains("Giveaway", ignoreCase = true)

            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = if (isPremium) Color(0xFFF3E5F5) else Color(0xFFF5F5F5)
                ),
                elevation = CardDefaults.cardElevation(6.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        event.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    if (isPremium) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Premium",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier
                                .background(Color(0xFF9C27B0), shape = RoundedCornerShape(6.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text("📅 ${event.date}    ⏱ ${event.duration}", fontSize = 13.sp, color = Color.DarkGray)
                    Text("📍 ${event.location}", fontSize = 13.sp, color = Color.DarkGray)
                    Text("🎤 Speaker: ${event.speaker}", fontSize = 13.sp, color = Color.DarkGray)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(event.description, style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(12.dp))

                    val isRsvped = rsvpStatus[event.title] == true

                    if (isRsvped) {
                        Text(
                            "✅ You’ve applied for this event",
                            color = Color(0xFF39803B),
                            fontWeight = FontWeight.SemiBold
                        )
                    } else {
                        if (event.duration === "Instant Entry") {
                            Button(onClick = { /* RSVP logic */ }, modifier = Modifier.align(Alignment.End), enabled = false) {
                                Text("RSVP")
                            }
                        } else {
                            Button(onClick = { /* RSVP logic */ }, modifier = Modifier.align(Alignment.End)) {
                                Text("RSVP")
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun SpecialDealsTab() {
    val deals = listOf(
        "🎁 2 Free Grooming Coupons at Bark & Bubbles",
        "🦴 15% off Premium Treats at DoggoMart",
        "🏥 Free Initial Checkup at PetCare Vet Center",
        "🎒 Free Walk Bag from PupPacks with Premium Signup"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8EAF6))
            .padding(16.dp)
    ) {
        Text(
            "Premium Partner Deals",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier
                .background(Color(0xFF9C27B0), shape = RoundedCornerShape(6.dp))
                .padding(horizontal = 30.dp, vertical = 2.dp)
        )
        //Text("🟣 Partner Deals", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6A1B9A))
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(deals) { deal ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(deal, fontWeight = FontWeight.SemiBold)
                        Text("Available exclusively for Premium members", fontSize = 12.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(5.dp))
                        Button(
                            onClick = { /* Collect deal logic */ },
                            modifier = Modifier.align(Alignment.Start),  enabled = false,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9C27B0))
                        ) {
                            Text("Collect Deal", color = Color.White)
                        }
                    }

                }
            }
        }
    }
}


@Composable
fun BlogTab() {
    val allCategories = listOf("All", "Training Advice", "Health & Wellness")
    var selectedCategory by remember { mutableStateOf("All") }

    val blogSections = listOf(
        BlogCategory("Training Advice", listOf(
            BlogPost(
                "How to Train a Puppy to Walk on Leash",
                "Sarah Barkley",
                "Tips to avoid pulling and improve heel behavior...",
                "Training your puppy to walk nicely on leash is all about consistency and timing. Use short sessions and lots of rewards."
            ),
            BlogPost(
                "Positive Reinforcement Techniques",
                "Trainer Dan",
                "Reward-based approaches that work wonders...",
                "Positive reinforcement includes treats, praise, or toys after good behavior. Avoid punishment — reward what you want repeated."
            )
        )),
        BlogCategory("Health & Wellness", listOf(
            BlogPost(
                "Hydration Tips for Summer Walks",
                "Vet Emily",
                "Keeping your dog safe and hydrated in heat...",
                "Always carry water and a collapsible bowl. Walk during cooler hours and watch for signs of overheating."
            ),
            BlogPost(
                "Paw Care 101",
                "PawPin Staff",
                "How to prevent cracks, injuries, and discomfort...",
                "Trim nails regularly and use paw balm for dryness. Check pads for cuts or debris after each walk."
            )
        ))
    )

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

        // ===== Top Filter Chips =====
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(bottom = 16.dp)) {
            allCategories.forEach { category ->
                FilterChip(
                    selected = selectedCategory == category,
                    onClick = { selectedCategory = category },
                    label = { Text(category) }
                )
            }
        }

        // ===== Blog Posts List =====
        LazyColumn {
            blogSections
                .filter { selectedCategory == "All" || it.title == selectedCategory }
                .forEach { section ->
                    item {
                        Text("📝 ${section.title}", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(vertical = 12.dp))
                    }

                    items(section.posts) { post ->
                        var expanded by remember { mutableStateOf(false) }
                        val commentsCount = remember { (3..50).random() }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                                .clickable { expanded = !expanded }
                                .animateContentSize(),
                            elevation = CardDefaults.cardElevation(2.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFDFDFD))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                    Text(post.title, fontWeight = FontWeight.SemiBold, fontSize = 17.sp)
                                    Text("💬 $commentsCount", fontSize = 13.sp, color = Color.DarkGray)
                                }
                                Text("✍️ ${post.author}", fontSize = 13.sp, color = Color.Gray)
                                Spacer(Modifier.height(6.dp))
                                Text(
                                    text = if (expanded) post.content else post.excerpt,
                                    style = MaterialTheme.typography.bodySmall,
                                    maxLines = if (expanded) Int.MAX_VALUE else 3
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                // Open Blog Button
                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = if (expanded) "Show less ▲" else "...Read more ▼",
                                        color = MaterialTheme.colorScheme.primary,
                                        fontSize = 10.sp
                                    )
                                    Button(
                                        onClick = { /* Mock: Open full blog */ },
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Text("Open Blog", fontSize = 10.sp)
                                    }
                                }
                            }
                        }
                    }
                }
        }
    }
}

