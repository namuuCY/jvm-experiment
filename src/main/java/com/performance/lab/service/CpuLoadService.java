package com.performance.lab.service;

import org.springframework.stereotype.Service;

@Service
public class CpuLoadService {

    // N-Queens 문제 해결 (재귀)
    public int solveNQueens(int n) {
        int[] board = new int[n]; // 인덱스는 행(row), 값은 열(col)
        return placeQueen(board, 0, n);
    }

    private int placeQueen(int[] board, int currentRow, int n) {
        if (currentRow == n) {
            return 1; // 해 하나 발견
        }
        int count = 0;

        for (int col = 0; col < n; col++) {
            if (isSafe(board, currentRow, col)) {
                board[currentRow] = col;
                // 재귀 호출 (Stack Depth 증가)
                count += placeQueen(board, currentRow + 1, n);
            }
        }
        return count;
    }

    private boolean isSafe(int[] board, int row, int col) {
        for (int i = 0; i < row; i++) {
            // 같은 열에 있거나, 대각선상에 있는 경우
            if (board[i] == col || Math.abs(row - i) == Math.abs(col - board[i])) {
                return false;
            }
        }
        return true;
    }
}
