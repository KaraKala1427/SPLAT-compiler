package splat.parser.elements;

import splat.lexer.Token;

public class Param extends Declaration {

	// Need to add some fields
	public Type type;
	public String label;

	// Need to add extra arguments for setting fields in the constructor
	public Param(Token tok, String label, Type type) {
		super(tok);
		this.label = label;
		this.type = type;
	}


	public Type getType() {
		return type;
	}

	public String getLabel() {
		return label;
	}

	public String toString() {
		return label + ": " + type;
	}
}
