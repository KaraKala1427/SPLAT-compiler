package splat.parser.elements;

import splat.lexer.Token;

import java.util.List;

public class ArgsExpr extends Expression{

    public List<Expression> args;

    public ArgsExpr(Token tok, List<Expression> args){
        super(tok);
        this.args = args;
    }

    public List<Expression> getArgs(){
        return args;
    }

    public String toString(){
        String result = "";
        Boolean first = true;
        for (Expression expr : args){
            if (first) {
                result =  result + expr;
                first = false;
            }
            result = result  + ", " + expr;
        }

        return result;
    }
}
