package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberBorderActive
import com.example.ui.theme.CyberCodeBg
import com.example.ui.theme.CyberPrimary
import com.example.ui.theme.CyberSecondary
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.CyberTextMuted
import com.example.ui.theme.CyberTextPrimary
import com.example.ui.theme.CyberTextSecondary

@Composable
fun OmrOptionCard(
    letter: String,
    optionText: String,
    isSelected: Boolean,
    isMultiSelect: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.015f else 1.0f,
        animationSpec = spring(dampingRatio = 0.75f, stiffness = 400f),
        label = "omr_scale"
    )

    val bgColor by animateColorAsState(
        targetValue = if (isSelected) Color(0xFF1E2235) else CyberSurface,
        label = "omr_bg"
    )

    val borderColor by animateColorAsState(
        targetValue = if (isSelected) CyberPrimary else CyberBorder,
        label = "omr_border"
    )

    val indicatorBg by animateColorAsState(
        targetValue = if (isSelected) CyberPrimary else CyberSurfaceVariant,
        label = "omr_indicator_bg"
    )

    val indicatorBorder by animateColorAsState(
        targetValue = if (isSelected) CyberSecondary else CyberBorder,
        label = "omr_indicator_border"
    )

    val indicatorShape = if (isMultiSelect) RoundedCornerShape(6.dp) else CircleShape

    Box(
        modifier = modifier
            .fillMaxWidth()
            .scale(scale)
            .shadow(
                elevation = if (isSelected) 6.dp else 0.dp,
                shape = RoundedCornerShape(12.dp),
                spotColor = CyberPrimary
            )
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .border(
                width = if (isSelected) 1.5.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onSelect
            )
            .padding(horizontal = 14.dp, vertical = 12.dp)
            .testTag("omr_option_$letter")
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Animated Bubble Indicator
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(indicatorShape)
                    .background(indicatorBg)
                    .border(1.5.dp, indicatorBorder, indicatorShape),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Selected",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Text(
                        text = letter,
                        color = CyberTextSecondary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Option text (clean display removing duplicate 'A)' if present)
            val cleanText = if (optionText.startsWith("$letter) ")) {
                optionText.substring(3).trim()
            } else if (optionText.startsWith("$letter)")) {
                optionText.substring(2).trim()
            } else {
                optionText
            }

            Text(
                text = cleanText,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 14.5.sp,
                    lineHeight = 21.sp,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                    color = if (isSelected) CyberTextPrimary else Color(0xFFCBD5E1)
                ),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun TechnicalCodeEditor(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    minLines: Int = 5
) {
    val charCount = value.length
    val lineCount = if (value.isEmpty()) 1 else value.lines().size

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CyberCodeBg)
            .border(1.dp, CyberBorderActive.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
    ) {
        // Technical Header bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF0F1523))
                .border(width = 0.5.dp, color = CyberBorder, shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                .padding(horizontal = 12.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Terminal,
                    contentDescription = null,
                    tint = CyberSecondary,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "RESPONSE_INPUT.md",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Medium,
                    color = CyberTextSecondary
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Lines: $lineCount  |  Chars: $charCount",
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    color = CyberTextMuted
                )
            }
        }

        // Text input area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            if (value.isEmpty()) {
                Text(
                    text = placeholder,
                    color = CyberTextMuted,
                    fontSize = 13.5.sp,
                    fontFamily = FontFamily.Monospace,
                    lineHeight = 20.sp
                )
            }

            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (minLines > 6) 190.dp else 140.dp)
                    .testTag("technical_text_editor"),
                textStyle = TextStyle(
                    color = CyberTextPrimary,
                    fontSize = 13.5.sp,
                    fontFamily = FontFamily.Monospace,
                    lineHeight = 21.sp
                ),
                cursorBrush = SolidColor(CyberSecondary)
            )
        }
    }
}
