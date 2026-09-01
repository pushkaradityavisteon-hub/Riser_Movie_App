package com.example.movie_app.screen.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun BackButton(
    navController: NavController,
    tint: Color = Color.White,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = { navController.popBackStack() },
        modifier = modifier.padding(8.dp)
    ) {
        Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = "Back",
            tint = tint,
            modifier = Modifier.size(28.dp)
        )
    }
}
