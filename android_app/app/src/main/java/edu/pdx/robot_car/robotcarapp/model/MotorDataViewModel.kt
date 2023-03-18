package edu.pdx.robot_car.robotcarapp.model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import edu.pdx.robot_car.robotcarapp.MQTTClient
import org.eclipse.paho.client.mqttv3.*
import org.json.JSONObject

class MotorDataViewModel: ViewModel() {

    // These are examples of variables we might use in the view model
    // Basically any variable which we need to be global across fragments
    lateinit var mqttClient : MQTTClient
    lateinit var mqttClientID: String

    private val _motor1_speed = MutableLiveData<Float>()
    val motor1_speed: LiveData<Float> = _motor1_speed

    private val _motor2_speed = MutableLiveData<Float>()
    val motor2_speed: LiveData<Float> = _motor2_speed

    private val _upCounter = MutableLiveData<Int>(5)
    val upCounter: LiveData<Int> = _upCounter

    private val _downCounter = MutableLiveData<Int>(5)
    val downCounter: LiveData<Int> = _downCounter
    private val _rightCounter = MutableLiveData<Int>(5)
    val rightCounter: LiveData<Int> = _rightCounter
    private val _leftCounter = MutableLiveData<Int>(5)
    val leftCounter: LiveData<Int> = _leftCounter

    // This is an example of a method the ViewModel can do
    // Basically any interaction with or manipulation of the data that doesn't have to do with the UI
    fun updateSpeed(RPM: Float, motorNumber: Int) {
        when (motorNumber) {
            1 -> _motor1_speed.value = RPM
            2 -> _motor2_speed.value = RPM
        }
    }

    /*
    * @updateMotor
    * @param position
    *        0 -> up button pressed
    *        1 -> right button pressed
    *        2 -> left button pressed
    *        3 -> down button pressed
    * @brief upon press, update the state of the motor
    * for now, only one direction at a time, and a set value of 1, all others 0
    * */
    fun updateMotor(position: Int){
        when(position){
            0 -> {
                _upCounter.value = 1
                _downCounter.value = 0
                _rightCounter.value = 0
                _leftCounter.value = 0
            }
            1 -> {
                _upCounter.value = 0
                _downCounter.value = 0
                _rightCounter.value = 1
                _leftCounter.value = 0
            }
            2 -> {
                _upCounter.value = 0
                _downCounter.value = 1
                _rightCounter.value = 0
                _leftCounter.value = 0
            }
            3 -> {
                _upCounter.value = 0
                _downCounter.value = 0
                _rightCounter.value = 0
                _leftCounter.value = 1
            }
        }
    }

    /**
     * @method publishMQTTMessage
     * @param topic of type String. The MQTT topic you wish to publish to
     * @param message of type String. If using JSON Object, convert to string with .toString()
     * before invoking this method
     *
     * @brief This function takes in an MQTT topic, a message, and published the message to that topic
     * The reason it is in the ViewModel is so any fragment could publish to an MQTT topic easily.
     * @todo if the only place this is required is MainControlsFragment, it could be moved there
     */
      fun publishMQTTMessage(topic: String, message: String){
          if (mqttClient.isConnected()){
               mqttClient.publish(
                  topic,
                  message,
                  1,
                  false,
                  object : IMqttActionListener {
                      override fun onSuccess(asyncActionToken: IMqttToken?) {
                          val msg = "Successfully published topic: $topic"
                          Log.d("MQTT msg:",msg)
                      }

                      override fun onFailure(
                          asyncActionToken: IMqttToken?,
                          exception: Throwable?
                      ) {
                          val msg =
                              "Failed to publish: to topic: $topic exception: ${exception.toString()}"
                          Log.d("MQTT msg:", msg)
                      }
                  })
          } else {
              Log.d("MQTT msg:", "Impossible to publish, no server connected")
          }
      }

    /**
     * @method parseMQTTMessage
     * @param message of type MqttMessage.
     *
     * @brief This function takes in a received MQTT message, parses it, and updates the necessary
     * Live Data objects with new values, depending on the message topic and content
     * todo: If we add other sensor data, it can be parsed here too
     */
    fun parseMQTTMessage(message: MqttMessage?) {

        // temporary testing with just one value instead of JSON:
        val leftRPM = message.toString().toFloat()
        updateSpeed(leftRPM, 1)
        Log.d("Motor Data View Model","Left Motor Speed: ${motor1_speed.value}")

        /*
        val motorData = JSONObject(message.toString())
        val leftRPM = motorData.getString("Left_Motor").toInt().toFloat()
        val rightRPM = motorData.getString("Right_Motor").toInt().toFloat()
        updateSpeed(leftRPM, 1)
        updateSpeed(rightRPM, 2)
        Log.d("Motor Data View Model","Left Motor Speed: ${motor1_speed.value}")
        Log.d("Motor Data View Model","_Left Motor Speed: ${_motor1_speed.value}")
        Log.d("Motor Data View Model","Right Motor Speed: ${motor2_speed.value}")
        Log.d("Motor Data View Model","_Right Motor Speed: ${_motor2_speed.value}")*/
    }
}

