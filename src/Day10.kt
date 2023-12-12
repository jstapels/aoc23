
fun main() {
    val day = 10

    val pipeSpec = mapOf(
        '|' to listOf(upPos, downPos),
        '-' to listOf(leftPos, rightPos),
        'L' to listOf(upPos, rightPos),
        'J' to listOf(upPos, leftPos),
        '7' to listOf(leftPos, downPos),
        'F' to listOf(downPos, rightPos)
    )

    fun startPos(pipes: Array<CharArray>): Pos {
        val y = pipes.indexOfFirst { it.contains('S') }
        val x = pipes[y].indexOf('S')
        return x by y
    }

    fun Array<CharArray>.getPos(pos: Pos) = this[pos.y][pos.x]

    fun nextPipes(pos: Pos, data: Array<CharArray>) =
        pos.orthosBounded(maxX = data[0].size, maxY = data.size)
            .filter { checkPos ->
                val type = data.getPos(checkPos)
                type in pipeSpec
                        && pipeSpec[type]!!.any { (checkPos + it) == pos }
            }

    fun path(data: Array<CharArray>): List<Pos> {
        val start = startPos(data)
        var curPos = nextPipes(start, data).first()
        var lastPos = start
        val path = mutableListOf(start)
        while (curPos != start) {
            path.add(curPos)
            val nextPos = pipeSpec.getValue(data.getPos(curPos))
                .map { curPos + it }
                .first { it != lastPos }
            lastPos = curPos
            curPos = nextPos
        }

        return path
    }

    fun dumpMap(data: Array<CharArray>) {
        data.forEachIndexed { y, line ->
            line.forEachIndexed { x, c ->
                when (c) {
                    '|' -> print("│")
                    '-' -> print("─")
                    'L' -> print("└")
                    'J' -> print("┘")
                    'F' -> print("┌")
                    '7' -> print("┐")
                    else -> print(c)
                }
            }
            println()
        }
    }


    fun parseInput(input: List<String>) =
        input.map { it.toCharArray() }
            .toTypedArray()

    fun part1(input: List<String>): Int {
        val data = parseInput(input)
        val steps = path(data).size
        return (steps / 2)
    }

    // 0 1 2 3
    //  X
    // 1
    //
    // X = (1,1) -> UL(0,0), UR(1,0), LL(0,1), LR(1,1)

    val checkUL = upLeftPos
    val checkUR = upPos
    val checkLL = leftPos
    val checkLR = thisPos

    val blocked = mapOf(
        upPos to mapOf(checkUL to setOf('-', 'F', 'L'), checkUR to setOf('-', '7', 'J')),
        rightPos to mapOf(checkUR to setOf('|', 'F', '7'), checkLR to setOf('|', 'L', 'J')),
        downPos to mapOf(checkLL to setOf('-', 'F', 'L'), checkLR to setOf('-', '7', 'J')),
        leftPos to mapOf(checkUL to setOf('|', 'F', '7'), checkLL to setOf('|', 'L', 'J')),
    )

    fun pathToEdge(check: Pos, path: List<Pos>, data: Array<CharArray>): Boolean {
        val nodesToSearch = mutableListOf(check)
        val checkedNodes = mutableListOf<Pos>()
        val maxX = data[0].size + 1
        val maxY = data.size + 1

        while (nodesToSearch.isNotEmpty()) {
            val pos = nodesToSearch.removeLast()
            checkedNodes.add(pos)

            if (pos.x == 0 || pos.x == maxX ||
                pos.y == 0 || pos.y == maxY) {
                return true
            }

            if (pos.x in data[0].indices && pos.y in data.indices) {
                if (data.getPos(pos) == 'I') return false
                if (data.getPos(pos) == 'O') return true
            }

            blocked.forEach { (nextDir, checks) ->
                val free = checks.all { (checkDir, type) ->
                    val checkPos = pos + checkDir
                    (checkPos !in path || data.getPos(checkPos) !in type)
                }
                val nextPos = pos + nextDir
                if (free && nextPos !in checkedNodes) nodesToSearch.add(nextPos)
            }
        }

        return false
    }

    fun enclosed(path: List<Pos>, data: Array<CharArray>): List<Pos> {
        val interior = mutableListOf<Pos>()
        data.forEachIndexed { y, line ->
            line.forEachIndexed { x, type ->
                val pos = x by y
                if (pos !in path) {
                    val edge = pathToEdge(pos, path, data)
                    if (!edge) interior.add(pos)
                    if (type == '.') {
                        data[pos.y][pos.x] = if (edge) 'O' else 'I'
                    }
                }
            }
        }

        return interior
    }

    fun part2(input: List<String>): Int {
        val data = parseInput(input)
        val path = path(data)

        // Clean garbage
        data.forEachIndexed { y, line ->
            line.forEachIndexed { x, type ->
                if (Pos(x, y) !in path) {
                    data[y][x] = '.'
                }
            }
        }

        val interior = enclosed(path, data)

        dumpMap(data)

        return interior.size
    }

    println("OUTPUT FOR DAY $day")
    println("-".repeat(64))

    var testInput: List<String> = readInput("Day${day.pad(2)}_test")
    checkTest(4) { part1(testInput) }
    testInput = readInput("Day${day.pad(2)}_test2")
    checkTest(8) { part1(testInput) }

    testInput = readInput("Day${day.pad(2)}_test3")
    checkTest(4) { part2(testInput) }
    testInput = readInput("Day${day.pad(2)}_test4")
    checkTest(8) { part2(testInput) }
    testInput = readInput("Day${day.pad(2)}_test5")
    checkTest(10) { part2(testInput) }
    println("-".repeat(64))

    val input = readInput("Day${day.pad(2)}")
    solution { part1(input) }
    solution { part2(input) }
    println("-".repeat(64))
}
