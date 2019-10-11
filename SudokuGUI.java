/**
 *  GUI class for Sudoku play
 *  In MVC/MVP designs this combines the view and controller.
 */

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.event.*;
import java.util.*;		//for update();

public class SudokuGUI implements Observer {

    private JButton solveButton;
    private JButton exitButton;
    private JTextField[][] boardFields;  // visual display of board
    private SudokuBoard model;
    private static Font textFieldFont = new Font("Courier", Font.BOLD, 14);

    // constructor - create GUI layout
    public SudokuGUI(SudokuBoard model, String filename) {

        this.model = model;
        model.addObserver(this); // add the model
        JFrame frame = new javax.swing.JFrame("Sudoku - " + filename);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        boardFields = new JTextField[SudokuBoard.BOARD_SIZE][SudokuBoard.BOARD_SIZE];
        frame.add(createBoard(boardFields), BorderLayout.WEST);
        frame.add(createMenuPanel());
        frame.setSize(700, 800);
        frame.setLocation(100, 100);
        frame.pack();
        frame.setVisible(true);
    }

    // called indirectly to update the GUI when board changes
    public void update(Observable obs, Object obj) {
        Cell arg = (Cell) obj;
        int row = arg.getRow();
        int col = arg.getCol();
        boardFields[row][col].setText("" + arg.getvalue());
        boardFields[row][col].repaint();
        boardFields[row][col].validate();
    }

    // set up menu panel
    protected JPanel createMenuPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 5, 10));
        panel.setBorder(new CompoundBorder(new LineBorder(Color.DARK_GRAY, 2), new EmptyBorder(2, 2, 2, 2)));
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        solveButton = new JButton("Solve");
        panel.add(solveButton, gbc);
        solveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                model.solvePuzzle();
            }
        });

        gbc.gridy = 2;
        exitButton = new JButton("Exit");
        panel.add(exitButton, gbc);
        exitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        return panel;
    }

    // set up board part of GUI
    protected JPanel createBoard(JTextField boardFields[][]) {
        JPanel panel = new JPanel(new GridLayout(3, 3, 2, 2));
        panel.setBorder(new CompoundBorder(new LineBorder(Color.DARK_GRAY, 2), new EmptyBorder(2, 2, 2, 2)));

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                int rowIndex = row * 3;
                int colIndex = col * 3;
                panel.add(createSubBoard(boardFields, rowIndex, colIndex));
            }
        }
        return panel;
    }

    // create 3 x 3 subBoard view
    protected JPanel createSubBoard(JTextField[][] boardFields, int startRow, int startCol) {
        JPanel panel = new JPanel(new GridLayout(3, 3, 2, 2));
        panel.setBorder(new CompoundBorder(new LineBorder(Color.GRAY, 2), new EmptyBorder(2, 2, 2, 2)));

        populatecells(boardFields, startRow, startCol);
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                panel.add(boardFields[row + startRow][col + startCol]);
            }
        }
        return panel;
    }

    // create JTextFields as the cells in the board
    protected void populatecells(JTextField[][] cells, int startRow, int startCol) {
        for (int row = startRow; row < startRow + 3; row++) {
            for (int col = startCol; col < startCol + 3; col++) {
                cells[row][col] = new JTextField(3);
                cells[row][col].setHorizontalAlignment(JTextField.CENTER);
                cells[row][col].setFont(textFieldFont);
                // empty cells get different treatment
                if (model.getCellValue(row, col) == 0) {
                    cells[row][col].addActionListener(new InputListener());
                    cells[row][col].setEditable(true);
                } else {
                    cells[row][col].setEditable(false);
                    cells[row][col].setText("" + model.getCellValue(row, col));
                }
            }
        }
    }

    // Inner class used as ActionEvent listener for ALL cells
    private class InputListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            // All the 9*9 JTextFileds invoke this handler. We need to determine
            // which JTextField (which row and column) is the source for this invocation.
            int rowSelected = -1;
            int colSelected = -1;

            // Get the source object that fired the event
            JTextField source = (JTextField) e.getSource();
            // Scan JTextFileds for all rows and columns, and match with the source object
            boolean found = false;
            for (int row = 0; row < SudokuBoard.BOARD_SIZE && !found; ++row) {
                for (int col = 0; col < SudokuBoard.BOARD_SIZE && !found; ++col) {
                    if (boardFields[row][col] == source) {
                        rowSelected = row;
                        colSelected = col;
                        found = true;  // break the inner & outer loops
                    }
                }
            }

             /*
              * 1. Get the input String from boardFields[rowSelected][colSelected]
              * 2. Convert the String to a byte
              * 3. Check to see if the entry violates rules.  If not,
              *    set the background to green (Color.GREEN); otherwise, set to red (Color.RED).
              */
            String cellValue = boardFields[rowSelected][colSelected].getText().trim();
            char cellChar = cellValue.charAt(0);
            model.setCellValue(rowSelected, colSelected, Byte.parseByte(cellValue));
            if (model.columnOK(colSelected) &&
                    model.rowOK(rowSelected) &&
                    model.subArrayOK(rowSelected, colSelected))
                boardFields[rowSelected][colSelected].setBackground(Color.GREEN);
            else
                boardFields[rowSelected][colSelected].setBackground(Color.RED);

             /*
              * Check to see if the human has solved the puzzle.
              */
            if (model.boardComplete()) {
                JOptionPane.showMessageDialog(null,
                        "Congratulations! You finished the puzzle. Nice work.");
            }
        }
    }
}