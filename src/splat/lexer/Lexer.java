package splat.lexer;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Lexer {

	private File progFile;

	public Lexer(File progFile) {
		this.progFile = progFile;
	}

	public List<Token> tokenize() throws LexException, FileNotFoundException, IOException {
		List<Token> list = new ArrayList<Token>();

		BufferedReader reader = new BufferedReader(new FileReader(progFile));
		int ch;
		String value = "";
		int line = 1;
		int column = 1;
		boolean isQuoteOpened = false;
		do{
			ch = reader.read();
			column++;
			int category = getTokenSeparatorCategory(ch);
			if (category != 0 && !isQuoteOpened || (ch == '\"' && !isQuoteOpened)){
//				System.out.println("value: " + value + " char: " + (char)ch);
				if (ch == '\"'){
					isQuoteOpened = true;
					if (value.length() > 0){
						int lastCh = value.charAt(value.length()-1);
						int lastCategory = getTokenSeparatorCategory(lastCh);
						if (lastCategory != 1){
							Token token = new Token(value, line, column - 1 - value.length());
							list.add(token);
						}
					}
					value = "" + (char)ch;
					continue;
				}
				if (category == 1){
					if (value.length() > 0){
						if (value.equals("=")){ // must be :=
							throw new LexException("Unexpected " + (char)ch, line, column - 1);
						}
						Token token = new Token(value, line, column - 1 - value.length());
						list.add(token);
						value = "";
					}

					if (ch == '\n') {
						line++;
						column = 1;
					}
				}
				else if(category == 2){
					if (value.length() == 0){
						value = value + (char)ch;
					}
					else {
						int lastCh = value.charAt(value.length()-1);
						int lastCateg = getTokenSeparatorCategory(lastCh);
						if (lastCateg == 3 || lastCateg == 4){ // if alphanumeric
							Token token = new Token(value, line, column - 1 - value.length());
							list.add(token);
							value = "" + (char)ch;
						} else if (lastCateg == 2) {

							if (isPossibleToBeToken(value + (char)ch)){
								value = value + (char)ch;
								Token token = new Token(value, line, column - 1 - value.length());
								list.add(token);
								value = "";
							}
							else{
								Token token = new Token(value, line, column - 1 - value.length());
								list.add(token);
								value = "" + (char)ch;
							}
						}
					}
				}
				else if (category == 3){
					if (value.length() > 0){
						int lastCh = value.charAt(value.length()-1);
						int lastCateg = getTokenSeparatorCategory(lastCh);
						int firstCh = value.charAt(0);
						int firstCateg = getTokenSeparatorCategory(firstCh);
						if ( firstCateg == 4 ){  // a => 123abc
							throw new LexException("Unexpected " + (char)ch, line, column - 1);
						}
						else if(lastCateg == 2){
							Token token = createToken(value, line, column, ch);
							list.add(token);
							value = "";
						}
					}
					value = value + (char)ch;
				}
				else if (category == 4){
					if (value.length() > 0){
						int lastCh = value.charAt(value.length()-1);
						int lastCateg = getTokenSeparatorCategory(lastCh);
						if (lastCateg == 2){
							Token token = createToken(value, line, column, ch);
							list.add(token);
							value = "";
						} else  {
							value = value + (char)ch;
						}
					}
					value = value + (char)ch;
				}
			} else if (isQuoteOpened) {
				value = value + (char)ch;
				if (ch == '\"'){
					Token token = new Token(value, line, column - 1 - value.length());
					list.add(token);
					value = "";
					isQuoteOpened = false;
				} else if (ch == -1) {
					throw new LexException("quotation mark not closed", line, column - 1);
				}
			} else {
				throw new LexException("Unexpected " + (char)ch, line, column - 1);
			}
		} while (ch != -1);

		return list;
	}

	public int getTokenSeparatorCategory(int ch){
		int category = 0;
		switch (ch){
			case ' ':
				category = 1;
				break;
			case '\n':
				category = 1;
				break;
			case '\r':
				category = 1;
				break;
			case '\t':
				category = 1;
				break;
			case -1:
				category = 1;
				break;
			case '<':
				category = 2;
				break;
			case '>':
				category = 2;
				break;
			case '=':
				category = 2;
				break;
			case ':':
				category = 2;
				break;
			case '+':
				category = 2;
				break;
			case '-':
				category = 2;
				break;
			case '*':
				category = 2;
				break;
			case '/':
				category = 2;
				break;
			case '%':
				category = 2;
				break;
			case '{':
				category = 2;
				break;
			case '}':
				category = 2;
				break;
			case ',':
				category = 2;
				break;
			case ';':
				category = 2;
				break;
			case '(':
				category = 2;
				break;
			case ')':
				category = 2;
				break;
		}

		if ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') || ch == '_'){
			category = 3;
		} else if (Character.isDigit(ch)) {
			category =4;
		}

		return category;
	}

	public boolean isPossibleToBeToken(String value){
		if (value.equals("<=") || value.equals("==") || value.equals(">=") || value.equals(":=")){
			return true;
		}
		return false;
	}

	public Token createToken(String value, int line, int column, int ch) throws LexException{
		if (value.equals("=")){ // must be :=
			throw new LexException("Unexpected " + (char)ch, line, column - 1);
		}
		return new Token(value, line, column - 1 - value.length());

	}

}
