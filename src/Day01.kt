fun main() {
    val day = 1

    fun calibration(input: String): Int {
        val firstNum =  input.first { it.isDigit() }.digitToInt()
        val lastNum = input.last { it.isDigit() }.digitToInt()
        return firstNum * 10 + lastNum
    }


    fun part1(input: List<String>): Int {
        return input.sumOf { calibration(it) }
    }

    val numLookup = mapOf(
        "zero" to 0, "one" to 1, "two" to 2, "three" to 3, "four" to 4,
        "five" to 5, "six" to 6, "seven" to 7, "eight" to 8, "nine" to 9
    )
    val numRegex = Regex(numLookup.keys.joinToString("|"))

    fun num2digit(input: String): String {
        return input.replace(numRegex) { numLookup[it.value].toString() }
    }

    fun part2(input: List<String>): Int {
        return input.map { num2digit(it) }
            .sumOf { calibration(it) }
    }

    println("OUTPUT FOR DAY $day")
    println("-".repeat(64))

    val testInput = readInput("Day${day.pad(2)}_test")
    checkTest(142) { part1(testInput) }
    val testInput2 = readInput("Day${day.pad(2)}_test2")
    checkTest(281) { part2(testInput2) }
    println("-".repeat(64))

    val input = readInput("Day${day.pad(2)}")
    solution { part1(input) }
    solution { part2(input) }
    println("-".repeat(64))
}
