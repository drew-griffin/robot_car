/**
 * @file cntrl_logic.c
 * 
 * @authors Stephen, Drew, Noah
 * @copyright Portland State University, 2023
 * 
 * @brief
 * This is the source file for control functionality to the
 * push buttons, switches, encoder, and display.
 * 
 * <pre>
 * MODIFICATION HISTORY:
 * ---------------------
 * Ver  Who Date    Changes
 * -----------------------------------
 * 1.00a SW 23-Feb-2023 First release
 * </pre>
************************************************************/

#include "cntrl_logic.h"
#include "microblaze_sleep.h"
#include "uart.h"
#include "debug.h"

/********************Control Constants********************/
#define NBTNS                           5

/********************Local Variables********************/
static uint16_t setpoint = 512;
static bool pwmEnable = true;					// true to enable PWM output
static bool leftMotorForward = false;	// disregard boolean value, name says it all
static bool rightMotorForward = true;	// motors have opposite direction for forward movement

/**
 * read_user_IO() - reads user IO
 * 
 * @brief       Function that takes in a user IO struct and updates if state has changed.
 *              Passed along to display/processing system to handle
 * 
 * @param       pointer to user IO struct
 * 
 * @returns     updated user IO struct if anything has changed
*/
void read_user_IO(ptr_user_io_t uIO) {
    uint8_t btns = NX4IO_getBtns();
    if(uIO->button_state != btns) {
        uIO->button_state = btns;
        uIO->has_changed = true;
    }
}

/**
 * init_IO_struct() - setus up IO struct for user
 * 
 * @brief       Function that initializes uIO struct at startup
 * 
 * @param       pointer to a user IO struct
 * 
 * @returns     initialized user IO struct
*/
void init_IO_struct(ptr_user_io_t uIO) {
    uIO->button_state = 0x00;
    uIO->has_changed = true;
}

void update_pid(ptr_user_io_t uIO) {
    static uint8_t prev_btn = 0xff;

    if(uIO->has_changed) {
        if(prev_btn != uIO->button_state) {
            prev_btn = uIO->button_state;
            uint8_t btnMask = 0x01;
            for (int i = 0; i < NBTNS; i++) {
                // look at the buttons one at a time. If a button was pushed
                if (prev_btn & (btnMask << i)) {
                    // check btn[i] for changes
                    switch (i) {
                        //iterate through the buttons
                        case 0: // btnR: turn right 2 seconds
                    		HB3_setPWM(HB3_LEFT_BA, pwmEnable, setpoint, leftMotorForward);
                    		HB3_setPWM(HB3_RIGHT_BA, pwmEnable, setpoint, !rightMotorForward);
                    		usleep(2000 * 1000);
                    		HB3_setPWM(HB3_LEFT_BA, pwmEnable, 0, leftMotorForward);
                    		HB3_setPWM(HB3_RIGHT_BA, pwmEnable, 0, !rightMotorForward);
                    		usleep(10 * 1000);
                            break;

                        case 1: // btnL: turn left 2 seconds
                    		HB3_setPWM(HB3_LEFT_BA, pwmEnable, setpoint, !leftMotorForward);
                    		HB3_setPWM(HB3_RIGHT_BA, pwmEnable, setpoint, rightMotorForward);
                    		usleep(2000 * 1000);
                    		HB3_setPWM(HB3_LEFT_BA, pwmEnable, 0, !leftMotorForward);
                    		HB3_setPWM(HB3_RIGHT_BA, pwmEnable, 0, rightMotorForward);
                    		usleep(10 * 1000);
                            break;

                        case 2: // btnD: backward 1s
                    		HB3_setPWM(HB3_LEFT_BA, pwmEnable, setpoint, !leftMotorForward);
                    		HB3_setPWM(HB3_RIGHT_BA, pwmEnable, setpoint, !rightMotorForward);
                    		usleep(1000 * 1000);
                    		HB3_setPWM(HB3_LEFT_BA, pwmEnable, 0, !leftMotorForward);
                    		HB3_setPWM(HB3_RIGHT_BA, pwmEnable, 0, !rightMotorForward);
                    		usleep(10 * 1000);
                            break;

                        case 3: // btnU: forward 1s
                    		HB3_setPWM(HB3_LEFT_BA, pwmEnable, setpoint, leftMotorForward);
                    		HB3_setPWM(HB3_RIGHT_BA, pwmEnable, setpoint, rightMotorForward);
                    		usleep(1000 * 1000);
                    		HB3_setPWM(HB3_LEFT_BA, pwmEnable, 0, leftMotorForward);
                    		HB3_setPWM(HB3_RIGHT_BA, pwmEnable, 0, rightMotorForward);
                            break;

                        case 4: // btnC: forward, back, left, right
                    		HB3_setPWM(HB3_LEFT_BA, pwmEnable, setpoint, leftMotorForward);
                    		HB3_setPWM(HB3_RIGHT_BA, pwmEnable, setpoint, rightMotorForward);
                    		usleep(2000 * 1000);
                    		HB3_setPWM(HB3_LEFT_BA, pwmEnable, 0, leftMotorForward);
                    		HB3_setPWM(HB3_RIGHT_BA, pwmEnable, 0, rightMotorForward);
                    		usleep(10 * 1000);
                    		HB3_setPWM(HB3_LEFT_BA, pwmEnable, setpoint, !leftMotorForward);
                    		HB3_setPWM(HB3_RIGHT_BA, pwmEnable, setpoint, !rightMotorForward);
                    		usleep(2000 * 1000);
                    		HB3_setPWM(HB3_LEFT_BA, pwmEnable, 0, !leftMotorForward);
                    		HB3_setPWM(HB3_RIGHT_BA, pwmEnable, 0, !rightMotorForward);
                    		usleep(10 * 1000);
                    		HB3_setPWM(HB3_LEFT_BA, pwmEnable, setpoint, !leftMotorForward);
                    		HB3_setPWM(HB3_RIGHT_BA, pwmEnable, setpoint, rightMotorForward);
                    		usleep(3000 * 1000);
                    		HB3_setPWM(HB3_LEFT_BA, pwmEnable, 0, !leftMotorForward);
                    		HB3_setPWM(HB3_RIGHT_BA, pwmEnable, 0, rightMotorForward);
                    		usleep(10 * 1000);
                    		HB3_setPWM(HB3_LEFT_BA, pwmEnable, setpoint, leftMotorForward);
                    		HB3_setPWM(HB3_RIGHT_BA, pwmEnable, setpoint, !rightMotorForward);
                    		usleep(3000 * 1000);
                    		HB3_setPWM(HB3_LEFT_BA, pwmEnable, 0, leftMotorForward);
                    		HB3_setPWM(HB3_RIGHT_BA, pwmEnable, 0, !rightMotorForward);
                    		usleep(10 * 1000);
                            break;

                        default: //shouldn't get here
                            break;
                    }
                }
            }
        }
        uIO->has_changed = false;
    }
}

/**
 * set_wheel_direction() - sets the direction of the two wheels
 * 
 * @brief		sets wheel directions to be run after recv a message
 * 
 * @param		left_wheel  - left wheel direction
 * @param		right_wheel - right wheel direction
 * 
*/
void set_wheel_directions(bool left_wheel, bool right_wheel)
{
	leftMotorForward = left_wheel;
	rightMotorForward = right_wheel;
	if (1 == DEBUG)
	{
		xil_printf("left wheel is %d and right wheel is %d\r\n", leftMotorForward, rightMotorForward);
	}
}

/**
 * run_motors() - sets the motor controls
 * 
 * @brief		turns the motors on or off
 * 
 * @param		flag - tells us if we are running or stopping
 * 				the motors
*/
void run_motors(bool flag)
{
	if (1 == DEBUG)
	{
		xil_printf("For motor run left wheel is %d and right wheel is %d\r\n", leftMotorForward, rightMotorForward);
	}
	if(flag)
	{
		HB3_setPWM(HB3_LEFT_BA, pwmEnable, setpoint, leftMotorForward);
        HB3_setPWM(HB3_RIGHT_BA, pwmEnable, setpoint, rightMotorForward);
	}
	else
	{
		HB3_setPWM(HB3_LEFT_BA, pwmEnable, 0, leftMotorForward);
		HB3_setPWM(HB3_RIGHT_BA, pwmEnable, 0, rightMotorForward);
	}
}

/**
 * display() - displays wheel speed
 *
 * @brief       displays each wheel's speed after PID
*/
void display(void) {
		uint32_t HB3_RPM_left = HB3_getRPM(HB3_LEFT_BA);
		uint32_t HB3_RPM_right = HB3_getRPM(HB3_RIGHT_BA);

		NX4IO_SSEG_setDigit(SSEGHI, DIGIT7, CC_BLANK);
	    NX4IO_SSEG_setDigit(SSEGHI, DIGIT6, CC_BLANK);
	    NX4IO_SSEG_setDigit(SSEGHI, DIGIT5, HB3_RPM_left/10);
	    NX4IO_SSEG_setDigit(SSEGHI, DIGIT4, HB3_RPM_left%10);
	    NX4IO_SSEG_setDigit(SSEGLO, DIGIT3, CC_BLANK);
	    NX4IO_SSEG_setDigit(SSEGLO, DIGIT2, CC_BLANK);
	    NX4IO_SSEG_setDigit(SSEGLO, DIGIT1, HB3_RPM_right/10);
	    NX4IO_SSEG_setDigit(SSEGLO, DIGIT0, HB3_RPM_right%10);

}
