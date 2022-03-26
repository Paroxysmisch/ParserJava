package parser;

import finiteAutomaton.NFAe;
import finiteAutomaton.NonSequentialStateNumber;
import finiteAutomaton.NullTransitionOnArgument;
import grammar.Grammar;
import grammar.NullGrammarSymbolException;

import java.util.*;

public class Parser {
    private final Grammar grammar;
    private final Set<String> nullable; // Represents non-terminals which are nullable
    private final Map<String, Set<String>> first; // Represents the FIRST function which maps from a grammar symbol to the first terminal
    private final Map<String, Set<String>> follow; // Represents the FOLLOW function which maps from a non-terminal to possible terminals which may follow that non-terminal

    public Grammar getGrammar() {
        return grammar;
    }

    public Set<String> getNullable() {
        return nullable;
    }

    public Map<String, Set<String>> getFirst() {
        return first;
    }

    public Map<String, Set<String>> getFollow() {
        return follow;
    }

    public Parser(Grammar grammar, Set<String> nullable, Map<String, Set<String>> first, Map<String, Set<String>> follow) throws NullGrammarException {
        if (grammar == null) throw new NullGrammarException();
        this.grammar = grammar;
        this.nullable = Objects.requireNonNullElseGet(nullable, HashSet::new);
        this.first = Objects.requireNonNullElseGet(first, HashMap::new);
        this.follow = Objects.requireNonNullElseGet(follow, HashMap::new);
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

    public void generateFollow() throws NullGrammarSymbolException {
        int lastFollowSize = 0;
        Set<String> endOfInputFollowsStartSymbol = new HashSet<>() {{
            add("$");
        }};
        follow.put(grammar.getStartSymbol(), endOfInputFollowsStartSymbol); // By convention, the end of input symbol, $, follows the start symbol
        while (follow.size() > lastFollowSize) {
            lastFollowSize = first.size();
            // Loop through each production
            for (Map.Entry<String, Set<List<String>>> productionSet : grammar.getProductions().entrySet()) {
                for (List<String> production : productionSet.getValue()) {
                    // Iterate through each grammar symbol in the production
                    for (int i = 0; i < production.size(); ++i) {
                        if (Grammar.isTerminal(production.get(i))) continue;
                        // The current grammar symbol in the production is a non-terminal
                        String nonTerminal = production.get(i);
                        Set<String> previousFollow = follow.computeIfAbsent(nonTerminal, k -> new HashSet<>());
                        if ((i + 1) == production.size()) {
                            // nonTerminal happens to be the last grammar symbol in the production
                            Set<String> LHSFollow = follow.computeIfAbsent(productionSet.getKey(), k -> new HashSet<>());
                            previousFollow.addAll(LHSFollow);
                        } else {
                            // There is another grammar symbol in the production after nonTerminal
                            String nextGrammarSymbol = production.get(i + 1);
                            previousFollow.addAll(removeEpsilon(first.get(nextGrammarSymbol)));
                            if (first.get(nextGrammarSymbol).contains("")) {
                                Set<String> LHSFollow = follow.computeIfAbsent(productionSet.getKey(), k -> new HashSet<>());
                                previousFollow.addAll(LHSFollow);
                            }
                        }
                    }
                }
            }
        }
    }

    public NFAe generateNFAe() throws NullGrammarSymbolException, NullTransitionOnArgument, NonSequentialStateNumber {
        NFAe result = new NFAe(null, null, null);
        // When creating a new state, this is what it's number should be. Don't forget to increment the counter, once the state is created
        int nextStateNumber = 0;
        // Mapping from non-terminal to the states representing the start of their corresponding productions
        // needed to add epsilon transitions later
        Map<String, Set<Integer>> nonTerminalStatesMap = new HashMap<>();

        // Loop through each production
        for (Map.Entry<String, Set<List<String>>> productionSet : grammar.getProductions().entrySet()) {
            for (List<String> production : productionSet.getValue()) {
                // Handle special case where production body is just epsilon
                if (production.size() == 0) {
                    result.addAcceptingStates(nextStateNumber);
                    Set<Integer> previousStates = nonTerminalStatesMap.computeIfAbsent(productionSet.getKey(), k -> new HashSet<>());
                    previousStates.add(nextStateNumber);
                    ++nextStateNumber;
                } else {
                    // Start state where grammar symbol has been consumed
                    Set<Integer> previousStates = nonTerminalStatesMap.computeIfAbsent(productionSet.getKey(), k -> new HashSet<>());
                    previousStates.add(nextStateNumber);
                    ++nextStateNumber;
                    // Iterate through each grammar symbol in the production and add consumption transitions
                    for (String grammarSymbol : production) {
                        result.addTransition(nextStateNumber - 1, grammarSymbol, nextStateNumber);
                        ++nextStateNumber;
                    }
                    // Make the last grammar symbol an accepting state
                    result.makeAcceptingStates(nextStateNumber - 1);
                }
            }
        }
        return result;
    }
}
