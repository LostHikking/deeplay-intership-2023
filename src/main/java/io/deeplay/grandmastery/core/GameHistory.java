package io.deeplay.grandmastery.core;

import io.deeplay.grandmastery.domain.GameErrorCode;

import java.util.ArrayList;
import java.util.List;

/***
 * Класс для сохранения истории партии
 * */
public class GameHistory implements GameListener {
    private final List<Move> moves = new ArrayList<>();
    private Board board;

    /***
     * Метод устанавливает начальное состояние доски
     @param board Какая-то реализация доски
     */
    @Override
    public void setBoard(Board board) {
        if (this.board == null)
            this.board = board; // TODO: Сделать констуктор копирования в Board
    }

    /***
     * Метод записывает ход в историю
     @param move Ход
     */
    @Override
    public void makeMove(Move move) {
        moves.add(move);
    }

    /***
     * Метод возвращает пуста ли история в данный момент
     @return Пуста ли история
     */
    public boolean isEmpty() {
        return moves.isEmpty();
    }

    /***
     * Метод возвращает последний сделанный в партии ход
     @return последний ход
     */
    public Move getLastMove() {
        if (this.isEmpty())
            throw GameErrorCode.MOVE_NOT_FOUND.asException();

        return moves.get(moves.size() - 1);
    }

    /***
     * Метод возвращает все ходы в партии
     @return Все ходы партии
     */
    public List<Move> getMoves() {
        return moves;
    }

    /***
     * Метод возвращает доску
     @return Доску
     */
    public Board getBoard() {
        return board; // TODO: Возвращать не ссылку а новый объект, созданный через констуктор копирования
    }
}
