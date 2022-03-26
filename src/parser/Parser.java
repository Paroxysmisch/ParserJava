package parser;

import grammar.Grammar;

import java.util.*;

public class Parser {
    private final Grammar grammar;
    private final Set<String> nullable; // Represents non-terminals which are nullable
    private final Map<String, Set<String>> first; // Represents the FIRST function which maps from a grammar symbol to the first terminal

    public Grammar getGrammar() {
        return grammar;
    }

    public Set<String> getNullable() {
        return nullable;
    }

    public Map<String, Set<String>> getFirst() {
        return first;
    }

    public Parser(Grammar grammar, Set<String> nullable, Map<String, Set<String>> first) throws NullGrammarException {
        if (grammar == null) throw new NullGrammarException();
        this.grammar = grammar;
        this.nullable = Objects.requireNonNullElseGet(nullable, HashSet::new);
        this.first = Objects.requireNonNullElseGet(first, HashMap::new);
    }

    public void generateNullable() {
        int lastNullableSize = 0;
        nullable.add(""); // Epsilon is always nullable
        while (nullable.size() > lastNullableSize) {
            lastNullableSize = nullable.size();
            // Loop through each production
            for (Map.Entry<String, Set<List<String>>> productionSet : grammar.getProductions().entrySet()) {
                for (List<String> production : productionSet.getValue()) {
                    if (isNullable(production)) {
                        nullable.add(productionSet.getKey()); // LHS of production (the non-terminal) is nullable
                    }
                }
            }
        }
    }

    public boolean isNullable(List<String> grammarSymbols) {
        // Need to wrap each grammar symbol inside a list
        for (String grammarSymbol : grammarSymbols) {
            if (!nullable.contains(grammarSymbol)) return false;
        }
        return true;
    }

    public void generateFirst() {
        int lastFirstSize = 0;
        first.put("", new HashSet<>()); // The FIRST of epsilon is the empty set
        // The FIRST of each terminal is the terminal itself (represented as a singleton list)
        for (String terminal : grammar.getTerminals()) {
            HashSet<String> set = new HashSet<>() {{
                add(terminal);
            }};
            first.put(terminal, set);
        }
        while (first.size() > lastFirstSize) {
            lastFirstSize = first.size();
            // Loop through each production
            for (Map.Entry<String, Set<List<String>>> productionSet : grammar.getProductions().entrySet()) {
                for (List<String> production : productionSet.getValue()) {
                    Set<String> previousSet = first.computeIfAbsent(productionSet.getKey(), k -> new HashSet<>());
                    // Iterate through each grammar symbol in the production
                    int i = 0;
                    for (; i < production.size(); ++i) {
                        previousSet.addAll(removeEpsilon(first.get(production.get(i))));
                        if (!nullable.contains(production.get(i))) break;
                    }
                    if (i == production.size()) previousSet.add("");
                }
            }
        }
    }

    public Set<String> removeEpsilon(Set<String> set) {
        Set<String> result = new HashSet<>(set);
        result.remove("");
        return result;
    }
}
