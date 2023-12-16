import Foundation
import Combine
import FeatureCounter

func publisher<Output>(for kotlinNativeStateFlow: KotlinNativeStateFlow<Output>) -> AnyPublisher<Output, Error> {
    return NativeStateFlowPublisher(kotlinNativeStateFlow: kotlinNativeStateFlow)
        .eraseToAnyPublisher()
}

private struct NativeStateFlowPublisher<Output : AnyObject>: Publisher {
    typealias Output = Output
    typealias Failure = Error
    
    private let kotlinNativeStateFlow: KotlinNativeStateFlow<Output>
    
    init(kotlinNativeStateFlow: KotlinNativeStateFlow<Output>) {
        self.kotlinNativeStateFlow = kotlinNativeStateFlow
    }
    
    func receive<S>(subscriber: S) where S : Subscriber, Failure == S.Failure, Output == S.Input {
        let subscription = NativeStateFlowSubscription(
            kotlinNativeStateFlow: self.kotlinNativeStateFlow,
            subscriber: subscriber
        )
        subscriber.receive(subscription: subscription)
    }
}

private class NativeStateFlowSubscription<Output : AnyObject, S: Subscriber>:
    Subscription where S.Input == Output,
                       S.Failure == Error {
    
    private let kotlinNativeStateFlow: KotlinNativeStateFlow<Output>
    private var kotlinNativeCancellable: KotlinNativeCancellable?
    private var subscriber: S?
    
    init(
        kotlinNativeStateFlow: KotlinNativeStateFlow<Output>,
        subscriber: S
    ) {
        self.kotlinNativeStateFlow = kotlinNativeStateFlow
        self.subscriber = subscriber
    }
    
    func request(_ demand: Subscribers.Demand) {
        self.kotlinNativeCancellable = self.kotlinNativeStateFlow.collect(
            coroutineScope: KotlinNativeCoroutineScope.shared.default_,
            collector: { item in
                guard let subscriber = self.subscriber else { return }
                let _ = subscriber.receive(item)
            },
            complete: { error in
                if let error = error {
                    self.subscriber?.receive(completion: .failure(error))
                } else {
                    self.subscriber?.receive(completion: .finished)
                }
            },
            cancelled: { error in self.subscriber?.receive(completion: .failure(error)) }
        )
    }
    
    func cancel() {
        self.subscriber = nil
        self.kotlinNativeCancellable?.cancel()
        self.kotlinNativeCancellable = nil
    }
}
