
fun main() {

    val day = 19

    data class Part(val cats: Map<String, Int>)

    data class Check(val id: String, val cat: String? = null, val op: Char? = null, val num: Int? = null)

    fun parseChecks(s: String) =
        s.split(',')
            .map {
                if (! it.contains(':') ) {
                    return@map Check(it)
                }

                val (log, id) = it.split(':')
                val (cat, num) = log.split('<', '>')

                val op = if (it.contains('<')) {
                    '<'
                } else {
                    '>'
                }

                Check(id, cat, op, num.toInt())
            }

    data class Workflow(val id: String, val checks: List<Check>)

    fun parseWorkflow(s: String) =
        s.split('{', '}')
            .let { (id, ops) -> Workflow(id, parseChecks(ops)) }
    fun parsePart(s: String) =
        s.trim('{', '}')
            .split(',')
            .associate { it.split('=').let { (k, v) -> k to v.toInt() } }
            .let { Part(it) }

    fun runPart(wfs: Map<String, List<Check>>, part: Part, state: String = "in"): Boolean {
        if (state == "A") return true
        if (state == "R") return false

        val checks = wfs[state]!!

        checks.forEach { check ->
            when (check.op) {
                null -> return runPart(wfs, part, check.id)
                '<' -> if (part.cats[check.cat!!]!! < check.num!!)
                    return runPart(wfs, part, check.id)
                '>' -> if (part.cats[check.cat!!]!! > check.num!!)
                    return runPart(wfs, part, check.id)
            }
        }

        error("$state -> $part")
    }


    fun parseInput(input: List<String>): Pair<List<Workflow>, List<Part>> {
        val (ws, ps) = input.split()

        val workflows = ws.map { parseWorkflow(it) }
        val parts = ps.map { parsePart(it) }

        return Pair(workflows, parts)
    }

    fun part1(input: List<String>): Int {
        val (workflows, parts) = parseInput(input)
        val workFlowMap = workflows.associate { it.id to it.checks }
        return parts.filter { runPart(workFlowMap, it) }
            .sumOf { it.cats.values.sum() }
    }

    fun IntRange.splitLeft(v:Int) =
        Pair(start..v, (v+1)..endInclusive)
    fun IntRange.splitRight(v:Int) =
        Pair(start..<v, v..endInclusive)

    val startingCatRange = 1..4000
    val startRanges = mapOf("x" to startingCatRange, "m" to startingCatRange, "a" to startingCatRange, "s" to startingCatRange)
    fun combos(workflows: Map<String, List<Check>>, state: String = "in", ranges: Map<String, IntRange> = startRanges): List<Map<String, IntRange>> {
        when (state) {
            "A" -> return listOf(ranges)
            "R" -> return emptyList()
        }

        val checks = workflows[state]!!

        val curRanges = ranges.toMutableMap()
        val valids = mutableListOf<Map<String, IntRange>>()
        checks.forEach { check ->
            when {
                check.op == null -> return valids + combos(workflows, check.id, curRanges)
                else -> {
                    val catId = check.cat!!
                    val catRange = curRanges[catId]!!
                    if (check.op == '<') {
                        val (l, r) = catRange.splitRight(check.num!!)
                        valids.addAll(combos(workflows, check.id, curRanges + (catId to l)))
                        curRanges[catId] = r
                    } else {
                        val (l, r) = catRange.splitLeft(check.num!!)
                        valids.addAll(combos(workflows, check.id, curRanges + (catId to r)))
                        curRanges[catId] = l
                    }
                }
            }
        }

        return valids
    }

    fun part2(input: List<String>): ULong {
        val (workflows, _) = parseInput(input)
        val workFlowMap = workflows.associate { it.id to it.checks }
        val validCombos = combos(workFlowMap)
        println("Answer!! -> $validCombos")
        return validCombos.sumOf { ranges ->
            ranges.values
                .fold(1UL) { l, r -> l * r.size().toULong() }
        }
    }

    println("OUTPUT FOR DAY $day")
    println("-".repeat(64))

    val testInput = readInput("Day${day.pad(2)}_test")
    checkTest(19114) { part1(testInput) }
    checkTest(167409079868000UL) { part2(testInput) }
    println("-".repeat(64))

    val input = readInput("Day${day.pad(2)}")
    solution { part1(input) }
    solution { part2(input) }
    println("-".repeat(64))
}
