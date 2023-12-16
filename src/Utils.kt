import java.math.BigInteger
import java.security.MessageDigest
import kotlin.system.measureTimeMillis
import kotlin.io.path.Path
import kotlin.io.path.readLines

const val debug = false

val spaceRe = """\s+""".toRegex()

val thisPos = Pos(0, 0)
val upPos = Pos(0, -1)
val upRightPos = Pos(1, -1)
val rightPos = Pos(1, 0)
val downRightPos = Pos(1, 1)
val downPos = Pos(0, 1)
val downLeftPos = Pos(-1, 1)
val leftPos = Pos(-1, 0)
val upLeftPos = Pos(-1, -1)

enum class Dir(val pos: Pos) {
    UP (upPos),
    RIGHT (rightPos),
    DOWN (downPos),
    LEFT (leftPos)
}


val orthoPos = listOf(upPos, rightPos, downPos, leftPos)
val adjacentPos = listOf(upPos, upRightPos, rightPos, downRightPos, downPos, downLeftPos, leftPos, upLeftPos)

data class Pos(val x: Int, val y: Int) {
    operator fun plus(p: Pos) =
        Pos(x + p.x, y + p.y)

    fun orthos() = orthoPos.map { this + it }

    fun adjacents() = adjacentPos.map { this + it }

    fun orthosBounded(minX: Int = 0, minY: Int = 0, maxX: Int = Int.MAX_VALUE, maxY: Int = Int.MAX_VALUE) =
        orthos().filter { (x, y) -> x in minX..maxX && y in minY..maxY }

    fun adjacentsBounded(minX: Int = 0, minY: Int = 0, maxX: Int = Int.MAX_VALUE, maxY: Int = Int.MAX_VALUE) =
        adjacents().filter { (x, y) -> x in minX..maxX && y in minY..maxY }
}

infix fun Int.by(that: Int) = Pos(this, that)

data class Grid<T>(
    private val width: Int,
    private val height: Int,
    private val data: MutableList<T> = ArrayList(width * height)
) {
    constructor(width: Int, height: Int, initialValue: T) : this(width, height) {
        repeat(width * height) {
            data.add(initialValue)
        }
    }
    constructor(data: Collection<Collection<T>>) : this(data.first().size, data.size, data.flatten().toMutableList())

    companion object {
        fun fromStrings(data: List<String>) =
            Grid(data[0].length, data.size, data.flatMap { it.toList() }.toMutableList())
    }

    val maxX = width - 1
    val maxY = height - 1
    val pos: List<Pos> by lazy { data.indices.map { toPos(it) } }

    private fun checkBounds(x: Int, y: Int) {
        if (x !in 0 ..< width) throw IllegalArgumentException("$x out of bounds for grid with width $width")
        if (y !in 0 ..< height) throw IllegalArgumentException("$y out of bounds for grid with height $height")
    }

    fun row(y: Int) =
        data.slice(y * width..<(y+1) * width)

    fun col(x: Int) =
        data.filterIndexed { i, _ -> i % width == x }

    operator fun get(x: Int, y: Int) =
        checkBounds(x, y).let { data[y * width + x] }

    operator fun set(x: Int, y: Int, v: T) =
        checkBounds(x, y).let { data[y * width + x] = v }

    operator fun get(p: Pos) = get(p.x, p.y)
    operator fun set(p: Pos, v: T) = set(p.x, p.y, v)


    fun toPos(idx: Int) =
        Pos(idx % width, idx / width)

    fun inBounds(p: Pos) = p.x >= 0 && p.y >= 0 && p.x < width && p.y < height

    fun orthos(p: Pos) = p.orthos().filter { inBounds(it) }

    fun adjacents(p: Pos) = p.adjacents().filter { inBounds(it) }

    fun dump() = (0..maxY).forEach { println(row(it)) }
}

fun Int.pad(n: Int) =
    this.toString().padStart(n, '0')

operator fun <T> Array<Array<T>>.get(x: Int, y: Int) =
    this[x][y]

fun List<CharSequence>.col(col:Int) =
    map { it[col] }
        .joinToString("")

fun <T> List<T>.indexOf(element: T, startIndex: Int): Int {
    var i = startIndex
    while (i < size) {
        if (get(i) == element) return i
        i++
    }
    return -1
}

// From: https://www.reddit.com/r/Kotlin/comments/isg16h/comment/g5fvsw3/?utm_source=share&utm_medium=web2x&context=3
fun <T> Iterable<T>.combinations(length: Int): Sequence<List<T>> =
    sequence {
        val pool = this@combinations as? List<T> ?: toList()
        val n = pool.size
        if(length > n) return@sequence
        val indices = IntArray(length) { it }
        while(true) {
            yield(indices.map { pool[it] })
            var i = length
            do {
                i--
                if(i == -1) return@sequence
            } while(indices[i] == i + n - length)
            indices[i]++
            for(j in i+1 until length) indices[j] = indices[j - 1] + 1
        }
    }

// From: https://www.reddit.com/r/Kotlin/comments/isg16h/comment/g5fvsw3/?utm_source=share&utm_medium=web2x&context=3
fun <T> Iterable<T>.permutations(length: Int? = null): Sequence<List<T>> =
    sequence {
        val pool = this@permutations as? List<T> ?: toList()
        val n = pool.size
        val r = length ?: n
        if(r > n) return@sequence
        val indices = IntArray(n) { it }
        val cycles = IntArray(r) { n - it }
        yield(List(r) { pool[indices[it]] })
        if(n == 0) return@sequence
        cyc@ while(true) {
            for(i in r-1 downTo 0) {
                cycles[i]--
                if(cycles[i] == 0) {
                    val temp = indices[i]
                    for(j in i until n-1) indices[j] = indices[j+1]
                    indices[n-1] = temp
                    cycles[i] = n - i
                } else {
                    val j = n - cycles[i]
                    indices[i] = indices[j].also { indices[j] = indices[i] }
                    yield(List(r) { pool[indices[it]] })
                    continue@cyc
                }
            }
            return@sequence
        }
    }

fun <T> List<T>.split(predicate: (T) -> Boolean): List<List<T>> {
    val results = mutableListOf<List<T>>()
    var chunk = mutableListOf<T>()

    forEach {
        if (predicate(it)) {
            results.add(chunk)
            chunk = mutableListOf()
        } else {
            chunk.add(it)
        }
    }
    results.add(chunk)

    return results
}

fun LongRange.span() = last - start
fun IntRange.span() = last - start

// From: https://www.baeldung.com/kotlin/lcm
fun findLCM(a: Long, b: Long): Long {
    val larger = if (a > b) a else b
    val maxLcm = a * b
    var lcm = larger
    while (lcm <= maxLcm) {
        if (lcm % a == 0L && lcm % b == 0L) {
            return lcm
        }
        lcm += larger
    }
    return maxLcm
}



/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = Path("src/$name.txt").readLines()

/**
 * Converts string to md5 hash.
 */
fun String.md5() = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
    .toString(16)
    .padStart(32, '0')

/**
 * Quick assertion check.
 */
fun <T> checkThat(actual: T, expected: T) {
    if (actual != expected)
        throw AssertionError("Actual $actual does not equal $expected")
}

var testCount = 1
fun <T> checkTest(expected: T, runner: () -> T) {
    var actual: T
    val ms = measureTimeMillis { actual = runner() }
    val good = actual == expected
    val out = if (good) "Pass! 🎉" else "Fail! ❌"
    println("Executed test ${testCount++} in $ms ms | $actual == $expected | $out")
    if (! good) throw AssertionError()
}

var solutionCount = 1
fun <T> solution(runner: () -> T) {
    var result: T
    val ms = measureTimeMillis { result = runner() }
    println("Solution ${solutionCount++} in $ms ms | $result")
}

fun debug(s: Any) {
    if (debug) println(s)
}
