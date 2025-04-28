package CRDT.opBased.generic.exceptions;

import generic.concurrency.Clock;

public class IncompatibleClocksException extends RuntimeException {

    public IncompatibleClocksException(Clock first, Clock second) {
        super("Incompatible clocks: tried to compare a clock implemented by " + first.getClass() + " with another " +
                "implemented by " + second.getClass() + ".");
    }
}
