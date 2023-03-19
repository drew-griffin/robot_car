/**
 * @file sys_init.h
 * 
 * @authors Stephen, Drew, Noah
 * @copyright Portland State University, 2023
 * 
 * @brief
 * This is the header file for initilization of the system for the PID_Controller
 * software.
 * 
 * MODIFICATION HISTORY:
 * ---------------------
 * Ver  Who Date    Changes
 * -----------------------------------
 * 0.01  SW 23-Feb-2023 First release
 * 1.00  TEAM 19-Mar-2023 Version 1 full functionality relase
************************************************************/

#ifndef SYS_INIT_H
#define SYS_INIT_H

#include "xstatus.h"
#include "platform.h"

/**
 * system_init() - sets up the system startup code
 * 
 * @return if system initialization was successful
*/
int system_init(void);

#endif // SYS_INIT_H
