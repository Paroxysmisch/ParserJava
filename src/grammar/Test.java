package grammar;

public class Test {
    public static void main(String[] args) throws InvalidNonTerminalException, NullGrammarSymbolException {
        Grammar grammar = new Grammar("A");
        System.out.println(grammar.isTerminal("b"));
        System.out.println(grammar.isTerminal("B"));
    }
}
