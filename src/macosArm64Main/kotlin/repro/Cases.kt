package repro

import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.time.DurationUnit
import kotlin.time.TimeSource

/**
 * Starts [count] coroutines that each collect a flow, then cancels them all
 * and returns how long the cancellation took in milliseconds.
 *
 * This is what happens when a screen closes and its viewModelScope is
 * cancelled. Every cancelled coroutine throws a CancellationException, and
 * Kotlin/Native implements every Kotlin throw as a C++ throw. When [combined]
 * is true each coroutine collects `combine(a, b)`, which throws again inside
 * the operator.
 */
fun cancelCoroutines(count: Int, combined: Boolean): Double = runBlocking {
    val a = MutableStateFlow(0)
    val b = MutableStateFlow(0)
    val parent = Job()
    repeat(count) {
        launch(parent, start = CoroutineStart.UNDISPATCHED) {
            if (combined) {
                combine(a, b) { x, y -> x + y }.collect {}
            } else {
                a.collect {}
            }
        }
    }

    val mark = TimeSource.Monotonic.markNow()
    parent.cancel()
    parent.join()
    mark.elapsedNow().toDouble(DurationUnit.MILLISECONDS)
}
