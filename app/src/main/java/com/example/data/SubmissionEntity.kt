package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "test_submissions")
data class SubmissionEntity(
    @PrimaryKey
    val id: String, // "${memberId}_${sectionName}"
    val memberId: String,
    val memberName: String,
    val sectionName: String,
    val isSubmitted: Boolean = false,
    val submittedAt: Long = 0L,
    val isGraded: Boolean = false,
    val gradedAt: Long = 0L,
    val answersJson: String = "", // formatted JSON
    val manualMarksJson: String = "", // formatted JSON
    val leadNotesJson: String = "", // formatted JSON
    val autoObjectiveScore: Float = 0f,
    val manualDescriptiveScore: Float = 0f,
    val totalScore: Float = 0f,
    val maxScore: Float = 46f
)
