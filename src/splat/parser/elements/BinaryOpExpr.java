package splat.parser.elements;

import splat.lexer.Token;

public class BinaryOpExpr extends Expression{

    public String operator;

    public Expression expr1;
    public Expression expr2;

    public BinaryOpExpr(Token tok, String operator, Expression expr1, Expression expr2){
        super(tok);
        this.operator = operator;
        this.expr1 = expr1;
        this.expr2 = expr2;
    }

    public String getOperator(){
        return operator;
    }

    public Expression getExpr1(){
        return expr1;
    }
    public Expression getExpr2(){
        return expr2;
    }

    public String toString(){
        return "(" + expr1 + operator + expr2 + ")";
    }
}
