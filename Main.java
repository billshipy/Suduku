import javax.swing.*;

public class Main {

    private static String boardFileName;

    public static void main(String[] args){
        boardFileName = args[0];
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                //Main mainRunMVC = new Main();
                SudokuBoard mySudokuBoard = new SudokuBoard(boardFileName);
                SudokuGUI   mySudokuGUI   = new SudokuGUI(mySudokuBoard, boardFileName);
            }
        });
    } // main()
}