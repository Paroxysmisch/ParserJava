package grammar;

import java.util.*;

public class Grammar {
    Set<String> nonTerminals;
    Set<String> terminals;
    Map<String, List<String>> productions; // This is a map from non-terminals to a list of grammar symbols (terminals and non-terminals)
    String startSymbol; // This must be a non-terminal

    public Grammar(String startSymbol) throws InvalidNonTerminalException, NullGrammarSymbolException {
        if (isTerminal(startSymbol)) throw new InvalidNonTerminalException(startSymbol);
        nonTerminals = new HashSet<>() {{
            add(startSymbol);
        }};
        terminals = new HashSet<>();
        productions = new HashMap<>();
        this.startSymbol = startSymbol;
    }

    public Grammar addProduction(String nonTerminal, List<String> production) throws InvalidNonTerminalException, NullGrammarSymbolException {
        if (isTerminal(nonTerminal)) throw new InvalidNonTerminalException(nonTerminal);
        productions.put(nonTerminal, production);
        return this;
    }

    public boolean isTerminal(String input) throws NullGrammarSymbolException {
        if (input == null) throw new NullGrammarSymbolException();
        return input.charAt(0) != Character.toUpperCase(input.charAt(0));
    }
}
