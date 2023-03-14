/**
 * @fragment MainControlsFragment
 * @data     03/09/2023
 * @brief    This fragment allows the user to input basic controls to the robot car (forward, back, left, right).
 *           This fragment will take this data, and publish it to a singular MQTT topic in JSON format.
 *           The car can then subscribe to the corresponding topic, parse the message, and move accordingly
 * @priority HIGH (necessary)
 */
package edu.pdx.robot_car.robotcarapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Message
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import edu.pdx.robot_car.robotcarapp.databinding.FragmentMainControlsBinding
import edu.pdx.robot_car.robotcarapp.model.MotorDataViewModel
import org.json.JSONObject

/**
 * @fragment MainControlsFragment
 * @data     03/10/2023
 * @brief    This fragment will contain four buttons to control movement of the robot.
 *              (Stretch) It might contain speed level buttons too.
 *              (Stretch) The user can click a button to go to a Motor Status Detail fragment
 *              (Stretch) The user can click a button to go to a live video feed.
 * @priority MEDIUM (necessary)
 */

class MainControlsFragment : Fragment() {

    private var binding: FragmentMainControlsBinding? = null
    private val sharedViewModel: MotorDataViewModel by activityViewModels()
    private val directionMessage = "Current motor direction is"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentBinding = FragmentMainControlsBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        Log.d("Main Controls Fragment: ","Fragment Created")
        return fragmentBinding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

            binding?.apply {
                lifecycleOwner = viewLifecycleOwner
                viewModel = sharedViewModel
                mainControlsFragment = this@MainControlsFragment
            }


        // Set listeners for each button
        binding?.up?.setOnClickListener{
            sharedViewModel.updateMotor((0))
            packageDataAndSend()
            binding?.directionStatus?.text = "$directionMessage up"
            // For the up counter, I tried using the XML to directly print the value instead of adjusting it here.
            //Log.d("MainControlsFragment","Up Counter: ${sharedViewModel.upCounter.value}")
        }
        binding?.right?.setOnClickListener{
            sharedViewModel.updateMotor((1))
            packageDataAndSend()
            binding?.directionStatus?.text = "$directionMessage right"
            //binding?.rightCount?.text = "Right Counter: ${sharedViewModel.rightCounter.value}"
        }
        binding?.down?.setOnClickListener{
            sharedViewModel.updateMotor((2))
            packageDataAndSend()
            binding?.directionStatus?.text = "$directionMessage down"
            //binding?.downCount?.text = "Down Counter: ${sharedViewModel.downCounter.value}"
        }
        binding?.left?.setOnClickListener{
            sharedViewModel.updateMotor((3))
            packageDataAndSend()
            binding?.directionStatus?.text = "$directionMessage left"
            //binding?.leftCount?.text = "Left Counter: ${sharedViewModel.leftCounter.value}"
        }

        binding?.motorStatusButton?.setOnClickListener{
            findNavController().navigate(R.id.action_mainControlsFragment_to_motorStatusFragment)
        }
        binding?.videoFeedButton?.setOnClickListener{
            findNavController().navigate(R.id.action_mainControlsFragment_to_videoFeedFragment)
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
    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("Main Controls Fragment: ","Fragment Destroyed")
        binding = null
    }
}


