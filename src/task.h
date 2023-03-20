/**
 * @file task.h
 * 
 * @authors Stephen, Drew, Noah
 * @copyright Portland State University, 2023
 * 
 * @brief
 * This is the header file handles the task
 * scheduler functions
 * 
 * MODIFICATION HISTORY:
 * ---------------------
 * Ver  Who Date    Changes
 * -----------------------------------
 * 0.01  SW 11-Mar-2023 First release
 * 1.00  TEAM 19-Mar-2023 Version 1 full functionality release
************************************************************/

#ifndef TASK_H
#define TASK_H

#include <stdlib.h>
#include <stdio.h>

typedef enum
{
    idle,
    processing,
    run,
    end,
} task_t;

void idle_state(void);
void processing_state(void);
void run_state(void);
void end_state(void);

#endif
