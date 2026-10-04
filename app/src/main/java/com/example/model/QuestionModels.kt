package com.example.model

enum class SectionType(val title: String, val shortTitle: String, val description: String) {
    BACKEND("Backend / APIs", "Backend", "APIs, HTTP Protocols, Databases, Auth & Architecture"),
    FRONTEND("Frontend / UI", "Frontend", "User Interfaces, React, Responsive Design, State & UX"),
    INTEGRATION("Integration / Testing", "Testing", "Integration Tests, Bug Triage, Network & Demo QA"),
    DEVOPS("DevOps / Deployment", "DevOps", "Docker, CI/CD, Git, Environments & Incident Response")
}

enum class QuestionType(val label: String) {
    MCQ("OMR Single Choice"),
    MULTI_SELECT("OMR Multi-Select"),
    SHORT_ANSWER("Short Answer"),
    SCENARIO("Practical Scenario"),
    FINAL_CHALLENGE("Final System Challenge")
}

data class Question(
    val id: Int,
    val section: SectionType,
    val type: QuestionType,
    val text: String,
    val options: List<String> = emptyList(), // For MCQ & Multi-select
    val correctAnswers: List<String> = emptyList(), // e.g. ["A"] or ["A", "B", "D"] - NEVER revealed to member!
    val maxMarks: Int,
    val topic: String,
    val referenceRubric: String // Guide for Team Lead when manually grading
)

data class TeamMember(
    val id: String,
    val name: String,
    val email: String,
    val assignedSection: SectionType,
    val avatarBgHex: Long = 0xFF6366F1
)

enum class PerformanceClass(val label: String, val colorHex: Long) {
    EXCELLENT("EXCELLENT", 0xFF10B981), // Emerald
    GOOD("GOOD", 0xFF06B6D4), // Cyan
    NEEDS_IMPROVEMENT("NEEDS IMPROVEMENT", 0xFFF59E0B), // Amber
    NOT_READY("NOT READY", 0xFFEF4444) // Red
}

object AuditQuestions {
    val TEAM_MEMBERS = listOf(
        TeamMember("m1", "Harry", "harry.lead@hackathon.internal", SectionType.BACKEND, 0xFF6366F1),
        TeamMember("m2", "Sarah Chen", "sarah.ui@hackathon.internal", SectionType.FRONTEND, 0xFFEC4899),
        TeamMember("m3", "Jordan Patel", "jordan.qa@hackathon.internal", SectionType.INTEGRATION, 0xFF10B981),
        TeamMember("m4", "Priya Sharma", "priya.ops@hackathon.internal", SectionType.DEVOPS, 0xFFF59E0B),
        TeamMember("m5", "Liam Vance", "liam.fullstack@hackathon.internal", SectionType.BACKEND, 0xFF8B5CF6)
    )

    val BACKEND_QUESTIONS = listOf(
        Question(
            id = 1,
            section = SectionType.BACKEND,
            type = QuestionType.MCQ,
            text = "What is an API used for?",
            options = listOf(
                "A) Connecting different parts of an application",
                "B) Designing logos",
                "C) Editing videos",
                "D) Increasing screen brightness"
            ),
            correctAnswers = listOf("A"),
            maxMarks = 1,
            topic = "Core API Concepts",
            referenceRubric = "Option A is the fundamental definition of Application Programming Interfaces."
        ),
        Question(
            id = 2,
            section = SectionType.BACKEND,
            type = QuestionType.MCQ,
            text = "Which HTTP method is normally used to get data?",
            options = listOf(
                "A) GET",
                "B) POST",
                "C) DELETE",
                "D) PATCH"
            ),
            correctAnswers = listOf("A"),
            maxMarks = 1,
            topic = "HTTP Protocol",
            referenceRubric = "GET method is the idempotent HTTP verb used for retrieving representations of resources."
        ),
        Question(
            id = 3,
            section = SectionType.BACKEND,
            type = QuestionType.MCQ,
            text = "What does HTTP 404 usually mean?",
            options = listOf(
                "A) Server is successful",
                "B) Resource was not found",
                "C) Password is correct",
                "D) Database is deleted"
            ),
            correctAnswers = listOf("B"),
            maxMarks = 1,
            topic = "HTTP Protocol",
            referenceRubric = "404 Not Found indicates the origin server did not find a current representation for target resource."
        ),
        Question(
            id = 4,
            section = SectionType.BACKEND,
            type = QuestionType.MULTI_SELECT,
            text = "Which are good API practices? (Select all that apply)",
            options = listOf(
                "A) Validate input",
                "B) Use meaningful status codes",
                "C) Expose passwords in errors",
                "D) Keep response formats consistent"
            ),
            correctAnswers = listOf("A", "B", "D"),
            maxMarks = 2,
            topic = "API Best Practices",
            referenceRubric = "A, B, and D are standard REST engineering standards. C is a critical security vulnerability."
        ),
        Question(
            id = 5,
            section = SectionType.BACKEND,
            type = QuestionType.SHORT_ANSWER,
            text = "What is the difference between authentication and authorization?",
            maxMarks = 5,
            topic = "Security & Auth",
            referenceRubric = "Authentication: Verifying identity ('Who are you?'). Authorization: Granting permissions to resources ('What can you access?'). Look for clear separation and token/role examples."
        ),
        Question(
            id = 6,
            section = SectionType.BACKEND,
            type = QuestionType.MCQ,
            text = "Where should secret API keys normally be kept?",
            options = listOf(
                "A) Environment variables / secret storage",
                "B) Public frontend code",
                "C) README file",
                "D) Screenshot"
            ),
            correctAnswers = listOf("A"),
            maxMarks = 1,
            topic = "Security & Secrets",
            referenceRubric = "Secrets must live in runtime environment variables, vaults, or encrypted secret managers."
        ),
        Question(
            id = 7,
            section = SectionType.BACKEND,
            type = QuestionType.SCENARIO,
            text = "The frontend sends data to the backend, but the backend returns an error. What are the first 3 things you would check?",
            maxMarks = 5,
            topic = "Debugging & Troubleshooting",
            referenceRubric = "1) HTTP status code & response error body/schema. 2) Backend console/server logs & exception stack traces. 3) Network payload (headers, JSON formatting, Content-Type) vs backend validation constraints."
        ),
        Question(
            id = 8,
            section = SectionType.BACKEND,
            type = QuestionType.MCQ,
            text = "What is a database used for?",
            options = listOf(
                "A) Storing application data",
                "B) Styling buttons",
                "C) Creating animations",
                "D) Hosting images only"
            ),
            correctAnswers = listOf("A"),
            maxMarks = 1,
            topic = "Database Fundamentals",
            referenceRubric = "Databases provide structured, persistent data storage and querying capabilities."
        ),
        Question(
            id = 9,
            section = SectionType.BACKEND,
            type = QuestionType.MULTI_SELECT,
            text = "Which can cause an API to fail? (Select all that apply)",
            options = listOf(
                "A) Invalid input",
                "B) Database failure",
                "C) Wrong endpoint",
                "D) Good documentation"
            ),
            correctAnswers = listOf("A", "B", "C"),
            maxMarks = 2,
            topic = "API Reliability",
            referenceRubric = "A (validation reject), B (500 internal crash), and C (404/wrong routing) cause API failures."
        ),
        Question(
            id = 10,
            section = SectionType.BACKEND,
            type = QuestionType.SHORT_ANSWER,
            text = "What is an API endpoint? Give one example.",
            maxMarks = 5,
            topic = "API Architecture",
            referenceRubric = "Endpoint definition: A specific URI/URL path where an API receives requests to perform operations on a resource. Example: GET /api/v1/users or POST /api/tasks."
        ),
        Question(
            id = 11,
            section = SectionType.BACKEND,
            type = QuestionType.MCQ,
            text = "What does HTTP 500 generally indicate?",
            options = listOf(
                "A) Server-side error",
                "B) Successful request",
                "C) Page not found",
                "D) User logged out"
            ),
            correctAnswers = listOf("A"),
            maxMarks = 1,
            topic = "HTTP Protocol",
            referenceRubric = "500 Internal Server Error represents an unexpected unhandled condition encountered by the server."
        ),
        Question(
            id = 12,
            section = SectionType.BACKEND,
            type = QuestionType.SCENARIO,
            text = "Your API works on your laptop but not after deployment. What would you check?",
            maxMarks = 5,
            topic = "DevOps & Deployment",
            referenceRubric = "1) Environment variables & database credentials configured on the host. 2) Port bindings (PORT env), host firewall, and CORS origins. 3) Host logs/container runtime crashes."
        ),
        Question(
            id = 13,
            section = SectionType.BACKEND,
            type = QuestionType.SHORT_ANSWER,
            text = "Why is input validation important in a backend?",
            maxMarks = 5,
            topic = "Security & Validation",
            referenceRubric = "Prevents injection attacks (SQLi, XSS, NoSQLi), stops corrupt data from polluting database, guarantees expected types, and yields clear 400 Bad Request messages."
        ),
        Question(
            id = 14,
            section = SectionType.BACKEND,
            type = QuestionType.MCQ,
            text = "Which method is commonly used to create a new resource?",
            options = listOf(
                "A) POST",
                "B) GET",
                "C) DELETE",
                "D) HEAD"
            ),
            correctAnswers = listOf("A"),
            maxMarks = 1,
            topic = "REST Architecture",
            referenceRubric = "POST is the standard RESTful method for submitting entity data to create a new subordinate resource."
        ),
        Question(
            id = 15,
            section = SectionType.BACKEND,
            type = QuestionType.FINAL_CHALLENGE,
            text = "Design a simple API for a college maintenance system. Write 4 useful endpoints and what each one does.",
            maxMarks = 10,
            topic = "System Design",
            referenceRubric = "Must provide 4 distinct endpoints with HTTP method, path, and action. Example: 1) POST /api/maintenance/requests (create ticket), 2) GET /api/maintenance/requests (list all), 3) PATCH /api/maintenance/requests/{id}/status (update status), 4) DELETE /api/maintenance/requests/{id} (cancel ticket)."
        )
    )

    val FRONTEND_QUESTIONS = listOf(
        Question(
            id = 1,
            section = SectionType.FRONTEND,
            type = QuestionType.MCQ,
            text = "What is the main job of a frontend?",
            options = listOf(
                "A) Provide the user interface and interact with backend services",
                "B) Manage electricity",
                "C) Replace the database",
                "D) Deploy servers only"
            ),
            correctAnswers = listOf("A"),
            maxMarks = 1,
            topic = "Frontend Fundamentals",
            referenceRubric = "Option A correctly captures UI rendering and client-side communication."
        ),
        Question(
            id = 2,
            section = SectionType.FRONTEND,
            type = QuestionType.MCQ,
            text = "What is React mainly used for?",
            options = listOf(
                "A) Building user interfaces",
                "B) Managing databases only",
                "C) Creating operating systems",
                "D) Configuring routers"
            ),
            correctAnswers = listOf("A"),
            maxMarks = 1,
            topic = "UI Libraries",
            referenceRubric = "React is an open-source front-end JavaScript library for building user interfaces based on components."
        ),
        Question(
            id = 3,
            section = SectionType.FRONTEND,
            type = QuestionType.MCQ,
            text = "What does responsive design mean?",
            options = listOf(
                "A) UI adapts to different screen sizes",
                "B) UI has more animations",
                "C) UI works only on desktop",
                "D) UI has no buttons"
            ),
            correctAnswers = listOf("A"),
            maxMarks = 1,
            topic = "Responsive Design",
            referenceRubric = "Responsive design makes web/app pages render well on a variety of devices, screen sizes, and orientations."
        ),
        Question(
            id = 4,
            section = SectionType.FRONTEND,
            type = QuestionType.MULTI_SELECT,
            text = "Which make a UI professional? (Select all that apply)",
            options = listOf(
                "A) Consistent spacing",
                "B) Clear typography",
                "C) Random colors everywhere",
                "D) Useful feedback/animations"
            ),
            correctAnswers = listOf("A", "B", "D"),
            maxMarks = 2,
            topic = "UI/UX Principles",
            referenceRubric = "A, B, and D provide polish and visual hierarchy. C degrades aesthetics and accessibility."
        ),
        Question(
            id = 5,
            section = SectionType.FRONTEND,
            type = QuestionType.SHORT_ANSWER,
            text = "What are components in React? Give two examples.",
            maxMarks = 5,
            topic = "Component Architecture",
            referenceRubric = "Independent, reusable pieces of UI that accept inputs (props) and return React elements. Examples: Header, NavigationBar, ProductCard, PrimaryButton."
        ),
        Question(
            id = 6,
            section = SectionType.FRONTEND,
            type = QuestionType.MCQ,
            text = "What should the UI show while waiting for a slow API response?",
            options = listOf(
                "A) Loading state",
                "B) Blank broken screen",
                "C) Fake success",
                "D) Nothing"
            ),
            correctAnswers = listOf("A"),
            maxMarks = 1,
            topic = "User Experience",
            referenceRubric = "Loading states (spinners, skeleton screens, progress bars) inform users that processing is underway."
        ),
        Question(
            id = 7,
            section = SectionType.FRONTEND,
            type = QuestionType.SCENARIO,
            text = "The backend takes 3 seconds to respond. How would you make the UI feel responsive during that time?",
            maxMarks = 5,
            topic = "Perceived Performance",
            referenceRubric = "1) Skeleton loaders with shimmer. 2) Optimistic UI updates. 3) Disable interactive button with spinner to prevent duplicate submissions. 4) Micro-interactions / progress indicators."
        ),
        Question(
            id = 8,
            section = SectionType.FRONTEND,
            type = QuestionType.MCQ,
            text = "Why should buttons have clear labels?",
            options = listOf(
                "A) Users need to understand the action",
                "B) It increases database size",
                "C) It hides errors",
                "D) It replaces navigation"
            ),
            correctAnswers = listOf("A"),
            maxMarks = 1,
            topic = "Usability",
            referenceRubric = "Descriptive verbs provide clear signifiers for user agency and prevent accidental mistakes."
        ),
        Question(
            id = 9,
            section = SectionType.FRONTEND,
            type = QuestionType.MULTI_SELECT,
            text = "Which states should an API-driven UI handle? (Select all that apply)",
            options = listOf(
                "A) Loading",
                "B) Success",
                "C) Error",
                "D) Empty data"
            ),
            correctAnswers = listOf("A", "B", "C", "D"),
            maxMarks = 2,
            topic = "State Management",
            referenceRubric = "All 4 states are mandatory for resilient production frontend applications."
        ),
        Question(
            id = 10,
            section = SectionType.FRONTEND,
            type = QuestionType.SHORT_ANSWER,
            text = "What is the difference between props and state in React?",
            maxMarks = 5,
            topic = "React Mechanics",
            referenceRubric = "Props: Read-only inputs passed into a component from parent. State: Mutable internal data owned and managed by the component that triggers re-renders on change."
        ),
        Question(
            id = 11,
            section = SectionType.FRONTEND,
            type = QuestionType.SCENARIO,
            text = "Your dashboard looks good on your laptop but breaks on a smaller screen. How would you fix it?",
            maxMarks = 5,
            topic = "Responsive Layouts",
            referenceRubric = "1) Implement responsive CSS media queries or Flex/Grid auto-wrap. 2) Convert multi-column grids to stacked single-column. 3) Switch top horizontal nav to collapsible drawer. 4) Check overflow hidden and flexible units."
        ),
        Question(
            id = 12,
            section = SectionType.FRONTEND,
            type = QuestionType.MCQ,
            text = "Why should secret API keys not be placed in frontend code?",
            options = listOf(
                "A) Users can inspect frontend code",
                "B) React cannot use strings",
                "C) CSS will break",
                "D) Browsers cannot display keys"
            ),
            correctAnswers = listOf("A"),
            maxMarks = 1,
            topic = "Frontend Security",
            referenceRubric = "Frontend code is delivered to and inspected via DevTools by end-users, exposing secret credentials."
        ),
        Question(
            id = 13,
            section = SectionType.FRONTEND,
            type = QuestionType.SHORT_ANSWER,
            text = "What is a good error message for a failed API request? Give an example.",
            maxMarks = 5,
            topic = "Error Handling",
            referenceRubric = "Must be user-friendly, empathetic, explain what failed without raw stack trace, and give clear next steps. Example: 'Unable to save profile changes due to network interruption. Please check connection and try again.'"
        ),
        Question(
            id = 14,
            section = SectionType.FRONTEND,
            type = QuestionType.MULTI_SELECT,
            text = "Which improve accessibility? (Select all that apply)",
            options = listOf(
                "A) Good contrast",
                "B) Keyboard-friendly controls",
                "C) Labels for inputs",
                "D) Tiny unreadable text"
            ),
            correctAnswers = listOf("A", "B", "C"),
            maxMarks = 2,
            topic = "Accessibility (a11y)",
            referenceRubric = "A, B, and C meet WCAG guidelines. D fails visual readability guidelines."
        ),
        Question(
            id = 15,
            section = SectionType.FRONTEND,
            type = QuestionType.FINAL_CHALLENGE,
            text = "Design the main screen of a hackathon project. List the important UI sections and what each should show.",
            maxMarks = 10,
            topic = "Product UI Design",
            referenceRubric = "Should outline: 1) Navigation & Header (Brand, user profile, actions). 2) Summary / Hero metric cards. 3) Primary workfeed/content view (cards/table with filters). 4) Interactive action/FAB or detail panel. 5) Status feedback toast / empty state."
        )
    )

    val INTEGRATION_QUESTIONS = listOf(
        Question(
            id = 1,
            section = SectionType.INTEGRATION,
            type = QuestionType.MCQ,
            text = "What is integration testing?",
            options = listOf(
                "A) Testing whether different parts work together",
                "B) Testing only colors",
                "C) Testing keyboard speed",
                "D) Designing a logo"
            ),
            correctAnswers = listOf("A"),
            maxMarks = 1,
            topic = "Testing Fundamentals",
            referenceRubric = "Integration testing evaluates interactions between combined modules and subsystems."
        ),
        Question(
            id = 2,
            section = SectionType.INTEGRATION,
            type = QuestionType.MCQ,
            text = "What is a bug?",
            options = listOf(
                "A) Incorrect or unexpected application behavior",
                "B) A new feature",
                "C) A database table",
                "D) A Git branch"
            ),
            correctAnswers = listOf("A"),
            maxMarks = 1,
            topic = "Quality Assurance",
            referenceRubric = "A defect or flaw causing incorrect or unintended results."
        ),
        Question(
            id = 3,
            section = SectionType.INTEGRATION,
            type = QuestionType.MCQ,
            text = "What does HTTP 500 usually tell you?",
            options = listOf(
                "A) A server-side problem occurred",
                "B) Everything is correct",
                "C) User logged in",
                "D) File downloaded"
            ),
            correctAnswers = listOf("A"),
            maxMarks = 1,
            topic = "Error Triage",
            referenceRubric = "500 indicates server-side unhandled exception or crash."
        ),
        Question(
            id = 4,
            section = SectionType.INTEGRATION,
            type = QuestionType.MULTI_SELECT,
            text = "Which are useful when debugging? (Select all that apply)",
            options = listOf(
                "A) Logs",
                "B) Browser console",
                "C) Network requests",
                "D) Randomly changing many files"
            ),
            correctAnswers = listOf("A", "B", "C"),
            maxMarks = 2,
            topic = "Debugging Methodology",
            referenceRubric = "A, B, and C provide observability and telemetry. D introduces regression chaos."
        ),
        Question(
            id = 5,
            section = SectionType.INTEGRATION,
            type = QuestionType.SHORT_ANSWER,
            text = "What is the difference between an error and its root cause?",
            maxMarks = 5,
            topic = "Root Cause Analysis",
            referenceRubric = "Error: The observable symptom, crash, or failure message. Root Cause: The core underlying defect in logic, data format, or missing condition that produced the failure."
        ),
        Question(
            id = 6,
            section = SectionType.INTEGRATION,
            type = QuestionType.SCENARIO,
            text = "The frontend says “Failed to fetch”, but the backend is running. What would you check step by step?",
            maxMarks = 5,
            topic = "CORS & Connectivity",
            referenceRubric = "1) Check browser DevTools console for CORS violation errors. 2) Verify host/port mismatch (e.g. localhost vs 127.0.0.1 or http vs https). 3) Verify backend CORS middleware headers. 4) Send direct curl/Postman request."
        ),
        Question(
            id = 7,
            section = SectionType.INTEGRATION,
            type = QuestionType.MCQ,
            text = "What is a regression?",
            options = listOf(
                "A) A feature that worked before stops working after a change",
                "B) A successful deployment",
                "C) A database backup",
                "D) A UI component"
            ),
            correctAnswers = listOf("A"),
            maxMarks = 1,
            topic = "Regression Testing",
            referenceRubric = "Regressions occur when software bug impairs existing functionality following code edits."
        ),
        Question(
            id = 8,
            section = SectionType.INTEGRATION,
            type = QuestionType.MULTI_SELECT,
            text = "Which should a good bug report contain? (Select all that apply)",
            options = listOf(
                "A) Steps to reproduce",
                "B) Expected result",
                "C) Actual result",
                "D) Only the word “broken”"
            ),
            correctAnswers = listOf("A", "B", "C"),
            maxMarks = 2,
            topic = "Bug Reporting",
            referenceRubric = "A, B, and C enable engineers to reproduce and verify fixes. D is useless."
        ),
        Question(
            id = 9,
            section = SectionType.INTEGRATION,
            type = QuestionType.SHORT_ANSWER,
            text = "Why is it important to reproduce a bug before trying to fix it?",
            maxMarks = 5,
            topic = "Test Strategy",
            referenceRubric = "Ensures you understand the exact conditions triggering failure, prevents making blind guesses that break other features, and gives a clear verification test."
        ),
        Question(
            id = 10,
            section = SectionType.INTEGRATION,
            type = QuestionType.SCENARIO,
            text = "Two teammates disagree about whether a bug is in the frontend or backend. How would you find the actual source?",
            maxMarks = 5,
            topic = "Inter-team Isolation",
            referenceRubric = "Inspect network requests and responses in DevTools or Postman. If the frontend sends valid payload and backend returns 500 or corrupt schema -> Backend bug. If backend returns correct schema but UI crashes -> Frontend bug."
        ),
        Question(
            id = 11,
            section = SectionType.INTEGRATION,
            type = QuestionType.MCQ,
            text = "What is a smoke test?",
            options = listOf(
                "A) A quick check that the main application functions work",
                "B) A security attack",
                "C) A database query",
                "D) A design review"
            ),
            correctAnswers = listOf("A"),
            maxMarks = 1,
            topic = "Testing Types",
            referenceRubric = "Preliminary test to check if basic, critical functionalities work before deeper testing."
        ),
        Question(
            id = 12,
            section = SectionType.INTEGRATION,
            type = QuestionType.SHORT_ANSWER,
            text = "What should you test before giving a hackathon demo? List at least five things.",
            maxMarks = 5,
            topic = "Demo Readiness",
            referenceRubric = "1) End-to-end happy path flow. 2) Clean database seeding/fresh account. 3) Demo screen resolution / font scaling. 4) Internet/hotspot connectivity. 5) Fallback recorded video/mock data."
        ),
        Question(
            id = 13,
            section = SectionType.INTEGRATION,
            type = QuestionType.MULTI_SELECT,
            text = "Which tests can be useful in a project? (Select all that apply)",
            options = listOf(
                "A) Unit tests",
                "B) Integration tests",
                "C) End-to-end tests",
                "D) No testing"
            ),
            correctAnswers = listOf("A", "B", "C"),
            maxMarks = 2,
            topic = "Test Pyramid",
            referenceRubric = "Unit, integration, and E2E form the classic test pyramid. D is detrimental."
        ),
        Question(
            id = 14,
            section = SectionType.INTEGRATION,
            type = QuestionType.SCENARIO,
            text = "A feature works locally but fails after merging into the main branch. What could be the reason and how would you investigate?",
            maxMarks = 5,
            topic = "Merge & Branch Diagnostics",
            referenceRubric = "Reasons: Merge conflict resolution errors, missing environment variables, conflicting dependency updates. Investigation: git diff main, git log, reviewing CI test failures, comparing local config with main config."
        ),
        Question(
            id = 15,
            section = SectionType.INTEGRATION,
            type = QuestionType.FINAL_CHALLENGE,
            text = "Create a simple 10-step final testing checklist for a 24-hour hackathon project.",
            maxMarks = 10,
            topic = "Comprehensive QA",
            referenceRubric = "1) Auth flow. 2) Core CRUD operations. 3) Input validation. 4) Error state displays. 5) Mobile/laptop responsiveness. 6) Database reset/seed. 7) Secret keys sanity. 8) Live deployment health. 9) Cross-browser test. 10) 3-minute timed presenter rehearsal."
        )
    )

    val DEVOPS_QUESTIONS = listOf(
        Question(
            id = 1,
            section = SectionType.DEVOPS,
            type = QuestionType.MCQ,
            text = "What is Docker mainly used for?",
            options = listOf(
                "A) Packaging applications and dependencies consistently",
                "B) Designing UI",
                "C) Writing presentations",
                "D) Editing photos"
            ),
            correctAnswers = listOf("A"),
            maxMarks = 1,
            topic = "Containerization",
            referenceRubric = "Docker containerizes software so it runs reliably across differing environments."
        ),
        Question(
            id = 2,
            section = SectionType.DEVOPS,
            type = QuestionType.MCQ,
            text = "What is Git mainly used for?",
            options = listOf(
                "A) Version control",
                "B) Database hosting",
                "C) UI design",
                "D) Password storage"
            ),
            correctAnswers = listOf("A"),
            maxMarks = 1,
            topic = "Version Control",
            referenceRubric = "Git is a distributed version control system tracking source code changes."
        ),
        Question(
            id = 3,
            section = SectionType.DEVOPS,
            type = QuestionType.MCQ,
            text = "Why are environment variables useful?",
            options = listOf(
                "A) They keep configuration and secrets outside hard-coded code",
                "B) They make buttons colorful",
                "C) They replace Git",
                "D) They remove the need for testing"
            ),
            correctAnswers = listOf("A"),
            maxMarks = 1,
            topic = "Configuration Management",
            referenceRubric = "Decouples credentials and environment-specific parameters from application code."
        ),
        Question(
            id = 4,
            section = SectionType.DEVOPS,
            type = QuestionType.MULTI_SELECT,
            text = "Which should be checked before deployment? (Select all that apply)",
            options = listOf(
                "A) Environment variables",
                "B) Ports/networking",
                "C) Build succeeds",
                "D) Secret keys committed publicly"
            ),
            correctAnswers = listOf("A", "B", "C"),
            maxMarks = 2,
            topic = "Pre-deployment Verification",
            referenceRubric = "A, B, and C are prerequisite checks. D is a security leak."
        ),
        Question(
            id = 5,
            section = SectionType.DEVOPS,
            type = QuestionType.SHORT_ANSWER,
            text = "What is the difference between a Docker image and a container?",
            maxMarks = 5,
            topic = "Docker Internals",
            referenceRubric = "Image: Immutable blueprint/package containing OS, code, and libraries. Container: A running, live, isolated execution instance of an image."
        ),
        Question(
            id = 6,
            section = SectionType.DEVOPS,
            type = QuestionType.SCENARIO,
            text = "Your frontend is deployed but cannot connect to the backend. What would you check?",
            maxMarks = 5,
            topic = "Production Connectivity",
            referenceRubric = "1) Frontend API base URL points to deployed domain (not localhost). 2) CORS headers on backend accept frontend domain. 3) HTTPS vs HTTP mixed-content restrictions. 4) Backend service status in cloud dashboard."
        ),
        Question(
            id = 7,
            section = SectionType.DEVOPS,
            type = QuestionType.MCQ,
            text = "What is CI/CD used for?",
            options = listOf(
                "A) Automating build, test and deployment processes",
                "B) Designing logos",
                "C) Writing database tables only",
                "D) Increasing RAM"
            ),
            correctAnswers = listOf("A"),
            maxMarks = 1,
            topic = "CI/CD Automation",
            referenceRubric = "Continuous Integration & Continuous Delivery automates building, linting, testing, and deployment."
        ),
        Question(
            id = 8,
            section = SectionType.DEVOPS,
            type = QuestionType.MULTI_SELECT,
            text = "Which are useful during deployment debugging? (Select all that apply)",
            options = listOf(
                "A) Application logs",
                "B) Container logs",
                "C) Network/port checks",
                "D) Guessing without checking logs"
            ),
            correctAnswers = listOf("A", "B", "C"),
            maxMarks = 2,
            topic = "Production Debugging",
            referenceRubric = "Observability tools (A, B, C) guide deterministic diagnosis. D is anti-pattern."
        ),
        Question(
            id = 9,
            section = SectionType.DEVOPS,
            type = QuestionType.SHORT_ANSWER,
            text = "Why should passwords and API keys not be committed to GitHub?",
            maxMarks = 5,
            topic = "DevSecOps",
            referenceRubric = "Repositories are indexed and targeted by automated vulnerability scrapers within seconds, causing credential theft, cloud resource hijacking, and severe financial/data loss."
        ),
        Question(
            id = 10,
            section = SectionType.DEVOPS,
            type = QuestionType.SCENARIO,
            text = "Your application works locally but crashes after deployment. Give a basic troubleshooting order.",
            maxMarks = 5,
            topic = "Incident Triage",
            referenceRubric = "1) Review runtime logs and exit codes. 2) Verify cloud environment variables match local .env. 3) Check database connectivity and network access rules. 4) Verify correct listening port."
        ),
        Question(
            id = 11,
            section = SectionType.DEVOPS,
            type = QuestionType.MCQ,
            text = "What is a health-check endpoint?",
            options = listOf(
                "A) An endpoint used to check whether a service is working",
                "B) A login page",
                "C) A CSS file",
                "D) A database password"
            ),
            correctAnswers = listOf("A"),
            maxMarks = 1,
            topic = "Observability",
            referenceRubric = "Endpoint (e.g. /healthz or /api/status) used by load balancers and orchestrators to probe service availability."
        ),
        Question(
            id = 12,
            section = SectionType.DEVOPS,
            type = QuestionType.SHORT_ANSWER,
            text = "What is a rollback and why can it be useful before a demo?",
            maxMarks = 5,
            topic = "Disaster Recovery",
            referenceRubric = "Reverting to the previous stable release or Git tag. Critical before a demo if a last-minute push broke the site, allowing instant recovery."
        ),
        Question(
            id = 13,
            section = SectionType.DEVOPS,
            type = QuestionType.MULTI_SELECT,
            text = "Which are good hackathon deployment practices? (Select all that apply)",
            options = listOf(
                "A) Keep a working backup",
                "B) Commit changes regularly",
                "C) Test deployment before demo time",
                "D) Make huge untested changes just before presentation"
            ),
            correctAnswers = listOf("A", "B", "C"),
            maxMarks = 2,
            topic = "Hackathon Best Practices",
            referenceRubric = "A, B, and C prevent last-minute disasters. D regularly ruins hackathon presentations."
        ),
        Question(
            id = 14,
            section = SectionType.DEVOPS,
            type = QuestionType.SCENARIO,
            text = "Five minutes before the demo, the deployed application stops working. What would you do first, second and third?",
            maxMarks = 5,
            topic = "Emergency Response",
            referenceRubric = "1) Revert/rollback cloud service to last verified working deploy tag. 2) Boot local instance with screen share standby. 3) Switch to demo backup video/slides if connection is lost."
        ),
        Question(
            id = 15,
            section = SectionType.DEVOPS,
            type = QuestionType.FINAL_CHALLENGE,
            text = "Write a final 10-step checklist for deploying and presenting a frontend + backend + database application.",
            maxMarks = 10,
            topic = "Full Stack Deployment",
            referenceRubric = "1) Code freeze. 2) Configure cloud env secrets. 3) DB migration/seed. 4) Live backend smoke test. 5) Build & deploy frontend. 6) Verify CORS/HTTPS. 7) Local fallback running. 8) Test incognito. 9) Rehearse script. 10) Backup demo video ready."
        )
    )

    fun getQuestionsForSection(section: SectionType): List<Question> {
        return when (section) {
            SectionType.BACKEND -> BACKEND_QUESTIONS
            SectionType.FRONTEND -> FRONTEND_QUESTIONS
            SectionType.INTEGRATION -> INTEGRATION_QUESTIONS
            SectionType.DEVOPS -> DEVOPS_QUESTIONS
        }
    }

    fun getTotalPossibleMarks(section: SectionType): Int {
        return getQuestionsForSection(section).sumOf { it.maxMarks }
    }
}
