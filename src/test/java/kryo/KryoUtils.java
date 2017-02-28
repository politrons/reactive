package kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import static junit.framework.TestCase.assertTrue;

public class KryoUtils {

    private Kryo kryo = new Kryo();

    @Test
    public void serializeDeserialize() throws FileNotFoundException {
        serializeObject();
        assertTrue(getDeserializeObject() != null);
    }

    private void serializeObject() throws FileNotFoundException {
        Output output = new Output(new FileOutputStream("file.bin"));
        SomeClass someObject = new SomeClass("a","b");
        kryo.writeObject(output, someObject);
        output.close();
    }

    private SomeClass getDeserializeObject() throws FileNotFoundException {
        Input input = new Input(new FileInputStream("file.bin"));
        SomeClass someObject = kryo.readObject(input, SomeClass.class);
        input.close();
        return someObject;
    }

}
