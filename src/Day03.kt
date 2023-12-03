
fun main() {
    val day = 3

    data class Part(val part: Int, val pos: List<Pos>)

    fun hasSymbol(input: List<String>, line: Int, cols: IntRange) =
        cols.flatMap { (it by line).adjacentsBounded(maxX = input[0].length - 1, maxY = input.size - 1) }
            .toSet()
            .any { input[it.y][it.x].let { c -> ! (c.isDigit() || c == '.') } }

    fun createPart(number: MatchResult, input: List<String>, y: Int): Part? =
        when(hasSymbol(input, y, number.range)) {
            true -> Part(number.value.toInt(), number.range.map { it by y })
            false -> null
        }

    val partNumberRe = """\d+""".toRegex()

    fun parseInput(input: List<String>) =
        input.flatMapIndexed { y, line ->
            partNumberRe.findAll(line)
                .mapNotNull { createPart(it, input, y) }
        }

    fun part1(input: List<String>): Int {
        val data = parseInput(input)
        return data.sumOf { it.part }
    }

    fun gears(input: List<String>) = input.flatMapIndexed { y: Int, line: String ->
        line.mapIndexedNotNull { x, c -> if (c == '*') x by y else null }
    }

    fun gearRatio(pos: Pos, parts: List<Part>): Long {
        val adjParts = parts.filter { p -> p.pos.any { it.adjacents().contains(pos) } }
            .toList()

        if (adjParts.size == 2) {
            return adjParts[0].part.toLong() * adjParts[1].part
        }

        return 0
    }

    fun part2(input: List<String>): Long {
        val data = parseInput(input)
        return gears(input).sumOf { gearRatio(it, data) }
    }

    println("OUTPUT FOR DAY $day")
    println("-".repeat(64))

    val testInput = readInput("Day${day.pad(2)}_test")
    checkTest(4361) { part1(testInput) }
    checkTest(467835) { part2(testInput) }
    println("-".repeat(64))

    val input = readInput("Day${day.pad(2)}")
    solution { part1(input) }
    solution { part2(input) }
    println("-".repeat(64))
}
