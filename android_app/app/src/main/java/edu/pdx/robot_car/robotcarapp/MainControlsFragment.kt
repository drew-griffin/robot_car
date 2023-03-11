/**
 * @fragment MainControlsFragment
 * @data     03/09/2023
 * @brief    This fragment allows the user to input basic controls to the robot car (forward, back, left, right).
 *           This fragment will take this data, and publish it to a singular MQTT topic in JSON format.
 *           The car can then subscribe to the corresponding topic, parse the message, and move accordingly
 * @priority HIGH (necessary)
 */
package edu.pdx.robot_car.robotcarapp

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import edu.pdx.robot_car.robotcarapp.databinding.FragmentMainControlsBinding
import edu.pdx.robot_car.robotcarapp.model.MotorDataViewModel

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentBinding = FragmentMainControlsBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

            binding?.apply {
                // Specify the fragment as the lifecycle owner
                lifecycleOwner = viewLifecycleOwner
                // Assign the view model to a property in the binding class
                viewModel = sharedViewModel
                // Assign the fragment
                //mainControlsFragment = this@MainControlsFragment
            }


        // Set listeners for each button
        binding?.up?.setOnClickListener{
            sharedViewModel.incrementCounter((0))
            binding?.upCount?.text = "Up Counter: ${sharedViewModel.upCounter.value}"
            Log.d("MainControlsFragment","Up Counter: ${sharedViewModel.upCounter.value}")
        }
        binding?.right?.setOnClickListener{
            sharedViewModel.incrementCounter((1))
            binding?.rightCount?.text = "Right Counter: ${sharedViewModel.rightCounter.value}"
        }
        binding?.down?.setOnClickListener{
            sharedViewModel.incrementCounter((2))
            binding?.downCount?.text = "Down Counter: ${sharedViewModel.downCounter.value}"
        }
        binding?.left?.setOnClickListener{
            sharedViewModel.incrementCounter((3))
            binding?.leftCount?.text = "Left Counter: ${sharedViewModel.leftCounter.value}"
        }

        binding?.motorStatusButton?.setOnClickListener{
            findNavController().navigate(R.id.action_mainControlsFragment_to_motorStatusFragment)
        }
        binding?.videoFeedButton?.setOnClickListener{
            findNavController().navigate(R.id.action_mainControlsFragment_to_videoFeedFragment)
        }
    }

}


