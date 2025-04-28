import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class MapCRDTTest {

    @Test
    public void testMapCRDT() {
        MapCRDT map1 = new MapCRDT(1);
        MapCRDT map2 = new MapCRDT(2);

        map1.Add("a", 1);
        map1.Add("b", 3);
        map1.Add("c", 5);

        map2.Add("p", 2);
        map2.Add("q", 4);
        map2.Add("r", 6);

        map1.Delete("d");
        map2.Delete("r");

        map1.Update("c", 7);
        map2.Update("r", 8);

        map1.Print();
        map2.Print();

        // Simulating marshalling and unmarshalling
        byte[] b1 = map1.ToMarshal();
        byte[] b2 = map2.ToMarshal();

        Pair<Integer, List<String>> data1 = MapCRDT.FromMarshalData(b1);
        Pair<Integer, List<String>> data2 = MapCRDT.FromMarshalData(b2);

        MapCRDT map3 = new MapCRDT(3);
        MapCRDT map4 = new MapCRDT(4);

        map3.Merge(data1.first, data1.second);
        map4.Merge(data2.first, data2.second);

        map3.Print();
        map4.Print();

        Assertions.assertEquals(map1.GetValues(), map3.GetValues());
        Assertions.assertEquals(map2.GetValues(), map4.GetValues());
    }

    public static void main(String[] args) {
        org.junit.platform.console.ConsoleLauncher.main(new String[] {
                "--select-class=CounterTest"
        });
    }
}
