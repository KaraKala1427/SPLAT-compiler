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
		int prevCh = -1;
		do{
			ch = reader.read();
			column++;
//			String line = reader.readLine();
			if (ch == ' ' || ch == '\n' || ch == '\r' || ch == '\t' || ch == -1){
				if (value.length() > 0){
					Token token = new Token(value, line, column - 1 - value.length());  // world
					list.add(token);
					value = "";
				}

				if (ch == '\n') {
					line++;
					System.out.println("new line: " + line + " by " + ch);
					column = 1;
				}
			}
			else if(ch == '?'){
				throw new LexException("Unexpected ? mark" , line, column);
			}
			else {
				value = value + (char)ch;
			}
		} while (ch != -1);

		return list;
	}

}
