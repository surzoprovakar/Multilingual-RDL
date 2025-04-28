import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class CounterTest {

    @Test
    public void testCounter() {
        Counter counter1 = new Counter(1);
        Counter counter2 = new Counter(2);

        counter1.Inc();
        counter1.Inc();

        counter2.Inc();
        counter2.Inc();
        counter2.Inc();

        counter1.Dec();
        counter2.Dec();

        System.out.println(counter1.Print());
        System.out.println(counter2.Print());

        Assertions.assertEquals(1, counter1.GetValue(), "Expected counter1 value to be 1");
        Assertions.assertEquals(2, counter2.GetValue(), "Expected counter2 value to be 2");

        // Simulating marshalling and unmarshalling
        byte[] b1 = counter1.ToMarshal();
        byte[] b2 = counter2.ToMarshal();

        Pair<Integer, List<String>> data1 = Counter.FromMarshalData(b1);
        Pair<Integer, List<String>> data2 = Counter.FromMarshalData(b2);

        Counter counter3 = new Counter(3);
        Counter counter4 = new Counter(4);

        counter3.Merge(data1.first, data1.second);
        counter4.Merge(data2.first, data2.second);

        System.out.println(counter3.Print()); // Expected: Counter_3:1
        System.out.println(counter4.Print()); // Expected: Counter_4:2

        Assertions.assertEquals(counter1.GetValue(), counter3.GetValue());
        Assertions.assertEquals(counter2.GetValue(), counter4.GetValue());
    }

    public static void main(String[] args) {
        org.junit.platform.console.ConsoleLauncher.main(new String[] {
                "--select-class=CounterTest"
        });
    }
}
