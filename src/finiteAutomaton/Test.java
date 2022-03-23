package finiteAutomaton;

public class Test {
    public static void main(String[] args) throws NullTransitionOnArgument {
        NFAe nfae = new NFAe(0, null, null)
                .addTransition(0, "a", 1)
                .addTransition(0, "b", 1)
                .addTransition(0, "b", 2)
                .addEpsilonTransition(2, 3);
        System.out.println(nfae);

        DFA dfa = new DFA(0, null)
                .addTransition(0, "a", 1)
                .addTransition(0, "b", 2)
                .addTransition(1, "c", 3)
                .addTransition(1, "c", 4); // Testing the overwriting capability of the DFA
        System.out.println(dfa);
    }
}
