package io.github.pavelannin.keemun.swiftui.interop

import platform.Foundation.NSError
import platform.Foundation.NSLocalizedDescriptionKey

/**
 * Represents an error as NSError.
 *
 * The returned [NSError] has `KotlinException` as the [NSError.domain], `0` as the [NSError.code] and
 * the [NSError.localizedDescription] is set to the [Throwable.message].
 * The Kotlin throwable can be retrieved from the [NSError.userInfo] with the key `KotlinException`.
 */
class KotlinNativeError(throwable: Throwable) : NSError(
    "KotlinException",
    0,
    mapOf(
        "KotlinException" to throwable,
        NSLocalizedDescriptionKey to (throwable.message ?: "")
    ),
)

/** Converts a [Throwable] to a [KotlinNativeError]. */
@HiddenFromObjC
fun Throwable.asKotlinNativeError() = KotlinNativeError(throwable = this)
