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

enum class OperationGroup { ADD_SUB, MULT_DIV }

data class RangeSpec(val min: Int, val max: Int) {
    fun random(): Int = Random.nextInt(min, max + 1)
}

data class LevelConfig(
    val addSubA: RangeSpec,
    val addSubB: RangeSpec,
    val multDivX: RangeSpec,  // quotient range for division
    val multDivY: RangeSpec   // divisor range for division
)

object DifficultyLevels {
    val levels: List<LevelConfig> = listOf(
        LevelConfig(
            addSubA = RangeSpec(10, 99),   addSubB = RangeSpec(10, 99),
            multDivX = RangeSpec(10, 99),  multDivY = RangeSpec(1, 9)
        ),
        LevelConfig(
            addSubA = RangeSpec(10, 99),   addSubB = RangeSpec(100, 999),
            multDivX = RangeSpec(10, 99),  multDivY = RangeSpec(10, 99)
        ),
        LevelConfig(
            addSubA = RangeSpec(100, 999), addSubB = RangeSpec(100, 999),
            multDivX = RangeSpec(10, 50),  multDivY = RangeSpec(100, 999)
        ),
        LevelConfig(
            addSubA = RangeSpec(100, 999), addSubB = RangeSpec(1000, 9999),
            multDivX = RangeSpec(51, 99),  multDivY = RangeSpec(100, 999)
        ),
        LevelConfig(
            addSubA = RangeSpec(1000, 9999), addSubB = RangeSpec(1000, 9999),
            multDivX = RangeSpec(100, 500), multDivY = RangeSpec(100, 999)
        ),
        LevelConfig(
            addSubA = RangeSpec(10000, 99999), addSubB = RangeSpec(10000, 99999),
            multDivX = RangeSpec(100, 999),    multDivY = RangeSpec(500, 999)
        )
    )

    fun configForLevel(level: Int): LevelConfig =
        levels[(level - 1).coerceIn(0, levels.lastIndex)]
}

object ProblemGenerator {

    fun generate(level: Int, group: OperationGroup): Problem {
        val config = DifficultyLevels.configForLevel(level)
        return when (group) {
            OperationGroup.ADD_SUB ->
                if (Random.nextBoolean()) generateAddition(config) else generateSubtraction(config)
            OperationGroup.MULT_DIV ->
                if (Random.nextBoolean()) generateMultiplication(config) else generateDivision(config)
        }
    }

    private fun generateAddition(config: LevelConfig): Problem {
        val a = config.addSubA.random()
        val b = config.addSubB.random()
        return Problem(a, b, Operation.ADDITION, a + b)
    }

    private fun generateSubtraction(config: LevelConfig): Problem {
        val a = config.addSubA.random()
        val b = config.addSubB.random()
        val larger = maxOf(a, b)
        val smaller = minOf(a, b)
        return Problem(larger, smaller, Operation.SUBTRACTION, larger - smaller)
    }

    private fun generateMultiplication(config: LevelConfig): Problem {
        val a = config.multDivX.random()
        val b = config.multDivY.random()
        return Problem(a, b, Operation.MULTIPLICATION, a * b)
    }

    private fun generateDivision(config: LevelConfig): Problem {
        // quotient from multDivX, divisor from multDivY
        // quotient >= 2 ensures dividend != divisor (avoids result = 1)
        var quotient: Int
        do { quotient = config.multDivX.random() } while (quotient < 2)
        val divisor = config.multDivY.random()
        val dividend = quotient * divisor
        return Problem(dividend, divisor, Operation.DIVISION, quotient)
    }
}
