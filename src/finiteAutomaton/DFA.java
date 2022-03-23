package finiteAutomaton;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DFA {
    private final int startState;
    private Map<Integer, Map<String, Integer>> transitionFunction;

    public DFA(int startState, Map<Integer, Map<String, Integer>> transitionFunction) {
        this.startState = startState;
        if (transitionFunction != null) {
            this.transitionFunction = transitionFunction;
        } else {
            this.transitionFunction = new HashMap<>();
        }
    }

    public DFA addTransition(int fromState, String transitionOn, int toState) throws NullTransitionOnArgument {
        if (transitionOn == null) throw new NullTransitionOnArgument();
        if (transitionFunction.containsKey(fromState)) {
            if (transitionFunction.get(fromState).containsKey(transitionOn)) {
                // Both are in the transitionFunction. We overwrite the old toState value with the new one
                transitionFunction.get(fromState).put(transitionOn, toState);
            } else {
                // Only the fromState is in the transitionFunction
                transitionFunction.get(fromState).put(transitionOn, toState);
            }
        } else {
            // Neither are in the transitionFunction
            Map<String, Integer> mapping = new HashMap<>() {{
                put(transitionOn, toState);
            }};
            transitionFunction.put(fromState, mapping);
        }
        return this;
    }

    @Override
    public String toString() {
        return "DFA Start state: " + startState + "\n" +
                "Transition function: \n" + transitionFunction + "\n";
    }
}
