import java.util.*

fun main() {
    val day = 4

    data class Card(val id: Int, val winners: Set<Int>, val numbers: List<Int>) {
        fun winningNumbers() =
            numbers.filter { winners.contains(it) }

        fun winningCount() =
            winningNumbers().size
    }

    val cardRe = """Card \s*(\d+): (.*) \| (.*)""".toRegex()

    fun parseNumbers(input: String) =
        input.trim()
            .split("""\s+""".toRegex())
            .map { it.toInt() }

    fun parseInput(input: List<String>): List<Card> =
        input
            .mapNotNull { cardRe.matchEntire(it) }
            .map { match ->
                val (idTxt, winnersTxt, numbersTxt) = match.destructured
                Card(idTxt.toInt(), parseNumbers(winnersTxt).toSet(), parseNumbers(numbersTxt))
            }

    fun part1(input: List<String>): Long {
        val data = parseInput(input)
        return data.map { it.winningCount() }
            .filter { it > 0 }
            .sumOf { 1L shl (it - 1) }
    }

    fun part2(input: List<String>): Int {
        val data = parseInput(input)
        val counts = Array(data.size) { 1 }

        data.forEachIndexed { i, c ->
            val count = counts[i]
            val copies = c.winningCount()
            (1..copies).forEach {
                if (i + it < counts.size - 1) { counts[i + it] += count }
            }
        }

        return counts.sumOf { it }
    }

    println("OUTPUT FOR DAY $day")
    println("-".repeat(64))

    val testInput = readInput("Day${day.pad(2)}_test")
    checkTest(13) { part1(testInput) }
    checkTest(30) { part2(testInput) }
    println("-".repeat(64))

    val input = readInput("Day${day.pad(2)}")
    solution { part1(input) }
    solution { part2(input) }
    println("-".repeat(64))
}
