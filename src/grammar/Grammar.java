package grammar;

import java.util.*;

public class Grammar {
    Set<String> nonTerminals;
    Set<String> terminals;
    Map<String, Set<List<String>>> productions; // This is a map from non-terminals to a list of grammar symbols (terminals and non-terminals)
    String startSymbol; // This must be a non-terminal

    public Set<String> getNonTerminals() {
        return nonTerminals;
    }

    public Set<String> getTerminals() {
        return terminals;
    }

    public Map<String, Set<List<String>>> getProductions() {
        return productions;
    }

    public String getStartSymbol() {
        return startSymbol;
    }

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

        Set<List<String>> previousRHS = productions.get(nonTerminal);
        if (previousRHS == null) {
            Set<List<String>> RHS = new HashSet<>() {{
                add(production);
            }};
            productions.put(nonTerminal, RHS);
        } else {
            previousRHS.add(production);
        }

        // Update the nonTerminals and Terminals sets
        nonTerminals.add(nonTerminal);
        for (String grammarSymbol : production) {
            if (isTerminal(grammarSymbol)) {
                terminals.add(grammarSymbol);
            } else {
                nonTerminals.add(grammarSymbol);
            }
        }
        return this;
    }

    public static boolean isTerminal(String input) throws NullGrammarSymbolException {
        if (input == null) throw new NullGrammarSymbolException();
        if (input.equals("")) return true; // We treat the empty string as a terminal
        return input.charAt(0) != Character.toUpperCase(input.charAt(0));
    }

    @Override
    public String toString() {
        return "Non-terminals: \n" + nonTerminals + "\n" +
                "Terminals: \n" + terminals + "\n" +
                "Productions: \n" + productions + "\n" +
                "Start Symbol: " + startSymbol;
    }
}
