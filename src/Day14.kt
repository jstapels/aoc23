
fun main() {
    val day = 14

    fun parseInput(input: List<String>) = Grid(input.map { it.toList() })

    fun tilt(data: List<Char>): List<Char> {
        val buf = data.toMutableList()
        var idx = 0
        while (idx < (buf.size - 1)) {
            if (buf[idx] == 'O' || buf[idx] == '#') {
                idx++
                continue
            }

            val nextRock = buf.indexOf('O', idx + 1)
            if (nextRock == -1) break

            val nextBlock = buf.indexOf('#', idx + 1)
            if (nextBlock != -1 && nextBlock < nextRock) {
                idx = nextBlock + 1
                continue
            }

            buf[idx] = 'O'
            buf[nextRock] = '.'
            idx++
        }

        return buf
    }

    fun tilt(platform: Grid<Char>) {
        repeat(platform.maxX + 1) { col ->
            val data = platform.col(col)
            val tiltedData = tilt(data)
            tiltedData.forEachIndexed { row, ch ->
                platform[col, row] = ch
            }
        }
    }

    fun load(platform: Grid<Char>) =
        (0..platform.maxY).sumOf { row ->
            val weight = platform.maxY - row + 1

            val rocks = platform.row(row)
                .count { it == 'O' }

            rocks * weight
        }


    fun cycle(platform: Grid<Char>, count: Int = 1000000000) {
        var cyclesLeft: Int? = null
        val hashes = mutableListOf(platform.hashCode())

        var curCount = 0
        while(curCount < count) {
            // UP
            repeat(platform.maxX + 1) {
                val data = platform.col(it)
                val tiltedData = tilt(data)
                tiltedData.forEachIndexed { row, ch ->
                    platform[it, row] = ch
                }
            }

            // LEFT
            repeat(platform.maxY + 1) {
                val data = platform.row(it)
                val tiltedData = tilt(data)
                tiltedData.forEachIndexed { col, ch ->
                    platform[col, it] = ch
                }
            }

            // DOWN
            repeat(platform.maxX + 1) {
                val data = platform.col(it).reversed()
                val tiltedData = tilt(data).reversed()
                tiltedData.forEachIndexed { row, ch ->
                    platform[it, row] = ch
                }
            }

            // RIGHT
            repeat(platform.maxY + 1) {
                val data = platform.row(it).reversed()
                val tiltedData = tilt(data).reversed()
                tiltedData.forEachIndexed { col, ch ->
                    platform[col, it] = ch
                }
            }

            // Repeating cycle?
            if (cyclesLeft == null) {
                val hash = platform.hashCode()

                if (hashes.contains(hash)) {
                    val cycleStart = hashes.indexOf(hash)
                    val cycleSize = hashes.size - cycleStart

                    //
                    // 0     S --- 6 --> 9
                    // 0 1 2 3 4 5 3 4 5 3
                    //
                    // start - 3
                    // size - 3
                    // 10 - 6 == 4 % 3 == 1

                    cyclesLeft = (((count - 1) - curCount) % cycleSize)
                }
                hashes.add(hash)
            } else {
                cyclesLeft--
                if (cyclesLeft == 0) return
            }

            curCount++
        }
    }

    fun part1(input: List<String>): Int {
        val data = parseInput(input)
        tilt(data)
        return load(data)
    }

    fun part2(input: List<String>): Int {
        val data = parseInput(input)
        cycle(data)
        return load(data)
    }

    println("OUTPUT FOR DAY $day")
    println("-".repeat(64))

    val testInput = readInput("Day${day.pad(2)}_test")
    checkTest(136) { part1(testInput) }
    checkTest(64) { part2(testInput) }
    println("-".repeat(64))

    val input = readInput("Day${day.pad(2)}")
    solution { part1(input) }
    solution { part2(input) }
    println("-".repeat(64))
}
