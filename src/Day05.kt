
fun main() {
    val day = 5

    data class Mapping(val src: String, val dst: String, val mappings: Map<LongRange, LongRange>) {
        fun mapping(range: LongRange): Pair<LongRange, LongRange> {
            val entry = mappings.entries
                .find { it.key.contains(range.first) }

            if (entry != null) return entry.toPair()

            val nextEntry = mappings.entries
                .filter { it.key.first > range.first }
                .minByOrNull { it.key.first }

            if (nextEntry != null) {
                val mapRange = range.first..<nextEntry.key.first
                return mapRange to mapRange
            }

            val mapRange = range.first..Long.MAX_VALUE
            return mapRange to mapRange
        }
    }

    val headingRegex = """(\w+)-to-(\w+) map:""".toRegex()
    fun parseMapping(input: List<String>): Mapping {
        val (src, dst) = headingRegex.matchEntire(input[0])!!.destructured
        val mappings = input.drop(1)
            .map { it.split(' ').map(String::toLong) }
            .associate { it[1].rangeUntil(it[1] + it[2]) to it[0].rangeUntil(it[0] + it[2]) }
        return Mapping(src, dst, mappings)
    }

    fun parseInput(input: List<String>, ranges: Boolean = false): Pair<List<LongRange>, List<Mapping>> {
        val seedData = input[0].removePrefix("seeds: ")
            .split(' ')
            .map { it.toLong() }

        val seeds = when (ranges) {
            true -> seedData.chunked(2)
                .map { it[0].rangeUntil(it[0] + it[1]) }
            false -> seedData.map { it.rangeUntil(it + 1)}
        }

        val mappings = input.drop(2)
            .split { it.isBlank() }
            .map { parseMapping(it) }

        return seeds to mappings
    }

    fun seedRanges(seeds: LongRange, mappings: List<Mapping>): List<LongRange> {
        if (mappings.isEmpty()) return listOf(seeds)

        val map = mappings.first()

        val (key, value) = map.mapping(seeds)

        if (seeds.last <= key.last) {
            val newStart = value.first + seeds.first - key.first
            val newEnd = newStart + seeds.span()
            val newRange = newStart.rangeTo(newEnd)

            return seedRanges(newRange, mappings.drop(1))
        } else {
            val newStart = value.first + seeds.first - key.first
            val newEnd = value.last
            val newRange = newStart.rangeTo(newEnd)

            val remStart = key.last + 1
            val remEnd = seeds.last
            val remRange = remStart.rangeTo(remEnd)

            return seedRanges(newRange, mappings.drop(1)) +
                    seedRanges(remRange, mappings)
        }
    }

    fun part1(input: List<String>): Long {
        val (seeds, mappings) = parseInput(input)
        val newSeeds = seeds.flatMap { seedRanges(it, mappings) }
        return newSeeds.minOf { it.first }
    }

    fun part2(input: List<String>): Long {
        val (seeds, mappings) = parseInput(input, true)
        val newSeeds = seeds.flatMap { seedRanges(it, mappings) }
        return newSeeds.minOf { it.first }
    }

    println("OUTPUT FOR DAY $day")
    println("-".repeat(64))

    val testInput = readInput("Day${day.pad(2)}_test")
    checkTest(35) { part1(testInput) }
    checkTest(46) { part2(testInput) }
    println("-".repeat(64))

    val input = readInput("Day${day.pad(2)}")
    solution { part1(input) }
    solution { part2(input) }
    println("-".repeat(64))
}
