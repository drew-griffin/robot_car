/**
 * @file task.c
 *
 * @authors Stephen, Drew, Noah
 * @copyright Portland State University, 2023
 *
 * @brief
 * This is the source file for implementing task functions
 * 
 * MODIFICATION HISTORY:
 * ---------------------
 * Ver  Who Date    Changes
 * -----------------------------------
 * 0.01  SW 11-Mar-2023 First release
 * 1.00  TEAM 19-Mar-2023 Version 1 full functionality release
 ************************************************************/

#include <stdbool.h>
#include "task.h"
#include "uart.h"
#include "nexys4io.h"
#include "debug.h"
#include "cntrl_logic.h"
#include "microblaze_sleep.h"
#include "myHB3IP.h"

// shared global state
extern task_t run_state_t;
extern uint16_t millimeters; 
extern uart_t   UART; 
bool running_motors = false;
extern uint8_t run_count;

/**************** Macros ****************/
#define GET_DIR 0x01
#define LED_RUN 0xFFFF

/**************** Local Global State ****************/
/**
 * Mapping is [0] == forward : [1] == backwards
 */
//                               Forw   Back
static bool left_direction[2] = {false, true};
static bool right_direction[2] = {true, false};
static uint8_t uart_msg;
enum run_times
{
    forward,
    right,
    left,
    back
};
static bool left_wheel, right_wheel;

/**************** Local Funcs ****************/
static void display_data(void);

void idle_state(void)
{
    if (UART.rx[PI])
    {
        // need to turn of uart interrupt to process the data
        // so we can operate without being blocked
        XUartLite_DisableInterrupt(&UART_Inst_Pi);
        run_state_t = processing;
        if (1 == DEBUG)
        {
            xil_printf("switching from idle_state to processing state\r\n");
        }
    }
    else
    {
        display_data();
    }
}

void processing_state(void)
{
    // we just want the last message stored in the buffer,
    // this way we just handle the last command given.
    // uart_buff_len always points to the next slot so last
    // message is N-1
    uint32_t last_message = UART.rx_buff_len[PI] - 1;
    uart_msg = UART.rx_buffer[PI][last_message];

    if (1 == DEBUG)
    {
        xil_printf("packet message was %d\r\n", uart_msg);
    }

    // 8-bit packet with bit0 == right direction and bit1 == left direction
    right_wheel = right_direction[(uart_msg & GET_DIR)];
    left_wheel = left_direction[((uart_msg >> 1) & GET_DIR)];
    set_wheel_directions(left_wheel, right_wheel);
    run_state_t = run;
}

void run_state(void)
{
    uint8_t motor_run_time;
    uint16_t sw = NX4IO_getSwitches();

    switch (uart_msg & 0x03)
    {
    case forward:
        motor_run_time = 3;
        //send the byte to receive mm to from the ultrasonic sensor 
        UART.tx_buffer[ULTRA][0] = 0x55;
        while(XUartLite_IsSending(&UART_Inst_Ultra)){}; 
        XUartLite_Send(&UART_Inst_Ultra, &UART.tx_buffer[ULTRA][0], 1);
        break;
    case right:
        motor_run_time = 1;
        break;
    case left:
        motor_run_time = 1;
        break;
    case back:
        motor_run_time = 3;
        break;
    default:
        // should never get here
        motor_run_time = 3;
        break;
    }

    if (1 == DEBUG)
    {
        xil_printf("running with direction code %d for amount of FIT ticks: %d\r\n",
                   uart_msg, motor_run_time);
    }
    running_motors = true;

    if (1 == DEBUG)
    {
        xil_printf("Distance in mm is %d\n\r", millimeters);
    }
    // if we are closer than 200mm (~8 inches) and direction is forward HALT
    if ((millimeters < 200) && (uart_msg & 0x03) == forward && ((sw & 0x1) == 0x1))
    {
        run_state_t = end;
        return; 
    }

    NX4IO_setLEDs(LED_RUN);
    
    while (run_count <= motor_run_time)
    {
        run_motors(true, sw);
        if (!XUartLite_IsSending(&UART_Inst_Pi))
        {
            UART.tx_buffer[PI][0] = (left_wheel) ? 0x2D : 0x2B;
            UART.tx_buffer[PI][1] = HB3_getRPM(HB3_LEFT_BA);
            UART.tx_buffer[PI][2] = (right_wheel) ? 0x2B : 0x2D;
            UART.tx_buffer[PI][3] = HB3_getRPM(HB3_RIGHT_BA);
            UART.tx_buffer[PI][4] = 0x0A; // \n
            if ((UART.tx_buffer[PI][1] != 0) && (UART.tx_buffer[PI][3] != 0))
            {
                XUartLite_Send(&UART_Inst_Pi, &UART.tx_buffer[PI][0], 5);
            }
        }
        display();
    } // wait here for the request time
    running_motors = false;
    run_count = 0;
    NX4IO_setLEDs(~LED_RUN);
    run_motors(false, 0);
    usleep(10 * 1000);

    run_state_t = end;
}

void end_state(void)
{
    if (1 == DEBUG)
    {
        xil_printf("Cleaning up system\r\n");
    }
    // say we have processed data for next run
    UART.rx[PI] = false;
    // reset buffer pointer
    UART.rx_buff_len[PI] = 0;
    // clear any data that came in while we processed
    XUartLite_ResetFifos(&UART_Inst_Pi);
    // re-enable the interrupt
    XUartLite_EnableInterrupt(&UART_Inst_Pi);
    // return to beginning
    run_state_t = idle;
}

/**************** Helper Functions *****************/
static void display_data(void)
{
    NX4IO_SSEG_setDigit(SSEGHI, DIGIT7, CC_BLANK);
    NX4IO_SSEG_setDigit(SSEGHI, DIGIT6, CC_BLANK);
    NX4IO_SSEG_setDigit(SSEGHI, DIGIT5, CC_0);
    NX4IO_SSEG_setDigit(SSEGHI, DIGIT4, CC_0);
    NX4IO_SSEG_setDigit(SSEGLO, DIGIT3, CC_BLANK);
    NX4IO_SSEG_setDigit(SSEGLO, DIGIT2, CC_BLANK);
    NX4IO_SSEG_setDigit(SSEGLO, DIGIT1, CC_0);
    NX4IO_SSEG_setDigit(SSEGLO, DIGIT0, CC_0);
}
