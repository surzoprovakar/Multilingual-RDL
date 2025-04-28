package generic.concurrency;

import CRDT.opBased.generic.exceptions.IncompatibleClocksException;
import generic.operations.GenericOperationInterface;

import java.util.Arrays;

//For more information on vector clocks check the paper:
//Colin J. Fidge (February 1988). "Timestamps in Message-Passing Systems That Preserve the Partial Ordering" (PDF).
//In K. Raymond (Ed.). Proc. of the 11th Australian Computer Science Conference (ACSC'88). pp. 56–66.

//Note that this class contains some useful methods that aren't offered by history, as they are requirements of a
//vector clock and not of the CRDT using this. As such, when implementing a distributed system, it may be desired to
//store the VectorClock as class instead of History interface.
//The VectorClock doubles down as both a concurrency/happens-before detector (requested by history) and a total order
//provider (requested by Clock). It is recommended to use the same vector clock for both, due to efficiency and easiness of use.
public class VectorClock implements History, Clock, Cloneable {

    //Indices: IDs of the replicas
    private int[] clock;

    public VectorClock(int[] clock) {
        this.clock = clock;
    }

    public int[] getClock() {
        return clock;
    }

    @Override
    public boolean isHappensBefore(GenericOperationInterface otherOp) {
        //op < otherOp
        boolean foundHigher = false;
        int[] otherClock = ((VectorClock)(otherOp.getHistory())).getClock();
        for (int i = 0; i < clock.length; i++) {
            if (clock[i] > otherClock[i])
                return false;
            if (clock[i] < otherClock[i])
                foundHigher = true;
        }
        //Clocks may be equal (i.e., same operation)
        return foundHigher;
    }

    @Override
    public boolean isConcurrent(GenericOperationInterface otherOp) {
        boolean foundHigher = false;
        boolean foundLower = false;
        int[] otherClock = ((VectorClock)(otherOp.getHistory())).getClock();
        for (int i = 0; i < clock.length; i++) {
            if (clock[i] > otherClock[i])
                foundLower = true;
            if (clock[i] < otherClock[i])
                foundHigher = true;
            if (foundLower && foundHigher)
                return true;
        }
        return false;
    }

    @Override
    public boolean isHappensAfter(GenericOperationInterface otherOp) {
        boolean foundLower = false;
        int[] otherClock = ((VectorClock)(otherOp.getHistory())).getClock();
        for (int i = 0; i < clock.length; i++) {
            if (clock[i] < otherClock[i])
                return false;
            if (clock[i] > otherClock[i])
                foundLower = true;
        }
        //Clocks may be equal (i.e., same operation)
        return foundLower;
    }

    @Override
    public Clock clone() {
        try {
            VectorClock newClock = (VectorClock) super.clone();
            newClock.clock = Arrays.copyOf(this.clock, this.clock.length);
            return newClock;
        }
        catch (CloneNotSupportedException e) {
            //Should never happen
            return null;
        }
    }

    /**
     * Increments the position specified by <b>pos</b>. Usually, <b>pos</b> corresponds to the clock owner position.
     * @param pos - the position to increment
     */
    public void increment(int pos) {
        clock[pos]++;
    }

    /**
     * Updates the local clock entries with the one received as argument, according to rules defined by Colin J. Fidge
     * @param otherClock: the clock used to update
     * @param senderId: the id of the replica that sent this VectorClock
     * @throws IncompatibleClocksException - if otherClock isn't an instance of VectorClock
     */
    public void update(Clock otherClock, int senderId) {
        if (!(otherClock instanceof VectorClock))
            throw new IncompatibleClocksException(this, otherClock);
        int[] otherClockArray = ((VectorClock)otherClock).getClock();
        clock[senderId] = Math.max(clock[senderId], otherClockArray[senderId] + 1);
        for (int i = 0; i < clock.length; i++)
            clock[i] = Math.max(clock[i], otherClockArray[i]);
    }

    /**
     * Compares two VectorClocks.
     * For concurrent operations, each entry is compared. The total order is defined as so: the clock with the smallest
     * value in the first non-equal entry is considered as having happened-before.
     * Example: this = [2, 3, 1]; o = [3, 3, 0] => returns -1, since this[0] < o[0].
     *          this = [2, 4, 2]; o = [1, 6, 3] => returns 1, since o[0] < this[0].
     *          this = [3, 1, 2]; o = [3, 1, 4] => returns -1, since this[0] = o[0], this[1] = o[1] and this[2] < o[2].
     * @param o - the clock to compare to
     * @throws IncompatibleClocksException - if <b>o</b> isn't an instance of VectorClock or the clocks are incomparable
     * (e.g., one is a clock for 3 replicas and the other is for 5).
     * @return -1 if <b>this</b> happened-before <b>o</b> or the total order decides so;
     * 1 if <b>o</b> happened-before <b>this</b> or the total order decides so; 0 if they have the same clock values.
     */
    @Override
    public int compareTo(Clock o) {
        if (!(o instanceof VectorClock))
            throw new IncompatibleClocksException(this, o);

        int[] otherClock = ((VectorClock) o).getClock();
        if (otherClock.length != clock.length)
            throw new IncompatibleClocksException(this, o);

        for (int i = 0; i < clock.length; i++) {
            if (clock[i] < otherClock[i])
                return -1;
            if (clock[i] > otherClock[i])
                return 1;
            //Same value for that entry, keep searching
        }
        //All entries are equivalent.
        return 0;
    }

    @Override
    public String toString() {
        StringBuilder res = new StringBuilder();
        res.append("[");
        for (int i = 0; i < clock.length - 1; i++)
            res.append(clock[i]).append(", ");
        res.append(clock[clock.length - 1]);
        res.append("]");
        return res.toString();
    }
}
