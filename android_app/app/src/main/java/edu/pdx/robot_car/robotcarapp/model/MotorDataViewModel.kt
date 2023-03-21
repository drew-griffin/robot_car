package edu.pdx.robot_car.robotcarapp.model

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import edu.pdx.robot_car.robotcarapp.*
import org.eclipse.paho.client.mqttv3.*
import org.json.JSONObject

class MotorDataViewModel: ViewModel() {

    lateinit var mqttClient : MQTTClient
    lateinit var mqttClientID: String

    private val _mqttNetwork = MutableLiveData<String>(MQTT_SERVER_URI)
    val mqttNetwork: LiveData<String> = _mqttNetwork

    private val _mqttKey = MutableLiveData<String>(MQTT_CLIENT_ID)
    val mqttKey: LiveData<String> = _mqttKey

    private val _mqttUsername = MutableLiveData<String>(MQTT_USERNAME)
    val mqttUsername: LiveData<String> = _mqttUsername

    private val _mqttPassword = MutableLiveData<String>(MQTT_PWD)
    val mqttPassword: LiveData<String> = _mqttPassword

    private val _mqttConnected = MutableLiveData<Boolean>()
    val mqttConnected: LiveData<Boolean> = _mqttConnected

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

    fun updateSpeed(RPM: Float, motorNumber: Int) {
        when (motorNumber) {
            1 -> _motor1_speed.value = RPM
            2 -> _motor2_speed.value = RPM
        }
    }

    fun updateMqttNetwork(newNetwork: String) {
        _mqttNetwork.value = newNetwork
    }
    fun updateMqttKey(newKey: String) {
        _mqttKey.value = newKey
    }
    fun updateMqttUsername(newUsername: String) {
        _mqttUsername.value = newUsername
    }
    fun updateMqttPassword(newPassword: String) {
        _mqttPassword.value = newPassword
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
        // TODO: Safe to delete after Noah tests JSON is working
        /*val leftRPM = message.toString().toFloat()
        updateSpeed(leftRPM, 1)
        Log.d("Motor Data View Model","Left Motor Speed: ${motor1_speed.value}")*/

        val motorData = JSONObject(message.toString())
        val leftRPM = motorData.getString("Left_Motor").toInt().toFloat()
        val rightRPM = motorData.getString("Right_Motor").toInt().toFloat()
        updateSpeed(leftRPM, 1)
        updateSpeed(rightRPM, 2)
        Log.d("Motor Data View Model","Left Motor Speed: ${motor1_speed.value}")
        Log.d("Motor Data View Model","Right Motor Speed: ${motor2_speed.value}")
    }

    fun connectToMQTT() {
        mqttClient.connect(
            mqttUsername.value.toString(),
            mqttPassword.value.toString(),
            object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Log.d(ContentValues.TAG, "Connection success")
                    _mqttConnected.value = true
                    // subscribe to motor status topic
                    subscribeToStatus(ROBOT_CAR_STATUS)
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.d(ContentValues.TAG, "Connection failure: ${exception.toString()}")
                    _mqttConnected.value = false
                    exception?.printStackTrace()
                }
            },

            object : MqttCallback {
                override fun messageArrived(topic: String?, message: MqttMessage?) {
                    val msg = "Received message: ${message.toString()} from topic: $topic"
                    Log.d(ContentValues.TAG, msg)

                    if (topic!! == ROBOT_CAR_STATUS){
                        Log.d("Welcome Fragment","ROBOT_CAR_STATUS message arrived, sending it to be parsed.")
                        parseMQTTMessage(message)
                    } else {
                        Log.d(ContentValues.TAG, "Received invalid topic: $topic")
                    }
                }

                override fun connectionLost(cause: Throwable?) {
                    Log.d(ContentValues.TAG, "Connection lost ${cause.toString()}")
                }

                override fun deliveryComplete(token: IMqttDeliveryToken?) {
                    Log.d(ContentValues.TAG, "Delivery complete")
                }
            })
    }

    private fun subscribeToStatus(subscribeTopic: String) {
        // subscribe to status topic only if connected to broker
        if (mqttClient.isConnected()) {
            mqttClient.subscribe(
                topic = subscribeTopic,
                qos = 1,
                object : IMqttActionListener {
                    override fun onSuccess(asyncActionToken: IMqttToken?) {
                        val msg = "Subscribed to: $subscribeTopic"
                        Log.d(ContentValues.TAG, msg)
                    }

                    override fun onFailure(
                        asyncActionToken: IMqttToken?,
                        exception: Throwable?
                    ) {
                        Log.d(
                            ContentValues.TAG, "Failed to subscribe: $subscribeTopic exception: ${exception.toString()}"
                        )
                    }
                })
        } else {
            Log.d(ContentValues.TAG, "Cannot subscribe to $subscribeTopic: Not connected to server")
        }
    }
}

