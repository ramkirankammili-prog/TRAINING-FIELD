package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Grade
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.EvaluationReport
import com.example.model.AuditQuestions
import com.example.model.PerformanceClass
import com.example.model.Question
import com.example.model.QuestionType
import com.example.model.SectionType
import com.example.model.TeamMember
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberBorderActive
import com.example.ui.theme.CyberCodeBg
import com.example.ui.theme.CyberDanger
import com.example.ui.theme.CyberPrimary
import com.example.ui.theme.CyberSecondary
import com.example.ui.theme.CyberSuccess
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.CyberTextMuted
import com.example.ui.theme.CyberTextPrimary
import com.example.ui.theme.CyberTextSecondary
import com.example.ui.theme.CyberWarning

@Composable
fun LeadDashboardScreen(
    members: List<TeamMember>,
    reports: Map<String, EvaluationReport>, // key: "${memberId}_${sectionName}"
    selectedMember: TeamMember,
    onSelectMember: (TeamMember) -> Unit,
    selectedSection: SectionType,
    onSelectSection: (SectionType) -> Unit,
    onSaveGrading: (memberId: String, section: SectionType, questionId: Int, mark: Float, note: String?) -> Unit,
    onSeedDemoData: () -> Unit,
    modifier: Modifier = Modifier
) {
    var leadTab by remember { mutableIntStateOf(0) } // 0: Member Comparison, 1: Manual Grading

    val currentReportKey = "${selectedMember.id}_${selectedSection.name}"
    val currentReport = reports[currentReportKey] ?: EvaluationReport(
        memberId = selectedMember.id,
        memberName = selectedMember.name,
        section = selectedSection,
        isSubmitted = false,
        isGraded = false,
        submittedAt = 0L,
        autoObjectiveScore = 0f,
        manualDescriptiveScore = 0f,
        totalScore = 0f,
        maxScore = AuditQuestions.getTotalPossibleMarks(selectedSection).toFloat(),
        percentage = 0f,
        performanceClass = PerformanceClass.NOT_READY,
        strongTopics = emptyList(),
        weakTopics = emptyList(),
        answeredCount = 0,
        totalQuestions = 15,
        answers = emptyMap(),
        manualMarks = emptyMap(),
        leadNotes = emptyMap()
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF090D15))
    ) {
        // Protected Lead Header Banner
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF101726)),
            border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorderActive),
            shape = RoundedCornerShape(14.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(CyberPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "LEAD EVALUATION CONSOLE",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontFamily = FontFamily.Monospace,
                                    letterSpacing = 1.sp,
                                    color = CyberTextPrimary
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color(0xFF1E293B))
                                        .padding(horizontal = 5.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = "CONFIDENTIAL",
                                        fontSize = 8.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = CyberSecondary
                                    )
                                }
                            }
                            Text(
                                text = "Evaluating 5-Member Hackathon Engineering Team",
                                fontSize = 11.sp,
                                color = CyberTextSecondary
                            )
                        }
                    }

                    // Seed Demo Submissions Button
                    Button(
                        onClick = onSeedDemoData,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1F293D)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CyberSecondary),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("seed_demo_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Seed Demo",
                            tint = CyberSecondary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Auto-Populate Team Data",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyberSecondary
                        )
                    }
                }
            }
        }

        // Sub-tabs: 1. Team Comparison Matrix | 2. Question-by-Question Grading
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            LeadSubTabButton(
                title = "Team Comparison Matrix",
                icon = Icons.Default.Insights,
                isSelected = leadTab == 0,
                onClick = { leadTab = 0 },
                modifier = Modifier.weight(1f)
            )
            LeadSubTabButton(
                title = "Manual Grading & Review",
                icon = Icons.Default.Grade,
                isSelected = leadTab == 1,
                onClick = { leadTab = 1 },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Content Area
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            if (leadTab == 0) {
                TeamComparisonView(
                    members = members,
                    reports = reports,
                    onInspectMember = { member, sec ->
                        onSelectMember(member)
                        onSelectSection(sec)
                        leadTab = 1
                    }
                )
            } else {
                ManualGradingView(
                    members = members,
                    selectedMember = selectedMember,
                    onSelectMember = onSelectMember,
                    selectedSection = selectedSection,
                    onSelectSection = onSelectSection,
                    report = currentReport,
                    onSaveGrading = onSaveGrading
                )
            }
        }
    }
}

@Composable
fun LeadSubTabButton(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bg = if (isSelected) CyberPrimary else CyberSurface
    val border = if (isSelected) CyberSecondary else CyberBorder

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bg)
            .border(1.dp, border, RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 9.dp, horizontal = 10.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isSelected) Color.White else CyberTextSecondary,
            modifier = Modifier.size(15.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = title,
            fontSize = 11.5.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) Color.White else CyberTextPrimary
        )
    }
}

@Composable
fun TeamComparisonView(
    members: List<TeamMember>,
    reports: Map<String, EvaluationReport>,
    onInspectMember: (TeamMember, SectionType) -> Unit
) {
    val scrollState = rememberScrollState()

    // Calculate aggregated team stats
    val allMemberReports = members.map { m ->
        val key = "${m.id}_${m.assignedSection.name}"
        reports[key]
    }
    val submittedCount = allMemberReports.count { it?.isSubmitted == true }
    val gradedCount = allMemberReports.count { it?.isGraded == true }
    val avgScore = if (allMemberReports.isNotEmpty()) {
        allMemberReports.mapNotNull { it?.percentage }.average().toFloat()
    } else 0f

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 14.dp, vertical = 4.dp)
            .padding(bottom = 60.dp)
    ) {
        // Analytics Summary Cards Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MetricCard(
                title = "COMPLETED",
                value = "$submittedCount / ${members.size}",
                subtext = "Submissions in",
                color = CyberSecondary,
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                title = "GRADED",
                value = "$gradedCount / ${members.size}",
                subtext = "Evaluations ready",
                color = CyberSuccess,
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                title = "TEAM AVG",
                value = "${if (avgScore.isNaN()) 0 else avgScore.toInt()}%",
                subtext = "Score average",
                color = CyberPrimary,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "5-MEMBER PERFORMANCE MATRIX",
            fontSize = 12.sp,
            fontWeight = FontWeight.ExtraBold,
            color = CyberTextSecondary,
            fontFamily = FontFamily.Monospace,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Member Cards List
        members.forEach { member ->
            val reportKey = "${member.id}_${member.assignedSection.name}"
            val report = reports[reportKey]
            val isSubmitted = report?.isSubmitted == true
            val isGraded = report?.isGraded == true

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onInspectMember(member, member.assignedSection) },
                colors = CardDefaults.cardColors(containerColor = CyberSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(Color(member.avatarBgHex)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = member.name.first().toString(),
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = member.name,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CyberTextPrimary
                                )
                                Text(
                                    text = "${member.assignedSection.title} • ${member.email}",
                                    fontSize = 11.sp,
                                    color = CyberTextSecondary
                                )
                            }
                        }

                        // Classification Badge
                        if (isSubmitted) {
                            val pClass = report?.performanceClass ?: PerformanceClass.NOT_READY
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(pClass.colorHex).copy(alpha = 0.2f))
                                    .border(1.dp, Color(pClass.colorHex), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = pClass.label,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(pClass.colorHex),
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFF1E293B))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "NOT STARTED",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CyberTextMuted
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (isSubmitted && report != null) {
                        // Score details row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF0F1420), RoundedCornerShape(8.dp))
                                .padding(horizontal = 10.dp, vertical = 7.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Total: ${report.totalScore.toInt()} / ${report.maxScore.toInt()} pts",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CyberTextPrimary,
                                    fontFamily = FontFamily.Monospace
                                )
                                Text(
                                    text = "Objective: ${report.autoObjectiveScore.toInt()} | Descriptive: ${report.manualDescriptiveScore.toInt()}",
                                    fontSize = 10.sp,
                                    color = CyberTextMuted
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "${report.percentage.toInt()}%",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = CyberSecondary,
                                    fontFamily = FontFamily.Monospace
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Grade",
                                    tint = CyberTextMuted,
                                    modifier = Modifier.size(15.dp)
                                )
                            }
                        }

                        // Topics Pill Row
                        if (report.strongTopics.isNotEmpty() || report.weakTopics.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                if (report.strongTopics.isNotEmpty()) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(Color(0xFF064E3B))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "Strong: ${report.strongTopics.first()}",
                                            fontSize = 9.sp,
                                            color = Color(0xFF6EE7B7)
                                        )
                                    }
                                }
                                if (report.weakTopics.isNotEmpty()) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(Color(0xFF7F1D1D))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "Review: ${report.weakTopics.first()}",
                                            fontSize = 9.sp,
                                            color = Color(0xFFFCA5A5)
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        Text(
                            text = "Awaiting examinee to take and submit the test.",
                            fontSize = 11.sp,
                            color = CyberTextMuted
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    subtext: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = CyberSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(
                text = title,
                fontSize = 9.5.sp,
                fontWeight = FontWeight.Bold,
                color = CyberTextMuted,
                fontFamily = FontFamily.Monospace
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = color,
                fontFamily = FontFamily.Monospace
            )
            Text(
                text = subtext,
                fontSize = 9.5.sp,
                color = CyberTextSecondary
            )
        }
    }
}

@Composable
fun ManualGradingView(
    members: List<TeamMember>,
    selectedMember: TeamMember,
    onSelectMember: (TeamMember) -> Unit,
    selectedSection: SectionType,
    onSelectSection: (SectionType) -> Unit,
    report: EvaluationReport,
    onSaveGrading: (memberId: String, section: SectionType, questionId: Int, mark: Float, note: String?) -> Unit
) {
    val questions = remember(selectedSection) { AuditQuestions.getQuestionsForSection(selectedSection) }
    val localMarks = remember { mutableStateMapOf<Int, Float>() }
    val localNotes = remember { mutableStateMapOf<Int, String>() }

    // Synchronize local editable marks from report
    androidx.compose.runtime.LaunchedEffect(report.manualMarks, report.leadNotes, selectedMember.id, selectedSection) {
        localMarks.clear()
        localMarks.putAll(report.manualMarks)
        localNotes.clear()
        localNotes.putAll(report.leadNotes)
    }

    // Dynamic score calculation
    val currentDescriptiveScore = localMarks.values.sum()
    val liveTotalScore = report.autoObjectiveScore + currentDescriptiveScore
    val livePercentage = if (report.maxScore > 0f) (liveTotalScore / report.maxScore) * 100f else 0f
    val liveClass = when {
        livePercentage >= 80f -> PerformanceClass.EXCELLENT
        livePercentage >= 60f -> PerformanceClass.GOOD
        livePercentage >= 40f -> PerformanceClass.NEEDS_IMPROVEMENT
        else -> PerformanceClass.NOT_READY
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 14.dp)
            .padding(bottom = 80.dp)
    ) {
        // Member & Section Picker Pills
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            members.forEach { m ->
                val isSelected = m.id == selectedMember.id
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isSelected) CyberPrimary else CyberSurface)
                        .border(1.dp, if (isSelected) CyberSecondary else CyberBorder, RoundedCornerShape(20.dp))
                        .clickable { onSelectMember(m) }
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = m.name,
                        fontSize = 11.5.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) Color.White else CyberTextPrimary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Section Selector Pills
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf(SectionType.BACKEND, SectionType.FRONTEND, SectionType.INTEGRATION, SectionType.DEVOPS).forEach { sec ->
                val isSelected = sec == selectedSection
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isSelected) Color(0xFF1E293B) else Color.Transparent)
                        .border(1.dp, if (isSelected) CyberSecondary else CyberBorder, RoundedCornerShape(6.dp))
                        .clickable { onSelectSection(sec) }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = sec.shortTitle,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) CyberSecondary else CyberTextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Sticky-style Live Score Evaluation Banner
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CyberSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorderActive),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "${selectedMember.name.uppercase()} • EVALUATION",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = CyberTextSecondary
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${liveTotalScore.toInt()} / ${report.maxScore.toInt()} Marks",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = CyberTextPrimary,
                                fontFamily = FontFamily.Monospace
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "(${livePercentage.toInt()}%)",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = CyberSecondary,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    // Classification Pill
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(liveClass.colorHex).copy(alpha = 0.2f))
                            .border(1.dp, Color(liveClass.colorHex), RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = liveClass.label,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(liveClass.colorHex),
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Auto-Scored Objective: ${report.autoObjectiveScore.toInt()} pts",
                        fontSize = 11.sp,
                        color = CyberSuccess
                    )
                    Text(
                        text = "Manually Evaluated: ${currentDescriptiveScore.toInt()} pts",
                        fontSize = 11.sp,
                        color = CyberPrimary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "QUESTIONS EVALUATION (15 TOTAL)",
            fontSize = 12.sp,
            fontWeight = FontWeight.ExtraBold,
            color = CyberTextSecondary,
            fontFamily = FontFamily.Monospace,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Question items
        questions.forEach { q ->
            val submittedAns = report.answers[q.id] ?: ""
            val isObjective = (q.type == QuestionType.MCQ || q.type == QuestionType.MULTI_SELECT)

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                colors = CardDefaults.cardColors(containerColor = CyberSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    // Header: Q num, Type, Max marks
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Q${q.id}.",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = CyberSecondary,
                                fontFamily = FontFamily.Monospace
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = q.type.label,
                                fontSize = 11.sp,
                                color = CyberTextSecondary
                            )
                        }

                        Text(
                            text = "Max: ${q.maxMarks} Marks",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyberTextMuted,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = q.text,
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = CyberTextPrimary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    if (isObjective) {
                        // Objective Grading Display
                        val objectiveEarned = com.example.data.AuditRepository.calculateSingleQuestionObjectiveScore(q, submittedAns)
                        val isFullyCorrect = objectiveEarned == q.maxMarks.toFloat()

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF0F1522), RoundedCornerShape(8.dp))
                                .padding(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (isFullyCorrect) Icons.Default.CheckCircle else Icons.Default.Close,
                                        contentDescription = null,
                                        tint = if (isFullyCorrect) CyberSuccess else CyberDanger,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Member Selected: ${if (submittedAns.isBlank()) "[No Answer]" else submittedAns}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isFullyCorrect) CyberSuccess else CyberDanger
                                    )
                                }

                                Text(
                                    text = "+${objectiveEarned.toInt()} / ${q.maxMarks} pts",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isFullyCorrect) CyberSuccess else CyberDanger,
                                    fontFamily = FontFamily.Monospace
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "Correct Answer Key: ${q.correctAnswers.joinToString(", ")}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = CyberSecondary,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    } else {
                        // Descriptive Evaluation with Interactive Mark Assignment
                        Column {
                            // Member's Response Box
                            Text(
                                text = "MEMBER'S SUBMITTED RESPONSE:",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = CyberTextMuted,
                                fontFamily = FontFamily.Monospace
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(CyberCodeBg, RoundedCornerShape(8.dp))
                                    .border(0.5.dp, CyberBorder, RoundedCornerShape(8.dp))
                                    .padding(10.dp)
                            ) {
                                Text(
                                    text = if (submittedAns.isBlank()) "[No response provided by candidate]" else submittedAns,
                                    fontSize = 12.5.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = if (submittedAns.isBlank()) CyberTextMuted else CyberTextPrimary,
                                    lineHeight = 18.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Lead Reference Rubric
                            Text(
                                text = "EVALUATION CRITERIA & RUBRIC:",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = CyberSecondary,
                                fontFamily = FontFamily.Monospace
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFF0F1E2E), RoundedCornerShape(8.dp))
                                    .padding(8.dp)
                            ) {
                                Text(
                                    text = q.referenceRubric,
                                    fontSize = 11.sp,
                                    color = Color(0xFFBAE6FD),
                                    lineHeight = 16.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Marks Slider & Quick Select
                            val currentMark = localMarks[q.id] ?: 0f
                            val currentNote = localNotes[q.id] ?: ""

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Assign Score:",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CyberTextPrimary
                                )
                                Text(
                                    text = "$currentMark / ${q.maxMarks} Marks",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CyberPrimary,
                                    fontFamily = FontFamily.Monospace
                                )
                            }

                            Slider(
                                value = currentMark,
                                onValueChange = { newScore ->
                                    val rounded = (Math.round(newScore * 2) / 2.0).toFloat()
                                    localMarks[q.id] = rounded
                                    onSaveGrading(selectedMember.id, selectedSection, q.id, rounded, null)
                                },
                                valueRange = 0f..q.maxMarks.toFloat(),
                                steps = (q.maxMarks * 2) - 1,
                                colors = SliderDefaults.colors(
                                    thumbColor = CyberSecondary,
                                    activeTrackColor = CyberPrimary,
                                    inactiveTrackColor = CyberSurfaceVariant
                                )
                            )

                            // Quick mark buttons (0, Half, Full)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                listOf(
                                    0f,
                                    (q.maxMarks / 2f),
                                    (q.maxMarks * 0.8f).let { (Math.round(it * 2) / 2.0).toFloat() },
                                    q.maxMarks.toFloat()
                                ).forEach { markVal ->
                                    OutlinedButton(
                                        onClick = {
                                            localMarks[q.id] = markVal
                                            onSaveGrading(selectedMember.id, selectedSection, q.id, markVal, null)
                                        },
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            containerColor = if (currentMark == markVal) CyberPrimary else Color.Transparent
                                        ),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder),
                                        shape = RoundedCornerShape(6.dp),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 3.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(
                                            text = "$markVal",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (currentMark == markVal) Color.White else CyberTextSecondary
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Lead evaluation note
                            OutlinedTextField(
                                value = currentNote,
                                onValueChange = { newNote ->
                                    localNotes[q.id] = newNote
                                    onSaveGrading(selectedMember.id, selectedSection, q.id, currentMark, newNote)
                                },
                                placeholder = {
                                    Text("Lead feedback/notes on this answer...", fontSize = 11.sp, color = CyberTextMuted)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("lead_note_q_${q.id}"),
                                textStyle = androidx.compose.ui.text.TextStyle(fontSize = 11.5.sp, color = CyberTextPrimary),
                                maxLines = 2,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = CyberSecondary,
                                    unfocusedBorderColor = CyberBorder
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
