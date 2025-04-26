// ChronicleMapMemoryBreakdown.java
import net.openhft.chronicle.map.ChronicleMap;
import net.openhft.chronicle.map.ChronicleMapBuilder;
import net.openhft.chronicle.map.VanillaChronicleMap;
import org.openjdk.jol.info.ClassLayout;          // only used for the tiny helper-arrays’ size
import java.nio.charset.StandardCharsets;
import java.util.stream.IntStream;

public class ChronicleMapMemoryBreakdown {

    private static final int N = 1_000_000; // number of entries

    public static void main(String[] args) throws Exception {

        /* 1. Build an in-memory Chronicle Map, off-heap by default */
        ChronicleMap<String, String> cmap = ChronicleMap
                .of(String.class, String.class)
                .name("offheap-map")
                .entries(N)
                .averageKey("k123456")          // helps Chronicle pre-size the buckets
                .averageValue("v123456")
                .create();

        /* 2. Insert the data */
        IntStream.range(0, N)
                .forEach(i -> cmap.put("k" + i, "v" + i));

        /* 3. Force a GC so the JVM’s own bookkeeping noise is minimal     *
         *    (Chronicle’s memory lives outside the heap, so GC doesn’t    *
         *    touch *those* bytes, but it keeps the demo comparable).      */
        System.gc();
        Thread.sleep(200);

        /* 4. Measure off-heap usage reported by Chronicle                 *
         *    (this is native memory, not visible to JOL or Runtime).      */
        long offHeapBytes = Math.abs(cmap.offHeapMemoryUsed());            // <-- key metric

        /* 5. Optional “logical” data size: rough UTF-8 length of every     *
         *    key + value. This gives you a feel for payload vs. overhead. */
        long logicalBytes = IntStream.range(0, N)
                .mapToLong(i -> ("k" + i).getBytes(StandardCharsets.UTF_8).length
                        + ("v" + i).getBytes(StandardCharsets.UTF_8).length)
                .sum();

        /* add the cost of the two tiny helper arrays so the comparison    *
         * to HashMap version stays apples-to-apples                        */
        Object[] keysArray   = new Object[0];
        Object[] valuesArray = new Object[0];
        logicalBytes += ClassLayout.parseInstance(keysArray).instanceSize();
        logicalBytes += ClassLayout.parseInstance(valuesArray).instanceSize();

        long overheadBytes = Math.abs(offHeapBytes - logicalBytes);
        int  overheadPerEntry = (int) (overheadBytes / cmap.size());

        /* 6. Print results (both raw bytes and human-friendly MiB) */
        System.out.printf("Total off-heap...............: %d bytes  (%.2f MiB)%n",
                offHeapBytes, offHeapBytes / 1024.0 / 1024.0);
        System.out.printf("Logical data (UTF-8).........: %d bytes  (%.2f MiB)%n",
                logicalBytes, logicalBytes / 1024.0 / 1024.0);
        System.out.printf("Structural overhead..........: %d bytes  (%.2f MiB)%n",
                overheadBytes, overheadBytes / 1024.0 / 1024.0);
        System.out.printf("Average overhead per entry...: %d bytes%n",
                overheadPerEntry);

        cmap.close();  // always close to release the native memory
    }
}
