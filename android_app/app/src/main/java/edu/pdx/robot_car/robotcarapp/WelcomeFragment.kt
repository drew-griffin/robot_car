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
    private val sharedViewModel: MotorDataViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentBinding = FragmentWelcomeBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        Log.d("WelcomeFragment: ","Fragment Created")
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = sharedViewModel
            welcomeFragment = this@WelcomeFragment
        }

        // Check if Internet connection is available
        // exit if it is not
        if (!isConnected()) {
            Log.d(TAG, "Internet connection NOT available")
            Toast.makeText(context, "Internet connection NOT available", Toast.LENGTH_LONG).show()
            activity?.finish()
        } else {
            Log.d(TAG, "Connected to the Internet")
            Toast.makeText(context, "Connected to the Internet", Toast.LENGTH_LONG).show()
        }

        //val mqttNetwork: String =  if (binding?.MQTTNetwork?.text.toString() != "") binding?.MQTTNetwork?.text.toString() else MQTT_SERVER_URI
        //val mqttKey: String =      if (binding?.MQTTKey?.text.toString() != "") binding?.MQTTKey?.text.toString() else MQTT_CLIENT_ID
        //val mqttUsername: String = if (binding?.MQTTUsername?.text.toString() != "") binding?.MQTTUsername?.text.toString() else MQTT_USERNAME
        //val mqttPassword: String = if (binding?.MQTTPassword?.text.toString() != "") binding?.MQTTPassword?.text.toString() else MQTT_PWD

        // open mQTT Broker communication
        //sharedViewModel.mqttClientID = MqttClient.generateClientId()
        //sharedViewModel.mqttClient = MQTTClient(context, mqttNetwork, sharedViewModel.mqttClientID)

        // When they click the button, navigate to the next screen
        binding?.apply {
            // Set up the button click listeners
            // TODO: Add key and custom username/password to the connect function
            // TODO: Export some of the MQTT functionality to the ViewModel since it is data-related, not UI-related
            connectButton.setOnClickListener {
                val mqttNetwork: String =  if (binding?.MQTTNetwork?.text.toString() != "") binding?.MQTTNetwork?.text.toString() else MQTT_SERVER_URI
                val mqttKey: String =      if (binding?.MQTTKey?.text.toString() != "") binding?.MQTTKey?.text.toString() else MQTT_CLIENT_ID
                val mqttUsername: String = if (binding?.MQTTUsername?.text.toString() != "") binding?.MQTTUsername?.text.toString() else MQTT_USERNAME
                val mqttPassword: String = if (binding?.MQTTPassword?.text.toString() != "") binding?.MQTTPassword?.text.toString() else MQTT_PWD
                // open mQTT Broker communication
                sharedViewModel.mqttClientID = MqttClient.generateClientId()
                sharedViewModel.mqttClient = MQTTClient(context, mqttNetwork, sharedViewModel.mqttClientID)

                sharedViewModel.mqttClient.connect(
                    mqttUsername,
                    mqttPassword,
                    object : IMqttActionListener {
                        override fun onSuccess(asyncActionToken: IMqttToken?) {
                            Log.d(TAG, "Connection success")
                            val successMsg = "MQTT Connection to $mqttNetwork Established"
                            Toast.makeText(context, successMsg, Toast.LENGTH_LONG).show()
                            findNavController().navigate(R.id.action_welcomeFragment_to_mainControlsFragment)
                            // subscribe to motor status topics
                            subscribeToStatus(ROBOT_CAR_STATUS)
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
                            if (topic!! == ROBOT_CAR_STATUS){
                                // TODO: Parse the message since both motors might be sent in the same message
                                // TODO: Or add sub-topics
                                sharedViewModel.updateSpeed(message.toString().toFloat(), 1)
                            } else {
                                Log.d(TAG, "Received invalid topic: $topic")
                            }
                        }

                        override fun connectionLost(cause: Throwable?) {
                            Log.d(TAG, "Connection lost ${cause.toString()}")
                        }

                        override fun deliveryComplete(token: IMqttDeliveryToken?) {
                            Log.d(TAG, "Delivery complete")
                        }
                    })
                //findNavController().navigate(R.id.action_welcomeFragment_to_mainControlsFragment)
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
        if (sharedViewModel.mqttClient.isConnected()) {
            sharedViewModel.mqttClient.subscribe(
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

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("Welcome Fragment: ","Fragment Destroyed")
        binding = null
    }

}