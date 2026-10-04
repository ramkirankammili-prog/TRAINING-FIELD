package com.example

import com.example.data.AuditRepository
import com.example.model.AuditQuestions
import com.example.model.SectionType
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testAllSectionsHave15Questions() {
        SectionType.values().forEach { section ->
            val questions = AuditQuestions.getQuestionsForSection(section)
            assertEquals("Section ${section.title} should have 15 questions", 15, questions.size)
        }
    }

    @Test
    fun testTeamMembersCount() {
        assertEquals("There should be 5 team members", 5, AuditQuestions.TEAM_MEMBERS.size)
    }

    @Test
    fun testObjectiveScoringMCQ() {
        val q1 = AuditQuestions.BACKEND_QUESTIONS.first { it.id == 1 }
        val scoreCorrect = AuditRepository.calculateSingleQuestionObjectiveScore(q1, "A")
        val scoreWrong = AuditRepository.calculateSingleQuestionObjectiveScore(q1, "B")
        assertEquals(1.0f, scoreCorrect, 0.01f)
        assertEquals(0.0f, scoreWrong, 0.01f)
    }

    @Test
    fun testObjectiveScoringMultiSelect() {
        val q4 = AuditQuestions.BACKEND_QUESTIONS.first { it.id == 4 }
        val scoreFull = AuditRepository.calculateSingleQuestionObjectiveScore(q4, "A,B,D")
        val scorePartial = AuditRepository.calculateSingleQuestionObjectiveScore(q4, "A,B")
        val scoreWrong = AuditRepository.calculateSingleQuestionObjectiveScore(q4, "A,C")
        assertEquals(2.0f, scoreFull, 0.01f)
        assertEquals(1.0f, scorePartial, 0.01f)
        assertEquals(0.0f, scoreWrong, 0.01f)
    }
}

