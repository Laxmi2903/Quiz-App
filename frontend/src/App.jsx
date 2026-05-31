import { useState } from "react";
import QuizList from "./QuizList";
import QuizPlay from "./QuizPlay";

export default function App() {
  const [currentQuizId, setCurrentQuizId] = useState(null);
  const [currentQuizTitle, setCurrentQuizTitle] = useState("");

  function handleSelectQuiz(id, title) {
    setCurrentQuizId(id);
    setCurrentQuizTitle(title);
  }

  function handleBackToList() {
    setCurrentQuizId(null);
    setCurrentQuizTitle("");
  }

  return (
    <div style={styles.appWrapper}>
      {currentQuizId === null ? (
        <QuizList onSelectQuiz={handleSelectQuiz} />
      ) : (
        <QuizPlay
          quizId={currentQuizId}
          quizTitle={currentQuizTitle}
          onBack={handleBackToList}
        />
      )}
    </div>
  );
}

const styles = {
  appWrapper: {
    minHeight: "100vh",
    backgroundColor: "#f0f4f8",
    fontFamily: "'Segoe UI', Tahoma, Geneva, Verdana, sans-serif",
  },
};