package pt.isel.pdm.chess4android.views

import android.content.Context
import pt.isel.pdm.chess4android.pieces.APiece
import pt.isel.pdm.chess4android.pieces.King
import java.lang.StringBuilder

class ChessBoard(
    private val ctx: Context,
    private val boardView: BoardView
) {
    private val SIDE = 8
    private var canMove = false
    var whiteTurn = true
    private val board = Array(SIDE) {Array(SIDE) {Tile(boardView, ctx, null, null, BoardPosition('a', 1), SIDE)} }
    private lateinit var moves: ArrayList<String>
    private var playedMoves: ArrayList<String> = ArrayList()
    var whiteKingTile: Tile? = null
    var blackKingTile: Tile? = null
    private var solution: MutableList<String>? = null
    private lateinit var gameOverCallback: () -> Unit
    private var cpuMove = false
    private var playerMove = false
    private var gameOver = false
    private var showSolution = false
    private var playingLocal = false

    private var attackingWhitePiece: Tile? = null
    private var attackingBlackPiece: Tile? = null

    //TODO() on tryMovePiece create a PGN version of the move and store it in moves

    //Logic to move pieces by the player
    private var currentlySelectedPosition: BoardPosition? = null
    fun getCurrentlySelectedPosition() = currentlySelectedPosition

    fun startGame() {
        initializeChessBoard()
        boardView.initializeBoard()
        playingLocal = true
        canMove = true
        playerMove = true
    }

    private fun changeTurn() {
        if(cpuMove && solution?.isNotEmpty() == true) {
            val solutionMove = solution!![0]
            val from = solutionMove.subSequence(0, 2)
            val to = solutionMove.subSequence(2, 4)

            val fromTile = getTile(BoardPosition(from[0], Character.getNumericValue(from[1])))
            val toTile = getTile(BoardPosition(to[0], Character.getNumericValue(to[1])))
            if(fromTile != null && toTile != null) {
                playedMoves.add(solutionMove)
                movePiece(fromTile, toTile)
            }

            solution?.removeAt(0)
            cpuMove = false
            changeTurn()
        }

        whiteTurn = !whiteTurn
        unselectTile()
    }

    private fun checkOutsideBoard(boardPosition: BoardPosition): Boolean {
        return (boardPosition.row < 1 || boardPosition.row > SIDE || boardPosition.column - 'a' < 0 || boardPosition.column - 'a' > SIDE - 1)
    }

    fun placeTile(tile: Tile, boardPosition: BoardPosition): Boolean {
        if (checkOutsideBoard(boardPosition)) return false
        board[boardPosition.column - 'a'][boardPosition.row - 1] = tile
        return true
    }

    fun placePiece(piece: APiece, boardPosition: BoardPosition): Boolean {
        getTile(boardPosition)?.setPiece(piece)
        return true
    }

    private fun getTile(row: Int, column: Int): Tile {
        return board[column][row - 1]
    }

    fun getTile(boardPosition: BoardPosition): Tile? {
        if (checkOutsideBoard(boardPosition)) return null
        return getTile(boardPosition.row, boardPosition.column - 'a')
    }

    private var previouslyChangedTiles: Pair<Tile, Tile>? = null
    private fun movePiece(tile: Tile, destinationTile: Tile) {
        val piece: APiece? = tile.getPiece()
        tile.removePiece()
        destinationTile.setPiece(piece!!)

        if(piece.type == Tile.Pieces.K) {
            if(piece.isWhite) whiteKingTile = destinationTile
            else blackKingTile = destinationTile
        }

        if(previouslyChangedTiles != null) {
            previouslyChangedTiles!!.first.setTileChangedHighlight(false)
            previouslyChangedTiles!!.second.setTileChangedHighlight(false)
            previouslyChangedTiles!!.first.invalidate()
            previouslyChangedTiles!!.second.invalidate()
        }

        previouslyChangedTiles = Pair(tile, destinationTile)

        tile.setTileChangedHighlight(true)
        tile.invalidate()
        destinationTile.setTileChangedHighlight(true)
        destinationTile.invalidate()

        if(solution?.isEmpty() == true) {
            gameOver = true
            gameOverCallback()
        }
    }

    private fun processMove(move: String): Boolean {
        //Castle along king side
        if(move == "O-O") {
            //Check if king is on the initial tile
            val row = if(whiteTurn) 1 else 8
            val supposedKingTile = getTile(BoardPosition('e', row))
            if(supposedKingTile?.getPiece()?.type == Tile.Pieces.K && supposedKingTile.getPiece()?.isWhite == whiteTurn) {
                val king: King = supposedKingTile.getPiece() as King

                //If this king can castle, perform caste move
                val destinationTile = getTile(BoardPosition('g', row))
                if(destinationTile != null && king.canKingSideCastle(this, supposedKingTile)) {
                    movePiece(supposedKingTile, destinationTile)
                    val rookTile = getTile(BoardPosition('h', row))
                    val rookDestinationTile = getTile(BoardPosition('f', row))
                    if (rookTile != null && rookDestinationTile != null) {
                        movePiece(rookTile, rookDestinationTile)
                        changeTurn()
                    }
                }
            }

            return true
        }

        //Castle along queen side
        if(move == "O-O-O") {
            return true
        }

        //Create
        val namedPiece: Boolean
        val capture: Boolean
        val pawnPromotion: Boolean
        val check: Boolean

        val moveSB = StringBuilder(move)
        var charPosition = moveSB.length - 1

        //If it's a check move
        check = moveSB[charPosition].compareTo('+') == 0
        if(check) moveSB.deleteAt(charPosition--)

        //If it's a pawn promotion
        pawnPromotion = !moveSB[charPosition].isDigit() && !moveSB[charPosition].isLowerCase()
        if(pawnPromotion) moveSB.deleteAt(charPosition--)

        val desiredPosition = BoardPosition(moveSB[charPosition - 1], Character.getNumericValue(moveSB[charPosition]))
        moveSB.delete(charPosition - 1, charPosition + 1)
        charPosition -= 2

        if(charPosition > 0) {
            capture = moveSB[charPosition].compareTo('x') == 0
            if(capture) moveSB.deleteAt(charPosition--)
        }

        var pieceTypeChar = 'P'
        if(charPosition >= 0) {
            namedPiece = !moveSB[0].isLowerCase()
            if(namedPiece) {
                pieceTypeChar = moveSB[0]
                moveSB.deleteAt(0)
            }
        }

        var desiredColumn: Char? = null
        var desiredRow: Int? = null
        if(charPosition > 0) {
            when(moveSB.length) {
                2 -> {
                    desiredColumn = moveSB[0]
                    desiredRow = Character.getNumericValue(moveSB[1])
                }
                1 -> {
                    if(moveSB[0].isDigit()) desiredRow = Character.getNumericValue(moveSB[0])
                    else desiredColumn = moveSB[0]
                }
            }
        }


        //TODO optimize this to loop only through the tiles that have pieces
        outerLoop@  for (row in 8 downTo 1) {
            for (column in 'a'..'h') {
                if(desiredColumn != null && column != desiredColumn) continue
                if(desiredRow != null && row != desiredRow) break

                val tile = getTile(BoardPosition(column, row)) ?: continue

                //check if piece is of the same type
                if(tile.getPiece()?.type?.name?.get(0) != pieceTypeChar) continue

                //If was a pawn, check if it's the pawn in the right column (as indicated on the first character of the move)
                if(pieceTypeChar == 'P' && tile.getPosition().column != move[0]) continue

                //check if the color of the piece corresponds with the current turn
                if(tile.getPiece()?.isWhite != whiteTurn) continue

                val possibleMoves = tile.getPiece()?.getPossibleMoves(this, tile) ?: continue

                for(possibleMove in possibleMoves) {
                    val possiblePosition = tile.getPosition() + possibleMove

                    if(possiblePosition.isEqual(desiredPosition)) {
                        val targetTile = getTile(desiredPosition)
                        if (targetTile != null) {

                            //Check for possibles
                            if(!canMovePiece(tile, targetTile)) continue

                            movePiece(tile, targetTile)
                            changeTurn()
                            break@outerLoop
                        }
                    }
                }
            }
        }

        return true
    }

    private fun canMovePiece(tile: Tile, targetTile: Tile): Boolean {
        val lastPiece = targetTile.getPiece()
        movePiece(tile, targetTile)

        //check for checkamates
        outerLoop@  for (row in 8 downTo 1) {
            for (column in 'a'..'h') {

                val currTile = getTile(BoardPosition(column, row)) ?: continue
                val possibleMoves = currTile.getPiece()?.getPossibleMoves(this, currTile) ?: continue

                for(possibleMove in possibleMoves) {
                    val currentKingTile = if(whiteTurn) whiteKingTile else blackKingTile
                    if(getTile(currTile.getPosition() + possibleMove) == currentKingTile) {
                        movePiece(targetTile, tile) //move piece back
                        if (lastPiece != null) {
                            targetTile.setPiece(lastPiece)
                        }
                        return false
                    }
                }
            }
        }

        //move piece back
        movePiece(targetTile, tile)
        return true
    }

    private fun uploadPgn(pgn: String) {
        initializeChessBoard()
        boardView.initializeBoard()
        moves = ArrayList(pgn.split(" "))
        for(move in moves) {
            processMove(move)
        }
        playerMove = true
    }

    private fun initializeChessBoard() {
        whiteTurn = true
        unselectTile()
    }

    fun click(clickedTile: Tile) {
        if(gameOver || !canMove) return

        //If no piece/tile currently selected
        if(currentlySelectedPosition == null)
            trySelectTile(clickedTile)
        else {
            //If we clicked a piece that was already selected, unselect it
            if(clickedTile.getPosition().isEqual(currentlySelectedPosition!!)) {
                unselectTile()
                return
            }

            //Tile and piece that are already selected
            val currentlySelectedTile = getTile(currentlySelectedPosition!!)
            val currentlySelectedPiece = currentlySelectedTile?.getPiece()

            //Newly clicked piece
            val clickedPiece = clickedTile.getPiece()

            //If the clicked tile contains a piece
            when {
                clickedPiece != null -> {
                    //If they are of the same color swap currently selected tile
                    when {
                        clickedPiece.isWhite == currentlySelectedPiece!!.isWhite //Currently selected piece can never be null due to logic implemented
                        -> selectTile(clickedTile) //Try to take the piece
                        tryMovePiece(clickedTile) -> {
                            //If it succeeded, unselect piece
                            changeTurn()
                        }
                        else -> {
                            unselectTile()
                        }
                    }

                } //If the clicked tile does not contain a piece
                //Move the currently selected piece to the now clicked tile
                tryMovePiece(clickedTile) -> {
                    changeTurn()
                }
                else -> {
                    unselectTile()
                }
            }
        }
    }

    private fun isCheckMate(): Boolean {
        val opponentColor = !whiteTurn
        val currentKingTile = if(opponentColor) whiteKingTile else blackKingTile

        val attackingPiece = if (opponentColor) attackingBlackPiece else attackingWhitePiece

        var noLongerInCheck = true

        //check for checkamates
        outerLoop@  for (row in 8 downTo 1) {
            for (column in 'a'..'h') {
                val currTile = getTile(BoardPosition(column, row)) ?: continue
                val possibleMoves = currTile.getPiece()?.getPossibleMoves(this, currTile) ?: continue

                //If it doesnt have a piece we are interested in continue
                if(currTile.getPiece() == null || currTile.getPiece()!!.isWhite != opponentColor) continue

                // Check if this move cancels out the attacking pieces
                for(possibleMove in possibleMoves) {

                    val destinationTile = getTile(currTile.getPosition() + possibleMove) ?: continue
                    val _savedPiece = destinationTile.getPiece()

                    //If the destination move is an attacking piece, we can attack that piece. That means we are not in check-mate just yet
                    // but we need to make recursive checks
                    if(attackingPiece == destinationTile) {
                        //TODO:
                        return true
                    }

                    //Make a pseudo-move so we can check how it impacts the game
                    movePiece(currTile, destinationTile)

                    val attackingPiecePossibleMoves = attackingPiece?.getPiece()?.getPossibleMoves(this, attackingPiece)
                    if (attackingPiecePossibleMoves != null) {
                        for( attackingPossibleMove in attackingPiecePossibleMoves) {
                            val attackingDestinationTile = getTile(currTile.getPosition() + possibleMove) ?: continue

                            // If the attacker is still attacking the king
                            if(attackingDestinationTile == currentKingTile)
                                noLongerInCheck = false
                        }
                    }

                    //Undo pseudo-move
                    movePiece(destinationTile, currTile)
                    if (_savedPiece != null) {
                        destinationTile.setPiece(_savedPiece)
                    }

                }
            }
        }

        //check if there is any piece that can remove the check
        return noLongerInCheck
    }

    private fun tryMovePiece(destinationTile: Tile): Boolean {
        if(!playingLocal)
        {
            val solutionMove = solution?.get(0)
            val from = solutionMove?.subSequence(0, 2)
            val to = solutionMove?.subSequence(2, 4)

            if(from != currentlySelectedPosition.toString()) return false
            if(to != destinationTile.getPosition().toString()) return false

            playedMoves.add(solutionMove)
            solution?.removeAt(0)
        }

        val selectedTile = getTile(currentlySelectedPosition!!)!!
        val selectedPiece = selectedTile.getPiece()!!

        if(!selectedPiece.canMove(this, selectedTile, destinationTile)) return false
        if(!canMovePiece(selectedTile, destinationTile)) return false

        movePiece(selectedTile, destinationTile)

        //Check if this move made a check on the enemy
        val possibleMoves = destinationTile.getPiece()?.getPossibleMoves(this, destinationTile)
        if (possibleMoves != null) {
            val currentKingTile = if(!whiteTurn) whiteKingTile else blackKingTile

            for(possibleMove in possibleMoves) {
                val test = getTile(destinationTile.getPosition() + possibleMove)

                if (test != null && currentKingTile != null) {
                    if(test.getPiece() == currentKingTile.getPiece()) {

                        if(whiteTurn) attackingWhitePiece = destinationTile
                        else attackingBlackPiece = destinationTile

                        // CHECK
                        if(isCheckMate())
                        {
                            boardView.erasePreviouslyDrawnMoves()
                            gameOver = true
                            gameOverCallback()
                        }
                    }
                }
            }
        }

        playerMove = false
        cpuMove = true
        return true
    }

    private fun trySelectTile(tile: Tile) {
        //If the clicked tile has no piece, do nothing
        if(tile.getPiece() == null) return

        //If the clicked tile has a piece of the current turn
        if(tile.getPiece()!!.isWhite != whiteTurn) return

        selectTile(tile)
    }

    private fun selectTile(tile: Tile) {
        val previouslySelectedTile = currentlySelectedPosition?.let { getTile(it) }

        currentlySelectedPosition = tile.getPosition()

        boardView.drawMoves(tile)
        //Draw both previously selected tile and newly selected tile
        previouslySelectedTile?.invalidate()
        tile.invalidate()
    }

    private fun unselectTile() {
        val previouslySelectedTile = currentlySelectedPosition?.let { getTile(it) }
        currentlySelectedPosition = null
        previouslySelectedTile?.invalidate()
        boardView.erasePreviouslyDrawnMoves()
    }

    fun uploadDailyPuzzle(pgnString: String, solution: Array<String>?, showSolution: Boolean) {
        if(solution != null)
            this.solution = solution.toMutableList()

        uploadPgn(pgnString)

        canMove     = true
        gameOver    = false
        playerMove  = true
        cpuMove     = false

        this.showSolution = showSolution
    }

    fun setGameOverCallback(gameOverCallback: () -> Unit) {
        this.gameOverCallback = gameOverCallback
    }

    fun getGameState(): ChessGame {
        return ChessGame(playedMoves.joinToString(" "), solution?.joinToString(" "), gameOver)
    }

    fun loadPreviousState(previousState: ChessGame?) {
        if(previousState == null) return

        unselectTile()
        if(previousState.moves.isNotEmpty())
            playedMoves = previousState.moves.split(" ") as ArrayList<String>
        if(!previousState.solution.isNullOrEmpty() && !previousState.solution.isNullOrBlank())
            solution = previousState.solution.split(" ").toMutableList()
        for(move in playedMoves) {
            val tile = getTile(BoardPosition(move[0], Character.getNumericValue(move[1])))
            val destTile = getTile(BoardPosition(move[2], Character.getNumericValue(move[3])))

            if(tile == null || destTile == null) continue

            movePiece(tile, destTile)
            changeTurn()
        }
        gameOver = previousState.gameOver
        if(gameOver)
            gameOverCallback()
        else
            playerMove = true
        cpuMove = false
    }

    fun nextSolutionMove() {
        if (!showSolution) return
        if (solution?.isEmpty() == true) return

        val solutionMove = solution?.get(0)
        solution?.removeAt(0)
        if (solutionMove == null) return

        val sourceTile = getTile(BoardPosition(solutionMove[0], Character.getNumericValue(solutionMove[1])))
        val destinationTile = getTile(BoardPosition(solutionMove[2], Character.getNumericValue(solutionMove[3])))

        if (sourceTile == null || destinationTile == null) return

        movePiece(sourceTile, destinationTile)
    }

}