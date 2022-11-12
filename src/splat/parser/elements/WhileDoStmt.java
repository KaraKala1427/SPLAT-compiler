package splat.parser.elements;

import splat.lexer.Token;

import java.util.List;

public class WhileDoStmt extends Statement{

    public Expression expr;

    public List<Statement> stmts;

    public WhileDoStmt(Token tok, Expression expr, List<Statement> stmts){
        super(tok);
        this.expr = expr;
        this.stmts = stmts;
    }

    public Expression getExpr(){
        return expr;
    }

    public List<Statement> getStmts(){
        return stmts;
    }

    public String toString(){
        String result = "while " + expr + " \n   do ";
        for (Statement stmt : stmts){
            result = result + stmt + " ";
        }
        result = result + "\n   end while ;";
        return  result;
    }
}
