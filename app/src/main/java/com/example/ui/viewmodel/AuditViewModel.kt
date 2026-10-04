package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.AuditRepository
import com.example.data.EvaluationReport
import com.example.model.AuditQuestions
import com.example.model.SectionType
import com.example.model.TeamMember
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AuditViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: AuditRepository

    init {
        val db = AppDatabase.getInstance(application)
        repository = AuditRepository(db.submissionDao())
    }

    private val _activeMember = MutableStateFlow(AuditQuestions.TEAM_MEMBERS[0])
    val activeMember: StateFlow<TeamMember> = _activeMember.asStateFlow()

    private val _selectedSection = MutableStateFlow(SectionType.BACKEND)
    val selectedSection: StateFlow<SectionType> = _selectedSection.asStateFlow()

    private val _isLeadMode = MutableStateFlow(false)
    val isLeadMode: StateFlow<Boolean> = _isLeadMode.asStateFlow()

    // Reports map: key is "${memberId}_${sectionName}"
    val reports: StateFlow<Map<String, EvaluationReport>> = repository.allSubmissions
        .combine(_activeMember) { submissions, _ ->
            val map = mutableMapOf<String, EvaluationReport>()
            val submissionsMap = submissions.associateBy { it.id }

            AuditQuestions.TEAM_MEMBERS.forEach { member ->
                listOf(
                    SectionType.BACKEND,
                    SectionType.FRONTEND,
                    SectionType.INTEGRATION,
                    SectionType.DEVOPS
                ).forEach { sec ->
                    val id = "${member.id}_${sec.name}"
                    val entity = submissionsMap[id]
                    map[id] = repository.buildEvaluationReport(member, sec, entity)
                }
            }
            map
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyMap()
        )

    fun selectMember(member: TeamMember) {
        _activeMember.value = member
        // Also auto-switch to their assigned section for seamless member experience
        _selectedSection.value = member.assignedSection
    }

    fun selectSection(section: SectionType) {
        _selectedSection.value = section
    }

    fun setLeadMode(enabled: Boolean) {
        _isLeadMode.value = enabled
    }

    fun saveAnswer(questionId: Int, answer: String) {
        val member = _activeMember.value
        val section = _selectedSection.value
        viewModelScope.launch {
            repository.saveAnswer(member.id, member.name, section, questionId, answer)
        }
    }

    fun submitTest(answers: Map<Int, String>) {
        val member = _activeMember.value
        val section = _selectedSection.value
        viewModelScope.launch {
            repository.submitTest(member.id, member.name, section, answers)
        }
    }

    fun updateGrading(memberId: String, section: SectionType, questionId: Int, mark: Float, note: String?) {
        viewModelScope.launch {
            repository.updateGrading(memberId, section, questionId, mark, note)
        }
    }

    fun resetSubmission(memberId: String, section: SectionType) {
        viewModelScope.launch {
            repository.resetSubmission(memberId, section)
        }
    }

    fun seedDemoTeamSubmissions() {
        viewModelScope.launch {
            repository.seedDemoTeamSubmissions()
        }
    }
}
