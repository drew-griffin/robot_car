"""
ECE 558 Final Project: Robot Car
Team Members: Emily Devlin, Noah Page, Drew Seidel, Stephen Weeks

This program runs on the RPI and connects to the MQTT Mosquitto server on the RPI.
It receives JSON messages sent from the Android App on the RobotCar/Move topic,
and passes on the corresponding hex number to the FPGA on the UART to control
the robot's movement. 

Sample JSON message: {"UP":1,"DOWN":0,"RIGHT":0,"LEFT":0}

Values that FPGA responds to on serial write
    0x00 = Forward
    0x01 = Rotate Right
    0x02 = Rotate Left
    0x03 = Reverse
"""

# mqtt import
import time
import json
import serial
from config import *
import paho.mqtt.client as mqtt
client = mqtt.Client()

ser = serial.Serial ('/dev/ttyS0', 9600, serial.EIGHTBITS, serial.PARITY_NONE)

# MQTT callback methods
def on_connect(client, userdata, flags, rc):
    print("Tried to connect to MQTT server: {}:{}...result: {}".format(
        mqtt_server_host,
        mqtt_server_port,
        mqtt.connack_string(rc)))

    # Check whether the result from connect is the CONNACK_ACCEPTED connack code
    # If conection was successful subscribe to the RobotCar/Move topic
    if rc == mqtt.CONNACK_ACCEPTED:
        client.subscribe (
            "RobotCar/Move",
            qos=1
        )
        time.sleep(1)

def on_subscribe(client, userdata, mid, granted_qos):
    print("Subscribed with QoS: {}".format(granted_qos[0]))

def publish_data(data):
    client.publish(
        topic = "RobotCar/Motors",
        payload = "L: " + str(data[0]) + " R: " + str(data[1])
    )

# decodes JSON and writes corresponding message to serial port
def on_message(client, userdata, msg):
    m_decode = str(msg.payload.decode("utf-8","ignore"))
    print("received: ",m_decode)
    m_in = json.loads(m_decode)
    if m_in["UP"] == 1:
        ser.write(b'\x00')
        print("Forward")
    if m_in["RIGHT"] == 1:
        ser.write(b'\x01')
        print("Right")
    if m_in["LEFT"] == 1:
        ser.write(b'\x02')
        print("Left")
    if m_in["DOWN"] == 1:
        ser.write(b'\x03')
        print("Reverse")

# main program
client.on_connect = on_connect
client.on_subscribe = on_subscribe
client.on_message = on_message

# connect to the MQTT server (which runs locally on the RPI)
# subscribing to the command topic is handled in on_connect()
client.connect (
    host=mqtt_server_host,
    #port=mqtt_server_port,
    keepalive=mqtt_keepalive
 )

#client.loop_forever()
loop_count = 0
while True:
    client.loop()
    data = ser.readline()
    if len(data) > 2:
        publish_data(data)
    else:
        publish_data([0,0])
    if loop_count % 4 == 0:
        ser.reset_input_buffer()
    loop_count += 1
