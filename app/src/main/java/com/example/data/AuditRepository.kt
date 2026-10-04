package com.example.data

import com.example.model.AuditQuestions
import com.example.model.PerformanceClass
import com.example.model.Question
import com.example.model.QuestionType
import com.example.model.SectionType
import kotlinx.coroutines.flow.Flow
import org.json.JSONObject

data class EvaluationReport(
    val memberId: String,
    val memberName: String,
    val section: SectionType,
    val isSubmitted: Boolean,
    val isGraded: Boolean,
    val submittedAt: Long,
    val autoObjectiveScore: Float,
    val manualDescriptiveScore: Float,
    val totalScore: Float,
    val maxScore: Float,
    val percentage: Float,
    val performanceClass: PerformanceClass,
    val strongTopics: List<String>,
    val weakTopics: List<String>,
    val answeredCount: Int,
    val totalQuestions: Int,
    val answers: Map<Int, String>,
    val manualMarks: Map<Int, Float>,
    val leadNotes: Map<Int, String>
)

class AuditRepository(private val dao: SubmissionDao) {

    val allSubmissions: Flow<List<SubmissionEntity>> = dao.getAllSubmissions()

    fun getSubmission(memberId: String, section: SectionType): Flow<SubmissionEntity?> {
        val id = "${memberId}_${section.name}"
        return dao.getSubmission(id)
    }

    suspend fun getSubmissionDirect(memberId: String, section: SectionType): SubmissionEntity? {
        val id = "${memberId}_${section.name}"
        return dao.getSubmissionDirect(id)
    }

    suspend fun saveAnswer(
        memberId: String,
        memberName: String,
        section: SectionType,
        questionId: Int,
        answer: String
    ) {
        val id = "${memberId}_${section.name}"
        val existing = dao.getSubmissionDirect(id)
        val currentAnswers = parseStringMap(existing?.answersJson ?: "")
        val updatedAnswers = currentAnswers.toMutableMap().apply {
            put(questionId, answer)
        }
        val questions = AuditQuestions.getQuestionsForSection(section)
        val autoScore = calculateAutoObjectiveScore(questions, updatedAnswers)
        val manualMarks = parseFloatMap(existing?.manualMarksJson ?: "")
        val manualScore = manualMarks.values.sum()
        val maxScore = questions.sumOf { it.maxMarks }.toFloat()

        val entity = SubmissionEntity(
            id = id,
            memberId = memberId,
            memberName = memberName,
            sectionName = section.name,
            isSubmitted = existing?.isSubmitted ?: false,
            submittedAt = existing?.submittedAt ?: 0L,
            isGraded = existing?.isGraded ?: false,
            gradedAt = existing?.gradedAt ?: 0L,
            answersJson = mapToStringJson(updatedAnswers),
            manualMarksJson = existing?.manualMarksJson ?: "{}",
            leadNotesJson = existing?.leadNotesJson ?: "{}",
            autoObjectiveScore = autoScore,
            manualDescriptiveScore = manualScore,
            totalScore = autoScore + manualScore,
            maxScore = maxScore
        )
        dao.insertOrUpdate(entity)
    }

    suspend fun submitTest(
        memberId: String,
        memberName: String,
        section: SectionType,
        answers: Map<Int, String>
    ) {
        val id = "${memberId}_${section.name}"
        val existing = dao.getSubmissionDirect(id)
        val questions = AuditQuestions.getQuestionsForSection(section)
        val autoScore = calculateAutoObjectiveScore(questions, answers)
        val manualMarks = parseFloatMap(existing?.manualMarksJson ?: "")
        val manualScore = manualMarks.values.sum()
        val maxScore = questions.sumOf { it.maxMarks }.toFloat()

        val entity = SubmissionEntity(
            id = id,
            memberId = memberId,
            memberName = memberName,
            sectionName = section.name,
            isSubmitted = true,
            submittedAt = System.currentTimeMillis(),
            isGraded = existing?.isGraded ?: false,
            gradedAt = existing?.gradedAt ?: 0L,
            answersJson = mapToStringJson(answers),
            manualMarksJson = existing?.manualMarksJson ?: "{}",
            leadNotesJson = existing?.leadNotesJson ?: "{}",
            autoObjectiveScore = autoScore,
            manualDescriptiveScore = manualScore,
            totalScore = autoScore + manualScore,
            maxScore = maxScore
        )
        dao.insertOrUpdate(entity)
    }

    suspend fun updateGrading(
        memberId: String,
        section: SectionType,
        questionId: Int,
        mark: Float,
        note: String? = null
    ) {
        val id = "${memberId}_${section.name}"
        val existing = dao.getSubmissionDirect(id) ?: return
        val currentMarks = parseFloatMap(existing.manualMarksJson).toMutableMap()
        currentMarks[questionId] = mark
        val currentNotes = parseStringMap(existing.leadNotesJson).toMutableMap()
        if (note != null) {
            currentNotes[questionId] = note
        }
        val manualScore = currentMarks.values.sum()
        val total = existing.autoObjectiveScore + manualScore

        val updated = existing.copy(
            isGraded = true,
            gradedAt = System.currentTimeMillis(),
            manualMarksJson = mapToFloatJson(currentMarks),
            leadNotesJson = mapToStringJson(currentNotes),
            manualDescriptiveScore = manualScore,
            totalScore = total
        )
        dao.insertOrUpdate(updated)
    }

    suspend fun resetSubmission(memberId: String, section: SectionType) {
        val id = "${memberId}_${section.name}"
        dao.deleteSubmission(id)
    }

    suspend fun seedDemoTeamSubmissions() {
        // Pre-populate realistic submissions for the 5-member team so lead dashboard can be explored immediately
        val members = AuditQuestions.TEAM_MEMBERS
        val sections = listOf(
            SectionType.BACKEND,
            SectionType.FRONTEND,
            SectionType.INTEGRATION,
            SectionType.DEVOPS,
            SectionType.BACKEND
        )

        for (i in members.indices) {
            val member = members[i]
            val section = sections[i]
            val questions = AuditQuestions.getQuestionsForSection(section)
            val answers = mutableMapOf<Int, String>()
            val manualMarks = mutableMapOf<Int, Float>()
            val leadNotes = mutableMapOf<Int, String>()

            questions.forEach { q ->
                when (q.type) {
                    QuestionType.MCQ -> {
                        // High accuracy for member 0, 1, 2; medium for 3; learning for 4
                        val pickCorrect = when (i) {
                            0, 1 -> true
                            2 -> (q.id % 4 != 0)
                            3 -> (q.id % 2 == 0)
                            else -> (q.id % 3 == 0)
                        }
                        answers[q.id] = if (pickCorrect) q.correctAnswers.first() else "C"
                    }
                    QuestionType.MULTI_SELECT -> {
                        val pickAll = (i <= 2)
                        answers[q.id] = if (pickAll) q.correctAnswers.joinToString(",") else "A"
                    }
                    QuestionType.SHORT_ANSWER, QuestionType.SCENARIO -> {
                        val sampleText = when (q.id) {
                            5 -> "Authentication verifies who you are (e.g. JWT token or password), while Authorization checks what resources or routes your role is allowed to access."
                            7 -> "1. Inspect request payload in network tab. 2. Check backend error logs & stacktrace. 3. Verify HTTP status code (400 vs 500) and headers."
                            10 -> "An endpoint is a reachable URL path that exposes a resource. E.g. GET /api/v1/students or POST /api/tickets."
                            12 -> "Check environment variables on the cloud server, check port bindings and CORS origins."
                            13 -> "Input validation sanitizes data, prevents SQL injection/XSS, and guarantees database schema integrity."
                            else -> "Clear technical implementation using standard patterns, modular handlers, and error catching."
                        }
                        answers[q.id] = sampleText
                        val mark = when (i) {
                            0 -> 4.5f
                            1 -> 4.0f
                            2 -> 3.5f
                            3 -> 2.5f
                            else -> 3.0f
                        }
                        manualMarks[q.id] = mark
                        leadNotes[q.id] = "Solid grasp of fundamentals demonstrated."
                    }
                    QuestionType.FINAL_CHALLENGE -> {
                        answers[q.id] = "Comprehensive architecture:\n1. POST /api/v1/maintenance/tickets (Create request)\n2. GET /api/v1/maintenance/tickets?status=open (List open issues)\n3. PATCH /api/v1/maintenance/tickets/:id (Assign/Update status)\n4. GET /api/v1/maintenance/tickets/:id/history (Audit trail)\nIncludes JWT role protection and PostgreSQL schema."
                        val mark = when (i) {
                            0 -> 9.0f
                            1 -> 8.5f
                            2 -> 7.0f
                            3 -> 5.5f
                            else -> 6.0f
                        }
                        manualMarks[q.id] = mark
                        leadNotes[q.id] = "Comprehensive endpoint design with proper REST semantics."
                    }
                }
            }

            val autoScore = calculateAutoObjectiveScore(questions, answers)
            val manualScore = manualMarks.values.sum()
            val maxScore = questions.sumOf { it.maxMarks }.toFloat()

            val entity = SubmissionEntity(
                id = "${member.id}_${section.name}",
                memberId = member.id,
                memberName = member.name,
                sectionName = section.name,
                isSubmitted = true,
                submittedAt = System.currentTimeMillis() - (i * 3600000L),
                isGraded = (i < 4),
                gradedAt = System.currentTimeMillis() - (i * 1800000L),
                answersJson = mapToStringJson(answers),
                manualMarksJson = mapToFloatJson(manualMarks),
                leadNotesJson = mapToStringJson(leadNotes),
                autoObjectiveScore = autoScore,
                manualDescriptiveScore = manualScore,
                totalScore = autoScore + manualScore,
                maxScore = maxScore
            )
            dao.insertOrUpdate(entity)
        }
    }

    fun buildEvaluationReport(
        member: com.example.model.TeamMember,
        section: SectionType,
        entity: SubmissionEntity?
    ): EvaluationReport {
        val questions = AuditQuestions.getQuestionsForSection(section)
        val answers = parseStringMap(entity?.answersJson ?: "")
        val manualMarks = parseFloatMap(entity?.manualMarksJson ?: "")
        val leadNotes = parseStringMap(entity?.leadNotesJson ?: "")

        val autoScore = entity?.autoObjectiveScore ?: calculateAutoObjectiveScore(questions, answers)
        val manualScore = entity?.manualDescriptiveScore ?: manualMarks.values.sum()
        val total = autoScore + manualScore
        val maxScore = questions.sumOf { it.maxMarks }.toFloat()
        val percentage = if (maxScore > 0f) (total / maxScore) * 100f else 0f

        val performanceClass = when {
            percentage >= 80f -> PerformanceClass.EXCELLENT
            percentage >= 60f -> PerformanceClass.GOOD
            percentage >= 40f -> PerformanceClass.NEEDS_IMPROVEMENT
            else -> PerformanceClass.NOT_READY
        }

        // Topic-wise performance
        val topicScores = mutableMapOf<String, Pair<Float, Float>>() // topic -> (earned, max)
        questions.forEach { q ->
            val cur = topicScores.getOrDefault(q.topic, Pair(0f, 0f))
            val earned = when (q.type) {
                QuestionType.MCQ, QuestionType.MULTI_SELECT -> {
                    calculateSingleQuestionObjectiveScore(q, answers[q.id] ?: "")
                }
                QuestionType.SHORT_ANSWER, QuestionType.SCENARIO, QuestionType.FINAL_CHALLENGE -> {
                    manualMarks[q.id] ?: 0f
                }
            }
            topicScores[q.topic] = Pair(cur.first + earned, cur.second + q.maxMarks)
        }

        val strongTopics = mutableListOf<String>()
        val weakTopics = mutableListOf<String>()

        topicScores.forEach { (topic, pair) ->
            if (pair.second > 0f) {
                val topicPct = (pair.first / pair.second) * 100f
                if (topicPct >= 75f) strongTopics.add(topic)
                else if (topicPct < 60f) weakTopics.add(topic)
            }
        }

        val answeredCount = questions.count { q -> !answers[q.id].isNullOrBlank() }

        return EvaluationReport(
            memberId = member.id,
            memberName = member.name,
            section = section,
            isSubmitted = entity?.isSubmitted == true,
            isGraded = entity?.isGraded == true,
            submittedAt = entity?.submittedAt ?: 0L,
            autoObjectiveScore = autoScore,
            manualDescriptiveScore = manualScore,
            totalScore = total,
            maxScore = maxScore,
            percentage = percentage,
            performanceClass = performanceClass,
            strongTopics = strongTopics,
            weakTopics = weakTopics,
            answeredCount = answeredCount,
            totalQuestions = questions.size,
            answers = answers,
            manualMarks = manualMarks,
            leadNotes = leadNotes
        )
    }

    companion object {
        fun calculateSingleQuestionObjectiveScore(q: Question, answer: String): Float {
            if (answer.isBlank()) return 0f
            return when (q.type) {
                QuestionType.MCQ -> {
                    val correct = q.correctAnswers.firstOrNull() ?: ""
                    if (answer.trim().startsWith(correct, ignoreCase = true) ||
                        answer.trim().equals(correct, ignoreCase = true)
                    ) {
                        q.maxMarks.toFloat()
                    } else 0f
                }
                QuestionType.MULTI_SELECT -> {
                    val selected = answer.split(",").map { it.trim().uppercase() }.filter { it.isNotEmpty() }.toSet()
                    val correct = q.correctAnswers.map { it.trim().uppercase() }.toSet()
                    if (selected == correct) {
                        q.maxMarks.toFloat()
                    } else if (selected.isNotEmpty() && correct.containsAll(selected)) {
                        // Partial credit: picked valid options and no wrong ones
                        1.0f
                    } else 0f
                }
                else -> 0f
            }
        }

        fun calculateAutoObjectiveScore(questions: List<Question>, answers: Map<Int, String>): Float {
            var score = 0f
            questions.forEach { q ->
                if (q.type == QuestionType.MCQ || q.type == QuestionType.MULTI_SELECT) {
                    val ans = answers[q.id] ?: ""
                    score += calculateSingleQuestionObjectiveScore(q, ans)
                }
            }
            return score
        }

        fun parseStringMap(jsonStr: String): Map<Int, String> {
            if (jsonStr.isBlank()) return emptyMap()
            return try {
                val obj = JSONObject(jsonStr)
                val map = mutableMapOf<Int, String>()
                val keys = obj.keys()
                while (keys.hasNext()) {
                    val key = keys.next()
                    map[key.toInt()] = obj.getString(key)
                }
                map
            } catch (e: Exception) {
                emptyMap()
            }
        }

        fun parseFloatMap(jsonStr: String): Map<Int, Float> {
            if (jsonStr.isBlank()) return emptyMap()
            return try {
                val obj = JSONObject(jsonStr)
                val map = mutableMapOf<Int, Float>()
                val keys = obj.keys()
                while (keys.hasNext()) {
                    val key = keys.next()
                    map[key.toInt()] = obj.getDouble(key).toFloat()
                }
                map
            } catch (e: Exception) {
                emptyMap()
            }
        }

        fun mapToStringJson(map: Map<Int, String>): String {
            val obj = JSONObject()
            map.forEach { (k, v) -> obj.put(k.toString(), v) }
            return obj.toString()
        }

        fun mapToFloatJson(map: Map<Int, Float>): String {
            val obj = JSONObject()
            map.forEach { (k, v) -> obj.put(k.toString(), v.toDouble()) }
            return obj.toString()
        }
    }
}
