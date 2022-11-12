package splat.parser.elements;

import splat.lexer.Token;

import java.util.List;

public class FunctionDecl extends Declaration {

	public String label;
	public List<Param> params;
	public ReturnType retType;
	public List<VariableDecl> localVarDecls;
	public List<Statement> stmts;

	// Need to add extra arguments for setting fields in the constructor 
	public FunctionDecl(Token tok,
						String label,
						List<Param> params,
						ReturnType retType,
						List<VariableDecl> localVarDecls,
						List<Statement> stmts) {
		super(tok);
		this.label = label;
		this.retType = retType;
		this.params = params;
		this.localVarDecls = localVarDecls;
		this.stmts = stmts;

	}

	public String getLabel(){
		return label;
	}

	public ReturnType getRetType(){
		return retType;
	}

	public List<Param> getParams(){
		return params;
	}

	public List<VariableDecl> getLocalVarDecls(){
		return localVarDecls;
	}

	public List<Statement> getStmts(){
		return stmts;
	}


	public String toString() {
		Boolean first = true;
		String result = label + " (";
		for (Param param : params){
			if (first) {
				result = result  + param;
				first = false;
			}
			else{
				result = result  + ", " + param;
			}
		}
		result = result  + "): " + retType + " is \n";

		for (VariableDecl localVarDecl : localVarDecls){
			result = "\t" + localVarDecl + "\n";
		}
		result = result + "begin \n";
		for (Statement stmt: stmts){
			result = result + "   " + stmt + "\n";
		}
		result = result + "end;";

		return result;
	}
}
