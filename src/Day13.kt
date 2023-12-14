
fun main() {
    val day = 13

    fun isMirror(offset: Int, getter: (Int) -> CharSequence, size: Int): Boolean {
        var before = offset
        var after = offset + 1

        while (before >= 0 && after < size) {
            if (getter(before).toString() != getter(after).toString()) return false
            before--
            after++
        }

        return true
    }

    fun isMirrorRow(data: List<CharSequence>, offset: Int) =
        isMirror(offset, data::get, data.size)

    fun isMirrorCol(data: List<CharSequence>, offset: Int) =
        isMirror(offset, data::col, data[0].length)


    fun getMirrorRow(data: List<CharSequence>, skip: Int = -1): Int? {
        val rows = (0..<data.lastIndex)
        return rows.firstOrNull { it != (skip - 1) && isMirrorRow(data, it) }?.let { it + 1 }
    }

    fun getMirrorCol(data: List<CharSequence>, skip: Int = -1): Int? {
        val cols = (0..<data[0].lastIndex)
        return cols.firstOrNull { it != (skip - 1) && isMirrorCol(data, it ) }?.let { it + 1 }
    }

    fun calcMirror(data: List<CharSequence>, skipRow: Int = -1, skipCol: Int = -1): Int? {
        val row = getMirrorRow(data, skipRow)
        if (row != null) return 100 * row
        val col = getMirrorCol(data, skipCol)
        return col
    }

    fun parseInput(input: List<String>) = input.split { it.isBlank() }

    fun part1(input: List<String>): Int {
        val data = parseInput(input)
        return data.sumOf { calcMirror(it) ?: 0 }
    }

    fun smudgeIt(data: List<String>): Int {
        val oldRow = getMirrorRow(data)
        val oldCol = getMirrorCol(data)

        val copy = data.map {
            StringBuilder(it)
        }.toMutableList()

        data.forEachIndexed { row, line ->
            line.forEachIndexed { col, ch ->
                val newChar = when (ch) {
                    '#' -> '.'
                    else -> '#'
                }

                copy[row][col] = newChar
                calcMirror(copy, oldRow ?: -1, oldCol ?: -1)?.also {
//                    println(copy.joinToString("\n"))
//                    println("--> $it")
                    return it
                }
                copy[row][col] = ch
            }
        }

        throw IllegalStateException("No smudge!!!\n${data.joinToString("\n")}")
    }

    fun part2(input: List<String>): Int {
        val data = parseInput(input)
        return data.sumOf { smudgeIt(it) }
    }

    println("OUTPUT FOR DAY $day")
    println("-".repeat(64))

    val testInput = readInput("Day${day.pad(2)}_test")
    checkTest(405) { part1(testInput) }
    checkTest(400) { part2(testInput) }
    println("-".repeat(64))

    val input = readInput("Day${day.pad(2)}")
    solution { part1(input) }
    solution { part2(input) }
    println("-".repeat(64))
}
