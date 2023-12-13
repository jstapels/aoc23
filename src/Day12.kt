
fun main() {
    val day = 12

    data class Condition(val data: String, val groups: List<Int>)

    val memory = mutableMapOf<Pair<String, List<Int>>, Long>()
    fun matchCount(springs: String, damaged: List<Int>, count: Int? = null): Long {
        if (count == null) {
            memory[springs to damaged]?.also { return it }
        }

        if (springs.isEmpty()) {
            return when {
                count != null && count != 0 -> 0
                damaged.isNotEmpty() -> 0
                else -> 1L
            }
        }

        val spring = springs.first()
        if (spring == '.') {
            return when {
                count != null && count != 0 -> 0
                else -> matchCount(springs.drop(1), damaged)
            }
        }

        if (spring == '#') {
            return when {
                count == null && damaged.isEmpty() -> 0
                count == null -> matchCount(springs.drop(1), damaged.drop(1), damaged.first() - 1)
                count == 0 -> 0
                else -> matchCount(springs.drop(1), damaged, count - 1)
            }
        }

        val left = matchCount("." + springs.drop(1), damaged, count)
        val right = matchCount("#" + springs.drop(1), damaged, count)
        if (count == null) memory[springs to damaged] = left + right

        return left + right
    }

    fun parseInput(input: List<String>, folded: Boolean = false) =
        input.map { line ->
            var (springs, counts) = line.split(' ')
            if (folded) {
                springs = (0..4).joinToString("?") { springs }
                counts = (0..4).joinToString(",") { counts }
            }
            val groups = counts.split(',')
                .map { it.toInt() }
            Condition(springs, groups)
        }

    fun part1(input: List<String>): Long {
        val data = parseInput(input)
        return data.sumOf { (springs, dmg) -> matchCount(springs, dmg) }
    }

    fun part2(input: List<String>): Long {
        val data = parseInput(input, true)
        return data.sumOf { (springs, dmg) -> matchCount(springs, dmg) }
    }

    println("OUTPUT FOR DAY $day")
    println("-".repeat(64))

    val testInput = readInput("Day${day.pad(2)}_test")
    checkTest(21) { part1(testInput) }
    checkTest(525152) { part2(testInput) }
    println("-".repeat(64))

    val input = readInput("Day${day.pad(2)}")
    solution { part1(input) }
    solution { part2(input) }
    println("-".repeat(64))
}
