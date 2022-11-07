package splat.parser.elements;

import splat.lexer.Token;

public class AssignmentStmt extends Statement{
    public String label;

    public Expression expr;

    public AssignmentStmt(Token tok, String label, Expression expr){
        super(tok);
        this.label = label;
        this.expr = expr;
    }

    public String toString(){
        String result = label;

        result = result + " := " + expr;

        return result;
    }
}
