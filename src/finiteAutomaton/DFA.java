package finiteAutomaton;

import java.util.*;

public class DFA {
    private int largestStateNumber = -1;
    private final Set<String> alphabet;
    private final Map<Integer, Map<String, Integer>> transitionFunction;

    public Map<Integer, Map<String, Integer>> getTransitionFunction() {
        return transitionFunction;
    }

    public DFA(Map<Integer, Map<String, Integer>> transitionFunction) {
        this.alphabet = new HashSet<>();
        this.transitionFunction = Objects.requireNonNullElseGet(transitionFunction, HashMap::new);
    }

    public DFA addTransition(int fromState, String transitionOn, int toState) throws NullTransitionOnArgument, NonSequentialStateNumber {
        if (transitionOn == null) throw new NullTransitionOnArgument();
        alphabet.add(transitionOn);

        if (fromState > largestStateNumber + 1) throw new NonSequentialStateNumber(fromState, largestStateNumber);
        if (fromState == largestStateNumber + 1) ++largestStateNumber;
        if (toState > largestStateNumber + 1) throw new NonSequentialStateNumber(toState, largestStateNumber);
        if (toState == largestStateNumber + 1) ++largestStateNumber;

        if (transitionFunction.containsKey(fromState)) {
            transitionFunction.get(fromState).put(transitionOn, toState);
        } else {
            // Neither are in the transitionFunction
            Map<String, Integer> mapping = new HashMap<>() {{
                put(transitionOn, toState);
            }};
            transitionFunction.put(fromState, mapping);
        }
        return this;
    }

    public NFAe convertToNFAe() {
        Map<Integer, Map<String, Set<Integer>>> newTransitionFunction = new HashMap<>();
        for (Map.Entry<Integer, Map<String, Integer>> entry : this.transitionFunction.entrySet()) {
            Map<String, Integer> innerMap = entry.getValue();
            Map<String, Set<Integer>> newInnerMap = new HashMap<>();
            for (Map.Entry<String, Integer> innerEntry : innerMap.entrySet()) {
                Set<Integer> newSet = new HashSet<>() {{
                    add(innerEntry.getValue());
                }};
                newInnerMap.put(innerEntry.getKey(), newSet);
            }
            newTransitionFunction.put(entry.getKey(), newInnerMap);
        }
        return new NFAe(newTransitionFunction, null);
    }

    @Override
    public String toString() {
        return "DFA Transition function: \n" + transitionFunction;
    }
}
