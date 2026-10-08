package com.asura.logger.core.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.asura.logger.Log
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

/**
 * ============================================================================
 * MVI (Model-View-Intent) Core Architecture Contracts
 * ============================================================================
 *
 * MVI enforces a strictly Unidirectional Data Flow (UDF):
 *
 *   [ User Action / System Event ]
 *                 │
 *                 ▼
 *           [ UiIntent ]  (Sent from View to ViewModel)
 *                 │
 *                 ▼
 *         [ ViewModel / Reducer ] (Processes intent, invokes Domain UseCases)
 *           ┌─────┴───────────────┐
 *           ▼                     ▼
 *      [ UiState ]           [ UiEffect ] (One-time side effect)
 *     (Rendered by View)    (Handled by View, e.g., Toasts, Audio)
 */

/**
 * Marker interface for all UI States.
 *
 * Represents an immutable snapshot of everything needed to render a screen.
 * Every state change produces a new instance of [UiState].
 */
interface UiState

/**
 * Marker interface for all UI Intents.
 *
 * Represents any intention or action initiated by the user or the lifecycle
 * (e.g., clicking a button, entering text, or screen loading).
 */
interface UiIntent

/**
 * Marker interface for all UI Effects (Side-Effects).
 *
 * Represents single-fire events that should not be persisted in the state,
 * such as navigation, showing toasts/snackbars, or triggering alarms.
 */
interface UiEffect

/**
 * Abstract Base ViewModel that powers MVI communication.
 *
 * @param S The concrete [UiState] managed by this ViewModel.
 * @param I The concrete [UiIntent] accepted by this ViewModel.
 * @param E The concrete [UiEffect] emitted by this ViewModel.
 * @param initialState The initial state to start the screen with.
 */
abstract class MviViewModel<S : UiState, I : UiIntent, E : UiEffect>(
    initialState: S
) : ViewModel() {

    // Internal mutable state holder backing the public immutable StateFlow
    private val _state: MutableStateFlow<S> = MutableStateFlow(initialState)

    /**
     * Publicly observable StateFlow representing the current UI state.
     * The UI layer collects this to render widgets.
     */
    val state: StateFlow<S> = _state.asStateFlow()

    // Buffered channel to deliver one-shot side effects to subscribers
    private val _effect: Channel<E> = Channel(Channel.BUFFERED)

    /**
     * Flow of single-fire UI side effects.
     */
    val effect: Flow<E> = _effect.receiveAsFlow()

    /**
     * Helper accessor to read the latest state snapshot synchronously.
     */
    val currentState: S
        get() = _state.value

    /**
     * Entry point for UI layer to send user actions/intents into the ViewModel.
     *
     * @param intent The user intent to process.
     */
    fun sendIntent(intent: I) {
        Log.d("Received UI intent: $intent", tag = "MviViewModel_sendIntent()")
        handleIntent(intent)
    }

    /**
     * Subclasses must override this to handle incoming [UiIntent]s.
     * This is where business rules or domain use-cases are invoked.
     *
     * @param intent The intent to be handled.
     */
    protected abstract fun handleIntent(intent: I)

    /**
     * Reduces and updates the current state atomically.
     *
     * @param reducer Lambda taking the current state and returning the updated state.
     */
    protected fun setState(reducer: S.() -> S) {
        val oldState = _state.value
        val newState = oldState.reducer()
        _state.value = newState
        Log.d("State updated: oldState=$oldState -> newState=$newState", tag = "MviViewModel_setState()")
    }

    /**
     * Emits a one-time side effect to the view layer asynchronously.
     *
     * @param effect The side effect to deliver.
     */
    protected fun sendEffect(effect: E) {
        Log.i("Emitting side effect: $effect", tag = "MviViewModel_sendEffect()")
        viewModelScope.launch {
            _effect.send(effect)
        }
    }
}
