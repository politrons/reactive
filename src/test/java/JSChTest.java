import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * Created by pabloperezgarcia on 11/05/2017.
 */
public class JSChTest {

    @Test
    public void sshCall() throws JSchException, IOException {
        JSch jsch = new JSch();
        Session session = jsch.getSession("pabloperezgarcia", "localhost", 22);
        session.setPassword("Politron1981!");
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);
        session.connect();

        ChannelExec channel = (ChannelExec) session.openChannel("exec");
        BufferedReader in = new BufferedReader(new InputStreamReader(channel.getInputStream()));
        channel.setCommand("docker ps -constantClass");
//        channel.setPty(true);
        channel.connect(1000);

        String msg = null;
        while ((msg = in.readLine()) != null) {
            System.out.println(msg);
        }

        channel.disconnect();
        session.disconnect();
    }

}
