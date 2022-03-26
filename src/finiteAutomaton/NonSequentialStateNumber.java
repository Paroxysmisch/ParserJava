package finiteAutomaton;

public class NonSequentialStateNumber extends FiniteAutomatonException {
    public NonSequentialStateNumber(int stateNumber, int previousMax) {
        super();
        System.err.println("State number: " + stateNumber + " too large for previous maximum: " + previousMax);
    }
}
