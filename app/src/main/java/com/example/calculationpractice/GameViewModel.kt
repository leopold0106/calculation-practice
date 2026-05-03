package com.example.calculationpractice

import androidx.lifecycle.ViewModel
import kotlin.random.Random

data class GameUiState(
    val currentProblem: Problem,
    val level: Int = 1,
    val totalCorrect: Int = 0,
    val totalAttempts: Int = 0,
    val consecutiveWrong: Int = 0,
    val feedback: FeedbackState = FeedbackState.None,
    val userInput: String = "",
    val addSubProgress: Int = 0,
    val multDivProgress: Int = 0
) {
    val levelProgress: Int get() = addSubProgress + multDivProgress
}

sealed class FeedbackState {
    object None : FeedbackState()
    object Correct : FeedbackState()
    data class Wrong(val correctAnswer: Int) : FeedbackState()
}

class GameViewModel : ViewModel() {

    companion object {
        const val CORRECT_PER_GROUP = 5
        const val CORRECT_TO_ADVANCE = 10
        const val WRONG_TO_REGRESS = 3
        const val MIN_LEVEL = 1
    }

    var uiState: GameUiState = GameUiState(
        currentProblem = ProblemGenerator.generate(1, OperationGroup.ADD_SUB)
    )
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

        val currentGroup = when (uiState.currentProblem.operation) {
            Operation.ADDITION, Operation.SUBTRACTION -> OperationGroup.ADD_SUB
            Operation.MULTIPLICATION, Operation.DIVISION -> OperationGroup.MULT_DIV
        }

        var newAddSub = uiState.addSubProgress
        var newMultDiv = uiState.multDivProgress
        var newConsecWrong = uiState.consecutiveWrong
        var newLevel = uiState.level

        if (isCorrect) {
            newConsecWrong = 0
            when (currentGroup) {
                OperationGroup.ADD_SUB -> newAddSub = (newAddSub + 1).coerceAtMost(CORRECT_PER_GROUP)
                OperationGroup.MULT_DIV -> newMultDiv = (newMultDiv + 1).coerceAtMost(CORRECT_PER_GROUP)
            }
            if (newAddSub >= CORRECT_PER_GROUP && newMultDiv >= CORRECT_PER_GROUP) {
                newLevel = uiState.level + 1
                newAddSub = 0
                newMultDiv = 0
            }
        } else {
            newConsecWrong += 1
            if (newConsecWrong >= WRONG_TO_REGRESS) {
                newLevel = (uiState.level - 1).coerceAtLeast(MIN_LEVEL)
                newAddSub = 0
                newMultDiv = 0
                newConsecWrong = 0
            }
        }

        val nextGroup = pickGroup(newAddSub, newMultDiv)
        val feedback: FeedbackState =
            if (isCorrect) FeedbackState.Correct else FeedbackState.Wrong(uiState.currentProblem.answer)

        uiState = uiState.copy(
            currentProblem = ProblemGenerator.generate(newLevel, nextGroup),
            level = newLevel,
            totalCorrect = newTotalCorrect,
            totalAttempts = newTotalAttempts,
            consecutiveWrong = newConsecWrong,
            feedback = feedback,
            userInput = "",
            addSubProgress = newAddSub,
            multDivProgress = newMultDiv
        )
    }

    private fun pickGroup(addSub: Int, multDiv: Int): OperationGroup {
        val needAddSub = addSub < CORRECT_PER_GROUP
        val needMultDiv = multDiv < CORRECT_PER_GROUP
        return when {
            needAddSub && needMultDiv -> if (Random.nextBoolean()) OperationGroup.ADD_SUB else OperationGroup.MULT_DIV
            needAddSub -> OperationGroup.ADD_SUB
            else -> OperationGroup.MULT_DIV
        }
    }
}
