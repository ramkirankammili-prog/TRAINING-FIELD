package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyberSecondary

@Composable
fun DesignedByHarryWatermark(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .alpha(0.38f)
            .background(
                color = Color(0x33000000),
                shape = RoundedCornerShape(12.dp)
            )
            .border(
                width = 0.5.dp,
                color = Color(0x33FFFFFF),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Code,
                contentDescription = null,
                tint = CyberSecondary,
                modifier = Modifier
                    .size(10.dp)
                    .padding(end = 3.dp)
            )
            Text(
                text = "Designed by Harry",
                fontSize = 9.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = FontFamily.Monospace,
                color = Color(0xCCFFFFFF),
                letterSpacing = 0.8.sp
            )
        }
    }
}
