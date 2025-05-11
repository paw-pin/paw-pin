// ChooseRoleScreen.kt
package com.example.pawpin_v2.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.pawpin_v2.R

@Composable
fun ChooseRoleScreen(onRoleSelected: (UserRole) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Purple-bordered circle logo
        Box(
            modifier = Modifier
                .size(150.dp)
                .background(MaterialTheme.colorScheme.surface, shape = CircleShape)
                .border(3.dp, Color(0xFF9C27B0), shape = CircleShape)
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_new),
                contentDescription = "PawPin Logo",
                modifier = Modifier.size(150.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { onRoleSelected(UserRole.DogWalker) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Enter as Dog Walker")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { onRoleSelected(UserRole.DogOwner) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Enter as Dog Owner")
        }
    }
}


enum class UserRole {
    DogWalker, DogOwner
}