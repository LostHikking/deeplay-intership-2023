package io.deeplay.grandmastery.utils;

import io.deeplay.grandmastery.core.Board;
import io.deeplay.grandmastery.core.HashBoard;
import io.deeplay.grandmastery.core.Move;
import io.deeplay.grandmastery.core.Position;
import io.deeplay.grandmastery.domain.Color;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

/** Класс-утилита для ботов. */
@Slf4j
public class BotUtils {
  /**
   * Функция возвращает список доступных ходов для определённого цвета на доске.
   *
   * @param board Доска
   * @param color Цвет
   * @return Список ходов
   */
  public static List<Move> getPossibleMoves(Board board, Color color) {
    var possibleMove = new ArrayList<Move>();
    var positions = board.getAllPiecePositionByColor(color);

    for (Position position : positions) {
      possibleMove.addAll(board.getPiece(position).getAllMoves(board, position));
    }

    return possibleMove;
  }

  /**
   * Функция возвращает новую доску, после хода, не меняя передаваемую доску.
   *
   * @param board Доска
   * @param move Ход
   * @return Новая доска
   */
  public static Board getCopyBoardAfterMove(Move move, Board board) {
    var tempBoard = new HashBoard();
    Boards.copy(board).accept(tempBoard);
    var piece = tempBoard.getPiece(move.from());
    piece.move(tempBoard, move);

    return tempBoard;
  }

  /**
   * Метод отправляет данные в BufferedWriter.
   *
   * @param out BufferedWriter
   * @param json Данные
   * @throws IOException Ошибка при отправке данных
   */
  public static void send(BufferedWriter out, String json) throws IOException {
    log.info("Отправили данные клиенту - " + json);
    out.write(json);
    out.newLine();
    out.flush();
  }
}
