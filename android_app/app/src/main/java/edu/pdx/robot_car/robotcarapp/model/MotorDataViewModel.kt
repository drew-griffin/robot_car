package edu.pdx.robot_car.robotcarapp.model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MotorDataViewModel: ViewModel() {

    // These are examples of variables we might use in the view model
    // Basically any variable which we need to be global across fragments
    private val _MQTT_username = MutableLiveData<String>()
    val MQTT_username: LiveData<String> = _MQTT_username

    private val _motor1_speed = MutableLiveData<Double>()
    val motor1_speed: LiveData<Double> = _motor1_speed

    private val _motor1_position = MutableLiveData<String>()
    val motor1_position: LiveData<String> = _motor1_position

    private val _upCounter = MutableLiveData<Int>(5)
    val upCounter: LiveData<Int> = _upCounter

    private val _upCounterString = MutableLiveData<String>()
    val upCounterString: LiveData<String> = _upCounterString

    private val _downCounter = MutableLiveData<Int>(5)
    val downCounter: LiveData<Int> = _downCounter
    private val _rightCounter = MutableLiveData<Int>(5)
    val rightCounter: LiveData<Int> = _rightCounter
    private val _leftCounter = MutableLiveData<Int>(5)
    val leftCounter: LiveData<Int> = _leftCounter

    // This is an example of a method the ViewModel can do
    // Basically any interaction with or manipulation of the data that doesn't have to do with the UI
    fun setSpeed(RPM: Double) {
        _motor1_speed.value = RPM
    }

    fun incrementCounter(position: Int){
        when(position){
            0 -> {
                _upCounter.value?.plus(5)
                _upCounterString.value = "Up Count: $_upCounter.value.toString()"
                Log.d("MotorDataViewModel", "Up Counter: ${_upCounter.value}")
            }
            1 -> {
                _rightCounter.value?.plus(5)
                Log.d("MotorDataViewModel", "Right Counter: ${_rightCounter.value}")
            }
            2 -> _downCounter.value?.plus(5)
            3 -> {
                _leftCounter.value?.plus(5)
                Log.d("MotorDataViewModel", "Left Counter: ${_leftCounter.value}")
            }
        }
    }

}