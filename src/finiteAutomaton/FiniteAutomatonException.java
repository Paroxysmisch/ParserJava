package finiteAutomaton;

public class FiniteAutomatonException extends Exception { }

class NullTransitionOnArgument extends FiniteAutomatonException { }

class NonSequentialStateNumber extends FiniteAutomatonException {
    public NonSequentialStateNumber(int stateNumber, int previousMax) {
        super();
        System.err.println("State number: " + stateNumber + " too large for previous maximum: " + previousMax);
    }
}
