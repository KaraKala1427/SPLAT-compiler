package splat.parser.elements;

import splat.lexer.Token;

public class PrintStmt extends Statement{

    public Expression expr;

    public PrintStmt(Token tok, Expression expr){
        super(tok);
        this.expr = expr;
    }

    public Expression getExpr(){
        return expr;
    }
    public String toString(){
        return "print " + expr + " ;";
    }
}
