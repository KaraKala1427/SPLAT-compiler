package splat.parser.elements;

import splat.lexer.Token;

public class ReturnStmt extends Statement{

    public Expression expr;

    public ReturnStmt(Token tok){
        super(tok);
    }

    public ReturnStmt(Token tok, Expression expr){
        super(tok);
        this.expr = expr;
    }

    public Expression getExpr(){
        return expr;
    }

    public String toString(){
        if (expr == null){
            return "return" + " ;";
        }
        return "return" + " " + expr + " ;";
    }
}
