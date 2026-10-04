-- ==============================================================================
-- TEAM SKILL AUDIT - SUPABASE PRODUCTION DATABASE SCHEMA
-- ==============================================================================

-- 1. EXTENSIONS
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- 2. ENUM TYPES
DO $$ BEGIN
    CREATE TYPE section_enum AS ENUM ('BACKEND', 'FRONTEND', 'INTEGRATION', 'DEVOPS');
EXCEPTION
    WHEN duplicate_object THEN null;
END $$;

DO $$ BEGIN
    CREATE TYPE question_type_enum AS ENUM ('MCQ', 'MULTI_SELECT', 'SHORT_ANSWER', 'SCENARIO', 'FINAL_CHALLENGE');
EXCEPTION
    WHEN duplicate_object THEN null;
END $$;

DO $$ BEGIN
    CREATE TYPE performance_class_enum AS ENUM ('EXCELLENT', 'GOOD', 'NEEDS_IMPROVEMENT', 'NOT_READY');
EXCEPTION
    WHEN duplicate_object THEN null;
END $$;

-- ------------------------------------------------------------------------------
-- 3. PROFILES / TEAM MEMBERS TABLE
-- ------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS public.profiles (
    id UUID PRIMARY KEY REFERENCES auth.users(id) ON DELETE CASCADE,
    email TEXT UNIQUE NOT NULL,
    full_name TEXT NOT NULL,
    role TEXT NOT NULL DEFAULT 'member' CHECK (role IN ('member', 'lead', 'admin')),
    assigned_section section_enum NOT NULL DEFAULT 'BACKEND',
    avatar_color TEXT DEFAULT '#6366F1',
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- ------------------------------------------------------------------------------
-- 4. QUESTION BANK TABLE
-- ------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS public.questions (
    id SERIAL PRIMARY KEY,
    section section_enum NOT NULL,
    question_number INT NOT NULL CHECK (question_number BETWEEN 1 AND 15),
    type question_type_enum NOT NULL,
    text TEXT NOT NULL,
    options JSONB DEFAULT '[]'::jsonb, -- e.g. ["A) ...", "B) ..."]
    correct_answers JSONB DEFAULT '[]'::jsonb, -- e.g. ["A"] or ["A", "B", "D"]
    max_marks NUMERIC(4,2) NOT NULL DEFAULT 1.0,
    topic TEXT NOT NULL,
    reference_rubric TEXT NOT NULL, -- Grading guide for lead
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (section, question_number)
);

-- ------------------------------------------------------------------------------
-- 5. TEST SUBMISSIONS TABLE (Main Slip-Test State)
-- ------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS public.test_submissions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID REFERENCES auth.users(id) ON DELETE CASCADE,
    member_name TEXT NOT NULL,
    section section_enum NOT NULL,
    is_submitted BOOLEAN NOT NULL DEFAULT false,
    submitted_at TIMESTAMPTZ,
    is_graded BOOLEAN NOT NULL DEFAULT false,
    graded_at TIMESTAMPTZ,
    
    -- Candidate responses map: { "1": "A", "4": "A,B,D", "5": "Authentication verifies who..." }
    answers JSONB NOT NULL DEFAULT '{}'::jsonb,
    
    -- Lead manual marks map: { "5": 4.5, "7": 5.0, "15": 9.0 }
    manual_marks JSONB NOT NULL DEFAULT '{}'::jsonb,
    
    -- Lead notes map: { "5": "Good grasp of token vs session" }
    lead_notes JSONB NOT NULL DEFAULT '{}'::jsonb,
    
    -- Aggregated scores
    auto_objective_score NUMERIC(5,2) NOT NULL DEFAULT 0.0,
    manual_descriptive_score NUMERIC(5,2) NOT NULL DEFAULT 0.0,
    total_score NUMERIC(5,2) NOT NULL DEFAULT 0.0,
    max_score NUMERIC(5,2) NOT NULL DEFAULT 46.0,
    percentage NUMERIC(5,2) NOT NULL DEFAULT 0.0,
    performance_class performance_class_enum NOT NULL DEFAULT 'NOT_READY',
    
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    
    -- Constraint: One active test per user per section
    UNIQUE (user_id, section)
);

-- ------------------------------------------------------------------------------
-- 6. AUDIT LOGS TABLE (For Lead Tracking)
-- ------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS public.audit_logs (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    submission_id UUID REFERENCES public.test_submissions(id) ON DELETE CASCADE,
    lead_user_id UUID REFERENCES auth.users(id) ON DELETE SET NULL,
    action TEXT NOT NULL, -- 'AUTO_SCORED', 'DESCRIPTIVE_GRADED', 'STATUS_CHANGED'
    details JSONB DEFAULT '{}'::jsonb,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- ------------------------------------------------------------------------------
-- 7. ROW LEVEL SECURITY (RLS) POLICIES
-- ------------------------------------------------------------------------------
ALTER TABLE public.profiles ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.questions ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.test_submissions ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.audit_logs ENABLE ROW LEVEL SECURITY;

-- Profiles Policies
CREATE POLICY "Public profiles are readable by authenticated users"
ON public.profiles FOR SELECT
TO authenticated
USING (true);

CREATE POLICY "Users can update their own profile"
ON public.profiles FOR UPDATE
TO authenticated
USING (auth.uid() = id);

-- Questions Policies:
-- Students can read questions, but correct_answers and reference_rubric should only be viewed by leads or stripped in public view
CREATE POLICY "Anyone can view test questions"
ON public.questions FOR SELECT
TO authenticated
USING (true);

-- Test Submissions Policies:
-- 1. Members can view and update their own submission
CREATE POLICY "Users can view their own submissions"
ON public.test_submissions FOR SELECT
TO authenticated
USING (auth.uid() = user_id OR EXISTS (
    SELECT 1 FROM public.profiles WHERE id = auth.uid() AND role IN ('lead', 'admin')
));

CREATE POLICY "Users can insert their own submissions"
ON public.test_submissions FOR INSERT
TO authenticated
WITH CHECK (auth.uid() = user_id);

CREATE POLICY "Users can update their own unsubmitted tests"
ON public.test_submissions FOR UPDATE
TO authenticated
USING (
    (auth.uid() = user_id AND is_submitted = false)
    OR EXISTS (SELECT 1 FROM public.profiles WHERE id = auth.uid() AND role IN ('lead', 'admin'))
);

-- ------------------------------------------------------------------------------
-- 8. AUTOMATIC SCORE CALCULATION TRIGGER
-- ------------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION public.handle_submission_score_update()
RETURNS TRIGGER AS $$
BEGIN
    NEW.total_score := COALESCE(NEW.auto_objective_score, 0) + COALESCE(NEW.manual_descriptive_score, 0);
    IF NEW.max_score > 0 THEN
        NEW.percentage := ROUND((NEW.total_score / NEW.max_score) * 100, 2);
    ELSE
        NEW.percentage := 0;
    END IF;

    IF NEW.percentage >= 80 THEN
        NEW.performance_class := 'EXCELLENT';
    ELSIF NEW.percentage >= 60 THEN
        NEW.performance_class := 'GOOD';
    ELSIF NEW.percentage >= 40 THEN
        NEW.performance_class := 'NEEDS_IMPROVEMENT';
    ELSE
        NEW.performance_class := 'NOT_READY';
    END IF;

    NEW.updated_at := now();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trigger_calculate_scores ON public.test_submissions;
CREATE TRIGGER trigger_calculate_scores
BEFORE INSERT OR UPDATE OF auto_objective_score, manual_descriptive_score, max_score
ON public.test_submissions
FOR EACH ROW
EXECUTE FUNCTION public.handle_submission_score_update();

-- ------------------------------------------------------------------------------
-- 9. USER SIGNUP PROFILE SYNCHRONIZATION TRIGGER
-- ------------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION public.handle_new_user()
RETURNS TRIGGER AS $$
BEGIN
    INSERT INTO public.profiles (id, email, full_name, role)
    VALUES (
        NEW.id,
        NEW.email,
        COALESCE(NEW.raw_user_meta_data->>'full_name', split_part(NEW.email, '@', 1)),
        COALESCE(NEW.raw_user_meta_data->>'role', 'member')
    )
    ON CONFLICT (id) DO NOTHING;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

DROP TRIGGER IF EXISTS on_auth_user_created ON auth.users;
CREATE TRIGGER on_auth_user_created
AFTER INSERT ON auth.users
FOR EACH ROW EXECUTE FUNCTION public.handle_new_user();
