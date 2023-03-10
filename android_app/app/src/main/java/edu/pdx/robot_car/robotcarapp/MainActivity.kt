/**
 * @class MainActivity
 * @date  03/09/2023
 * @brief MainActivity hosts the RobotCar activity, the navigation controller,
 * and the corresponding fragments for the activity
 */

package edu.pdx.robot_car.robotcarapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import edu.pdx.robot_car.robotcarapp.R

class MainActivity : AppCompatActivity(R.layout.activity_main){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        setupActionBarWithNavController(navController)
    }
}