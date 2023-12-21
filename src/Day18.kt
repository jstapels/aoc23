import kotlin.math.absoluteValue
import kotlin.math.sign

fun main() {
    val day = 18

    fun charToDir(dir: Char) = when (dir) {
        'R', '0' -> Dir.RIGHT
        'D', '1' -> Dir.DOWN
        'L', '2' -> Dir.LEFT
        else -> Dir.UP
    }

    data class Dig(val dir: Dir, val len: Long)

    data class Coord(val x: Long, val y: Long)

    fun digToCoords(digs: Collection<Dig>): List<Coord> {
        var c = Coord(0L, 0L)
        return digs.map { d ->
            c = when (d.dir) {
                Dir.RIGHT -> Coord(c.x + d.len, c.y)
                Dir.DOWN -> Coord(c.x, c.y - d.len)
                Dir.LEFT -> Coord(c.x - d.len, c.y)
                Dir.UP -> Coord(c.x, c.y + d.len)
            }
            c
        }
    }

    fun orthoArea(coords: List<Coord>): Long {
        val all = listOf(coords.last()) + coords
        val area = all.windowed(2)
            .sumOf { (l, r) -> (r.x - l.x) * r.y }
        val perim = all.windowed(2)
            .sumOf { (l, r) -> (r.x - l.x).absoluteValue + (r.y - l.y).absoluteValue }
        return area + ((perim / 2) + 1)
    }

    fun parseInput(input: List<String>, color: Boolean = false) =
        input.map { it.split(' ') }
            .map { (d, l, c) ->
                if (color) {
                    val col = c.trim('(', '#', ')')
                    val dir = charToDir(col.last())
                    val len = col.dropLast(1).toLong(16)
                    Dig(dir, len)
                } else {
                    val dir = charToDir(d.first())
                    val len = l.toLong()
                    Dig(dir, len)
                }
            }

    fun part1(input: List<String>): Long {
        val data = parseInput(input)
        val coords = digToCoords(data)
        return orthoArea(coords)
    }

    fun part2(input: List<String>): Long {
        val data = parseInput(input, true)
        val coords = digToCoords(data)
        return orthoArea(coords)
    }

    println("OUTPUT FOR DAY $day")
    println("-".repeat(64))

    val testInput0 = readInput("Day${day.pad(2)}_test0")
    val testInput = readInput("Day${day.pad(2)}_test")
    checkTest(27) { part1(testInput0) }
    checkTest(62) { part1(testInput) }
    checkTest(952408144115L) { part2(testInput) }
    println("-".repeat(64))

    val input = readInput("Day${day.pad(2)}")
    solution { part1(input) }
    solution { part2(input) }
    println("-".repeat(64))
}
