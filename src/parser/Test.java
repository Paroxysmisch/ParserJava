package parser;

import grammar.Grammar;
import grammar.InvalidNonTerminalException;
import grammar.NullGrammarSymbolException;

import java.util.List;

public class Test {
    public static void main(String[] args) throws InvalidNonTerminalException, NullGrammarSymbolException, NullGrammarException {
        Grammar grammar = new Grammar("T")
                .addProduction("T", List.of("R"))
                .addProduction("T", List.of("a", "T", "c"))
                .addProduction("R", List.of(""))
                .addProduction("R", List.of("b", "R"));

        Parser parser = new Parser(grammar, null, null);
        parser.generateNullable();
        System.out.println(parser.getNullable());
    }
}
