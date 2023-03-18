/**
 * @fragment VideoFeedFragment
 * @data     03/09/2023
 * @brief    This fragment will display a livestream of data being published by the RobotCar in video format.
 * @priority Low (Stretch goal)
 */

package edu.pdx.robot_car.robotcarapp

import android.net.Uri
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
import edu.pdx.robot_car.robotcarapp.databinding.FragmentVideoFeedBinding
import edu.pdx.robot_car.robotcarapp.databinding.FragmentWelcomeBinding
import edu.pdx.robot_car.robotcarapp.model.MotorDataViewModel

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

        //binding?.videoView?.setVideoURI(Uri.parse("android.resource://"+context?.packageResourcePath+"/"+R.raw.movie))
        //binding?.videoView?.start()
    }
}