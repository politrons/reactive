// HashMapNodeSize.java
import org.openjdk.jol.info.ClassLayout;


// HashMapMemoryBreakdown.java
import org.openjdk.jol.info.GraphLayout;
import org.openjdk.jol.info.ClassLayout;

import java.util.HashMap;

public class HashMapNodeSize {

    private static final int N = 1_000_000;  // number of insertions

    public static void main(String[] args) throws Exception {

        // 1. Fill the map
        var map = new HashMap<String, String>(N * 2);  // large capacity → avoids re-hashing
        for (int i = 0; i < N; i++) {
            map.put("k" + i, "v" + i);
        }

        // 2. Force a GC cycle to minimise background noise
        System.gc();
        Thread.sleep(200);  // brief pause to let the GC finish

        // 3. Total deep size of the entire HashMap structure + data
        long totalMapBytes = GraphLayout.parseInstance(map).totalSize();

        // 4. “Windows” pointing at every key and every value
        Object[] keys   = map.keySet().toArray();
        Object[] values = map.values().toArray();

        // 5. Deep size of all user data (keys + values)
        long dataBytes = GraphLayout.parseInstance(keys, values).totalSize();

        // 6. Optional refinement: subtract the tiny cost of the two window arrays themselves
        long keysArrBytes   = ClassLayout.parseInstance(keys).instanceSize();
        long valuesArrBytes = ClassLayout.parseInstance(values).instanceSize();
        dataBytes -= (keysArrBytes + valuesArrBytes);

        // 7. Structural overhead and average overhead per entry (integer)
        long overheadBytes    = totalMapBytes - dataBytes;
        long overheadPerEntry = overheadBytes / map.size();   // no decimals

        // 8. Results
        System.out.printf("Total HashMap...............: %,d bytes%n", totalMapBytes);
        System.out.printf("Data (keys + values)........: %,d bytes%n", dataBytes);
        System.out.printf("Structural overhead.........: %,d bytes%n", overheadBytes);
        System.out.printf("Average overhead per entry..: %d bytes%n", overheadPerEntry);
    }


}
