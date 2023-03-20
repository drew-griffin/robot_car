/**
 * @fragment VideoFeedFragment
 * @date     03/09/2023
 * @brief    This fragment displays a livestream of data being published by the RobotCar in video format.
 */

package edu.pdx.robot_car.robotcarapp

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import edu.pdx.robot_car.robotcarapp.databinding.FragmentMainControlsBinding
import edu.pdx.robot_car.robotcarapp.databinding.FragmentVideoFeedBinding
import edu.pdx.robot_car.robotcarapp.databinding.FragmentWelcomeBinding
import edu.pdx.robot_car.robotcarapp.model.MotorDataViewModel
import org.json.JSONObject

class VideoFeedFragment : Fragment() {
    private var binding: FragmentVideoFeedBinding? = null
    private val sharedViewModel: MotorDataViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentBinding = FragmentVideoFeedBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        Log.d("Video Feed Fragment: ", "Fragment Created")
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = sharedViewModel
            videoFeedFragment = this@VideoFeedFragment
        }

        binding?.mjpegview?.setUrl("http://192.168.137.56:8081")
        binding?.mjpegview?.startStream()

        // Set listeners for each button
        binding?.up?.setOnClickListener{
            sharedViewModel.updateMotor((0))
            packageDataAndSend()
        }
        binding?.right?.setOnClickListener{
            sharedViewModel.updateMotor((1))
            packageDataAndSend()
        }
        binding?.down?.setOnClickListener{
            sharedViewModel.updateMotor((2))
            packageDataAndSend()
        }
        binding?.left?.setOnClickListener{
            sharedViewModel.updateMotor((3))
            packageDataAndSend()
        }
        binding?.motorModeButton?.setOnClickListener{
            findNavController().navigate(R.id.action_videoFeedFragment_to_mainControlsFragment)
        }

    }
    /**
     * @function packageDataAndSend
     * Takes in the state of the motor directs from latest motor update
     * Packages the data, and uses publishMQQTMessage method in the MotorDataViewModel
     * to send the message over MQTT
     */
    private fun packageDataAndSend(){
        val messageJSON = JSONObject()
        messageJSON.put("UP", sharedViewModel.upCounter.value)
        messageJSON.put("DOWN", sharedViewModel.downCounter.value)
        messageJSON.put("RIGHT", sharedViewModel.rightCounter.value)
        messageJSON.put("LEFT", sharedViewModel.leftCounter.value)
        val message = messageJSON.toString()
        sharedViewModel.publishMQTTMessage(ROBOT_CAR_CONTROL, message)
    }
    override fun onResume(){
        super.onResume()
        sharedViewModel.mqttConnected.observe(viewLifecycleOwner) {
            if (sharedViewModel.mqttConnected.value == true) {
                val successMsg = "Still connected to MQTT Network"
               // Toast.makeText(context, successMsg, Toast.LENGTH_SHORT).show()
            } else {
                val failureMsg = "MQTT Connection was lost. Reconnecting now."
                Toast.makeText(context, failureMsg, Toast.LENGTH_LONG).show()
                // Connect to MQTT using the data view model
                sharedViewModel.connectToMQTT()
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("Video Feed Fragment: ","Fragment Destroyed")
        binding = null
    }
}