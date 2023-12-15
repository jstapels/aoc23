
fun main() {
    val day = 15

    fun parseInput(input: List<String>) =
        input.first().split(',')

    fun hash(data: String) =
        data.fold(0) { hash, value ->
            ((hash + value.code) * 17) % 256
        }

    fun part1(input: List<String>): Int {
        val data = parseInput(input)
        return data.sumOf { hash(it) }
    }

    data class Lens(val id: String, val op: Char, val power: Int? = null) {
        fun hash() = hash(id)
        fun isAdd() = op == '='
    }

    fun parseLens(input: List<String>) = input.first()
        .split(',')
        .map { when (it.last()) {
            '-' -> Lens(it.dropLast(1), '-')
            else -> {
                val (id, power) = it.split('=')
                Lens(id, '=', power.toInt())
            }
        }}

    fun setupBoxes(data: List<Lens>): Map<Int, List<Lens>> {
        val boxes = mutableMapOf<Int, MutableList<Lens>>()

        data.forEach { lens ->
            val boxId = lens.hash()
            val curBox = boxes[boxId] ?: mutableListOf()

            if (lens.isAdd()) {
                val existingLens = curBox.indexOfFirst { it.id == lens.id }
                if (existingLens != -1) curBox[existingLens] = lens
                else curBox.add(lens)
                boxes[boxId] = curBox
            } else {
                curBox.removeIf { it.id == lens.id }
                if (curBox.isEmpty()) boxes.remove(boxId)
            }
        }

        return boxes
    }

    fun lensPower(boxes: Map<Int,List<Lens>>) =
        boxes.entries
            .sortedBy { it.key }
            .sumOf { (boxId, lenses) ->
                lenses.mapIndexed { i, lens -> (1 + boxId) * (1 + i) * lens.power!! }
                    .sum()
            }

    fun part2(input: List<String>): Int {
        val data = parseLens(input)
        val boxes = setupBoxes(data)
        return lensPower(boxes)
    }

    println("OUTPUT FOR DAY $day")
    println("-".repeat(64))

    val testInput = readInput("Day${day.pad(2)}_test")
    checkTest(52) { part1(listOf("HASH")) }
    checkTest(1320) { part1(testInput) }
    checkTest(145) { part2(testInput) }
    println("-".repeat(64))

    val input = readInput("Day${day.pad(2)}")
    solution { part1(input) }
    solution { part2(input) }
    println("-".repeat(64))
}
