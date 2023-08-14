package io.deeplay.grandmastery;

import io.deeplay.grandmastery.domain.ChessType;
import io.deeplay.grandmastery.domain.Color;
import io.deeplay.grandmastery.domain.GameMode;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.net.Socket;

public record ServerPlayer(
    Socket socket,
    BufferedReader in,
    BufferedWriter out,
    String name,
    Color color,
    GameMode gameMode,
    ChessType chessType) {}
