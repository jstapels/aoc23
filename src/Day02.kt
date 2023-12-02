
fun main() {
    val day = 2

    data class Pull(val red: Int, val green: Int, val blue: Int)
    data class Game(val id: Int, val pulls: List<Pull>)

    fun Game.minCubes() = Pull(
        pulls.maxOf { it.red },
        pulls.maxOf { it.green },
        pulls.maxOf { it.blue }
    )

    fun parsePull(input: String): Pull {
        val cubes = input.split(", ")

        var red = 0
        var green = 0
        var blue = 0
        cubes.forEach {
            val (count, color) = it.split(" ")

            when (color) {
                "red" -> red = count.toInt()
                "green" -> green = count.toInt()
                else -> blue = count.toInt()
            }
        }

        return Pull(red, green, blue)
    }

    fun parsePulls(input: String) = input.split("; ")
        .map { parsePull(it) }
        .toList()

    fun parseGame(input: String): Game {
        val (game, pulls) = input.split(": ")
        val (_, id) = game.split(" ")

        return Game(id.toInt(), parsePulls(pulls))
    }

    fun parseInput(input: List<String>) = input.map { parseGame(it) }
        .toList()

    fun Game.valid(red: Int, green: Int, blue: Int) = pulls.any {
        it.red > red || it.green > green || it.blue > blue
    }.not()

    fun part1(input: List<String>, red: Int = 12, green: Int = 13, blue: Int = 14): Int {
        val data = parseInput(input)

        return data.filter { it.valid(red, green, blue) }
            .sumOf { it.id }
    }

    fun part2(input: List<String>): Int {
        val data = parseInput(input)

        return data.map { it.minCubes() }
            .sumOf { it.red * it.green * it.blue }
    }

    println("OUTPUT FOR DAY $day")
    println("-".repeat(64))

    val testInput = readInput("Day${day.pad(2)}_test")
    checkTest(8) { part1(testInput) }
    checkTest(2286) { part2(testInput) }
    println("-".repeat(64))

    val input = readInput("Day${day.pad(2)}")
    solution { part1(input) }
    solution { part2(input) }
    println("-".repeat(64))
}
