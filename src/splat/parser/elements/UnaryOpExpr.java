package splat.parser.elements;

import splat.lexer.Token;

public class UnaryOpExpr extends Expression{

    public String operator;

    public Expression expr;

    public UnaryOpExpr(Token tok, String operator, Expression expr){
        super(tok);
        this.operator = operator;
        this.expr = expr;
    }

    public String getOperator(){
        return operator;
    }

    public Expression getExpr(){
        return expr;
    }

    public String toString(){
        return "(" + operator + expr + ")";
    }
}
