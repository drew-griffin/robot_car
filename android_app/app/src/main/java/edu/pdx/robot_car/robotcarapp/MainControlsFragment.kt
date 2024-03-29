/**
 * @fragment MainControlsFragment
 * @date     03/10/2023
 * @brief    This fragment contains four arrow buttons to control movement of the robot.
 *           It contains two gauges showing the motor speeds.
 *           The user can click a button to go to a live video feed.
 */
package edu.pdx.robot_car.robotcarapp

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.github.anastr.speedviewlib.components.Style
import edu.pdx.robot_car.robotcarapp.databinding.FragmentMainControlsBinding
import edu.pdx.robot_car.robotcarapp.model.MotorDataViewModel
import org.eclipse.paho.client.mqttv3.MqttClient
import org.json.JSONObject
import java.util.*

class MainControlsFragment : Fragment() {

    private var binding: FragmentMainControlsBinding? = null
    private val sharedViewModel: MotorDataViewModel by activityViewModels()

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
            binding?.directionStatus?.text = "The robot moved forward"
        }
        binding?.right?.setOnClickListener{
            sharedViewModel.updateMotor((1))
            packageDataAndSend()
            binding?.directionStatus?.text = "The robot turned right"
        }
        binding?.down?.setOnClickListener{
            sharedViewModel.updateMotor((2))
            packageDataAndSend()
            binding?.directionStatus?.text = "The robot moved backward"
        }
        binding?.left?.setOnClickListener{
            sharedViewModel.updateMotor((3))
            packageDataAndSend()
            binding?.directionStatus?.text = "The robot turned left"
        }

        sharedViewModel.motor1_speed.observe(viewLifecycleOwner){
            newSpeed -> binding?.speedView?.setSpeedAt(newSpeed)
        }
        sharedViewModel.motor2_speed.observe(viewLifecycleOwner){
                newSpeed -> binding?.speedView2?.setSpeedAt(newSpeed)
        }

        binding?.speedView?.apply{
            unit = " RPM"
            unitTextColor = Color.WHITE
            speedTextColor = Color.WHITE
            minSpeed = -65.0F
            maxSpeed = 65.0F
            withTremble = false
            makeSections(2, Color.CYAN, Style.BUTT)
            sections[0].color = Color.LTGRAY
            sections[1].color = Color.GREEN
        }

        binding?.speedView2?.apply{
            unit = " RPM"
            unitTextColor = Color.WHITE
            speedTextColor = Color.WHITE
            minSpeed = -65.0F
            maxSpeed = 65.0F
            withTremble = false
            makeSections(2, Color.CYAN, Style.BUTT)
            sections[0].color = Color.MAGENTA
            sections[1].color = Color.YELLOW
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

    override fun onResume(){
        super.onResume()
        sharedViewModel.mqttConnected.observe(viewLifecycleOwner) {
            if (sharedViewModel.mqttConnected.value == true) {
                val successMsg = "Still connected to MQTT Network"
                //Toast.makeText(context, successMsg, Toast.LENGTH_SHORT).show()
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
        Log.d("Main Controls Fragment: ","Fragment Destroyed")
        binding = null
    }
}


