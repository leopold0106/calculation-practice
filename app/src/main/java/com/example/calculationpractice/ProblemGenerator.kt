package com.example.calculationpractice

import kotlin.random.Random

data class Problem(
    val operandA: Int,
    val operandB: Int,
    val operation: Operation,
    val answer: Int
) {
    val symbol: String get() = operation.symbol
    val displayText: String get() = "$operandA $symbol $operandB = ?"
}

enum class Operation(val symbol: String) {
    ADDITION("+"),
    SUBTRACTION("−"),
    MULTIPLICATION("×"),
    DIVISION("÷")
}

data class DifficultyConfig(
    val level: Int,
    val maxNumber: Int,
    val operations: List<Operation>,
    val multLimit: Int = maxNumber
)

object DifficultyLevels {
    val levels: List<DifficultyConfig> = listOf(
        DifficultyConfig(
            level = 1,
            maxNumber = 9,
            operations = listOf(Operation.ADDITION, Operation.SUBTRACTION)
        ),
        DifficultyConfig(
            level = 2,
            maxNumber = 20,
            operations = listOf(Operation.ADDITION, Operation.SUBTRACTION, Operation.MULTIPLICATION),
            multLimit = 5
        ),
        DifficultyConfig(
            level = 3,
            maxNumber = 50,
            operations = listOf(Operation.ADDITION, Operation.SUBTRACTION, Operation.MULTIPLICATION, Operation.DIVISION),
            multLimit = 10
        ),
        DifficultyConfig(
            level = 4,
            maxNumber = 100,
            operations = listOf(Operation.ADDITION, Operation.SUBTRACTION, Operation.MULTIPLICATION, Operation.DIVISION),
            multLimit = 12
        ),
        DifficultyConfig(
            level = 5,
            maxNumber = 200,
            operations = listOf(Operation.ADDITION, Operation.SUBTRACTION, Operation.MULTIPLICATION, Operation.DIVISION)
        ),
        DifficultyConfig(
            level = 6,
            maxNumber = 500,
            operations = listOf(Operation.ADDITION, Operation.SUBTRACTION, Operation.MULTIPLICATION, Operation.DIVISION)
        )
    )

    fun configForLevel(level: Int): DifficultyConfig {
        val index = (level - 1).coerceIn(0, levels.lastIndex)
        return levels[index]
    }
}

object ProblemGenerator {

    fun generate(level: Int): Problem {
        val config = DifficultyLevels.configForLevel(level)
        return when (config.operations.random()) {
            Operation.ADDITION -> generateAddition(config)
            Operation.SUBTRACTION -> generateSubtraction(config)
            Operation.MULTIPLICATION -> generateMultiplication(config)
            Operation.DIVISION -> generateDivision(config)
        }
    }

    private fun generateAddition(config: DifficultyConfig): Problem {
        val a = Random.nextInt(1, config.maxNumber + 1)
        val b = Random.nextInt(1, config.maxNumber + 1)
        return Problem(a, b, Operation.ADDITION, a + b)
    }

    private fun generateSubtraction(config: DifficultyConfig): Problem {
        val a = Random.nextInt(1, config.maxNumber + 1)
        val b = Random.nextInt(1, a + 1)
        return Problem(a, b, Operation.SUBTRACTION, a - b)
    }

    private fun generateMultiplication(config: DifficultyConfig): Problem {
        val a = Random.nextInt(1, config.maxNumber + 1)
        val bMax = config.multLimit.coerceAtMost(config.maxNumber)
        val b = Random.nextInt(1, bMax + 1)
        return Problem(a, b, Operation.MULTIPLICATION, a * b)
    }

    private fun generateDivision(config: DifficultyConfig): Problem {
        val maxDivisor = config.multLimit.coerceAtMost(config.maxNumber)
        val b = Random.nextInt(2, maxDivisor + 1)
        val maxQuotient = (config.maxNumber / b).coerceAtLeast(1)
        val quotient = Random.nextInt(1, maxQuotient + 1)
        val a = b * quotient
        return Problem(a, b, Operation.DIVISION, quotient)
    }
}
