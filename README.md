Author Pablo Perez Garcia 

# ReactiveX

![My image](src/main/resources/img/flatMap.png)

Marble diagrams are not clear enough?.

Here we cover with some practical examples, the most common use of the ReactiveX platform for Java.

To reference to the real documentation go here https://github.com/ReactiveX/RxJava/wiki

RxScala examples [here](https://github.com/politrons/reactiveScala)

 ![My image](src/main/resources/img/rsz_reactive-extensions.png)

All the most common features of Observable

* **Contactable**

    ![My image](src/main/resources/img/rsz_publishconnectc.png)
    * [HotObservable](src/test/java/rx/observables/connectable/HotObservable.java)
 
* **Combining**

    ![My image](src/main/resources/img/rsz_1zipo.png)
    * [Chain](src/test/java/rx/observables/combining/ObservableChain.java)
    * [Concat](src/test/java/rx/observables/combining/ObservableConcat.java)
    * [Merge](src/test/java/rx/observables/combining/ObservableMerge.java)
    * [Zip](src/test/java/rx/observables/combining/ObservableZip.java)
    * [Switch](src/test/java/rx/observables/combining/ObservableSwitch.java)

* **Creating**

    ![My image](src/main/resources/img/rsz_1createc.png)
    * [Create](src/test/java/rx/observables/creating/ObservableCreate.java)
    * [Defer](src/test/java/rx/observables/creating/ObservableDefer.java)
    * [Interval](src/test/java/rx/observables/creating/ObservableInterval.java)
    * [Subscription](src/test/java/rx/observables/creating/ObservableSubscription.java)
    
* **Filtering**

    ![My image](src/main/resources/img/rsz_1filter.png)
    * [Debounce](src/test/java/rx/observables/filtering/ObservableDebounce.java)
    * [Distinct](src/test/java/rx/observables/filtering/ObservableDistinct.java)
    * [Skip](src/test/java/rx/observables/filtering/ObservableSkip.java)
    * [Take](src/test/java/rx/observables/filtering/ObservableTake.java)
    * [First](src/test/java/rx/observables/filtering/ObservableFirst.java)


* **Transforming**

    ![My image](src/main/resources/img/rsz_flatmap.png)
    * [Map](src/test/java/rx/observables/transforming/ObservableMap.java)
    * [FlatMap](src/test/java/rx/observables/transforming/ObservableFlatMap.java)
    * [GroupBy](src/test/java/rx/observables/transforming/ObservableGroupBy.java)
    * [Scan](src/test/java/rx/observables/transforming/ObservableScan.java)
    * [Collect](src/test/java/rx/observables/transforming/ObservableCollect.java)
    * [Buffer](src/test/java/rx/observables/transforming/ObservableBuffer.java)
    * [Window](src/test/java/rx/observables/transforming/ObservableWindow.java)
    * [Compose](src/test/java/rx/observables/transforming/ObservableCompose.java)

* **Scheduler**

    ![My image](src/main/resources/img/rsz_2subscribeonc.png)
    * [Asynchronous](src/test/java/rx/observables/scheduler/ObservableAsynchronous.java)
    
* **Errors**

    ![My image](src/main/resources/img/rsz_2subscribeonc.png)
    * [Exceptions](src/test/java/rx/observables/errors/ObservableExceptions.java)
        
* **Utils**
    * [Delay](src/test/java/rx/observables/utils/ObservableDelay.java)
    * [AmbConditional](src/test/java/rx/observables/utils/ObservableAmbConditional.java)
    * [Cache](src/test/java/rx/observables/utils/ObservableCache.java)
    * [ToBlocking](src/test/java/rx/observables/utils/ObservableToBlocking.java)

## Single

An Observable that just emit 1 item through the pipeline.

* [SingleFeatures](src/test/java/rx/single/SingleFeatures.java)

## Relay

A subject which subscribe observers and keep the pipeline open all the time.

* [Relay](src/test/java/rx/relay/Relay.java)

##  ![My image](src/main/resources/img/reactor.png) Spring Reactor 

The reactive stream API implementation of Spring.

![My image](src/main/resources/img/rsz_1createc.png)
* [Creating](src/test/java/reactor/ReactorCreating.java)

![My image](src/main/resources/img/rsz_1zipo.png)
* [Combining](src/test/java/reactor/ReactorCombining.java)

![My image](src/main/resources/img/rsz_flatmap.png)
* [Transforming](src/test/java/reactor/ReactorTransforming.java)

![My image](src/main/resources/img/rsz_1filter.png)
* [Filtering](src/test/java/reactor/ReactorFiltering.java)
    
![My image](src/main/resources/img/rsz_2subscribeonc.png)
* [Async](src/test/java/reactor/ReactorAsync.java)

## Observer V Iterator Pattern

An explanation, comparative and benchmark between these two patterns.

* [ObserverVsIterator](src/test/java/rx/utils/ObserverVsIterator.java)

## RxJava V Spring Reactor

A Comparative and benchmark between these two frameworks.

* [ReactorVsRx](src/test/java/rx/utils/ReactorVsRx.java)

## Java 8

![My image](src/main/resources/img/rsz_stream.jpg)

Stream API, Functions and Promises with examples of how to use it.

* [Stream](src/test/java/java8/StreamUtils.java)
* [Functions](src/test/java/java8/Functions.java)
* [CompletableFuture](src/test/java/java8/CompletableFutureFeature.java)


## Java 9

![My image](src/main/resources/img/java-9.png)

The most important features with particle examples of how to use it.

* [Flow](src/test/java/java9/FlowFeatures.java)
* [Features](src/test/java/java9/UtilFeatures.java)
* [Optional](src/test/java/java9/OptionalImprovements.java)
* [Module system](src/test/java/module-info.java.bak)

## Software craftsmanship

* [(S)ingle responsibility principle](src/test/java/good_practices/SRP.java)
* [(O)pen/Closed principle](src/test/java/good_practices/OpenClosedPrinciple.java)
* [(L)iskov substitution principle](src/test/java/good_practices/LiskovSubstitutionPrinciple.java)
* [(I)nterface segregation principle](src/test/java/good_practices/InterfaceSegregationPrinciple.java)
* [(D)on't repeat yourself](src/test/java/good_practices/DRY.java)
