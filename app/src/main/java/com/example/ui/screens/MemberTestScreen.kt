package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AuditQuestions
import com.example.model.Question
import com.example.model.QuestionType
import com.example.model.SectionType
import com.example.model.TeamMember
import com.example.ui.components.OmrOptionCard
import com.example.ui.components.TechnicalCodeEditor
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberBorderActive
import com.example.ui.theme.CyberPrimary
import com.example.ui.theme.CyberSecondary
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.CyberTextMuted
import com.example.ui.theme.CyberTextPrimary
import com.example.ui.theme.CyberTextSecondary
import com.example.ui.theme.CyberWarning

@Composable
fun MemberTestScreen(
    section: SectionType,
    member: TeamMember,
    isSubmitted: Boolean,
    existingAnswers: Map<Int, String>,
    onSaveAnswer: (questionId: Int, answer: String) -> Unit,
    onSubmitTest: (answers: Map<Int, String>) -> Unit,
    onResetTestForDemo: () -> Unit,
    modifier: Modifier = Modifier
) {
    val questions = remember(section) { AuditQuestions.getQuestionsForSection(section) }
    var currentQuestionIndex by remember { mutableIntStateOf(0) }
    val localAnswers = remember { mutableStateMapOf<Int, String>() }
    var showSubmitDialog by remember { mutableStateOf(false) }

    // Sync external saved answers into local state when section/member changes
    LaunchedEffect(existingAnswers, member.id, section) {
        localAnswers.clear()
        localAnswers.putAll(existingAnswers)
        currentQuestionIndex = 0
    }

    val currentQuestion = questions.getOrElse(currentQuestionIndex) { questions.first() }
    val answeredCount = questions.count { q -> !localAnswers[q.id].isNullOrBlank() }
    val progress = answeredCount.toFloat() / questions.size.toFloat()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF090D15))
    ) {
        // Section Header & Progress Summary Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            colors = CardDefaults.cardColors(containerColor = CyberSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder),
            shape = RoundedCornerShape(14.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = section.title.uppercase(),
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 15.sp,
                                letterSpacing = 1.sp,
                                color = CyberTextPrimary,
                                fontFamily = FontFamily.Monospace
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0xFF1E293B))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = if (isSubmitted) "SUBMITTED & LOCKED" else "IN PROGRESS",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSubmitted) CyberSecondary else CyberWarning
                                )
                            }
                        }
                        Text(
                            text = "Examinee: ${member.name} • Strictly Confidential Slip-Test",
                            fontSize = 11.5.sp,
                            color = CyberTextSecondary
                        )
                    }

                    // Progress Pill
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "$answeredCount / ${questions.size} Answered",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyberSecondary,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "${(progress * 100).toInt()}% Complete",
                            fontSize = 10.sp,
                            color = CyberTextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = CyberSecondary,
                    trackColor = CyberSurfaceVariant
                )
            }
        }

        // Question Navigator Palette (Numbered pills 1 to 15)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 14.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            questions.forEachIndexed { index, q ->
                val isCurrent = index == currentQuestionIndex
                val isAnswered = !localAnswers[q.id].isNullOrBlank()

                val pillBg = when {
                    isCurrent -> CyberPrimary
                    isAnswered -> Color(0xFF1B2E3D)
                    else -> CyberSurface
                }
                val pillBorder = when {
                    isCurrent -> CyberSecondary
                    isAnswered -> Color(0xFF0EA5E9)
                    else -> CyberBorder
                }

                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(pillBg)
                        .border(1.dp, pillBorder, RoundedCornerShape(8.dp))
                        .clickable {
                            currentQuestionIndex = index
                        }
                        .testTag("question_palette_${q.id}"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${q.id}",
                        fontSize = 12.sp,
                        fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                        color = if (isCurrent) Color.White else if (isAnswered) CyberSecondary else CyberTextSecondary,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Main Question Card Area (Scrollable)
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 14.dp)
        ) {
            if (isSubmitted) {
                // If submitted, show lock banner + read-only review
                SubmittedLockedBanner(
                    member = member,
                    section = section,
                    onReset = onResetTestForDemo
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 80.dp)
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    colors = CardDefaults.cardColors(containerColor = CyberSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        // Badges Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(CyberPrimary.copy(alpha = 0.2f))
                                        .border(1.dp, CyberPrimary, RoundedCornerShape(6.dp))
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = "QUESTION ${currentQuestion.id} / ${questions.size}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color(0xFF1E293B))
                                        .padding(horizontal = 7.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = currentQuestion.type.label,
                                        fontSize = 10.5.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = CyberSecondary
                                    )
                                }
                            }

                            // Marks pill
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFF0F231D))
                                    .border(1.dp, Color(0xFF10B981), RoundedCornerShape(12.dp))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "[ ${currentQuestion.maxMarks} Marks ]",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF34D399),
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Question Topic Pill
                        Text(
                            text = "TOPIC: ${currentQuestion.topic.uppercase()}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = CyberTextMuted,
                            letterSpacing = 0.5.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Question Prompt Text
                        Text(
                            text = currentQuestion.text,
                            style = androidx.compose.material3.MaterialTheme.typography.titleMedium.copy(
                                fontSize = 16.sp,
                                lineHeight = 24.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = CyberTextPrimary
                            )
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        // Input Interface based on Question Type
                        when (currentQuestion.type) {
                            QuestionType.MCQ -> {
                                val currentAns = localAnswers[currentQuestion.id] ?: ""
                                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    currentQuestion.options.forEach { optionText ->
                                        val letter = optionText.take(1)
                                        val isSelected = currentAns == letter

                                        OmrOptionCard(
                                            letter = letter,
                                            optionText = optionText,
                                            isSelected = isSelected,
                                            isMultiSelect = false,
                                            onSelect = {
                                                if (!isSubmitted) {
                                                    localAnswers[currentQuestion.id] = letter
                                                    onSaveAnswer(currentQuestion.id, letter)
                                                }
                                            }
                                        )
                                    }
                                }
                            }

                            QuestionType.MULTI_SELECT -> {
                                val currentAns = localAnswers[currentQuestion.id] ?: ""
                                val selectedLetters = currentAns.split(",").map { it.trim() }.filter { it.isNotEmpty() }.toSet()

                                Text(
                                    text = "✦ Multiple choices may be correct. Select all matching options.",
                                    fontSize = 11.sp,
                                    color = CyberSecondary,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )

                                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    currentQuestion.options.forEach { optionText ->
                                        val letter = optionText.take(1)
                                        val isSelected = selectedLetters.contains(letter)

                                        OmrOptionCard(
                                            letter = letter,
                                            optionText = optionText,
                                            isSelected = isSelected,
                                            isMultiSelect = true,
                                            onSelect = {
                                                if (!isSubmitted) {
                                                    val updated = if (isSelected) {
                                                        selectedLetters - letter
                                                    } else {
                                                        selectedLetters + letter
                                                    }
                                                    val serialized = updated.sorted().joinToString(",")
                                                    localAnswers[currentQuestion.id] = serialized
                                                    onSaveAnswer(currentQuestion.id, serialized)
                                                }
                                            }
                                        )
                                    }
                                }
                            }

                            QuestionType.SHORT_ANSWER, QuestionType.SCENARIO, QuestionType.FINAL_CHALLENGE -> {
                                val currentAns = localAnswers[currentQuestion.id] ?: ""

                                Column {
                                    Text(
                                        text = if (currentQuestion.type == QuestionType.FINAL_CHALLENGE)
                                            "✦ Comprehensive System Challenge. Detail endpoints, payload formats, and edge-cases."
                                        else
                                            "✦ Provide your technical explanation or reasoning below:",
                                        fontSize = 11.sp,
                                        color = CyberTextSecondary,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )

                                    TechnicalCodeEditor(
                                        value = currentAns,
                                        onValueChange = { newText ->
                                            if (!isSubmitted) {
                                                localAnswers[currentQuestion.id] = newText
                                                onSaveAnswer(currentQuestion.id, newText)
                                            }
                                        },
                                        placeholder = if (currentQuestion.type == QuestionType.FINAL_CHALLENGE)
                                            "// Write 4 endpoints with HTTP methods, paths, and purpose...\n1. POST /api/...\n2. GET /api/...\n3. PATCH /api/...\n4. DELETE /api/..."
                                        else
                                            "// Type your explanation or step-by-step procedure here...",
                                        minLines = if (currentQuestion.type == QuestionType.FINAL_CHALLENGE) 8 else 5
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Bottom Action Bar (Fixed, respects gesture navigation)
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color(0xFF0C101A),
            tonalElevation = 8.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(width = 0.5.dp, color = CyberBorder)
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Previous Button
                OutlinedButton(
                    onClick = {
                        if (currentQuestionIndex > 0) currentQuestionIndex--
                    },
                    enabled = currentQuestionIndex > 0,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = CyberTextPrimary,
                        disabledContentColor = CyberTextMuted
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (currentQuestionIndex > 0) CyberBorder else Color.Transparent
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Previous",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Previous", fontSize = 12.sp)
                }

                // Middle Quick Jump / Indicator
                Text(
                    text = "${currentQuestionIndex + 1} of ${questions.size}",
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    color = CyberTextSecondary
                )

                // Next or Submit Button
                if (currentQuestionIndex < questions.size - 1) {
                    Button(
                        onClick = {
                            currentQuestionIndex++
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CyberPrimary),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text("Next", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Next",
                            modifier = Modifier.size(16.dp)
                        )
                    }
                } else {
                    // Final Submit Button
                    Button(
                        onClick = {
                            showSubmitDialog = true
                        },
                        enabled = !isSubmitted,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF10B981),
                            disabledContainerColor = Color(0xFF1E293B)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        modifier = Modifier.testTag("submit_test_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Submit",
                            modifier = Modifier.size(16.dp),
                            tint = if (isSubmitted) CyberTextMuted else Color.White
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isSubmitted) "Submitted" else "Submit Test",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSubmitted) CyberTextMuted else Color.White
                        )
                    }
                }
            }
        }
    }

    // Confirmation Modal before Final Submission
    if (showSubmitDialog) {
        val unanswered = questions.size - answeredCount

        AlertDialog(
            onDismissRequest = { showSubmitDialog = false },
            containerColor = CyberSurface,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (unanswered > 0) Icons.Default.Warning else Icons.Default.AssignmentTurnedIn,
                        contentDescription = null,
                        tint = if (unanswered > 0) CyberWarning else CyberSecondary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Submit Slip-Test?",
                        color = CyberTextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            text = {
                Column {
                    Text(
                        text = "You have answered $answeredCount of ${questions.size} questions.",
                        color = CyberTextPrimary,
                        fontSize = 13.5.sp
                    )
                    if (unanswered > 0) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "⚠️ Warning: $unanswered question(s) remain unanswered.",
                            color = CyberWarning,
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Once submitted, your paper will be securely locked and routed to the Team Lead for evaluation. Answer keys and scores are confidential to the Lead Dashboard.",
                        color = CyberTextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSubmitDialog = false
                        onSubmitTest(localAnswers)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text("Confirm Submission", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showSubmitDialog = false }) {
                    Text("Keep Editing", color = CyberTextSecondary)
                }
            }
        )
    }
}

@Composable
fun SubmittedLockedBanner(
    member: TeamMember,
    section: SectionType,
    onReset: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF101B2E)),
        border = androidx.compose.foundation.BorderStroke(1.dp, CyberSecondary),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(CyberSecondary.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Locked",
                        tint = CyberSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Test Locked & Submitted",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.5.sp,
                        color = CyberTextPrimary
                    )
                    Text(
                        text = "Responses received. Awaiting Team Lead manual grading.",
                        fontSize = 11.sp,
                        color = CyberTextSecondary
                    )
                }
            }

            // Quick reset button for demo/testing purpose
            OutlinedButton(
                onClick = onReset,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = CyberSecondary),
                border = androidx.compose.foundation.BorderStroke(1.dp, CyberSecondary.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Retake",
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Retake", fontSize = 11.sp)
            }
        }
    }
}
