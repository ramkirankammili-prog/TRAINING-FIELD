package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.AuditQuestions
import com.example.ui.components.DesignedByHarryWatermark
import com.example.ui.components.SupabaseAuthDialog
import com.example.ui.components.TopNavBar
import com.example.ui.screens.LeadDashboardScreen
import com.example.ui.screens.MemberTestScreen
import com.example.ui.theme.CyberBg
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.AuditViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: AuditViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                TeamSkillAuditApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun TeamSkillAuditApp(viewModel: AuditViewModel) {
    val activeMember by viewModel.activeMember.collectAsStateWithLifecycle()
    val selectedSection by viewModel.selectedSection.collectAsStateWithLifecycle()
    val isLeadMode by viewModel.isLeadMode.collectAsStateWithLifecycle()
    val reports by viewModel.reports.collectAsStateWithLifecycle()

    var showAuthDialog by remember { mutableStateOf(false) }
    var authenticatedEmail by remember { mutableStateOf<String?>(null) }

    val currentSubmissionKey = "${activeMember.id}_${selectedSection.name}"
    val currentReport = reports[currentSubmissionKey]

    // Handle back button when in Lead Mode
    BackHandler(enabled = isLeadMode) {
        viewModel.setLeadMode(false)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = CyberBg,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { _ ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(CyberBg)
                .navigationBarsPadding()
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Fixed Top Navigation Bar (Positioned right at the top)
                TopNavBar(
                    selectedSection = selectedSection,
                    onSectionSelected = { sec ->
                        viewModel.selectSection(sec)
                    },
                    activeMember = activeMember,
                    onMemberChanged = { mem ->
                        viewModel.selectMember(mem)
                    },
                    isLeadMode = isLeadMode,
                    onToggleLeadMode = { enabled ->
                        viewModel.setLeadMode(enabled)
                    },
                    authenticatedEmail = authenticatedEmail,
                    onOpenAuth = {
                        showAuthDialog = true
                    },
                    modifier = Modifier.statusBarsPadding()
                )

                // Main Workspace: Member Question Paper OR Lead Evaluation Suite
                Box(modifier = Modifier.weight(1f)) {
                    if (isLeadMode) {
                        LeadDashboardScreen(
                            members = AuditQuestions.TEAM_MEMBERS,
                            reports = reports,
                            selectedMember = activeMember,
                            onSelectMember = { mem -> viewModel.selectMember(mem) },
                            selectedSection = selectedSection,
                            onSelectSection = { sec -> viewModel.selectSection(sec) },
                            onSaveGrading = { memId, sec, qId, mark, note ->
                                viewModel.updateGrading(memId, sec, qId, mark, note)
                            },
                            onSeedDemoData = {
                                viewModel.seedDemoTeamSubmissions()
                            }
                        )
                    } else {
                        MemberTestScreen(
                            section = selectedSection,
                            member = activeMember,
                            isSubmitted = currentReport?.isSubmitted == true,
                            existingAnswers = currentReport?.answers ?: emptyMap(),
                            onSaveAnswer = { qId, ans ->
                                viewModel.saveAnswer(qId, ans)
                            },
                            onSubmitTest = { answers ->
                                viewModel.submitTest(answers)
                            },
                            onResetTestForDemo = {
                                viewModel.resetSubmission(activeMember.id, selectedSection)
                            }
                        )
                    }
                }
            }

            // Fixed Subtle Watermark on side of screen (Non-intrusive)
            DesignedByHarryWatermark(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 12.dp, bottom = 12.dp)
            )

            // Supabase Auth Dialog (Sign In / Sign Up)
            SupabaseAuthDialog(
                isOpen = showAuthDialog,
                onDismiss = { showAuthDialog = false },
                onAuthSuccess = { userEmail ->
                    authenticatedEmail = userEmail
                    showAuthDialog = false
                    // Redirect the user to the Home page ('/')
                    viewModel.setLeadMode(false)
                }
            )
        }
    }
}
