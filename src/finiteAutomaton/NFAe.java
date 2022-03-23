package finiteAutomaton;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class NFAe {
    private final int startState;
    private Map<Integer, Map<String, Set<Integer>>> transitionFunction;
    private Map<Integer, Set<Integer>> epsilonTransitionFunction;

    public int getStartState() {
        return startState;
    }

    public Map<Integer, Map<String, Set<Integer>>> getTransitionFunction() {
        return transitionFunction;
    }

    public Map<Integer, Set<Integer>> getEpsilonTransitionFunction() {
        return epsilonTransitionFunction;
    }

    public NFAe(int startState, Map<Integer, Map<String, Set<Integer>>> transitionFunction, Map<Integer, Set<Integer>> epsilonTransitionFunction) {
        this.startState = startState;
        if (transitionFunction != null) {
            this.transitionFunction = transitionFunction;
        } else {
            this.transitionFunction = new HashMap<>();
        }
        if (epsilonTransitionFunction != null) {
            this.epsilonTransitionFunction = epsilonTransitionFunction;
        } else {
            this.epsilonTransitionFunction = new HashMap<>();
        }
    }

    public NFAe addTransition(int fromState, String transitionOn, int toState) throws NullTransitionOnArgument {
        if (transitionOn == null) throw new NullTransitionOnArgument();
        if (transitionFunction.containsKey(fromState)) {
            if (transitionFunction.get(fromState).containsKey(transitionOn)) {
                // Both are in the transitionFunction
                transitionFunction.get(fromState).get(transitionOn).add(toState);
            } else {
                // Only the fromState is in the transitionFunction
                Set<Integer> destination = new HashSet<>() {{
                    add(toState);
                }};
                transitionFunction.get(fromState).put(transitionOn, destination);
            }
        } else {
            // Neither are in the transitionFunction
            Set<Integer> destination = new HashSet<>() {{
                add(toState);
            }};
            Map<String, Set<Integer>> mapping = new HashMap<>() {{
                put(transitionOn, destination);
            }};
            transitionFunction.put(fromState, mapping);
        }
        return this;
    }

    public NFAe addEpsilonTransition(int fromState, int toState) {
        if (epsilonTransitionFunction.containsKey(fromState)) {
            // fromState is already in the epsilonTransitionFunction
            epsilonTransitionFunction.get(fromState).add(toState);
        } else {
            // fromState is not in the epsilonTransitionFunction
            Set<Integer> destination = new HashSet<>() {{
                add(toState);
            }};
            epsilonTransitionFunction.put(fromState, destination);
        }
        return this;
    }

    @Override
    public String toString() {
        return "NFAe Start state: " + startState + "\n" +
                "Transition function: \n" + transitionFunction + "\n" +
                "Epsilon transition function: \n" + epsilonTransitionFunction + "\n";
    }
}
