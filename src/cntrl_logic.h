/**
 * @file cntrl_logic.h
 * 
 * @authors Stephen, Drew, Noah
 * @copyright Portland State University, 2023
 * 
 * @brief
 * This is the header file for control functionality
 * 
 * <pre>
 * MODIFICATION HISTORY:
 * ---------------------
 * Ver  Who Date    Changes
 * -----------------------------------
 * 1.00a SW 23-Feb-2023 First release
 * </pre>
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

void run_motors(bool flag);

#endif
