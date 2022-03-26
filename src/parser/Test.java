package parser;

import finiteAutomaton.NonSequentialStateNumber;
import finiteAutomaton.NullTransitionOnArgument;
import grammar.Grammar;
import grammar.InvalidNonTerminalException;
import grammar.NullGrammarSymbolException;

import java.util.List;

public class Test {
    public static void main(String[] args) throws InvalidNonTerminalException, NullGrammarSymbolException, NullGrammarException, NullTransitionOnArgument, NonSequentialStateNumber {
        // An unambiguous grammar
        Grammar grammar = new Grammar("T'")
                .addProduction("T", List.of("R"))
                .addProduction("T", List.of("a", "T", "c"))
                .addProduction("R", List.of())
                .addProduction("R", List.of("b", "R"));
        System.out.println(grammar);
        System.out.println();

        Parser parser = new Parser(grammar, null, null, null);
        parser.generateNullable();
        System.out.println(parser.getNullable());
        parser.generateFirst();
        System.out.println(parser.getFirst());
        parser.generateFollow();
        System.out.println(parser.getFollow());
        System.out.println();
        System.out.println(parser.generateNFAe());
        System.out.println();
        System.out.println(parser.generateDFA());
    }
}
