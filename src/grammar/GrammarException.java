package grammar;

public class GrammarException extends Exception { }

class InvalidNonTerminalException extends GrammarException {
    public InvalidNonTerminalException(String input) {
        super();
        System.err.println("Invalid non-terminal: " + input);
    }
}

class NullGrammarSymbolException extends GrammarException { }
