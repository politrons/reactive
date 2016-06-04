# Reactive
Pablo Perez Garcia 

Project to experiment with ReactiveX API.

Here we will cover with examples the most common use of the ReactiveX platform.


### Observables

All the most common features of Observable

* [HotObservable] (src/test/java/rx/observables/HotObservable.java)
* [ObservableAmbConditional] (src/test/java/rx/observables/ObservableAmbConditional.java)
* [ObservableCache] (src/test/java/rx/observables/ObservableCache.java)
* [ObservableChain] (src/test/java/rx/observables/ObservableChain.java)
* [ObservableConcat] (src/test/java/rx/observables/ObservableConcat.java)
* [ObservableDefer] (src/test/java/rx/observables/ObservableDefer.java)
* [ObservableFlatMap] (src/test/java/rx/observables/ObservableFlatMap.java)
* [ObservableGroupBy] (src/test/java/rx/observables/ObservableGroupBy.java)
* [ObservableInterval] (src/test/java/rx/observables/ObservableInterval.java)
* [ObservableMerge] (src/test/java/rx/observables/ObservableMerge.java)
* [ObservableScan] (src/test/java/rx/observables/ObservableScan.java)
* [ObservableSubscribeOn] (src/test/java/rx/observables/ObservableSubscribeOn.java)
* [ObservableSubscription] (src/test/java/rx/observables/ObservableSubscription.java)
* [ObservableSwitch] (src/test/java/rx/observables/ObservableSwitch.java)
* [ObservableToBlocking] (src/test/java/rx/observables/ObservableToBlocking.java)
* [ObservableZip] (src/test/java/rx/observables/ObservableZip.java)


### Single

An Observable that just emit 1 item through the pipeline.

* [SingleFeatures] (src/test/java/rx/single/SingleFeatures.java)

### Relay

A subject which will subscribe observers and it will keep the pipeline open all the time.

* [Relay] (src/test/java/rx/relay/Relay.java)