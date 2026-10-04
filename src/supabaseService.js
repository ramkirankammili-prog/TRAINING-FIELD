import { supabase } from './supabaseClient';

/**
 * Supabase Data Service for Team Skill Audit
 */
export const supabaseService = {
  // 1. Fetch Question Paper for a given Section
  async getQuestions(section) {
    const { data, error } = await supabase
      .from('questions')
      .select('*')
      .eq('section', section)
      .order('question_number', { ascending: true });

    if (error) throw error;
    return data;
  },

  // 2. Fetch User Submission for a Section
  async getSubmission(userId, section) {
    const { data, error } = await supabase
      .from('test_submissions')
      .select('*')
      .eq('user_id', userId)
      .eq('section', section)
      .maybeSingle();

    if (error) throw error;
    return data;
  },

  // 3. Save or Update In-Progress Test Answers
  async saveAnswers(userId, memberName, section, answers, autoScore = 0) {
    const { data, error } = await supabase
      .from('test_submissions')
      .upsert(
        {
          user_id: userId,
          member_name: memberName,
          section: section,
          answers: answers,
          auto_objective_score: autoScore,
          is_submitted: false,
        },
        { onConflict: 'user_id,section' }
      )
      .select()
      .single();

    if (error) throw error;
    return data;
  },

  // 4. Final Test Submission (Locks the paper)
  async submitTest(userId, memberName, section, answers, autoScore = 0) {
    const { data, error } = await supabase
      .from('test_submissions')
      .upsert(
        {
          user_id: userId,
          member_name: memberName,
          section: section,
          answers: answers,
          auto_objective_score: autoScore,
          is_submitted: true,
          submitted_at: new Date().toISOString(),
        },
        { onConflict: 'user_id,section' }
      )
      .select()
      .single();

    if (error) throw error;
    return data;
  },

  // 5. Lead Evaluation: Fetch All Team Submissions
  async getAllSubmissions() {
    const { data, error } = await supabase
      .from('test_submissions')
      .select('*, profiles(email, full_name, role, assigned_section)')
      .order('submitted_at', { ascending: false });

    if (error) throw error;
    return data;
  },

  // 6. Lead Evaluation: Grade Descriptive Questions & Assign Marks
  async gradeSubmission(submissionId, manualMarks, leadNotes, manualScore) {
    const { data, error } = await supabase
      .from('test_submissions')
      .update({
        manual_marks: manualMarks,
        lead_notes: leadNotes,
        manual_descriptive_score: manualScore,
        is_graded: true,
        graded_at: new Date().toISOString(),
      })
      .eq('id', submissionId)
      .select()
      .single();

    if (error) throw error;
    return data;
  },
};
