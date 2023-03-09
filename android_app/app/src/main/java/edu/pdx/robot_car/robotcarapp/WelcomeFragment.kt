package edu.pdx.robot_car.robotcarapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import edu.pdx.robot_car.robotcarapp.databinding.FragmentWelcomeBinding
import edu.pdx.robot_car.robotcarapp.model.MotorDataViewModel

class WelcomeFragment : Fragment() {

    private var binding: FragmentWelcomeBinding? = null
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

        // When they click the button, navigate to the next screen
        // TODO: Also connect to MQTT - still need to add that functionality
        binding?.apply {
            // Set up the button click listeners
            button3.setOnClickListener {
                findNavController().navigate(R.id.action_welcomeFragment_to_mainControlsFragment)
            }
        }
    }
}