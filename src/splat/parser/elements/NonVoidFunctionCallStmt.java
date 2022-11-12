package splat.parser.elements;

import splat.lexer.Token;

public class NonVoidFunctionCallStmt extends Statement{

    public Expression label;

    public Expression args;

    public NonVoidFunctionCallStmt(Token tok, Expression label, Expression args){
        super(tok);
        this.label = label;
        this.args = args;
    }

    public Expression getLabel(){
        return label;
    }

    public Expression getArgs(){
        return args;
    }

    public String toString(){
        return label + "(" + args + ");";
    }
}
