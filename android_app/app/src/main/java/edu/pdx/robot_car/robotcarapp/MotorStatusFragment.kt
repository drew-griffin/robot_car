/**
 * @fragment MotorStatusFragment
 * @data     03/09/2023
 * @brief    This fragment will subscribe to a singular JSON formatted topic that the robot car publishes.
 *           The robot car will then parse through the data containing motor status, and display on the screen
 *           accordingly
 * @priority MEDIUM (Stretch goal)
 */
package edu.pdx.robot_car.robotcarapp

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import edu.pdx.robot_car.robotcarapp.databinding.FragmentMainControlsBinding
import edu.pdx.robot_car.robotcarapp.databinding.FragmentMotorStatusBinding
import edu.pdx.robot_car.robotcarapp.model.MotorDataViewModel

/**
 * @fragment MotorStatusFragment
 * @data     03/10/2023
 * @brief    This fragment will contain gauges to display information about the two motors.
 *           It also contains a Back button in the Navigation Bar to return to Main Controls Fragment.
 *           Should it contain a button to go to Live Video as well?
 * @priority LOW
 */
class MotorStatusFragment : Fragment() {

    private var binding: FragmentMotorStatusBinding? = null
    private val sharedViewModel: MotorDataViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentBinding = FragmentMotorStatusBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        Log.d("Motor Status Fragment: ","Fragment Created")
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = sharedViewModel
            motorStatusFragment = this@MotorStatusFragment
        }


        binding?.speedView?.apply{
            withTremble = false
            unit = " RPM"
            minSpeed = 0.0F
            maxSpeed = 100.0F
            sharedViewModel.motor1_speed.value?.let { speedTo(it) }
        }
        binding?.speedView2?.apply{
            withTremble = false
            unit = " RPM"
            minSpeed = 0.0F
            maxSpeed = 100.0F
            sharedViewModel.motor2_speed.value?.let { speedTo(it) }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("Motor Status Fragment: ","Fragment Destroyed")
        binding = null
    }
}