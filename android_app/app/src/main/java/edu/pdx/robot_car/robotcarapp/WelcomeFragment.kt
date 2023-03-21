/**
 * @fragment WelcomeFragment
 * @date     03/09/2023
 * @brief    This fragment contains a welcome page for the user.
 *           The user can change MQTT broker information and passwords.
 *           Connect button will connect to MQTT and navigate to MainControlsFragment
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
            Toast.makeText(context, "Internet connection NOT available", Toast.LENGTH_SHORT).show()
            activity?.finish()
        } else {
            Log.d(TAG, "Connected to the Internet")
            Toast.makeText(context, "Connected to the Internet", Toast.LENGTH_SHORT).show()
        }

        // When they click the button, navigate to the next screen
        binding?.apply {
            // Set up the button click listeners
            // TODO: Add key and custom username/password to the connect function
            connectButton.setOnClickListener {
                if (binding?.MQTTNetwork?.text.toString() != "")
                    sharedViewModel.updateMqttNetwork(binding?.MQTTNetwork?.text.toString())
                if (binding?.MQTTKey?.text.toString() != "")
                    sharedViewModel.updateMqttKey(binding?.MQTTKey?.text.toString())
                if (binding?.MQTTUsername?.text.toString() != "")
                    sharedViewModel.updateMqttUsername(binding?.MQTTUsername?.text.toString())
                if (binding?.MQTTPassword?.text.toString() != "")
                    sharedViewModel.updateMqttPassword(binding?.MQTTPassword?.text.toString())

                // Connect to MQTT using the data view model
                sharedViewModel.mqttClientID = MqttClient.generateClientId()
                sharedViewModel.mqttClient = MQTTClient(context, sharedViewModel.mqttNetwork.value!!, sharedViewModel.mqttClientID)
                sharedViewModel.connectToMQTT()

                // Check if the connection worked
                sharedViewModel.mqttConnected.observe(viewLifecycleOwner) {
                    if (sharedViewModel.mqttConnected.value == true) {
                        val successMsg = "MQTT Connection to ${sharedViewModel.mqttNetwork.value} Established"
                        Toast.makeText(context, successMsg, Toast.LENGTH_SHORT).show()
                        findNavController().navigate(R.id.action_welcomeFragment_to_mainControlsFragment)
                    } else {
                        val failureMsg = "MQTT Connection to ${sharedViewModel.mqttNetwork.value} failed."
                        Toast.makeText(context, failureMsg, Toast.LENGTH_LONG).show()
                    }
                }
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

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("Welcome Fragment: ","Fragment Destroyed")
        binding = null
    }
}