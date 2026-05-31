import { useState, useEffect } from "react";

const API_BASE = "http://localhost:8080";

export default function QuizList({ onSelectQuiz }) {
  const [quizzes, setQuizzes] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetch(`${API_BASE}/api/quizzes`)
      .then((res) => {
        if (!res.ok) throw new Error(`Server error: ${res.status}`);
        return res.json();
      })
      .then((data) => {
        setQuizzes(data);
        setLoading(false);
      })
      .catch((err) => {
        setError(err.message);
        setLoading(false);
      });
  }, []);

  if (loading) return <div style={styles.center}>Loading quizzes...</div>;
  if (error)   return <div style={styles.center}>❌ {error}</div>;

  return (
    <div style={styles.page}>
      <div style={styles.container}>
        <h1 style={styles.heading}>📝 Quiz App</h1>
        <p style={styles.subheading}>Select a quiz to begin</p>

        {quizzes.length === 0 ? (
          <p style={styles.empty}>No quizzes available.</p>
        ) : (
          <div style={styles.grid}>
            {quizzes.map((quiz) => (
              <div key={quiz.id} style={styles.card}>
                <div style={styles.cardBody}>
                  <span style={styles.category}>{quiz.category || "General"}</span>
                  <h2 style={styles.cardTitle}>{quiz.title}</h2>
                  <p style={styles.cardDesc}>
                    {quiz.description || "No description provided."}
                  </p>
                </div>
                <button
                  style={styles.button}
                  onClick={() => onSelectQuiz(quiz.id, quiz.title)}
                  onMouseEnter={(e) => (e.target.style.backgroundColor = "#1d4ed8")}
                  onMouseLeave={(e) => (e.target.style.backgroundColor = "#2563eb")}
                >
                  Start Quiz →
                </button>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}

const styles = {
  page: {
    minHeight: "100vh",
    padding: "40px 16px",
    backgroundColor: "#f0f4f8",
  },
  container: {
    maxWidth: "900px",
    margin: "0 auto",
  },
  heading: {
    fontSize: "2rem",
    fontWeight: "700",
    color: "#1e293b",
    marginBottom: "4px",
  },
  subheading: {
    color: "#64748b",
    marginBottom: "32px",
    fontSize: "1rem",
  },
  grid: {
    display: "grid",
    gridTemplateColumns: "repeat(auto-fill, minmax(260px, 1fr))",
    gap: "20px",
  },
  card: {
    backgroundColor: "#ffffff",
    borderRadius: "12px",
    padding: "24px",
    boxShadow: "0 1px 4px rgba(0,0,0,0.08)",
    display: "flex",
    flexDirection: "column",
    justifyContent: "space-between",
    gap: "16px",
    border: "1px solid #e2e8f0",
  },
  cardBody: {
    display: "flex",
    flexDirection: "column",
    gap: "8px",
  },
  category: {
    fontSize: "0.72rem",
    fontWeight: "600",
    textTransform: "uppercase",
    letterSpacing: "0.08em",
    color: "#2563eb",
    backgroundColor: "#eff6ff",
    padding: "2px 8px",
    borderRadius: "999px",
    width: "fit-content",
  },
  cardTitle: {
    fontSize: "1.1rem",
    fontWeight: "700",
    color: "#1e293b",
    margin: 0,
  },
  cardDesc: {
    fontSize: "0.875rem",
    color: "#64748b",
    margin: 0,
    lineHeight: "1.5",
  },
  button: {
    backgroundColor: "#2563eb",
    color: "#fff",
    border: "none",
    borderRadius: "8px",
    padding: "10px 16px",
    fontSize: "0.9rem",
    fontWeight: "600",
    cursor: "pointer",
    transition: "background-color 0.15s ease",
    textAlign: "center",
  },
  center: {
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    minHeight: "100vh",
    fontSize: "1.1rem",
    color: "#64748b",
  },
  empty: {
    color: "#94a3b8",
    fontSize: "1rem",
  },
};
