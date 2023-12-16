
fun main() {
    val day = 16

    fun parseInput(input: List<String>) = Grid.fromStrings(input)

    fun followBeam(data: Grid<Char>, pos: Pos = 0 by 0, dir: Dir = Dir.RIGHT, visited: MutableMap<Pos, MutableSet<Dir>> = mutableMapOf()): Set<Pos> {
        if (! data.inBounds(pos)) return visited.keys
        if (visited[pos]?.contains(dir) == true) return visited.keys

        val dirs = visited.getOrPut(pos) { mutableSetOf() }
        dirs.add(dir)

        val type = data[pos]

        val nextDirs = when (dir) {
            Dir.UP -> when (type) {
                '/' -> listOf(Dir.RIGHT)
                '\\' -> listOf(Dir.LEFT)
                '-' -> listOf(Dir.LEFT, Dir.RIGHT)
                else -> listOf(Dir.UP)
            }
            Dir.RIGHT -> when (type) {
                '/' -> listOf(Dir.UP)
                '\\' -> listOf(Dir.DOWN)
                '|' -> listOf(Dir.UP, Dir.DOWN)
                else -> listOf(Dir.RIGHT)
            }
            Dir.DOWN -> when (type) {
                '/' -> listOf(Dir.LEFT)
                '\\' -> listOf(Dir.RIGHT)
                '-' -> listOf(Dir.LEFT, Dir.RIGHT)
                else -> listOf(Dir.DOWN)
            }
            Dir.LEFT -> when (type) {
                '/' -> listOf(Dir.DOWN)
                '\\' -> listOf(Dir.UP)
                '|' -> listOf(Dir.UP, Dir.DOWN)
                else -> listOf(Dir.LEFT)
            }
        }

        nextDirs.forEach {
            followBeam(data, pos + it.pos, it, visited)
        }

        return visited.keys
    }

    fun part1(input: List<String>): Int {
        val data = parseInput(input)
        val charged = followBeam(data)
        return charged.size
    }

    fun part2(input: List<String>): Int {
        val data = parseInput(input)

        val maxUp = (0..data.maxX)
            .map { followBeam(data, it by data.maxY, Dir.UP) }
            .maxOf { it.size }
        val maxRight = (0..data.maxY)
            .map { followBeam(data, 0 by it, Dir.RIGHT) }
            .maxOf { it.size }
        val maxDown = (0..data.maxX)
            .map { followBeam(data, it by 0, Dir.DOWN) }
            .maxOf { it.size }
        val maxLeft = (0..data.maxY)
            .map { followBeam(data, data.maxX by it, Dir.LEFT) }
            .maxOf { it.size }

        return listOf(maxUp, maxRight, maxDown, maxLeft).max()
    }

    println("OUTPUT FOR DAY $day")
    println("-".repeat(64))

    val testInput = readInput("Day${day.pad(2)}_test")
    checkTest(46) { part1(testInput) }
    checkTest(51) { part2(testInput) }
    println("-".repeat(64))

    val input = readInput("Day${day.pad(2)}")
    solution { part1(input) }
    solution { part2(input) }
    println("-".repeat(64))
}
