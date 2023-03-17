/**
 * @filename MQQTConstants.kt
 * @data  03/09/2023
 * @brief file contains all relevant MQTT constants for robot car
 * @todo  <Change for our project>
 */

package edu.pdx.robot_car.robotcarapp

const val MQTT_SERVER_URI 		= "tcp://192.168.137.56"
const val MQTT_CLIENT_ID        = ""
const val MQTT_USERNAME         = ""
const val MQTT_PWD              = ""


// topics and messages
const val ROBOT_CAR_CONTROL     = "RobotCar/Move"
const val ROBOT_CAR_STATUS      = "RobotCar/Motors"