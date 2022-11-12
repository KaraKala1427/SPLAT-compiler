package splat.parser.elements;

import splat.lexer.Token;

public class PrintLineStmt extends Statement{


    public PrintLineStmt(Token tok){
        super(tok);
    }


    public String toString(){
        return "print_line" + " ;";
    }
}
