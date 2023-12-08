
fun main() {
    val day = 8

    val turnRegex = """(\S+) = \((\S+), (\S+)\)""".toRegex()
    fun parseNodes(input: List<String>) =
        input.mapNotNull { turnRegex.matchEntire(it) }
            .map { it.destructured }
            .associate { (key, left, right) -> key to (left to right) }

    fun followPath(turns: String, nodes: Map<String, Pair<String, String>>): Int {
        var node = "AAA"
        var steps = 0

        while (node != "ZZZ") {
            val turn = turns[steps % turns.length]
            val choices = nodes.getValue(node)

            node = when (turn) {
                'L' -> choices.first
                else -> choices.second
            }
            steps++
        }

        return steps
    }

    fun ghostPath(turns: String, nodes: Map<String, Pair<String, String>>, start: String): List<String> {
        val path = mutableListOf(start)

        while(true) {
            val turn = turns[(path.size - 1) % turns.length]
            val choices = nodes.getValue(path.last())
            val next = when (turn) {
                'L' -> choices.first
                else -> choices.second
            }
            path.add(next)
            val loop = path.indexOf(next)

            val lastIndex = path.size - 1
            if (loop in 0..<lastIndex) {
                if (loop % turns.length == lastIndex % turns.length) {
                    return path
                }
            }
        }
    }

    fun ghostPaths(turns: String, nodes: Map<String, Pair<String, String>>): Long {
        val paths = nodes.keys
            .filter { it.last() == 'A' }
            .map { ghostPath(turns, nodes, it) }

        data class PathInfo(val loop: Int, val exit: Int, val length: Int)
        fun PathInfo.isExit(steps: Long) =
            ((steps - loop) % length.toLong()).toInt() + loop == exit

        val infos = paths.map { path ->
            val loop = path.indexOf(path.last())
            val exit = path.mapIndexedNotNull { i, n -> i.takeIf { n.last() == 'Z' } }.last()
            val length = path.size - loop - 1
            PathInfo(loop, exit, length).also { println(it) }
        }

        val nextStep = infos.maxOf { it.length }
        var steps = infos.maxOf { it.exit }.toLong()
        while (infos.any { ! it.isExit(steps) }) {
            steps += nextStep
        }
        return steps
    }

    fun part1(input: List<String>): Int {
        val turns = input.first()
        val nodes = parseNodes(input.drop(2))
        return followPath(turns, nodes)
    }

    fun part2(input: List<String>): Long {
        val turns = input.first()
        val nodes = parseNodes(input.drop(2))
        return ghostPaths(turns, nodes)
    }

    println("OUTPUT FOR DAY $day")
    println("-".repeat(64))

    val testInput = readInput("Day${day.pad(2)}_test")
    checkTest(2) { part1(testInput) }
    val testInput2 = readInput("Day${day.pad(2)}_test2")
    checkTest(6) { part1(testInput2) }
    val testInput3 = readInput("Day${day.pad(2)}_test3")
    checkTest(6) { part2(testInput3) }
    println("-".repeat(64))

    val input = readInput("Day${day.pad(2)}")
    solution { part1(input) }
    solution { part2(input) }
    println("-".repeat(64))
}
