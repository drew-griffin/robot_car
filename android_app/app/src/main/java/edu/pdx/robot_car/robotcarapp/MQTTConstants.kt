package edu.pdx.robot_car.robotcarapp

const val MQTT_SERVER_URI_KEY   = "MQTT_SERVER_URI"
const val MQTT_CLIENT_ID_KEY    = "MQTT_CLIENT_ID"
const val MQTT_USERNAME_KEY     = "MQTT_USERNAME"
const val MQTT_PWD_KEY          = "MQTT_PWD"

//const val MQTT_SERVER_URI       = "tcp://broker.hivemq.com:1883"
const val MQTT_SERVER_URI 		= "tcp://broker.hivemq.com"
const val MQTT_CLIENT_ID        = ""
const val MQTT_USERNAME         = ""
const val MQTT_PWD              = ""

// topics and messages for RPI
const val LITE_TOPIC        	= "profroyk/Raspberry"
const val STS_TOPIC        		= "profroyk/Android/status"

const val LITEON_MESSAGE    	= "liteon"
const val LITEOFF_MESSAGE  		= "liteoff"
const val GETSTS_MESSAGE    	= "getstatus"

//topics and messages for Pico W
const val PICO_LITEON_TOPIC		= "profroyk/pico/led_on"
const val PICO_LITEOFF_TOPIC	= "profroyk/pico/led_off"
const val PICO_LITETOGGLE_TOPIC	= "profroyk/pico/led_toggle"
const val PICO_STS_TOPIC		= "profroyk/ws/aht20_tempc"