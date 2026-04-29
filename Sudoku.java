package sudoku;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class Sudoku implements ActionListener {

    JFrame window;
    JTextField[][] cells = new JTextField[9][9];
    JButton easyBtn, mediumBtn, hardBtn, checkBtn, solveBtn;

    int[][] board = new int[9][9];
    int[][] solution = new int[9][9];

    public Sudoku() {
        window = new JFrame("Sudoku Game");
        window.setSize(500, 600);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setLayout(new BorderLayout());

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridLayout(9, 9, 2, 2));
        centerPanel.setBackground(Color.BLACK);

        Font myFont = new Font("Arial", Font.BOLD, 22);

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                cells[i][j] = new JTextField();
                cells[i][j].setFont(myFont);
                cells[i][j].setHorizontalAlignment(JTextField.CENTER);

                cells[i][j].addKeyListener(new KeyAdapter() {
                    public void keyTyped(KeyEvent e) {
                        char input = e.getKeyChar();
                        JTextField source = (JTextField) e.getSource();

                        if (input < '1' || input > '9' || source.getText().length() >= 1) {
                            e.consume();
                        }
                    }
                });

                centerPanel.add(cells[i][j]);
            }
        }

        JPanel bottomPanel = new JPanel();
        easyBtn = new JButton("Easy");
        mediumBtn = new JButton("Medium");
        hardBtn = new JButton("Hard");
        checkBtn = new JButton("Check");
        solveBtn = new JButton("Solve All");

        easyBtn.addActionListener(this);
        mediumBtn.addActionListener(this);
        hardBtn.addActionListener(this);
        checkBtn.addActionListener(this);
        solveBtn.addActionListener(this);

        bottomPanel.add(easyBtn);
        bottomPanel.add(mediumBtn);
        bottomPanel.add(hardBtn);
        bottomPanel.add(checkBtn);
        bottomPanel.add(solveBtn);

        window.add(centerPanel, BorderLayout.CENTER);
        window.add(bottomPanel, BorderLayout.SOUTH);

        generateNewPuzzle(30);

        window.setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == easyBtn) {
            generateNewPuzzle(30);
        } else if (e.getSource() == mediumBtn) {
            generateNewPuzzle(45);
        } else if (e.getSource() == hardBtn) {
            generateNewPuzzle(60);
        } else if (e.getSource() == checkBtn) {
            validateUserAnswers();
        } else if (e.getSource() == solveBtn) {
            showEntireSolution();
        }
    }

    public void validateUserAnswers() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (cells[i][j].isEditable()) {
                    String text = cells[i][j].getText().trim();

                    if (text.isEmpty()) {
                        cells[i][j].setBackground(Color.RED);
                    } else {
                        try {
                            int number = Integer.parseInt(text);
                            if (number != solution[i][j]) {
                                cells[i][j].setBackground(Color.RED);
                            } else {
                                cells[i][j].setBackground(Color.WHITE);
                            }
                        } catch (NumberFormatException ex) {
                            cells[i][j].setBackground(Color.RED);
                        }
                    }
                }
            }
        }
    }

    public void showEntireSolution() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                cells[i][j].setText(solution[i][j] + "");
                if (cells[i][j].isEditable()) {
                    cells[i][j].setBackground(Color.WHITE);
                }
            }
        }
    }

    public void generateNewPuzzle(int holes) {
        Random rand = new Random();

        boolean finished = false;
        while (!finished) {
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    board[i][j] = 0;
                }
            }

            for (int i = 0; i < 11; i++) {
                int r = rand.nextInt(9);
                int c = rand.nextInt(9);
                int num = rand.nextInt(9) + 1;
                if (isMoveSafe(r, c, num)) {
                    board[r][c] = num;
                }
            }

            if (solveSudoku()) {
                finished = true;
            }
        }

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                solution[i][j] = board[i][j];
            }
        }

        int count = 0;
        while (count < holes) {
            int r = rand.nextInt(9);
            int c = rand.nextInt(9);
            if (board[r][c] != 0) {
                board[r][c] = 0;
                count = count + 1;
            }
        }

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (board[i][j] != 0) {
                    cells[i][j].setText(board[i][j] + "");
                    cells[i][j].setEditable(false);
                    cells[i][j].setBackground(Color.LIGHT_GRAY);
                } else {
                    cells[i][j].setText("");
                    cells[i][j].setEditable(true);
                    cells[i][j].setBackground(Color.WHITE);
                }
            }
        }
    }

    public boolean isMoveSafe(int row, int col, int num) {
        for (int i = 0; i < 9; i++) {
            if (board[row][i] == num) {
                return false;
            }
            if (board[i][col] == num) {
                return false;
            }
        }

        int startRow = row - row % 3;
        int startCol = col - col % 3;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[startRow + i][startCol + j] == num) {
                    return false;
                }
            }
        }

        return true;
    }

    public boolean solveSudoku() {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (board[row][col] == 0) {
                    for (int num = 1; num <= 9; num++) {
                        if (isMoveSafe(row, col, num)) {
                            board[row][col] = num;
                            if (solveSudoku()) {
                                return true;
                            }
                            board[row][col] = 0;
                        }
                    }
                    return false;
                }
            }
        }
        return true;
    }

    public static void main(String[] args) {
        new Sudoku();
    }
}