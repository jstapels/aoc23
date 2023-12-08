
fun main() {
    val day = 7

    val cardRanks = mapOf(
        '*' to 0,
        '2' to 2,
        '3' to 3,
        '4' to 4,
        '5' to 5,
        '6' to 6,
        '7' to 7,
        '8' to 8,
        '9' to 9,
        'T' to 10,
        'J' to 11,
        'Q' to 12,
        'K' to 13,
        'A' to 14
    )

    fun isXOfAKind(hand: String, x: Int) = hand.toSet().any { c -> hand.count { it == c } == x }

    fun isFullHouse(hand: String) = isXOfAKind(hand, 3) && isXOfAKind(hand, 2)

    fun isTwoPair(hand: String) = hand.toSet()
        .combinations(2)
        .any { (l, r) -> hand.count { it == l } == 2 && hand.count { it == r } == 2}

    fun compareCards(l: String, r: String): Int {
        if (l == r) return 0
        val lc = l.first()
        val rc = r.first()
        if (lc == rc) return compareCards(l.drop(1), r.drop(1))
        val lr = cardRanks.getValue(lc)
        val rr = cardRanks.getValue(rc)
        return lr.compareTo(rr)
    }

    fun handRank(hand: String) = when {
        isXOfAKind(hand, 5) -> 100
        isXOfAKind(hand, 4) -> 90
        isFullHouse(hand) -> 80
        isXOfAKind(hand, 3) -> 70
        isTwoPair(hand) -> 60
        isXOfAKind(hand, 2) -> 50
        else -> 1 //hand.maxOf { cardRanks.getValue(it) }
    }

    val wildCard = 'J'
    fun wildHand(hand: String): String {
        if (! hand.contains(wildCard)) return hand
        val best = hand.filter { it != wildCard }
            .groupingBy { it }
            .eachCount()
            .maxByOrNull { it.value }
            ?.key ?: wildCard
        return hand.replace(best, wildCard)
    }

    val spec = '*'
    fun compareHands(l: String, r: String, wild: Boolean = false): Int {
        val lw = if (wild) wildHand(l) else l
        val rw = if (wild) wildHand(r) else r
        val lr = if (wild) l.replace(wildCard, spec) else l
        val rr = if (wild) r.replace(wildCard, spec) else r
        return handRank(lw).compareTo(handRank(rw))
            .takeUnless { it == 0 }
            ?: compareCards(lr, rr)
    }

    fun parseInput(input: List<String>): List<Pair<String, Int>> =
        input.map { it.split(' ') }
            .map { it[0] to it[1].toInt() }

    fun part1(input: List<String>): Int {
        val data = parseInput(input)
            .toMap()

        val rankedHands = data.keys
            .sortedWith { l, r -> compareHands(l, r) }

        return rankedHands.mapIndexed { i, h -> data.getValue(h) * (i + 1) }
            .sum()
    }

    fun part2(input: List<String>): Int {
        val data = parseInput(input)
            .toMap()

        val rankedHands = data.keys
            .sortedWith { l, r -> compareHands(l, r, true) }

        println(rankedHands)

        return rankedHands.mapIndexed { i, h -> data.getValue(h) * (i + 1) }
            .sum()
    }

    println("OUTPUT FOR DAY $day")
    println("-".repeat(64))

    val testInput = readInput("Day${day.pad(2)}_test")
    checkTest(6440) { part1(testInput) }
    checkTest(5905) { part2(testInput) }
    println("-".repeat(64))

    val input = readInput("Day${day.pad(2)}")
    solution { part1(input) }
    solution { part2(input) }
    println("-".repeat(64))
}
