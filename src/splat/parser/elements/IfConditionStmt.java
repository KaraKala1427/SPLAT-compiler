package splat.parser.elements;

import splat.lexer.Token;

import java.util.List;

public class IfConditionStmt extends Statement{

    public Expression expr;
    public List<Statement> stmts1;
    public List<Statement> stmts2;

    public IfConditionStmt(Token tok, Expression expr, List<Statement> stmts1){
        super(tok);
        this.stmts1 = stmts1;
        this.expr = expr;
    }

    public IfConditionStmt(Token tok, Expression expr, List<Statement> stmts1, List<Statement> stmts2){
        super(tok);
        this.stmts1 = stmts1;
        this.stmts2 = stmts2;
        this.expr = expr;
    }

    public Expression getExpr(){
        return expr;
    }

    public List<Statement> getStmts1(){
        return stmts1;
    }
    public List<Statement> getStmts2(){
        return stmts2;
    }

    public String toString(){
        String  result = "if " + expr + "\n   then ";
        for (Statement stmt : stmts1){
            result = result + stmt + " ";
        }
        result = result + "\n   ";
        if (stmts2 != null){
            result = result + "else ";
            for (Statement stmt : stmts2){
                result = result + stmt + " ";
            }
            result = result + "\n   ";
        }
        return result + "end if ;";
    }
}
