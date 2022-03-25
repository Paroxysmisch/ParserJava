package grammar;

import java.util.List;

public class Test {
    public static void main(String[] args) throws InvalidNonTerminalException, NullGrammarSymbolException {
        Grammar grammar = new Grammar("A");
        System.out.println(grammar.isTerminal("b"));
        System.out.println(grammar.isTerminal("B"));
        grammar.addProduction("A", List.of("a", "A", "B", "a"));
        grammar.addProduction("B", List.of());
        System.out.println(grammar);
    }
}
