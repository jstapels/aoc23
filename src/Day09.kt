
fun main() {
    val day = 9

    fun parseInput(input: List<String>) =
        input.map { line ->
            line.split(' ')
                .map { it.toLong() }
        }

    fun calcDiff(data: List<Long>) =
        data.windowed(2)
            .map { (l, r) -> r - l }

    fun getDiffs(data: List<Long>): List<List<Long>> {
        val diffs = mutableListOf(data)

        while (diffs.last().any { it != 0L }) {
            diffs.add(calcDiff(diffs.last()))
        }

        return diffs
    }

    fun nextSeq(data: List<Long>): Long {
        val diffs = getDiffs(data)
        return diffs.sumOf { it.last() }
    }

    fun part1(input: List<String>): Long {
        val data = parseInput(input)
        return data.sumOf { nextSeq(it) }
    }

    fun prevSeq(data: List<Long>): Long {
        val diffs = getDiffs(data)
        return diffs.map { it.first() }
            .reduceRight { l, r -> l - r }
    }

    fun part2(input: List<String>): Long {
        val data = parseInput(input)
        return data.sumOf { prevSeq(it) }
    }

    println("OUTPUT FOR DAY $day")
    println("-".repeat(64))

    val testInput = readInput("Day${day.pad(2)}_test")
    checkTest(114) { part1(testInput) }
    checkTest(2) { part2(testInput) }
    println("-".repeat(64))

    val input = readInput("Day${day.pad(2)}")
    solution { part1(input) }
    solution { part2(input) }
    println("-".repeat(64))
}
