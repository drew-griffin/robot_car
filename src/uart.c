/**
 * @file uart.c
 *
 * @authors Stephen, Drew, Noah
 * @copyright Portland State University, 2023
 *
 * @brief
 * This is the source file for the UART IRQ handler
 *
 * MODIFICATION HISTORY:
 * ---------------------
 * Ver  Who Date    Changes
 * -----------------------------------
 * 0.01  SW 23-Feb-2023 First release
 * 1.00  TEAM 19-Mar-2023 Version 1 full functionality release
 ************************************************************/

#include "uart.h"
#include "nexys4io.h"
#include "xuartlite_l.h"

// global variables
uint16_t millimeters = 0;
uart_t UART;

// local variables
static bool dprx_on = true;
static bool dprx_ultra_on = true;

extern task_t run_state_t; // used to end the state if motor is too near something

void init_buffers(void)
{
     //initialize all rx ULTRA paramaters to false/0
    UART.rx[PI] = false;
    UART.rx_buff_len[PI] = 0;
    UART.rx[ULTRA] = false;
    UART.rx_buff_len[ULTRA] = 0;

    //set all rx buffers to 0
    for (int i = 0; i < NUM_UARTS; i++)
    {
        for (int j = 0; j < UART_BUFF_SIZE; j++)
        {
            UART.rx_buffer[i][j] = 0;
        }
    }


    //initialize all tx PI paramaters to false/0
    UART.tx[PI] = false; 
    UART.tx_buff_len[PI] = 0; 
    UART.tx_processing[PI] = false; 
    UART.tx[ULTRA] = false; 
    UART.tx_buff_len[ULTRA] = 0; 
    UART.tx_processing[ULTRA] = false; 

    //set all tx buffers to 0
    for (int i = 0; i < NUM_UARTS; i++)
    {
        for (int j = 0; j < SEND_BUFF_SIZE; j++)
        {
            UART.tx_buffer[i][j] = 0;
        }
    }
}

// UART RX IRQ for Raspberry Pi
void rx_pi_irq(void *CallBackRef)
{
    NX4IO_SSEG_setDecPt(SSEGLO, DIGIT0, dprx_on);
    // use low level library to read tell buffer is clear
    while (!XUartLite_IsReceiveEmpty(UARLITE_BASE_ADDR_PI))
    {
        // receiving the byte hear clears the buffer
        UART.rx_buffer[PI][UART.rx_buff_len[PI]] = XUartLite_RecvByte(UARLITE_BASE_ADDR_PI);
        UART.rx_buff_len[PI] += 1;
        UART.rx[PI] = true;
    }
    dprx_on = (dprx_on) ? false : true;
}

// UART RX IRQ for UltraSonic sensor
void rx_ultra_irq(void *CallBackRef)
{
    NX4IO_SSEG_setDecPt(SSEGLO, DIGIT1, dprx_ultra_on);
    // use low level library to read tell buffer is clear
    while (!XUartLite_IsReceiveEmpty(UARLITE_BASE_ADDR_ULTRA))
    {
        XUartLite_Recv(&UART_Inst_Ultra, &UART.rx_buffer[ULTRA][0], 2);
        // millimeters is formed from the two bytes sent in by the ultrasonic sensor 
        millimeters = (UART.rx_buffer[ULTRA][1] << 16) | UART.rx_buffer[ULTRA][0];
    }

    XUartLite_ResetFifos(&UART_Inst_Ultra);
    dprx_ultra_on = (dprx_ultra_on) ? false : true;
}

