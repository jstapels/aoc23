
fun main() {
    val day = 6

    fun parseInput(input: List<String>): List<Pair<Int, Int>> {
        val times = input[0].removePrefix("Time:").trim()
            .split(spaceRe)
            .map { it.toInt() }

        val dists = input[1].removePrefix("Distance:").trim()
            .split(spaceRe)
            .map { it.toInt() }

        return times zip dists
    }

    fun calc(time: Int) = 0.rangeTo(time)
        .map { it * (time - it) }

    fun wins(time: Int, dist: Int) =
        calc(time).count { it > dist }

    fun part1(input: List<String>): Int {
        val data = parseInput(input)

        return data.map { wins(it.first, it.second) }
            .reduce(Int::times)
    }

    fun parseInput2(input: List<String>): Pair<Long, Long> {
        val time = input[0].removePrefix("Time:")
            .replace(" ", "")
            .toLong()

        val dist = input[1].removePrefix("Distance:")
            .replace(" ", "")
            .toLong()

        return time to dist
    }

    fun part2(input: List<String>): Int {
        val (time, dist) = parseInput2(input)

        return 0L.rangeTo(time)
            .count { (it * (time - it)) > dist }
    }

    println("OUTPUT FOR DAY $day")
    println("-".repeat(64))

    val testInput = readInput("Day${day.pad(2)}_test")
    checkTest(288) { part1(testInput) }
    checkTest(71503) { part2(testInput) }
    println("-".repeat(64))

    val input = readInput("Day${day.pad(2)}")
    solution { part1(input) }
    solution { part2(input) }
    println("-".repeat(64))
}
