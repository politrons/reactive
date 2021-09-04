package akka;

import org.junit.Test;
import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

public class AkkaFeatures {


    @Test
    public void akkaFeatures() throws InterruptedException {
        ActorSystem<ActorOneMessage> hello = ActorSystem.create(ActorOne.create(), "actorOne");
        hello.tell(new ActorOneMessageOne("hello Akka world in Java"));
        Thread.sleep(60000);
    }

    public static class ActorOne {

        public static Behavior<ActorOneMessage> create() {
            return Behaviors.setup(ctx -> Behaviors.receive(ActorOneMessage.class)
                    .onMessage(ActorOneMessageOne.class, message -> processMessageOne(ctx, message))
                    .onMessage(ActorOneMessageTwo.class, ActorOne::processMessageTwo)
                    .build());
        }

        private static Behavior<ActorOneMessage> processMessageOne(ActorContext<ActorOneMessage> ctx,
                                                                   ActorOneMessageOne command) {
            System.out.println("Message receive outside Actor System world:" + command.value);
            ActorRef<ActorTwoMessage> actorTwo = ctx.spawn(ActorTwo.create(), "ActorTwo");
            System.out.println("Sending the message to ActorTwo in ActorSystem");
            actorTwo.tell(new ActorTwoMessage("", ctx.getSelf()));
            return Behaviors.same();
        }

        private static Behavior<ActorOneMessage> processMessageTwo(ActorOneMessageTwo message) {
            System.out.println("Response fom actorTwo received:" + message.value);
            return Behaviors.same();
        }
    }

    public static class ActorTwo extends AbstractBehavior<ActorTwoMessage> {

        public static Behavior<ActorTwoMessage> create() {
            return Behaviors.setup(ActorTwo::new);
        }

        private ActorTwo(ActorContext<ActorTwoMessage> context) {
            super(context);
        }

        @Override
        public Receive<ActorTwoMessage> createReceive() {
            return newReceiveBuilder().onMessage(ActorTwoMessage.class, this::onGreet).build();
        }

        private Behavior<ActorTwoMessage> onGreet(ActorTwoMessage command) {
            System.out.printf("Hello %s!%n", command.replyTo.path().name());
            System.out.println("Sending back message to ActorOne in ActorSystem");
            command.replyTo.tell(new ActorOneMessageTwo("Copy that buddy, hello!"));
            return this;
        }
    }

    interface ActorOneMessage {
    }

    public static final class ActorOneMessageOne implements ActorOneMessage {
        public final String value;

        public ActorOneMessageOne(String value) {
            this.value = value;
        }
    }

    public static final class ActorOneMessageTwo implements ActorOneMessage {
        public final String value;

        public ActorOneMessageTwo(String value) {
            this.value = value;
        }
    }

    public static final class ActorTwoMessage {
        public final String whom;
        public final ActorRef<ActorOneMessage> replyTo;

        public ActorTwoMessage(String whom, ActorRef<ActorOneMessage> replyTo) {
            this.whom = whom;
            this.replyTo = replyTo;
        }
    }


}
