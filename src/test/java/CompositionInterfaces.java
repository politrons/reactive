import io.vavr.collection.List;
import org.junit.Test;

public class CompositionInterfaces {

    interface StateWriter {
        public void write(String value);
    }

    class StateWriterImpl implements StateWriter {

        @Override
        public void write(String value) {

        }
    }

    static public abstract class A {

        public StateWriter STATE_WRITER;

        abstract public int hello_world();

        void sendReport(String report) {
            STATE_WRITER.write(report);
        }

        void setStateWriter(StateWriter stateWriter) {
            this.STATE_WRITER = stateWriter;
        }
    }

    static public class AImpl extends A {

        @Override
        public int hello_world() {
            sendReport("RECEIVED");
            return 0;
        }
    }

    @Test
    public void composeInterface() {

        List.of(1,1,1,3,4).forEach(d -> System.out.println(d));

        A a = new AImpl();

        //Once Service provider load the Handler class, we will set KafkaStateWriter
        a.setStateWriter(new StateWriterImpl());
        a.hello_world();
    }

}
