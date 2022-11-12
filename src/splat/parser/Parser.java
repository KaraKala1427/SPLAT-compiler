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
		Token tokenLabel = tokens.remove(0);
		String label = tokenLabel.getValue();
		if (isKeyword(label)){
			throw new ParseException("Invalid label declaration", tokenLabel);
		}
		checkNext("(");
		List<Param> params = parseParams();
		checkNext(")");
		checkNext(":");

		Token tokenReturnType = tokens.remove(0);
		ReturnType retType = convertStringToRetType(tokenReturnType);

		checkNext("is");

		List<VariableDecl> localVarDecls = parseLocalVarDecls();

		checkNext("begin");

		List<Statement> stmts = parseStmts();

		checkNext("end");
		checkNext(";");

		return new FunctionDecl(tokenLabel, tokenLabel.getValue(), params, retType, localVarDecls, stmts);
	}

	/*
	 * <var-decl> ::= <label> : <type> ;
	 */
	private VariableDecl parseVarDecl() throws ParseException {;
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

	private List<VariableDecl> parseLocalVarDecls() throws ParseException{
		List<VariableDecl> localVarDecls = new ArrayList<VariableDecl>();
		while (!peekNext("begin")) {
			VariableDecl varDecl = parseVarDecl();
			localVarDecls.add(varDecl);
		}

		return localVarDecls;
	}

	private List<Param> parseParams() throws ParseException{
		List<Param> params = new ArrayList<Param>();

		while (!peekNext(")")) {
			Param param = parseParam();
			params.add(param);
			if (peekNext(",")){
				checkNext(",");
			}
		}

		return params;
	}

	private Param parseParam() throws ParseException{
		Token tokenLabel = tokens.remove(0);
		String label = tokenLabel.getValue();
		if (isKeyword(label)){
			throw new ParseException("Invalid label declaration", tokenLabel);
		}
		checkNext(":");
		Token tokenType = tokens.remove(0);
		Type type = convertStringToType(tokenType);
		return new Param(tokenLabel, label, type);
	}
	
	/*
	 * <stmts> ::= (  <stmt>  )*
	 */
	private List<Statement> parseStmts() throws ParseException {
		List<Statement> stmts = new ArrayList<Statement>();
		while (!(
				(peekNext("end") && peekTwoAhead(";")) ||
				(peekNext("end") && peekTwoAhead("if")) ||
						peekNext("else") ||
						(peekNext("end") && peekTwoAhead("while"))
				)
		) {
			Statement stmt = parseStmt();
			stmts.add(stmt);
		}

		return stmts;
	}

	private Statement parseStmt() throws ParseException{
//		System.out.println(tokens.get(0).getValue());
		if (peekTwoAhead(":=")) {
			return parseAssignmentStmt();
		} else if (peekNext("return")) {
			return parseReturnStmt();
		} else if (peekNext("print_line")) {
			return parsePrintLineStmt();
		} else if (peekNext("print")) {
			return parsePrintStmt();
		} else if (isLabel(tokens.get(0)) && peekTwoAhead("(") && !isKeyword(tokens.get(0).getValue())) {
			return parseNonVoidFuncStmt();
		} else if (peekNext("if")) {
			return parseIfConditionStmt();
		} else if (peekNext("while")) {
			return parseWhileDoStmt();
		} else {
			Token tok = tokens.get(0);
			System.out.println("at: " + tok.getLine() + ":" + tok.getColumn());
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
	
	private ReturnStmt parseReturnStmt() throws ParseException{
		Token tokenReturn = tokens.remove(0);
		if (peekNext(";")){
			ReturnStmt returnStmt = new ReturnStmt(tokenReturn);
			checkNext(";");
			return returnStmt;
		} else {
			Expression expr = parseExpressions();
			checkNext(";");
			return new ReturnStmt(tokenReturn, expr);
		}
	}

	private PrintLineStmt parsePrintLineStmt() throws ParseException{
		Token tokenPrintLine = tokens.remove(0);
		if (peekNext(";")){
			PrintLineStmt printLineStmt = new PrintLineStmt(tokenPrintLine);
			checkNext(";");
			return printLineStmt;
		} else {
			throw new ParseException("Expected ;",tokens.get(0));
		}
	}

	private PrintStmt parsePrintStmt() throws ParseException{
		Token tokenPrint = tokens.remove(0);
		Expression expr = parseExpressions();
		checkNext(";");
		return new PrintStmt(tokenPrint, expr);
	}

	private NonVoidFunctionCallStmt parseNonVoidFuncStmt() throws ParseException{
		Token tok = tokens.remove(0);
		Expression label = new LabelExpr(tok, tok.getValue());
		checkNext("(");
		List<Expression> argsList = new ArrayList<Expression>();
		while(!peekNext(")")){
			if (peekNext(",")){
				checkNext(",");
			}
			Expression expr = parseExpressions();
			argsList.add(expr);
		}
		checkNext(")");
		checkNext(";");
		Expression args = new ArgsExpr(tok, argsList);
		return new NonVoidFunctionCallStmt(tok, label, args);
	}

	private IfConditionStmt parseIfConditionStmt() throws ParseException{
		Token tokenIf = tokens.remove(0);
		Expression expr = parseExpressions();
		checkNext("then");
		List<Statement> stmts1 = parseStmts();
		if (peekNext("end")){
			IfConditionStmt ifConditionStmt = new IfConditionStmt(tokenIf, expr, stmts1);
			checkNext("end");
			checkNext("if");
			checkNext(";");
			return ifConditionStmt;
		} else if (peekNext("else")) {
			checkNext("else");
			List<Statement> stmts2 = parseStmts();
			IfConditionStmt ifConditionStmt = new IfConditionStmt(tokenIf, expr, stmts1, stmts2);
			checkNext("end");
			checkNext("if");
			checkNext(";");
			return ifConditionStmt;
		}else {
			throw new ParseException("Error: else or end expected, got: " + tokens.get(0).getValue(), tokens.get(0));
		}
	}

	private WhileDoStmt parseWhileDoStmt() throws ParseException{
		Token tokenWhile = tokens.remove(0);
		Expression expr = parseExpressions();
		checkNext("do");
		List<Statement> stmts = parseStmts();
		checkNext("end");
		checkNext("while");
		checkNext(";");

		return new WhileDoStmt(tokenWhile, expr, stmts);
	}

	private Expression parseExpressions() throws ParseException{
		Token tok = tokens.remove(0);
		if (isLiteral(tok)){
			return new LiteralExpr(tok, tok.getValue());
		} else if (isLabel(tok) && !peekNext("(")) {
			return new LabelExpr(tok, tok.getValue());
		} else if (isLabel(tok) && peekNext("(")) {
			Expression label = new LabelExpr(tok, tok.getValue());
			checkNext("(");
			List<Expression> argsList = new ArrayList<Expression>();
			while(!peekNext(")")){
				if (peekNext(",")){
					checkNext(",");
				}
				Expression expr = parseExpressions();
				argsList.add(expr);
			}
			checkNext(")");

			Expression args = new ArgsExpr(tok, argsList);
			
			return new NonVoidFunctionCallExpr(tok, label, args);

		} else if (tok.getValue().equals("(")) {
//			System.out.println("here:" + tok.getValue() + " next:" + tokens.get(0).getValue());
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
			System.out.println("at: " + tok.getLine() + ":" + tok.getColumn());
			throw new ParseException("Expression expected 1", tok);
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
		if (label.matches("^[_a-zA-z]\\w*$")) return true;
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

	private ReturnType convertStringToRetType(Token tokenRetType) throws ParseException{
		String typeStr = tokenRetType.getValue();

		ReturnType retType;
		if (typeStr.equals("Integer")) {
			retType = ReturnType.Integer;
		}
		else if (typeStr.equals("String")) {
			retType = ReturnType.String;
		}
		else if (typeStr.equals("Boolean")) {
			retType = ReturnType.Boolean;
		}
		else if (typeStr.equals("void")) {
			retType = ReturnType.Void;
		}
		else {
			throw new ParseException("Invalid type " + typeStr, tokenRetType);
		}

		return retType;
	}


}
