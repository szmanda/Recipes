 package com.example.recipes

 import androidx.lifecycle.ViewModel
 import kotlinx.coroutines.flow.MutableStateFlow
 import kotlinx.coroutines.flow.StateFlow
 import kotlinx.coroutines.flow.asStateFlow
 import kotlinx.coroutines.flow.update
 import java.time.LocalDateTime
 import java.time.Duration
 import java.time.Duration.*

 data class TimerUiState(
     val remainingTime: Duration = Duration.ofSeconds(10),
     val isRunning: Boolean = false,
     val isSet: Boolean = true,
     val lastUpdatedAt: LocalDateTime = LocalDateTime.now(),
 )

 /// ## ViewModel (part of Android Jetpack)
 /// https://developer.android.com/topic/libraries/architecture/viewmodel
 ///
 /// The ViewModel class is a business logic or screen level state holder.
 /// It exposes state to the UI and encapsulates related business logic.
 /// Its principal advantage is that it caches state and persists it through
 /// configuration changes. This means that your UI doesn’t have to fetch data
 /// again when navigating between activities, or following configuration changes,
 /// such as when rotating the screen.
 class TimerViewModel : ViewModel() {

     // Expose screen UI state
     private val _uiState = MutableStateFlow(TimerUiState())
     var uiState: StateFlow<TimerUiState> = _uiState.asStateFlow()

     fun setTimer(duration: Duration) {
         _uiState.update { currentState ->
             currentState.copy(
                 remainingTime = duration,
                 isSet = true,
             )
         }
     }
     fun startTimer() {
         _uiState.update { currentState ->
             currentState.copy(
                 isRunning = true,
                 lastUpdatedAt = LocalDateTime.now(),
             )
         }
     }
     fun pauseTimer() {
         _uiState.update { currentState ->
             currentState.copy(
                 isRunning = false,
             )
         }
     }
     fun resetTimer() {
         _uiState.update { currentState ->
             currentState.copy(
                 isRunning = false,
                 isSet = false,
                 remainingTime = Duration.ZERO,
             )
         }
     }
     fun updateTimer() {
         if (!_uiState.value.isRunning) return
         if (_uiState.value.remainingTime <= Duration.ZERO) {
             _uiState.update { currentState ->
                 currentState.copy(
                     isRunning = false,
                     remainingTime = Duration.ZERO,
                 )
             }
             return
         }
         // decrease time
         _uiState.update { currentState ->
             currentState.copy(
                 remainingTime = currentState.remainingTime - between(currentState.lastUpdatedAt, LocalDateTime.now()),
                 lastUpdatedAt = LocalDateTime.now(),
             )
         }
     }
 }