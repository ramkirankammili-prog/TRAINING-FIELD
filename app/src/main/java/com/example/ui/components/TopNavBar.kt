package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.IntegrationInstructions
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Web
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AuditQuestions
import com.example.model.SectionType
import com.example.model.TeamMember
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberBorderActive
import com.example.ui.theme.CyberPrimary
import com.example.ui.theme.CyberSecondary
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.CyberTextMuted
import com.example.ui.theme.CyberTextPrimary
import com.example.ui.theme.CyberTextSecondary

@Composable
fun TopNavBar(
    selectedSection: SectionType,
    onSectionSelected: (SectionType) -> Unit,
    activeMember: TeamMember,
    onMemberChanged: (TeamMember) -> Unit,
    isLeadMode: Boolean,
    onToggleLeadMode: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    authenticatedEmail: String? = null,
    onOpenAuth: () -> Unit = {}
) {
    var memberMenuExpanded by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color(0xFF0C101A),
        tonalElevation = 6.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(width = 0.5.dp, color = CyberBorder)
        ) {
            // Top Status Bar: Branding, Active Member Picker, and Lead Dashboard Toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 5.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Brand Logo & Title
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onToggleLeadMode(false) }
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(CyberPrimary, CyberSecondary)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = "Audit Logo",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(9.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "TEAM SKILL AUDIT",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 14.sp,
                                letterSpacing = 1.2.sp,
                                color = CyberTextPrimary,
                                fontFamily = FontFamily.Monospace
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0xFF1E293B))
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = "SLIP-TEST",
                                    fontSize = 8.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CyberSecondary
                                )
                            }
                        }
                        Text(
                            text = "Internal Assessment Suite",
                            fontSize = 10.sp,
                            color = CyberTextMuted
                        )
                    }
                }

                // Controls: Member Selector + Lead Mode Switcher
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Member Profile Selector
                    Box {
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(CyberSurface)
                                .border(1.dp, CyberBorder, RoundedCornerShape(20.dp))
                                .clickable { memberMenuExpanded = true }
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                                .testTag("member_selector_button"),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(22.dp)
                                    .clip(CircleShape)
                                    .background(Color(activeMember.avatarBgHex)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = activeMember.name.first().toString(),
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text(
                                    text = activeMember.name,
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = CyberTextPrimary
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = memberMenuExpanded,
                            onDismissRequest = { memberMenuExpanded = false },
                            modifier = Modifier
                                .background(CyberSurface)
                                .border(1.dp, CyberBorder, RoundedCornerShape(8.dp))
                        ) {
                            Text(
                                text = "SELECT TEAM MEMBER (5 TOTAL)",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = CyberTextMuted,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                            )
                            AuditQuestions.TEAM_MEMBERS.forEach { member ->
                                DropdownMenuItem(
                                    text = {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(
                                                modifier = Modifier
                                                    .size(22.dp)
                                                    .clip(CircleShape)
                                                    .background(Color(member.avatarBgHex)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = member.name.first().toString(),
                                                    color = Color.White,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Column {
                                                Text(
                                                    text = member.name,
                                                    fontSize = 12.5.sp,
                                                    fontWeight = if (member.id == activeMember.id) FontWeight.Bold else FontWeight.Normal,
                                                    color = if (member.id == activeMember.id) CyberSecondary else CyberTextPrimary
                                                )
                                                Text(
                                                    text = "Assigned: ${member.assignedSection.shortTitle}",
                                                    fontSize = 10.sp,
                                                    color = CyberTextMuted
                                                )
                                            }
                                        }
                                    },
                                    onClick = {
                                        onMemberChanged(member)
                                        memberMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Supabase Auth Button
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF131A29))
                            .border(1.dp, CyberBorder, RoundedCornerShape(8.dp))
                            .clickable { onOpenAuth() }
                            .padding(horizontal = 7.dp, vertical = 5.dp)
                            .testTag("supabase_auth_button"),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Supabase Auth",
                            tint = if (authenticatedEmail != null) CyberSecondary else CyberTextMuted,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (authenticatedEmail != null) authenticatedEmail.substringBefore("@") else "Sign In",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (authenticatedEmail != null) CyberSecondary else CyberTextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Lead Mode Toggle Button
                    val leadBtnBg by animateColorAsState(
                        targetValue = if (isLeadMode) CyberPrimary else Color(0xFF131A29),
                        label = "lead_btn_bg"
                    )
                    val leadBorder by animateColorAsState(
                        targetValue = if (isLeadMode) CyberSecondary else CyberBorder,
                        label = "lead_btn_border"
                    )

                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(leadBtnBg)
                            .border(1.dp, leadBorder, RoundedCornerShape(8.dp))
                            .clickable { onToggleLeadMode(!isLeadMode) }
                            .padding(horizontal = 9.dp, vertical = 6.dp)
                            .testTag("lead_dashboard_toggle"),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isLeadMode) Icons.Default.AdminPanelSettings else Icons.Default.Lock,
                            contentDescription = "Lead Mode",
                            tint = if (isLeadMode) Color.White else CyberSecondary,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = if (isLeadMode) "EXIT LEAD" else "LEAD DASHBOARD",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = if (isLeadMode) Color.White else CyberTextPrimary
                        )
                    }
                }
            }

            // Fixed Top Navigation Bar with Separate Tabs (1 to 4)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF090D15))
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 8.dp, vertical = 3.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                SectionTabItem(
                    index = 1,
                    title = "Backend / APIs",
                    icon = Icons.Default.Dns,
                    isSelected = !isLeadMode && selectedSection == SectionType.BACKEND,
                    onClick = {
                        onToggleLeadMode(false)
                        onSectionSelected(SectionType.BACKEND)
                    }
                )

                SectionTabItem(
                    index = 2,
                    title = "Frontend / UI",
                    icon = Icons.Default.Web,
                    isSelected = !isLeadMode && selectedSection == SectionType.FRONTEND,
                    onClick = {
                        onToggleLeadMode(false)
                        onSectionSelected(SectionType.FRONTEND)
                    }
                )

                SectionTabItem(
                    index = 3,
                    title = "Integration / Testing",
                    icon = Icons.Default.IntegrationInstructions,
                    isSelected = !isLeadMode && selectedSection == SectionType.INTEGRATION,
                    onClick = {
                        onToggleLeadMode(false)
                        onSectionSelected(SectionType.INTEGRATION)
                    }
                )

                SectionTabItem(
                    index = 4,
                    title = "DevOps / Deployment",
                    icon = Icons.Default.CloudDone,
                    isSelected = !isLeadMode && selectedSection == SectionType.DEVOPS,
                    onClick = {
                        onToggleLeadMode(false)
                        onSectionSelected(SectionType.DEVOPS)
                    }
                )
            }
        }
    }
}

@Composable
fun SectionTabItem(
    index: Int,
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val bg by animateColorAsState(
        targetValue = if (isSelected) Color(0xFF1E263D) else Color.Transparent,
        label = "tab_bg"
    )
    val border by animateColorAsState(
        targetValue = if (isSelected) CyberPrimary else Color.Transparent,
        label = "tab_border"
    )

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bg)
            .border(1.dp, border, RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 7.dp)
            .testTag("section_tab_$index"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .clip(CircleShape)
                .background(if (isSelected) CyberPrimary else CyberSurfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "$index",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) Color.White else CyberTextMuted,
                fontFamily = FontFamily.Monospace
            )
        }

        Spacer(modifier = Modifier.width(7.dp))

        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isSelected) CyberSecondary else CyberTextMuted,
            modifier = Modifier.size(15.dp)
        )

        Spacer(modifier = Modifier.width(6.dp))

        Text(
            text = title,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) CyberTextPrimary else CyberTextSecondary
        )
    }
}
