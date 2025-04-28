package tCRDT.register;

import generic.concurrency.Clock;
import generic.concurrency.History;
import generic.concurrency.Policy;
import tCRDT.TCRDT;

import java.util.Set;

public interface TRegisterInterface extends TCRDT<RegisterOperation> {

    RegisterOperation assign(History hist, Policy<RegisterOperation> hb, Policy<RegisterOperation> selfPolicy, Policy<RegisterOperation> otherPolicy, String value, Clock clk);

    Set<String> value();
}
