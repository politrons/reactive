Author Pablo Perez Garcia 

# ReactiveX

![My image](src/main/resources/img/flatMap.png)

Marble diagrams are not clear enough?.

Here we cover with some practical examples, the most common use of the ReactiveX platform for Java.

This project it´s just a complement to play around and put in practice what I´ve learned in the platform.

To reference to the real documentation go here https://github.com/ReactiveX/RxJava/wiki

RxScala examples [here](https://github.com/politrons/reactiveScala)

 ![My image](src/main/resources/img/rsz_reactive-extensions.png)

All the most common features of Observable

* **Contactable**

    ![My image](src/main/resources/img/rsz_publishconnectc.png)
    * [HotObservable] (src/test/java/rx/observables/connectable/HotObservable.java)
 
* **Combining**

    ![My image](src/main/resources/img/rsz_1zipo.png)
    * [Chain] (src/test/java/rx/observables/combining/ObservableChain.java)
    * [Concat] (src/test/java/rx/observables/combining/ObservableConcat.java)
    * [Merge] (src/test/java/rx/observables/combining/ObservableMerge.java)
    * [Zip] (src/test/java/rx/observables/combining/ObservableZip.java)
    * [Switch] (src/test/java/rx/observables/combining/ObservableSwitch.java)

* **Creating**

    ![My image](src/main/resources/img/rsz_1createc.png)
    * [Create] (src/test/java/rx/observables/creating/ObservableCreate.java)
    * [Defer] (src/test/java/rx/observables/creating/ObservableDefer.java)
    * [Interval] (src/test/java/rx/observables/creating/ObservableInterval.java)
    * [Subscription] (src/test/java/rx/observables/creating/ObservableSubscription.java)
    
* **Filtering**

    ![My image](src/main/resources/img/rsz_1filter.png)
    * [Debounce] (src/test/java/rx/observables/filtering/ObservableDebounce.java)
    * [Distinct] (src/test/java/rx/observables/filtering/ObservableDistinct.java)
    * [Skip] (src/test/java/rx/observables/filtering/ObservableSkip.java)
    * [Take] (src/test/java/rx/observables/filtering/ObservableTake.java)

* **Transforming**

    ![My image](src/main/resources/img/rsz_flatmap.png)
    * [Map] (src/test/java/rx/observables/transforming/ObservableMap.java)
    * [FlatMap] (src/test/java/rx/observables/transforming/ObservableFlatMap.java)
    * [GroupBy] (src/test/java/rx/observables/transforming/ObservableGroupBy.java)
    * [Scan] (src/test/java/rx/observables/transforming/ObservableScan.java)
    * [Collect] (src/test/java/rx/observables/transforming/ObservableCollect.java)
    * [Buffer] (src/test/java/rx/observables/transforming/ObservableBuffer.java)
    * [Window] (src/test/java/rx/observables/transforming/ObservableWindow.java)
    * [Compose] (src/test/java/rx/observables/transforming/ObservableCompose.java)

* **Scheduler**

    ![My image](src/main/resources/img/rsz_2subscribeonc.png)
    * [Asynchronous] (src/test/java/rx/observables/scheduler/ObservableAsynchronous.java)
    
* **Errors**

    ![My image](src/main/resources/img/rsz_2subscribeonc.png)
    * [Exceptions] (src/test/java/rx/observables/errors/ObservableExceptions.java)
        
* **Utils**
    * [Delay] (src/test/java/rx/observables/utils/ObservableDelay.java)
    * [AmbConditional] (src/test/java/rx/observables/utils/ObservableAmbConditional.java)
    * [Cache] (src/test/java/rx/observables/utils/ObservableCache.java)
    * [ToBlocking] (src/test/java/rx/observables/utils/ObservableToBlocking.java)


### Single

An Observable that just emit 1 item through the pipeline.

* [SingleFeatures] (src/test/java/rx/single/SingleFeatures.java)

### Relay

A subject which subscribe observers and keep the pipeline open all the time.

* [Relay] (src/test/java/rx/relay/Relay.java)

### Observer V Iterator Pattern

An explanation, comparative and benchmark between these two patterns.

* [ObserverVsIterator] (src/test/java/rx/utils/ObserverVsIterator.java)

### RxJava V Spring Reactor

A Comparative and benchmark between these two frameworks.

* [ReactorVsRx] (src/test/java/rx/utils/ReactorVsRx.java)

### Java 8

![My image](src/main/resources/img/rsz_stream.jpg)

The Java 8 Stream API with examples of most useful operators.

* [Stream] (src/test/java/stream/StreamUtils.java)

The Java 8 Functions with particle examples of how to use it.

* [Functions] (src/test/java/stream/Functions.java)


