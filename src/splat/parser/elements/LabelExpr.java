package splat.parser.elements;

import splat.lexer.Token;

public class LabelExpr extends Expression{

    public String value;

    public LabelExpr(Token tok, String value){
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
