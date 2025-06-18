package reactive;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DatabaseBenchmark {
    // PostgreSQL connection settings
    private static final String POSTGRES_URL      = "jdbc:postgresql://localhost:5432/your_database";
    private static final String POSTGRES_USER     = "your_user";
    private static final String POSTGRES_PASSWORD = "your_password";

    // TigerBeetle connection settings
    // (See notes below—there’s no official JDBC driver yet, so this URL won’t work
    // unless you install a third-party driver that registers itself with DriverManager)
    private static final String TIGERBEETLE_URL      = "jdbc:tigerbeetle://localhost:3000/tigerbeetle";
    private static final String TIGERBEETLE_USER     = "your_user";
    private static final String TIGERBEETLE_PASSWORD = "your_password";

    public static void main(String[] args) throws Exception {
//        System.out.println("Benchmarking PostgreSQL:");
//        runBenchmark(POSTGRES_URL, POSTGRES_USER, POSTGRES_PASSWORD);

        System.out.println("Benchmarking TigerBeetle:");
        runBenchmark(TIGERBEETLE_URL, TIGERBEETLE_USER, TIGERBEETLE_PASSWORD);
    }

    private static void runBenchmark(String url, String user, String password) throws Exception {
        //
        // 1) Ensure the driver class is loaded. For PostgreSQL this is:
        Class.forName("org.postgresql.Driver");
        //
        //  If you ever obtain a JDBC driver for TigerBeetle, you’d do:
        //    Class.forName("com.some.vendor.tigerbeetle.Driver");
        //

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            // WRITE BENCHMARK
            long startWrite = System.currentTimeMillis();
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO test_table (data) VALUES (?)")) {
                for (int i = 0; i < 10_000; i++) {
                    ps.setString(1, "Test Data " + i);
                    ps.executeUpdate();
                }
            }
            long writeTime = System.currentTimeMillis() - startWrite;
            System.out.println("Write time: " + writeTime + " ms");

            // READ BENCHMARK
            long startRead = System.currentTimeMillis();
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT data FROM test_table")) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        rs.getString("data");
                    }
                }
            }
            long readTime = System.currentTimeMillis() - startRead;
            System.out.println("Read time: " + readTime + " ms");
        }
    }
}
