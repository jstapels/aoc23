fun main() {
    val day = 17

    data class Cart(val pos: Pos, val heat: Int, val dir: Dir)

    fun findPath(data: Grid<Int>, ultra: Boolean = false): Int {
        val start = 0 by 0
        val goal = data.maxX by data.maxY

        val checked = mutableMapOf<Pair<Pos, Dir>, Int>().withDefault { Int.MAX_VALUE }

        val stepCount = if (ultra) 4..10 else 1..3

        val queue = mutableSetOf(
            Cart(start, 0, Dir.RIGHT),
            Cart(start, 0, Dir.DOWN)
        )

        while (queue.isNotEmpty()) {
            val cart = queue.minBy { it.heat }
            queue.remove(cart)

            if (checked.getValue(cart.pos to cart.dir) < cart.heat) {
                //println("Better path for $cart already found")
                continue
            }
            checked[cart.pos to cart.dir] = cart.heat

//            println("Checking -> $cart")

            if (cart.pos == goal) {
                return cart.heat
            }

            val nextDirs = when (cart.dir) {
                Dir.UP, Dir.DOWN -> listOf(Dir.LEFT, Dir.RIGHT)
                Dir.LEFT, Dir.RIGHT -> listOf(Dir.UP, Dir.DOWN)
            }

            nextDirs.forEach { dir ->
                var pos = cart.pos
                var heat = cart.heat
                var steps = 0

                while (steps <= stepCount.last) {
                    if (steps >= stepCount.first)
                        queue.add(Cart(pos, heat, dir))

                    pos += dir.pos
                    if (! data.inBounds(pos)) break

                    heat += data[pos]
                    steps++
                }
            }
        }

        throw IllegalStateException("No solution found")
    }

    fun parseInput(input: List<String>) =
        input.map { line ->
            line.map { it.digitToInt() }
        }.let { Grid(it) }

    fun part1(input: List<String>): Int {
        val data = parseInput(input)
        val heat = findPath(data)
        return heat
    }

    fun part2(input: List<String>): Int {
        val data = parseInput(input)
        val heat = findPath(data, true)
        return heat
    }

    println("OUTPUT FOR DAY $day")
    println("-".repeat(64))

    val testInput = readInput("Day${day.pad(2)}_test")
    checkTest(102) { part1(testInput) }
    checkTest(94) { part2(testInput) }
    val testInput2 = readInput("Day${day.pad(2)}_test2")
    checkTest(71) { part2(testInput2) }
    println("-".repeat(64))

    val input = readInput("Day${day.pad(2)}")
    solution { part1(input) }
    solution { part2(input) }
    println("-".repeat(64))
}
