/**
 * @class MainActivity
 * @date  03/09/2023
 * @brief MainActivity hosts the RobotCar activity, the navigation controller,
 * and the corresponding fragments for the activity
 */

package edu.pdx.robot_car.robotcarapp

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toolbar
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import edu.pdx.robot_car.robotcarapp.R

class MainActivity : AppCompatActivity(R.layout.activity_main){
    @SuppressLint("AppCompatMethod")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        actionBar?.setDisplayHomeAsUpEnabled(true)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController


        setupActionBarWithNavController(navController)
    }

    /**
     * Enables Back button in navigation toolbar
     */
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}