/**
 * @file cntrl_logic.h
 * 
 * @authors Stephen, Drew, Noah
 * @copyright Portland State University, 2023
 * 
 * @brief
 * This is the header file for control functionality
 * 
 * MODIFICATION HISTORY:
 * ---------------------
 * Ver  Who Date    Changes
 * -----------------------------------
 * 0.01  SW 23-Feb-2023 First release
 * 1.00  TEAM 19-Mar-2023 Version 1 full functionality release
************************************************************/

#ifndef CNTRL_LOGIC_H
#define CNTRL_LOGIC_H

#include <stdint.h>
#include <stdbool.h>
#include "nexys4io.h"
#include "xparameters.h"
//#include "PmodENC544.h"
#include "myHB3IP.h"

/*********Peripheral Device Constants****************************/
// Definitions for peripheral NEXYS4IO
#define 	N4IO_DEVICE_ID		    XPAR_NEXYS4IO_0_DEVICE_ID
#define 	N4IO_BASEADDR		    XPAR_NEXYS4IO_0_S00_AXI_BASEADDR
// Definitions for PMOD HB3
#define 	HB3_LEFT_BA 			XPAR_MYHB3IP_0_S00_AXI_BASEADDR
#define 	HB3_RIGHT_BA			XPAR_MYHB3IP_1_S00_AXI_BASEADDR


/*********Control Structs****************************/
typedef struct user_io {
    uint8_t button_state;
    bool has_changed;
} user_io_t, *ptr_user_io_t;

/**
 * read_user_IO() - reads and returns user IO data
 * 
 * @param       pointer to a user IO struct
 * 
 * @return      updates user IO struct with current data used for updating display values
*/
void read_user_IO(ptr_user_io_t uIO);

/**
 * init_IO_struct() - setus up IO struct for user
 * 
 * @param       pointer to a user IO struct
 * 
 * @returns     initialized user IO struct
*/
void init_IO_struct(ptr_user_io_t uIO);

void display(void);

void update_pid(ptr_user_io_t uIO);

void set_wheel_directions(bool left_wheel, bool right_wheel);

void run_motors(bool flag, uint16_t sw);

/**
 * setpoint_to_duty_cycle
 * @brief Setpoint in 10bit range to duty cycle 
 * 
 * @param setpoint 
 * @return uint8_t 
 */
uint8_t setpoint_to_duty_cycle(uint16_t setpoint); 

/**
 * duty_cycle_to_rpm
 * @brief duty cycle to rpm 
 * 
 * @param duty_cycle 
 * @return uint8_t 
 */
uint8_t duty_cycle_to_rpm(uint8_t duty_cycle); 

/**
 * @brief convert from error rpm to setpoint 
 * @param rpm  
 * @return uint16_t setpoint 
 */
uint16_t setpoint_from_rpm(uint8_t rpm); 

/**
 * control_pid
 * @brief main pid control loop 
 * updates state based on file globals, and the following params
 * @param MOTOR (left or right)
 * @param motorDirection (associated with the global leftMotorDirection, rightMotorDirection)
 * @param *preverror (pointer to the left and right previous error adresses to be updated and used)
 * @param *i (pointer to the left and right total integration adresses to be updated and used) 
 */
void control_pid(uint32_t MOTOR, bool motorDirection, uint8_t *preverror, uint8_t *i);


#endif
