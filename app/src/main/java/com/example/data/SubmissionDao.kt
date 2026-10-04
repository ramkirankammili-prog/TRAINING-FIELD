package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SubmissionDao {
    @Query("SELECT * FROM test_submissions ORDER BY submittedAt DESC")
    fun getAllSubmissions(): Flow<List<SubmissionEntity>>

    @Query("SELECT * FROM test_submissions WHERE id = :id LIMIT 1")
    fun getSubmission(id: String): Flow<SubmissionEntity?>

    @Query("SELECT * FROM test_submissions WHERE id = :id LIMIT 1")
    suspend fun getSubmissionDirect(id: String): SubmissionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(submission: SubmissionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(submissions: List<SubmissionEntity>)

    @Query("DELETE FROM test_submissions WHERE id = :id")
    suspend fun deleteSubmission(id: String)

    @Query("DELETE FROM test_submissions")
    suspend fun clearAll()
}
