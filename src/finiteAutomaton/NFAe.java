package finiteAutomaton;

import java.util.*;

public class NFAe {
    private int largestStateNumber = -1;
    private final Set<String> alphabet;
    private final Map<Integer, Map<String, Set<Integer>>> transitionFunction;
    private final Map<Integer, Set<Integer>> epsilonTransitionFunction;
    private final Set<Integer> acceptingStates;

    public Map<Integer, Map<String, Set<Integer>>> getTransitionFunction() {
        return transitionFunction;
    }

    public Map<Integer, Set<Integer>> getEpsilonTransitionFunction() {
        return epsilonTransitionFunction;
    }

    public NFAe(Map<Integer, Map<String, Set<Integer>>> transitionFunction, Map<Integer, Set<Integer>> epsilonTransitionFunction, Set<Integer> acceptingStates) {
        this.alphabet = new HashSet<>();
        this.transitionFunction = Objects.requireNonNullElseGet(transitionFunction, HashMap::new);
        this.epsilonTransitionFunction = Objects.requireNonNullElseGet(epsilonTransitionFunction, HashMap::new);
        this.acceptingStates = Objects.requireNonNullElseGet(acceptingStates, HashSet::new);
    }

    public NFAe addTransition(int fromState, String transitionOn, int toState) throws NullTransitionOnArgument, NonSequentialStateNumber {
        if (transitionOn == null) throw new NullTransitionOnArgument();
        alphabet.add(transitionOn);

        if (fromState > largestStateNumber + 1) throw new NonSequentialStateNumber(fromState, largestStateNumber);
        if (fromState == largestStateNumber + 1) ++largestStateNumber;
        if (toState > largestStateNumber + 1) throw new NonSequentialStateNumber(toState, largestStateNumber);
        if (toState == largestStateNumber + 1) ++largestStateNumber;

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

    public NFAe addEpsilonTransition(int fromState, int toState) throws NonSequentialStateNumber {
        if (fromState > largestStateNumber + 1) throw new NonSequentialStateNumber(fromState, largestStateNumber);
        if (fromState == largestStateNumber + 1) ++largestStateNumber;
        if (toState > largestStateNumber + 1) throw new NonSequentialStateNumber(toState, largestStateNumber);
        if (toState == largestStateNumber + 1) ++largestStateNumber;

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

    public NFAe makeAcceptingStates(Integer... states) {
        acceptingStates.addAll(Arrays.asList(states));
        return this;
    }

    public NFAe addAcceptingStates(Integer... states) {
        largestStateNumber += states.length;
        makeAcceptingStates(states);
        return this;
    }

    public DFA convertToDFA() throws NullTransitionOnArgument, NonSequentialStateNumber {
        DFA result = new DFA(null, null);

        Map<Integer, Set<Integer>> epsilonClosureCache = new HashMap<>();
        Map<Set<Integer>, Integer> newStateMapping = new HashMap<>();
        int newStateCounter = 0; // This is the number which should be assigned when creating a new state
        Set<Set<Integer>> seenNewStates = new HashSet<>();

        // Prepopulate the start state
        Set<Integer> startEpsilonClosure = computeEpsilonClosure(0);
        epsilonClosureCache.put(0, startEpsilonClosure);
        newStateMapping.put(startEpsilonClosure, newStateCounter);
        ++newStateCounter;

        Stack<Set<Integer>> newStatesStack = new Stack<>();
        newStatesStack.push(startEpsilonClosure);

        while(!newStatesStack.isEmpty()) {
            Set<Integer> stateToExplore = newStatesStack.pop();
            if (seenNewStates.contains(stateToExplore)) continue;
            seenNewStates.add(stateToExplore);

            // Iterate through the entire alphabet
            for (String alpha : alphabet) {
                Set<Integer> reachableStatesNoEpsilon = transitionToStates(stateToExplore, alpha);
                Set<Integer> reachableStatesWithEpsilon = computeEpsilonClosure(reachableStatesNoEpsilon, epsilonClosureCache);
                // Store this as a new state for the DFA, if a mapping is not already present
                if (!newStateMapping.containsKey(reachableStatesWithEpsilon)) {
                    newStateMapping.put(reachableStatesWithEpsilon, newStateCounter);
                    ++newStateCounter;
                }
                // Put this new transition in the DFA
                result.addTransition(newStateMapping.get(stateToExplore), alpha, newStateMapping.get(reachableStatesWithEpsilon));
                // Put this newly created state in the stack to explore later
                newStatesStack.push(reachableStatesWithEpsilon);
            }
        }

        // Calculate the new accepting states
        for (Map.Entry<Set<Integer>, Integer> entry : newStateMapping.entrySet()) {
            if (isNonEmptyIntersection(entry.getKey(), acceptingStates)) result.addAcceptingStates(entry.getValue());
        }

        System.out.println("New state mapping: \n" + newStateMapping);
        return result;
    }

    private boolean isNonEmptyIntersection(Set<Integer> setOne, Set<Integer> setTwo) {
        if (setOne.size() < setTwo.size()) {
            // Iterate on setOne as it is smaller
            for (int i : setOne) {
                if (setTwo.contains(i)) return true;
            }
        } else {
            // Iterate on setTwo as it is smaller
            for (int i : setTwo) {
                if (setOne.contains(i)) return true;
            }
        }
        return false;
    }

    private int calculateMaxStateNumber() {
        int numStates = -1;
        // First search the transitionFunction
        for (Map.Entry<Integer, Map<String, Set<Integer>>> entry : transitionFunction.entrySet()) {
            if (entry.getKey() > numStates) numStates = entry.getKey();
            for (Map.Entry<String, Set<Integer>> innerEntry : entry.getValue().entrySet()) {
                int maxInSet = innerEntry.getValue().stream().max(Comparator.comparingInt(Integer::intValue)).orElseThrow();
                if (maxInSet > numStates) numStates = maxInSet;
            }
        }
        // Next search the epsilonTransitionFunction
        for (Map.Entry<Integer, Set<Integer>> entry : epsilonTransitionFunction.entrySet()) {
            if (entry.getKey() > numStates) numStates = entry.getKey();
            int maxInSet = entry.getValue().stream().max(Comparator.comparingInt(Integer::intValue)).orElseThrow();
            if (maxInSet > numStates) numStates = maxInSet;
        }

        assert (largestStateNumber == numStates);
        return numStates;
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

    public Set<Integer> computeEpsilonClosure(Set<Integer> states, Map<Integer, Set<Integer>> epsilonClosureCache) {
        Set<Integer> result = new HashSet<>();

        for (int state : states) {
            // First consult the cache
            if (epsilonClosureCache.containsKey(state)) {
                result.addAll(epsilonClosureCache.get(state));
            } else {
                // Not present in the cache
                Set<Integer> computedEpsilonClosure = computeEpsilonClosure(state);
                epsilonClosureCache.put(state, computedEpsilonClosure);
                result.addAll(computedEpsilonClosure);
            }
        }

        return result;
    }

    public Set<Integer> transitionToStates(Set<Integer> startStates, String transitionOn) {
        Set<Integer> result = new HashSet<>();
        for (int startState : startStates) {
            Map<String, Set<Integer>> map = transitionFunction.get(startState);
            if (map != null) {
                Set<Integer> reachableStates = map.get(transitionOn);
                if (reachableStates != null) {
                    result.addAll(reachableStates);
                }
            }

        }
        return result;
    }

    @Override
    public String toString() {
        return "NFAe Transition Function: \n" + transitionFunction + "\n" +
                "Epsilon Transition Function: \n" + epsilonTransitionFunction + "\n" +
                "NFAe Accepting States: \n" + acceptingStates;
    }
}
