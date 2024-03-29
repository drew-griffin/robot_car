/**
 * @file main.c
 *
 * @authors Stephen, Drew, Noah
 * @copyright Portland State University, 2023
 *
 * @brief This file implements the main infinite forever loop for the embedded system
 *
 * MODIFICATION HISTORY:
 * ---------------------
 * Ver  Who Date    Changes
 * -----------------------------------
 * 0.01  SW 23-Feb-2023 First release
 * 1.00  TEAM 19-Mar-2023 Version 1 full functionality release
 ************************************************************/


#include <stdlib.h>
#include <stdio.h>
#include "xil_printf.h"
#include "microblaze_sleep.h"
#include "sys_init.h"
#include "cntrl_logic.h"
#include "debug.h"
#include "uart.h"
#include "task.h"


/*****************PID Control Instances*****************/
static ptr_user_io_t uIO;

/*****************Global State*****************/
task_t run_state_t = idle;
void (*task_scheduler[4])() = {idle_state, processing_state, run_state, end_state};

int main()
{

    xil_printf("PID Motor Controller System Starting\r\n\n");

    uint32_t sts = system_init();
    if (XST_SUCCESS != sts) {
        xil_printf("FATAL(main): System initialization failed\r\n");
        return 1;
    }

    init_IO_struct(uIO);
    init_buffers();

    microblaze_enable_interrupts();
    NX4IO_setLEDs(0x00000000); // clear LEDs, odd behavior where they turn on
    while(1)
    {
        task_scheduler[run_state_t]();
    }
    
    microblaze_disable_interrupts();
    cleanup_platform();
    return 0;
}

