import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class SetCRDTTest {

    @Test
    public void testSetCRDT() {
        SetCRDT set1 = new SetCRDT(1);
        SetCRDT set2 = new SetCRDT(2);

        set1.Add(1);
        set1.Add(3);
        set1.Add(5);

        set2.Add(2);
        set2.Add(4);
        set2.Add(6);

        set1.Remove(5);
        set2.Remove(8);

        set1.Print();
        set2.Print();

        // Simulating marshalling and unmarshalling
        byte[] b1 = set1.ToMarshal();
        byte[] b2 = set2.ToMarshal();

        Pair<Integer, List<String>> data1 = SetCRDT.FromMarshalData(b1);
        Pair<Integer, List<String>> data2 = SetCRDT.FromMarshalData(b2);

        SetCRDT set3 = new SetCRDT(3);
        SetCRDT set4 = new SetCRDT(4);

        set3.Merge(data1.first, data1.second);
        set4.Merge(data2.first, data2.second);

        set3.Print();
        set4.Print();

        Assertions.assertEquals(set1.GetValues(), set3.GetValues());
        Assertions.assertEquals(set2.GetValues(), set4.GetValues());
    }

    public static void main(String[] args) {
        org.junit.platform.console.ConsoleLauncher.main(new String[] {
                "--select-class=CounterTest"
        });
    }
}
