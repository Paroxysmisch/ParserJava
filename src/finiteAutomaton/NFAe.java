package finiteAutomaton;

import java.util.*;

public class NFAe {
    private final int startState;
    private final Map<Integer, Map<String, Set<Integer>>> transitionFunction;
    private final Map<Integer, Set<Integer>> epsilonTransitionFunction;

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
        this.transitionFunction = Objects.requireNonNullElseGet(transitionFunction, HashMap::new);
        this.epsilonTransitionFunction = Objects.requireNonNullElseGet(epsilonTransitionFunction, HashMap::new);
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

    public DFA convertToDFA() {
        Map<Integer, Set<Integer>> epsilonClosureMap = new HashMap<>();
        Set<Integer> visited = new HashSet<>(); // This is to deal with epsilon cycles

        return null;
    }

    public Set<Integer> computeEpsilonClosure(int startState, Map<Integer, Set<Integer>> epsilonClosureMap, Set<Integer> visited) {
        visited.add(startState);
        if (epsilonClosureMap.containsKey(startState)) return epsilonClosureMap.get(startState);
        Set<Integer> result = new HashSet<>();
        Set<Integer> reachableWithEpsilon = epsilonTransitionFunction.get(startState);
        for (int i : reachableWithEpsilon) {
            if (!visited.contains(i)) {
                Set<Integer> subResult = computeEpsilonClosure(i, epsilonClosureMap, visited);
                // Union result with subResult and store the answer in result
                result.addAll(subResult);
            }
        }
        // You can reach yourself from yourself
        result.add(startState);
        epsilonClosureMap.put(startState, result);
        return result;
    }

    public Set<Integer> computeEpsilonClosure(int startState) {
        // Perform a DFS on epsilon transitions to get the epsilon closure
        Set<Integer> visited = new HashSet<>();
        Stack<Integer> toVisit = new Stack<>();
        toVisit.add(startState);

        while (!toVisit.isEmpty()) {
            int state = toVisit.pop();
            if (!visited.contains(state)) {
                visited.add(state);
                Set<Integer> reachableWithEpsilon = epsilonTransitionFunction.get(state);
                if (reachableWithEpsilon != null) toVisit.addAll(reachableWithEpsilon);
            }
        }

        return visited;
    }

    @Override
    public String toString() {
        return "NFAe Start state: " + startState + "\n" +
                "Transition function: \n" + transitionFunction + "\n" +
                "Epsilon transition function: \n" + epsilonTransitionFunction + "\n";
    }
}
