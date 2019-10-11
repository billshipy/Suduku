/**
 *   Utility class to capture the content of a single Cell in
 *   the board.  Primarily used for working with GUI.
 */

public class Cell {
    private int row;
    private int col;
    private char value;

    // constructor converts board value to a character for GUI
    public Cell(int row, int col, byte value ){
        this.row = row; this.col = col;
        this.value = Byte.toString(value).charAt(0);
    }

    public int getRow(){return row;}
    public int getCol(){return col;}
    public char getvalue(){return value;}
}