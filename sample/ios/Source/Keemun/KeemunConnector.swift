import Foundation
import FeatureCounter

final class KeemunObservableStore<ViewState : AnyObject, Msg : AnyObject>: ObservableObject {
    
    private let nativeConnector: KeemunNativeFeatureConnector<ViewState, Msg>
    @Published private(set) var viewState: ViewState
    
    init(nativeConnector: KeemunNativeFeatureConnector<ViewState, Msg>) {
        self.nativeConnector = nativeConnector
        self.viewState = self.nativeConnector.nativeState.value
        self.nativeConnector.render(collector: { [weak self] state in self?.viewState = state })
    }
    
    func dispatch(_ msg: Msg) {
        self.nativeConnector.dispatch(msg: msg)
    }
    
    public func syncDispatch(_ msg: Msg) async throws {
        let _ = try await suspendFunction(for: nativeConnector.nativeSyncDispatch(msg: msg))
    }
}
