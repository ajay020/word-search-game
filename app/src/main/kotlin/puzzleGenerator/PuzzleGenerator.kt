import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import kotlin.random.Random

@Serializable
data class WordSearchPuzzle(
    val id: String,
    val grid: List<String>,
    val wordsToFind: List<String>,
)

@Serializable
data class PuzzleCollection(
    val puzzles: MutableList<WordSearchPuzzle> = mutableListOf(),
)

class WordSearchGenerator(
    private val size: Int,
) {
    private val grid = Array(size) { CharArray(size) { '.' } }
    private val placedWords = mutableListOf<String>()

    fun generatePuzzle(words: List<String>): WordSearchPuzzle {
        println("Generating puzzle with words: $words")
        words.forEach { word ->
            val upperWord = word.uppercase()
            var placed = false
            var attempts = 0
            while (!placed && attempts < 100) {
                val row = Random.nextInt(size)
                val col = Random.nextInt(size)
                val dRow = Random.nextInt(-1, 2)
                val dCol = Random.nextInt(-1, 2)
                if (dRow == 0 && dCol == 0) continue
                if (canPlaceWord(upperWord, row, col, dRow, dCol)) {
                    placeWord(upperWord, row, col, dRow, dCol)
                    placed = true
                    placedWords.add(upperWord)
                    println("Placed word: $upperWord")
                }
                attempts++
            }
            if (!placed) {
                println("Failed to place word: $upperWord")
            }
        }

        fillRemainingSpaces()
        printGrid()

        return WordSearchPuzzle(
            id = generateId(),
            grid = grid.map { it.joinToString("") },
            wordsToFind = placedWords,
        )
    }

    private fun generateId(): String = System.currentTimeMillis().toString()

    private fun canPlaceWord(
        word: String,
        row: Int,
        col: Int,
        dRow: Int,
        dCol: Int,
    ): Boolean {
        var currentRow = row
        var currentCol = col
        word.forEach { letter ->
            if (currentRow !in 0 until size || currentCol !in 0 until size) return false
            if (grid[currentRow][currentCol] != '.' && grid[currentRow][currentCol] != letter) return false
            currentRow += dRow
            currentCol += dCol
        }
        return true
    }

    private fun placeWord(
        word: String,
        row: Int,
        col: Int,
        dRow: Int,
        dCol: Int,
    ) {
        var currentRow = row
        var currentCol = col
        word.forEach { letter ->
            grid[currentRow][currentCol] = letter
            currentRow += dRow
            currentCol += dCol
        }
    }

    private fun fillRemainingSpaces() {
        for (i in 0 until size) {
            for (j in 0 until size) {
                if (grid[i][j] == '.') {
                    grid[i][j] = ('A'..'Z').random()
                }
            }
        }
    }

    private fun printGrid() {
        println("Current grid state:")
        grid.forEach { row ->
            println(row.joinToString(" "))
        }
    }
}

object PuzzleManager {
    private const val FILE_NAME = "puzzles.json"
    private val json = Json { prettyPrint = true }

    fun addPuzzle(puzzle: WordSearchPuzzle) {
        val collection = loadPuzzles()
        collection.puzzles.add(puzzle)
        savePuzzles(collection)
    }

    fun getPuzzle(id: String): WordSearchPuzzle? {
        val collection = loadPuzzles()
        return collection.puzzles.find { it.id == id }
    }

    fun getAllPuzzles(): List<WordSearchPuzzle> = loadPuzzles().puzzles

    private fun loadPuzzles(): PuzzleCollection {
        val file = File(FILE_NAME)
        return if (file.exists()) {
            json.decodeFromString(file.readText())
        } else {
            PuzzleCollection()
        }
    }

    private fun savePuzzles(collection: PuzzleCollection) {
        File(FILE_NAME).writeText(json.encodeToString(collection))
    }
}

fun main() {
    println("Enter words for the puzzle (comma-separated):")
    val input = readLine() ?: ""
    val words = input.split(",").map { it.trim() }

    println("Enter the size of the puzzle grid:")
    val size = readLine()?.toIntOrNull() ?: 8

    val generator = WordSearchGenerator(size)
    val puzzle = generator.generatePuzzle(words)

    PuzzleManager.addPuzzle(puzzle)

    println("Puzzle generated and added to puzzles.json")
    println("Puzzle ID: ${puzzle.id}")
    println("Words placed in the puzzle: ${puzzle.wordsToFind.joinToString(", ")}")
}
