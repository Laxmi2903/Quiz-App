import { useState, useEffect } from "react";

const API_BASE = "http://localhost:8080";

export default function QuizPlay({ quizId, quizTitle, onBack }) {
  const [questions, setQuestions]       = useState([]);
  const [loading, setLoading]           = useState(true);
  const [error, setError]               = useState(null);

  // Quiz play state
  const [currentIndex, setCurrentIndex] = useState(0);
  const [selectedOption, setSelectedOption] = useState(null); // "A" | "B" | "C" | "D"
  const [score, setScore]               = useState(0);
  const [totalMarks, setTotalMarks]     = useState(0);
  const [isFinished, setIsFinished]     = useState(false);
  const [answered, setAnswered]         = useState(false); // lock after choosing

  useEffect(() => {
    fetch(`${API_BASE}/api/questions/quiz/${quizId}`)
      .then((res) => {
        if (!res.ok) throw new Error(`Server error: ${res.status}`);
        return res.json();
      })
      .then((data) => {
        setQuestions(data);
        const total = data.reduce((sum, q) => sum + (q.marks || 0), 0);
        setTotalMarks(total);
        setLoading(false);
      })
      .catch((err) => {
        setError(err.message);
        setLoading(false);
      });
  }, [quizId]);

  if (loading) return <div style={styles.center}>Loading questions...</div>;
  if (error)   return <div style={styles.center}>❌ {error}</div>;
  if (questions.length === 0)
    return (
      <div style={styles.center}>
        <div style={{ textAlign: "center" }}>
          <p>No questions found for this quiz.</p>
          <button style={styles.backBtn} onClick={onBack}>← Back to Quizzes</button>
        </div>
      </div>
    );

  // ── Finished screen ──────────────────────────────────────────────────────────
  if (isFinished) {
    const percentage = totalMarks > 0 ? Math.round((score / totalMarks) * 100) : 0;
    const grade =
      percentage >= 80 ? "🏆 Excellent!" :
      percentage >= 60 ? "👍 Good job!"  :
      percentage >= 40 ? "📚 Keep practicing" :
                         "💪 Don't give up!";

    return (
      <div style={styles.page}>
        <div style={{ ...styles.card, maxWidth: "480px", textAlign: "center" }}>
          <div style={styles.resultEmoji}>{grade.split(" ")[0]}</div>
          <h2 style={styles.resultTitle}>{grade.slice(2)}</h2>
          <p style={styles.resultQuizName}>{quizTitle}</p>

          <div style={styles.scoreBox}>
            <div style={styles.scoreNumber}>{score} / {totalMarks}</div>
            <div style={styles.scoreLabel}>marks</div>
          </div>

          <div style={styles.percentBadge(percentage)}>
            {percentage}%
          </div>

          <div style={styles.statRow}>
            <span>Questions attempted</span>
            <strong>{questions.length}</strong>
          </div>

          <div style={{ display: "flex", gap: "12px", marginTop: "24px" }}>
            <button style={styles.backBtn} onClick={onBack}>← All Quizzes</button>
            <button
              style={styles.primaryBtn}
              onClick={() => {
                setCurrentIndex(0);
                setScore(0);
                setSelectedOption(null);
                setAnswered(false);
                setIsFinished(false);
              }}
            >
              Retry ↺
            </button>
          </div>
        </div>
      </div>
    );
  }

  // ── Active question ──────────────────────────────────────────────────────────
  const question = questions[currentIndex];
  const options = [
    { key: "A", text: question.optionA },
    { key: "B", text: question.optionB },
    { key: "C", text: question.optionC },
    { key: "D", text: question.optionD },
  ];

  function handleOptionChange(key) {
    if (answered) return; // locked after first selection
    setSelectedOption(key);
  }

  function handleNext() {
    if (!selectedOption) return; // must pick an answer

    // Score this question
    if (selectedOption === question.correctAnswer) {
      setScore((prev) => prev + (question.marks || 0));
    }
    setAnswered(true);

    // Wait briefly then advance
    setTimeout(() => {
      if (currentIndex + 1 >= questions.length) {
        setIsFinished(true);
      } else {
        setCurrentIndex((prev) => prev + 1);
        setSelectedOption(null);
        setAnswered(false);
      }
    }, 600);
  }

  function optionStyle(key) {
    let base = { ...styles.optionLabel };
    if (!answered) {
      if (selectedOption === key) base = { ...base, ...styles.optionSelected };
    } else {
      if (key === question.correctAnswer) base = { ...base, ...styles.optionCorrect };
      else if (key === selectedOption)    base = { ...base, ...styles.optionWrong };
    }
    return base;
  }

  const progress = ((currentIndex) / questions.length) * 100;

  return (
    <div style={styles.page}>
      <div style={styles.card}>
        {/* Header */}
        <div style={styles.header}>
          <button style={styles.backLink} onClick={onBack}>← Back</button>
          <span style={styles.quizName}>{quizTitle}</span>
          <span style={styles.counter}>
            {currentIndex + 1} / {questions.length}
          </span>
        </div>

        {/* Progress bar */}
        <div style={styles.progressTrack}>
          <div style={{ ...styles.progressFill, width: `${progress}%` }} />
        </div>

        {/* Question */}
        <div style={styles.questionBox}>
          <div style={styles.marksTag}>{question.marks} mark{question.marks !== 1 ? "s" : ""}</div>
          <p style={styles.questionText}>{question.questionText}</p>
        </div>

        {/* Options */}
        <div style={styles.optionsGrid}>
          {options.map(({ key, text }) => (
            <label key={key} style={optionStyle(key)}>
              <input
                type="radio"
                name="option"
                value={key}
                checked={selectedOption === key}
                onChange={() => handleOptionChange(key)}
                disabled={answered}
                style={{ display: "none" }}
              />
              <span style={styles.optionKey}>{key}</span>
              <span style={styles.optionText}>{text}</span>
            </label>
          ))}
        </div>

        {/* Next button */}
        <div style={{ marginTop: "24px", textAlign: "right" }}>
          <button
            style={{
              ...styles.primaryBtn,
              opacity: selectedOption ? 1 : 0.45,
              cursor: selectedOption ? "pointer" : "not-allowed",
            }}
            onClick={handleNext}
            disabled={!selectedOption}
          >
            {currentIndex + 1 === questions.length ? "Finish ✓" : "Next →"}
          </button>
        </div>
      </div>
    </div>
  );
}

// ── Styles ──────────────────────────────────────────────────────────────────────
const styles = {
  page: {
    minHeight: "100vh",
    backgroundColor: "#f0f4f8",
    display: "flex",
    alignItems: "flex-start",
    justifyContent: "center",
    padding: "40px 16px",
  },
  card: {
    backgroundColor: "#ffffff",
    borderRadius: "16px",
    padding: "32px",
    width: "100%",
    maxWidth: "640px",
    boxShadow: "0 2px 12px rgba(0,0,0,0.08)",
    border: "1px solid #e2e8f0",
  },
  center: {
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    minHeight: "100vh",
    fontSize: "1.1rem",
    color: "#64748b",
  },

  // Header
  header: {
    display: "flex",
    alignItems: "center",
    justifyContent: "space-between",
    marginBottom: "16px",
  },
  backLink: {
    background: "none",
    border: "none",
    color: "#2563eb",
    fontWeight: "600",
    cursor: "pointer",
    fontSize: "0.875rem",
    padding: 0,
  },
  quizName: {
    fontWeight: "700",
    color: "#1e293b",
    fontSize: "0.95rem",
    maxWidth: "200px",
    overflow: "hidden",
    textOverflow: "ellipsis",
    whiteSpace: "nowrap",
  },
  counter: {
    fontSize: "0.85rem",
    color: "#94a3b8",
    fontWeight: "600",
  },

  // Progress bar
  progressTrack: {
    height: "6px",
    backgroundColor: "#e2e8f0",
    borderRadius: "999px",
    marginBottom: "28px",
    overflow: "hidden",
  },
  progressFill: {
    height: "100%",
    backgroundColor: "#2563eb",
    borderRadius: "999px",
    transition: "width 0.4s ease",
  },

  // Question
  questionBox: {
    marginBottom: "24px",
  },
  marksTag: {
    display: "inline-block",
    fontSize: "0.72rem",
    fontWeight: "700",
    textTransform: "uppercase",
    letterSpacing: "0.06em",
    color: "#7c3aed",
    backgroundColor: "#f5f3ff",
    padding: "2px 8px",
    borderRadius: "999px",
    marginBottom: "10px",
  },
  questionText: {
    fontSize: "1.1rem",
    fontWeight: "600",
    color: "#1e293b",
    lineHeight: "1.6",
    margin: 0,
  },

  // Options
  optionsGrid: {
    display: "flex",
    flexDirection: "column",
    gap: "10px",
  },
  optionLabel: {
    display: "flex",
    alignItems: "center",
    gap: "12px",
    padding: "12px 16px",
    borderRadius: "10px",
    border: "2px solid #e2e8f0",
    cursor: "pointer",
    transition: "border-color 0.15s, background-color 0.15s",
    userSelect: "none",
    backgroundColor: "#fafafa",
  },
  optionSelected: {
    borderColor: "#2563eb",
    backgroundColor: "#eff6ff",
  },
  optionCorrect: {
    borderColor: "#16a34a",
    backgroundColor: "#f0fdf4",
    cursor: "default",
  },
  optionWrong: {
    borderColor: "#dc2626",
    backgroundColor: "#fef2f2",
    cursor: "default",
  },
  optionKey: {
    minWidth: "28px",
    height: "28px",
    borderRadius: "50%",
    backgroundColor: "#e2e8f0",
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    fontWeight: "700",
    fontSize: "0.8rem",
    color: "#475569",
    flexShrink: 0,
  },
  optionText: {
    fontSize: "0.95rem",
    color: "#334155",
  },

  // Buttons
  primaryBtn: {
    backgroundColor: "#2563eb",
    color: "#fff",
    border: "none",
    borderRadius: "8px",
    padding: "10px 24px",
    fontSize: "0.95rem",
    fontWeight: "600",
    cursor: "pointer",
    transition: "opacity 0.15s",
  },
  backBtn: {
    backgroundColor: "#f1f5f9",
    color: "#334155",
    border: "1px solid #e2e8f0",
    borderRadius: "8px",
    padding: "10px 20px",
    fontSize: "0.9rem",
    fontWeight: "600",
    cursor: "pointer",
    flex: 1,
  },

  // Result screen
  resultEmoji: {
    fontSize: "3.5rem",
    marginBottom: "8px",
  },
  resultTitle: {
    fontSize: "1.5rem",
    fontWeight: "700",
    color: "#1e293b",
    margin: "0 0 4px",
  },
  resultQuizName: {
    color: "#64748b",
    margin: "0 0 24px",
    fontSize: "0.9rem",
  },
  scoreBox: {
    backgroundColor: "#f8fafc",
    borderRadius: "12px",
    padding: "20px",
    marginBottom: "12px",
    border: "1px solid #e2e8f0",
  },
  scoreNumber: {
    fontSize: "2.5rem",
    fontWeight: "800",
    color: "#1e293b",
    lineHeight: 1,
  },
  scoreLabel: {
    fontSize: "0.85rem",
    color: "#94a3b8",
    marginTop: "4px",
  },
  percentBadge: (pct) => ({
    display: "inline-block",
    padding: "4px 16px",
    borderRadius: "999px",
    fontWeight: "700",
    fontSize: "1rem",
    marginBottom: "20px",
    backgroundColor: pct >= 60 ? "#dcfce7" : pct >= 40 ? "#fef9c3" : "#fee2e2",
    color: pct >= 60 ? "#15803d" : pct >= 40 ? "#92400e" : "#b91c1c",
  }),
  statRow: {
    display: "flex",
    justifyContent: "space-between",
    fontSize: "0.9rem",
    color: "#64748b",
    padding: "8px 0",
    borderTop: "1px solid #f1f5f9",
  },
};
