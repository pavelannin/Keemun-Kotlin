import Foundation
import Dispatch
import FeatureCounter

func suspendFunction<Result : AnyObject>(for kotlinMativeSuspend: KotlinNativeSuspend<Result>) async throws -> Result {
    try await SuspendFunctionTask<Result>(kotlinMativeSuspend: kotlinMativeSuspend).await()
}

private class SuspendFunctionTask<Result : AnyObject>: @unchecked Sendable {
    private let kotlinMativeSuspend: KotlinNativeSuspend<Result>
    private var kotlinNativeCancellable: KotlinNativeCancellable?
    
    init(kotlinMativeSuspend: KotlinNativeSuspend<Result>) {
        self.kotlinMativeSuspend = kotlinMativeSuspend
    }
    
    func await() async throws -> Result {
        try await withTaskCancellationHandler {
            try await withUnsafeThrowingContinuation { continuation in
                self.kotlinNativeCancellable = self.kotlinMativeSuspend.run(
                    coroutineScope: KotlinNativeCoroutineScope.shared.default_,
                    result: { result in continuation.resume(returning: result) },
                    error: { error in continuation.resume(throwing: error) },
                    cancelled: { _ in continuation.resume(throwing: CancellationError())}
                )
            }
        } onCancel: {
            self.kotlinNativeCancellable?.cancel()
            self.kotlinNativeCancellable = nil
        }
    }
}
