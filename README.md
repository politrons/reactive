Author Pablo Perez Garcia 

# ReactiveX

Marble diagrams are not clear enough?. ![My image](http://github.com/politrons/reactive/blob/master/src/main/resources/img/flatMap.png)

Here we cover with some practical examples, the most common use of the ReactiveX platform for Java.

This project it´s just a complement to play around and put in practice what I´ve learned in the platform.

To reference to the real documentation go here https://github.com/ReactiveX/RxJava/wiki


### Observables

All the most common features of Observable

* Contactable
    * [HotObservable] (src/test/java/rx/observables/connectable/HotObservable.java)

- Combining
    * [ObservableChain] (src/test/java/rx/observables/combining/ObservableChain.java)
    * [ObservableConcat] (src/test/java/rx/observables/combining/ObservableConcat.java)
    * [ObservableMerge] (src/test/java/rx/observables/combining/ObservableMerge.java)
    * [ObservableZip] (src/test/java/rx/observables/combining/ObservableZip.java)
    * [ObservableSwitch] (src/test/java/rx/observables/combining/ObservableSwitch.java)

* Creating
    * [ObservableDefer] (src/test/java/rx/observables/creating/ObservableDefer.java)
    * [ObservableInterval] (src/test/java/rx/observables/creating/ObservableInterval.java)
    * [ObservableSubscription] (src/test/java/rx/observables/creating/ObservableSubscription.java)

* Transforming
    * [ObservableMap] (src/test/java/rx/observables/transforming/ObservableMap.java)
    * [ObservableFlatMap] (src/test/java/rx/observables/transforming/ObservableFlatMap.java)
    * [ObservableGroupBy] (src/test/java/rx/observables/transforming/ObservableGroupBy.java)
    * [ObservableScan] (src/test/java/rx/observables/transforming/ObservableScan.java)
    * [ObservableBuffer] (src/test/java/rx/observables/transforming/ObservableBuffer.java)
    * [ObservableWindow] (src/test/java/rx/observables/transforming/ObservableWindow.java)
    * [ObservableCompose] (src/test/java/rx/observables/transforming/ObservableCompose.java)

* Scheduler
    * [ObservableAsynchronous] (src/test/java/rx/observables/scheduler/ObservableAsynchronous.java)

* Utils
    * [ObservableAmbConditional] (src/test/java/rx/observables/ObservableAmbConditional.java)
    * [ObservableCache] (src/test/java/rx/observables/ObservableCache.java)
    * [ObservableToBlocking] (src/test/java/rx/observables/ObservableToBlocking.java)


### Single

An Observable that just emit 1 item through the pipeline.

* [SingleFeatures] (src/test/java/rx/single/SingleFeatures.java)

### Relay

A subject which will subscribe observers and it will keep the pipeline open all the time.

* [Relay] (src/test/java/rx/relay/Relay.java)
