package akka;

import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.AskPattern;
import akka.actor.typed.javadsl.Behaviors;
import org.junit.Test;

import java.time.Duration;
import java.util.concurrent.CompletionStage;

public class AkkaFeatures {


    /**
     * [Request/Response] pattern: Using [ActorSystem.create] we're able to create the ActorRef/ActorSystem.
     * Then using [tell] we inform the ActorOne which communicate to ActorTwo using this pattern.
     */
    @Test
    public void requestResponse() throws InterruptedException {
        ActorSystem<ActorOneMessage> actorRef = ActorSystem.create(ActorOne.create(), "actorOne");
        actorRef.tell(new RequestResponseMessage("actorRef Akka world in Java"));
        Thread.sleep(60000);
    }

    /**
     * [Ask pattern]: Using [ActorSystem.create] we're able to create the ActorRef/ActorSystem.
     * Then Using [AskPattern.ask] and passing the actorRef and the message we want to send, together
     * with a timeout of the communication and an actor Scheduler, in order to schedule an action in the actor
     * using a specific ExecutorContext, we obtain a future [CompletionStage] which we can implement callback or
     * block(only for test purpose)  until the response arrive
     */
    @Test
    public void askPattern() throws InterruptedException {
        ActorSystem<ActorOneMessage> actorRef = ActorSystem.create(ActorOne.create(), "actorOne");
        CompletionStage<String> future =
                AskPattern.ask(
                        actorRef,
                        replyTo -> new AskPatternMessage("Ask Pattern", replyTo),
                        Duration.ofSeconds(3),
                        actorRef.scheduler());

        future.whenComplete((response, error) -> {
            if (error != null) {
                System.out.println("Error in Ask pattern communication. Caused by " + error.getCause());
            }
            System.out.println("[Ask pattern] Message response:" + response);
        });
        Thread.sleep(5000);
    }

    public static class ActorOne {

        /**
         * Using [Behaviors.setup] is a factory that create a [AkkaContext] and pass to a function
         * to be implemented by us. With that function expect as output the [Behavior[T]] so we
         * just create one using again factory [Behaviors.receive(T)] where T is the interface/class that
         * we accept as message type input in Actor.
         * Then we use [onMessage] for each subtype message (in case specify interface) that we expect
         * to receive, and then we implement the function where as input is the message that we receive,
         * and we must return a [Behavior[T]]
         */
        public static Behavior<ActorOneMessage> create() {
            return Behaviors.setup(ctx -> Behaviors.receive(ActorOneMessage.class)
                    .onMessage(RequestResponseMessage.class, message -> processRequestResponseMessage(ctx, message))
                    .onMessage(ActorOneRequestResponseMessage.class, ActorOne::processRequestResponseFromAnotherActor)
                    .onMessage(AskPatternMessage.class, ActorOne::processAskPatternMessage)
                    .build());
        }

        /**
         * We create an ActorRef to which we want to send the message, and in the message itself,
         * we send our ActorRef specifying the message we accept(Akka Typed ;) ) to expect a response
         * once the ActorTwo process the message.
         */
        private static Behavior<ActorOneMessage> processRequestResponseMessage(ActorContext<ActorOneMessage> ctx,
                                                                               RequestResponseMessage command) {
            System.out.println("Message receive outside Actor System world:" + command.value);
            ActorRef<ActorTwoRequestResponseMessage> actorTwo = ctx.spawn(ActorTwo.create(), "ActorTwo");
            System.out.println("Sending the message to ActorTwo in ActorSystem");
            actorTwo.tell(new ActorTwoRequestResponseMessage("", ctx.getSelf()));
            return Behaviors.same();
        }

        /**
         * Here we just receive the response from the Actor we just invoke, previously in [processRequestResponseMessage]
         */
        private static Behavior<ActorOneMessage> processRequestResponseFromAnotherActor(ActorOneRequestResponseMessage message) {
            System.out.println("Response fom actorTwo received:" + message.value);
            return Behaviors.same();
        }

        private static Behavior<ActorOneMessage> processAskPatternMessage(AskPatternMessage message) {
            System.out.println("[Ask pattern] message from outside actors:" + message.value);
            message.replyTo.tell("Copy that, ask pattern success!");
            return Behaviors.same();
        }
    }

    public static class ActorTwo {

        public static Behavior<ActorTwoRequestResponseMessage> create() {
            return Behaviors.setup(ctx -> Behaviors.receive(ActorTwoRequestResponseMessage.class)
                    .onMessage(ActorTwoRequestResponseMessage.class, ActorTwo::processMessage)
                    .build());
        }

        private static Behavior<ActorTwoRequestResponseMessage> processMessage(ActorTwoRequestResponseMessage command) {
            System.out.printf("Hello %s!%n", command.replyTo.path().name());
            System.out.println("Sending back message to ActorOne in ActorSystem");
            command.replyTo.tell(new ActorOneRequestResponseMessage("Copy that buddy, hello!"));
            return Behaviors.same();
        }
    }

    interface ActorOneMessage {
    }

    public static final class RequestResponseMessage implements ActorOneMessage {
        public final String value;

        public RequestResponseMessage(String value) {
            this.value = value;
        }
    }

    public static final class ActorOneRequestResponseMessage implements ActorOneMessage {
        public final String value;

        public ActorOneRequestResponseMessage(String value) {
            this.value = value;
        }
    }

    public static final class AskPatternMessage implements ActorOneMessage {
        public final String value;
        public final ActorRef<String> replyTo;

        public AskPatternMessage(String whom, ActorRef<String> replyTo) {
            this.value = whom;
            this.replyTo = replyTo;
        }
    }

    public static final class ActorTwoRequestResponseMessage {
        public final String whom;
        public final ActorRef<ActorOneMessage> replyTo;

        public ActorTwoRequestResponseMessage(String whom, ActorRef<ActorOneMessage> replyTo) {
            this.whom = whom;
            this.replyTo = replyTo;
        }
    }


}
