package splat.parser.elements;

import splat.lexer.Token;

public class LiteralExpr extends Expression{

    public String value;

    public LiteralExpr(Token tok, String value){
        super(tok);
        this.value = value;

    }

    public String getValue(){
        return value;
    }

    public String toString(){
        return value;
    }
}
