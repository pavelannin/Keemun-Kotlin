import SwiftUI
import FeatureCounter

@main
struct KeemunApp: App {
    private let counterFeatureScope = CounterFeatureScope()
    
	var body: some Scene {
		WindowGroup {
			CounterFeatureView(store: KeemunObservableStore(nativeConnector: counterFeatureScope.counter()))
		}
	}
}
