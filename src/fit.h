/**
 * @file fit.h
 * 
 * @authors Stephen, Drew, Noah
 * @copyright Portland State University, 2023
 * 
 * @brief
 * This is the header file for FIT interrupt handler
 * 
 * MODIFICATION HISTORY:
 * ---------------------
 * Ver  Who Date    Changes
 * -----------------------------------
 * 0.01  SW 23-Feb-2023 First release
 * 1.00  TEAM 19-Mar-2023 Version 1 full functionality relase
************************************************************/

#ifndef FIT_H
#define FIT_H

#include "xparameters.h"
#include "cntrl_logic.h"

/*********Peripheral Device Constants****************************/
// Definitions for Fixed Interval timer - 100 MHz input clock, 2Hz output
#define 	FIT_INTR_NUM		XPAR_MICROBLAZE_0_AXI_INTC_FIT_TIMER_0_INTERRUPT_INTR

/**
 * FIT_Handler() - blinks heartbeat LED every 0.25 seconds
 * 
 * @note: tied to INTC in sys_init.c
*/
void FIT_Handler(void);

#endif
