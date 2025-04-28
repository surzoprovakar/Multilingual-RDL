package generic.concurrency;

import generic.operations.GenericOperationInterface;

import java.io.Serializable;
import java.util.function.BiFunction;

public interface Policy<T extends GenericOperationInterface> extends BiFunction<T, T, Boolean>, Serializable {

    String getName();

}
