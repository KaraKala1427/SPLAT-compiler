package splat.lexer;

public class Token {
    private int line;
    private int column;
    private String value;

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    public String getValue() {
        return value;
    }

    public Token(String value, int line, int column){
        this.value = value;
        this.line = line;
        this.column = column;
    }

    public String toString() {
        return  "Token: " + value + ", at " + line + ":" + column;
    }
}
