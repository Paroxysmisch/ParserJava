package grammar;

public class InvalidNonTerminalException extends GrammarException {
    public InvalidNonTerminalException(String input) {
        super();
        System.err.println("Invalid non-terminal: " + input);
    }
}
