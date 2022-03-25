package finiteAutomaton;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Test {
    public static void main(String[] args) throws NullTransitionOnArgument, NonSequentialStateNumber {
        NFAe nfae = new NFAe(null, null)
                .addTransition(0, "a", 1)
                .addTransition(0, "b", 1)
                .addTransition(0, "b", 2)
                .addEpsilonTransition(2, 3);
        System.out.println(nfae);

        DFA dfa = new DFA(null)
                .addTransition(0, "a", 1)
                .addTransition(0, "b", 2)
                .addTransition(1, "c", 3)
                .addTransition(1, "c", 4); // Testing the overwriting capability of the DFA
        System.out.println(dfa);

        NFAe convertedDFA = dfa.convertToNFAe();
        System.out.println(convertedDFA);

        NFAe cyclicNFAe = new NFAe(null, null)
                .addEpsilonTransition(0, 1)
                .addEpsilonTransition(1, 2)
                .addEpsilonTransition(2, 0)
                .addEpsilonTransition(2, 3);
        System.out.println(cyclicNFAe);

        Map<Integer, Set<Integer>> epsilonClosureCache = new HashMap<>();
        Set<Integer> states = new HashSet<>() {{
            add(1);
            add(3);
        }};
        System.out.println(cyclicNFAe.computeEpsilonClosure(states, epsilonClosureCache));

        DFA converted = nfae.convertToDFA();
        System.out.println(converted);
    }
}
