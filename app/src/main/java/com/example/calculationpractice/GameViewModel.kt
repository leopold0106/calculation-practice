package com.example.calculationpractice

import androidx.lifecycle.ViewModel

data class GameUiState(
    val currentProblem: Problem,
    val level: Int = 1,
    val totalCorrect: Int = 0,
    val totalAttempts: Int = 0,
    val consecutiveCorrect: Int = 0,
    val consecutiveWrong: Int = 0,
    val feedback: FeedbackState = FeedbackState.None,
    val userInput: String = ""
) {
    val levelProgress: Int get() = consecutiveCorrect
}

sealed class FeedbackState {
    object None : FeedbackState()
    object Correct : FeedbackState()
    data class Wrong(val correctAnswer: Int) : FeedbackState()
}

class GameViewModel : ViewModel() {

    companion object {
        const val CORRECT_TO_ADVANCE = 5
        const val WRONG_TO_REGRESS = 3
        const val MIN_LEVEL = 1
    }

    var uiState: GameUiState = GameUiState(currentProblem = ProblemGenerator.generate(1))
        private set

    fun onInputChanged(input: String) {
        val filtered = input.filter { it.isDigit() }
        uiState = uiState.copy(userInput = filtered, feedback = FeedbackState.None)
    }

    fun onSubmit() {
        val input = uiState.userInput.trim()
        if (input.isEmpty()) return

        val guess = input.toIntOrNull() ?: return
        val isCorrect = guess == uiState.currentProblem.answer

        val newTotalAttempts = uiState.totalAttempts + 1
        val newTotalCorrect = if (isCorrect) uiState.totalCorrect + 1 else uiState.totalCorrect

        var newConsecutiveCorrect: Int
        var newConsecutiveWrong: Int
        var newLevel = uiState.level

        if (isCorrect) {
            newConsecutiveCorrect = uiState.consecutiveCorrect + 1
            newConsecutiveWrong = 0
            if (newConsecutiveCorrect >= CORRECT_TO_ADVANCE) {
                newLevel = uiState.level + 1
            }
        } else {
            newConsecutiveCorrect = 0
            newConsecutiveWrong = uiState.consecutiveWrong + 1
            if (newConsecutiveWrong >= WRONG_TO_REGRESS) {
                newLevel = (uiState.level - 1).coerceAtLeast(MIN_LEVEL)
            }
        }

        if (newLevel != uiState.level) {
            newConsecutiveCorrect = 0
            newConsecutiveWrong = 0
        }

        val feedback: FeedbackState = if (isCorrect) FeedbackState.Correct
                                      else FeedbackState.Wrong(uiState.currentProblem.answer)

        uiState = uiState.copy(
            currentProblem = ProblemGenerator.generate(newLevel),
            level = newLevel,
            totalCorrect = newTotalCorrect,
            totalAttempts = newTotalAttempts,
            consecutiveCorrect = newConsecutiveCorrect,
            consecutiveWrong = newConsecutiveWrong,
            feedback = feedback,
            userInput = ""
        )
    }
}
