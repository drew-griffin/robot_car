/**
 * @fragment WelcomeFragment
 * @data     03/09/2023
 * @brief    This fragment will contain a welcome page for the user. The user may be able to change broker information and
 *           passwords here as well. A connect button will traverse the user to the MainControlsFragment
 * @priority HIGH (necessary)
 */

package edu.pdx.robot_car.robotcarapp

import android.content.ContentValues.TAG
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import edu.pdx.robot_car.robotcarapp.databinding.FragmentWelcomeBinding
import edu.pdx.robot_car.robotcarapp.model.MotorDataViewModel
import org.eclipse.paho.client.mqttv3.*

class WelcomeFragment : Fragment() {

    private var binding: FragmentWelcomeBinding? = null
    private lateinit var mqttClient : MQTTClient
    private lateinit var mqttClientID: String
    private val sharedViewModel: MotorDataViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentBinding = FragmentWelcomeBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Check if Internet connection is available
        // exit if it is not
        if (!isConnected()) {
            Log.d(TAG, "Internet connection NOT available")
            Toast.makeText(context, "Internet connection NOT available", Toast.LENGTH_LONG).show()
            //finish()
        } else {
            Log.d(TAG, "Connected to the Internet")
            Toast.makeText(context, "Connected to the Internet", Toast.LENGTH_LONG).show()
        }

        // Assign user-entered or default values to the MQTT Settings
        val mqttNetwork: String = binding?.MQTTNetwork?.text.toString() ?: MQTT_SERVER_URI
        val mqttKey: String = binding?.MQTTKey?.text.toString() ?: MQTT_CLIENT_ID
        val mqttUsername: String = binding?.MQTTUsername?.text.toString() ?: MQTT_USERNAME
        val mqttPassword: String = binding?.MQTTPassword?.text.toString() ?: MQTT_PWD


        // open mQTT Broker communication
        mqttClientID = MqttClient.generateClientId()
        mqttClient = MQTTClient(context, mqttNetwork, mqttClientID)


        // When they click the button, navigate to the next screen
        binding?.apply {
            // Set up the button click listeners
            // TODO: Add key and custom username/password to the connect function
            // TODO: Export some of the MQTT functionality to the ViewModel since it is data-related, not UI-related
            connectButton.setOnClickListener {
                mqttClient.connect(
                    mqttUsername,
                    mqttPassword,
                    object : IMqttActionListener {
                        override fun onSuccess(asyncActionToken: IMqttToken?) {
                            Log.d(TAG, "Connection success")
                            val successMsg = "MQTT Connection to $mqttNetwork Established"
                            Toast.makeText(context, successMsg, Toast.LENGTH_LONG).show()

                            // subscribe to motor status topics
                            //subscribeToStatus(STS_TOPIC)
                        }

                        override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                            Log.d(TAG, "Connection failure: ${exception.toString()}")
                            val failureMsg =
                                "MQTT Connection to $mqttNetwork failed: ${exception?.toString()}"
                            Toast.makeText(context, failureMsg, Toast.LENGTH_LONG).show()
                            exception?.printStackTrace()
                        }
                    },

                    object : MqttCallback {
                        override fun messageArrived(topic: String?, message: MqttMessage?) {
                            val msg = "Received message: ${message.toString()} from topic: $topic"
                            Log.d(TAG, msg)

                            // since a message arrived I'm assuming that the topic string is not null
                            /*if (topic!! == STS_TOPIC) {
                               // do something
                            //}

                            else {
                                Log.d(TAG, "Received invalid topic: $topic")
                            }*/
                        }

                        override fun connectionLost(cause: Throwable?) {
                            Log.d(TAG, "Connection lost ${cause.toString()}")
                        }

                        override fun deliveryComplete(token: IMqttDeliveryToken?) {
                            Log.d(TAG, "Delivery complete")
                        }
                    })
                findNavController().navigate(R.id.action_welcomeFragment_to_mainControlsFragment)
            }
        }
    }

    // helper functions
    // isConnected: Borrowed from Roy Kravitz's MQTT LED example
    private fun isConnected(): Boolean {
        var result = false
        val cm = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities = cm.getNetworkCapabilities(cm.activeNetwork)
        if (capabilities != null) {
            result = when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN) -> true
                else -> false
            }
        }
        return result
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
                        Log.d(TAG, msg)
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    }

                    override fun onFailure(
                        asyncActionToken: IMqttToken?,
                        exception: Throwable?
                    ) {
                        Log.d(
                            TAG, "Failed to subscribe: $subscribeTopic exception: ${exception.toString()}"
                        )
                    }
                })
        } else {
            Log.d(TAG, "Cannot subscribe to $subscribeTopic: Not connected to server")
        }
    }

}