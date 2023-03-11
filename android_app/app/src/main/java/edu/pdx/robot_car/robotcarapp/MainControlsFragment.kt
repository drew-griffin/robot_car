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
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import edu.pdx.robot_car.robotcarapp.databinding.FragmentMainControlsBinding
import edu.pdx.robot_car.robotcarapp.databinding.FragmentWelcomeBinding
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


// Credit for the arrow icons:
//<a href="https://www.flaticon.com/free-icons/arrow" title="arrow icons">Arrow icons created by Freepik - Flaticon</a>
// TODO: Put this credit in a better place or make our own icons

class MainControlsFragment : Fragment() {

    private var binding: FragmentMainControlsBinding? = null
    private val sharedViewModel: MotorDataViewModel by activityViewModels()

    var moveUp: Boolean = false
    var moveDown: Boolean = false
    var moveRight: Boolean = false
    var moveLeft: Boolean = false

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

        // Set listeners for each button
        // TODO: Export this to MotorDataViewModel. On click, something should trigger MotorDataView model to update the data
        binding?.up?.setOnClickListener{
            moveUp = true
            Toast.makeText(context,"Up Button Pressed", Toast.LENGTH_SHORT).show()
        }
        // TODO: Add this functionality for the other 3 buttons too

        binding?.motorStatusButton?.setOnClickListener{
            findNavController().navigate(R.id.action_mainControlsFragment_to_motorStatusFragment)
        }
        binding?.videoFeedButton?.setOnClickListener{
            findNavController().navigate(R.id.action_mainControlsFragment_to_videoFeedFragment)
        }
    }
}


