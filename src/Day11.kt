fun main() {
    val day = 11

    fun getRow(input: List<String>, row: Int) =
        input[row].toList()

    fun getCol(input: List<String>, col: Int) =
        input.map { it[col] }

    fun emptyRows(input: List<String>) =
        input.mapIndexedNotNull { row, line ->
            if (line.all { it == '.' }) row else null
        }

    fun emptyCols(input: List<String>) =
        input[0].indices
            .mapNotNull { col ->
                if (getCol(input, col).all { it == '.' }) col else null
            }


    fun galaxies(data: List<String>) =
        data.flatMapIndexed { row, line ->
            line.mapIndexedNotNull { col, ch ->
                if (ch == '#') col by row
                else null
            }
        }

    fun dist(first: Pos, second: Pos, emptyRows: List<Int>, emptyCols: List<Int>, multiplier: Long): Long {
        val minX = minOf(first.x, second.x)
        val maxX = maxOf(first.x, second.x)
        val minY = minOf(first.y, second.y)
        val maxY = maxOf(first.y, second.y)

        val cols = (minX..<maxX).count { it in emptyCols }
        val rows = (minY..<maxY).count { it in emptyRows }

        return (maxX - minX) + (maxY - minY) + ((cols + rows) * (multiplier - 1))
    }

    fun part1(input: List<String>, space: Long = 2): Long {
        val rows = emptyRows(input)
        val cols = emptyCols(input)
        val locs = galaxies(input)

        return locs.combinations(2)
            .sumOf { (l, r) -> dist(l, r, rows, cols, space) }
    }

    println("OUTPUT FOR DAY $day")
    println("-".repeat(64))

    val testInput = readInput("Day${day.pad(2)}_test")
    checkTest(374) { part1(testInput) }
    checkTest(1030) { part1(testInput, 10) }
    println("-".repeat(64))

    val input = readInput("Day${day.pad(2)}")
    solution { part1(input) }
    solution { part1(input, 1000000) }
    println("-".repeat(64))
}
