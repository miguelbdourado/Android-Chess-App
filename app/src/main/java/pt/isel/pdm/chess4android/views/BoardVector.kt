package pt.isel.pdm.chess4android.views

data class BoardVector (val x: Int, val y: Int) {
    operator fun plus(increment: BoardVector): BoardVector {
        return BoardVector(x + increment.x, y + increment.y)
    }
}

data class BoardPosition (val column: Char, val row: Int) {
    operator fun plus(increment: BoardVector): BoardPosition {
        val newColumn = column + increment.x
        val newRow = row + increment.y

        //Clamp
        return BoardPosition(newColumn, newRow)
    }

    fun isEqual(other: BoardPosition) = column == other.column && row == other.row

    override fun toString(): String {
        return column.toString() + row
    }

}