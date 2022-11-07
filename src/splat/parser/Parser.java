package splat.parser;

import java.util.ArrayList;
import java.util.List;

import splat.lexer.Token;
import splat.parser.elements.*;

public class Parser {

	private List<Token> tokens;
	
	public Parser(List<Token> tokens) {
		this.tokens = tokens;
	}

	/**
	 * Compares the next token to an expected value, and throws
	 * an exception if they don't match.  This removes the front-most
	 * (next) token  
	 * 
	 * @param expected value of the next token
	 * @throws ParseException if the actual token doesn't match what 
	 * 			was expected
	 */
	private void checkNext(String expected) throws ParseException {

		Token tok = tokens.remove(0);
		
		if (!tok.getValue().equals(expected)) {
			throw new ParseException("Expected '"+ expected + "', got '" 
					+ tok.getValue()+ "'.", tok);
		}
	}
	
	/**
	 * Returns a boolean indicating whether or not the next token matches
	 * the expected String value.  This does not remove the token from the
	 * token list.
	 * 
	 * @param expected value of the next token
	 * @return true iff the token value matches the expected string
	 */
	private boolean peekNext(String expected) {
		return tokens.get(0).getValue().equals(expected);
	}
	
	/**
	 * Returns a boolean indicating whether or not the token directly after
	 * the front most token matches the expected String value.  This does 
	 * not remove any tokens from the token list.
	 * 
	 * @param expected value of the token directly after the next token
	 * @return true iff the value matches the expected string
	 */
	private boolean peekTwoAhead(String expected) {
		return tokens.get(1).getValue().equals(expected);
	}
	
	
	/*
	 *  <program> ::= program <decls> begin <stmts> end ;
	 */
	public ProgramAST parse() throws ParseException {
		
		try {
			// Needed for 'program' token position info
			Token startTok = tokens.get(0);
			
			checkNext("program");

			List<Declaration> decls = parseDecls();
			
			checkNext("begin");
			
			List<Statement> stmts = parseStmts();
			
			checkNext("end");
			checkNext(";");
	
			return new ProgramAST(decls, stmts, startTok);
			
		// This might happen if we do a tokens.get(), and nothing is there!
		} catch (IndexOutOfBoundsException ex) {
			
			throw new ParseException("Unexpectedly reached the end of file.", -1, -1);
		}
	}
	
	/*
	 *  <decls> ::= (  <decl>  )*
	 */
	private List<Declaration> parseDecls() throws ParseException {
		
		List<Declaration> decls = new ArrayList<Declaration>();
		
		while (!peekNext("begin")) {
			Declaration decl = parseDecl();
			decls.add(decl);
		}
//		System.out.println(decls);
		return decls;
	}
	
	/*
	 * <decl> ::= <var-decl> | <func-decl>
	 */
	private Declaration parseDecl() throws ParseException {

		if (peekTwoAhead(":")) {
			return parseVarDecl();
		} else if (peekTwoAhead("(")) {
			return parseFuncDecl();
		} else {
			Token tok = tokens.get(0);
			throw new ParseException("Declaration expected", tok);
		}
	}
	
	/*
	 * <func-decl> ::= <label> ( <params> ) : <ret-type> is 
	 * 						<loc-var-decls> begin <stmts> end ;
	 */
	private FunctionDecl parseFuncDecl() throws ParseException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * <var-decl> ::= <label> : <type> ;
	 */
	private VariableDecl parseVarDecl() throws ParseException {
//		Token tokenLabel = tokens.get(0);
		Token tokenLabel = tokens.remove(0);
		String label = tokenLabel.getValue();
		if (isKeyword(label)){
			throw new ParseException("Invalid label declaration", tokenLabel);
		}

		checkNext(":");

		Token tokenType = tokens.remove(0);
		Type type = convertStringToType(tokenType);

		checkNext(";");

		return new VariableDecl(tokenLabel, label, type);
	}
	
	/*
	 * <stmts> ::= (  <stmt>  )*
	 */
	private List<Statement> parseStmts() throws ParseException {
		List<Statement> stms = new ArrayList<Statement>();
		while (!peekNext("end") && !peekTwoAhead(";")) {
			Statement stmt = parseStmt();
			stms.add(stmt);
		}

		return stms;
	}

	private Statement parseStmt() throws ParseException{
		if (peekTwoAhead(":=")) {
			return parseAssignmentStmt();
//		} else if (peekTwoAhead("(")) {
//			return parseFuncDecl();
		} else {
			Token tok = tokens.get(0);
			throw new ParseException("Statement expected", tok);
		}
	}

	// <stmt> ::= <label> := <expr> ;
	private AssignmentStmt parseAssignmentStmt() throws ParseException{
		Token tokenLabel = tokens.remove(0);
		String label = tokenLabel.getValue();
		if (isKeyword(label)){
			throw new ParseException("Invalid label declaration", tokenLabel);
		}

		checkNext(":=");

		Expression expr = parseExpressions();

		checkNext(";");

		return new AssignmentStmt(tokenLabel, label, expr);
	}

	private Expression parseExpressions() throws ParseException{
		Token tok = tokens.remove(0);
		if (isLiteral(tok)){
			return new LiteralExpr(tok, tok.getValue());
		} else if (isLabel(tok) && !peekNext("(")) {
			return new LabelExpr(tok, tok.getValue());
		} else if (peekNext("(")) {
			Expression label = new LabelExpr(tok, tok.getValue());
			checkNext("(");
			List<Expression> argsList = new ArrayList<Expression>();
			while(!peekNext(")")){
				Expression expr = parseExpressions();
				argsList.add(expr);
			}
			checkNext(")");
			Expression args = new ArgsExpr(tok, argsList);
			
			return new NonVoidFunctionCallExpr(tok, label, args);
		} else if (tok.getValue().equals("(")) {
			Token tokUnaryCheck = tokens.get(0);
			if (isUnaryOp(tokUnaryCheck)){
				Token tokUnaryOp = tokens.remove(0);
				Expression exprUnaryOp = parseExpressions();
				checkNext(")");
				return new UnaryOpExpr(tok, tokUnaryOp.getValue(), exprUnaryOp);
			}
			else{
				Expression exprBinary1 = parseExpressions();
				Token tokBinaryCheck = tokens.get(0);
				if (isBinaryOp(tokBinaryCheck)){
					Token tokBinary = tokens.remove(0);
					Expression exprBinary2 = parseExpressions();
					checkNext(")");
					return new BinaryOpExpr(tok, tokBinary.getValue(), exprBinary1, exprBinary2);
				}
			}

		}
		else{
//			System.out.println("token is " + tok.getValue());
			throw new ParseException("Expression expected",tok);
		}
		return null;
	}

	public Type convertStringToType(Token tokenType) throws ParseException{
		String typeStr = tokenType.getValue();

		Type type;
		if (typeStr.equals("Integer")) {
			type = Type.Integer;
		}
		else if (typeStr.equals("String")) {
			type = Type.String;
		}
		else if (typeStr.equals("Boolean")) {
			type = Type.Boolean;
		}
		else {
			throw new ParseException("Invalid type " + typeStr, tokenType);
		}

		return type;
	}

	public Boolean isKeyword(String label){

		if (label.equals("if") || label.equals("else") || label.equals("while")
				|| label.equals("and") || label.equals("not")
				|| label.equals("do") || label.equals("or")
				|| label.equals("then") || label.equals("end") || label.equals("print")
				|| label.equals("print_line") || label.equals("return") || label.equals("program")
				|| label.equals("is") || label.equals("begin")
				|| label.equals("void") || label.equals("Integer")
				|| label.equals("String") || label.equals("Boolean")
				|| label.equals("true") || label.equals("false")){
			System.out.println("this is keyword");
			return true;
		}

		return false;
	}

	private Boolean isLiteral(Token tok){
		String value = tok.getValue();
		if (value.equals("true") || value.equals("false")
				||
				(value.charAt(0) == '\"' && value.charAt(value.length()-1) == '\"')
				||
				value.chars().allMatch(Character::isDigit)
		){
			return true;
		}

		return false;
	}

	private Boolean isLabel(Token token){
		String label = token.getValue();
		if (label.matches("^[_a-z]\\w*$")) return true;
		return false;
	}

	private Boolean isUnaryOp(Token token){
		String value = token.getValue();
		return (value.equals("not") || value.equals("-"));
	}

	private Boolean isBinaryOp(Token token){
		String value = token.getValue();
		if (value.equals("and") || value.equals("or") || value.equals(">") || value.equals("<")
				|| value.equals("==") || value.equals(">=") || value.equals("<=")
				|| value.equals("+") || value.equals("-") || value.equals("*")
				|| value.equals("/") || value.equals("%")){
			return true;
		}

		return false;
	}


}
