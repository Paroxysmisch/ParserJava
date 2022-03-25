package parser;

import grammar.Grammar;

import java.util.*;

public class Parser {
    private final Grammar grammar;
    private final Set<List<String>> nullable; // Represents grammar symbols which are nullable

    public Grammar getGrammar() {
        return grammar;
    }

    public Set<List<String>> getNullable() {
        return nullable;
    }

    public Parser(Grammar grammar, Set<List<String>> nullable) throws NullGrammarException {
        if (grammar == null) throw new NullGrammarException();
        this.grammar = grammar;
        this.nullable = Objects.requireNonNullElseGet(nullable, HashSet::new);
    }

    public void generateNullable() {
        int lastNullableSize = 0;
        nullable.add(List.of("")); // Epsilon is always nullable
        while (nullable.size() > lastNullableSize) {
            lastNullableSize = nullable.size();
            // Loop through each production
            for (Map.Entry<String, Set<List<String>>> productionSet : grammar.getProductions().entrySet()) {
                for (List<String> production : productionSet.getValue()) {
                    if (isNullable(production)) {
                        nullable.add(production); // RHS of production is nullable
                        nullable.add(List.of(productionSet.getKey())); // LHS of production (the non-terminal) is nullable
                    }
                }
            }
        }
    }

    public boolean isNullable(List<String> grammarSymbols) {
        // Need to wrap each grammar symbol inside a list
        for (String grammarSymbol : grammarSymbols) {
            if (!nullable.contains(List.of(grammarSymbol))) return false;
        }
        return true;
    }
}
