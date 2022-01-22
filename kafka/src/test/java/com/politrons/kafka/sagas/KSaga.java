package com.politrons.kafka.sagas;

import io.vavr.Function0;
import io.vavr.control.Try;

import java.util.function.Consumer;

public class KSaga {

    public static void main(String[] args) {
        KSaga.withAction(() -> "my local transaction")
                .withCompensation(()-> "revert transaction")
                .withActionChannel()
                .withCompensationChannel()
                .build();
    }

    public static <T> Action withAction(Function0<T> action) {
        return new Action(action);
    }

    record Action<T>(Function0<T> function) {

        public Compensation withCompensation(Function0<T> compensation) {
            return new Compensation(this, compensation);
        }
    }

    record Compensation<T>(Action<T> action,Function0<T> compensation ) {
        public ActionChannel withActionChannel(Consumer<byte[]> outputMessage) {
            return new ActionChannel(this, outputMessage);
        }
    }

    record ActionChannel<T>(Compensation<T> tCompensation, Consumer<byte[]> outputMessage) {

        public CompensationChannel withCompensationChannel() {
            return new CompensationChannel(this);
        }
    }

    record CompensationChannel<T>(Compensation<T> compensation) {
        public void build() {
            Try.of(compensation.action.function::apply);
        }
    }


}
