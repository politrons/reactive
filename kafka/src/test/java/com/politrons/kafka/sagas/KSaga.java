package com.politrons.kafka.sagas;

import io.vavr.Function0;

public class KSaga {

    public static void main(String[] args) {
        KSaga.withAction(() -> "my local transaction")
                .withCompensation()
                .withActionChannel()
                .withCompensationChannel()
                .build();
    }


    public static <T> Action withAction(Function0<T> action) {
        return new Action("");
    }

    record Action(String value) {

        public Compensation withCompensation() {
            return new Compensation();
        }
    }

    record Compensation() {
        public ActionChannel withActionChannel() {
            return new ActionChannel();
        }
    }

    record ActionChannel() {
        public CompensationChannel withCompensationChannel() {
            return new CompensationChannel();
        }
    }

    record CompensationChannel() {
        public void build() {

        }
    }


}
