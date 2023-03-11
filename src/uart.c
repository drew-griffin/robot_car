/**
 * @file sys_init.c
 * 
 * @authors Stephen, Drew, Noah
 * @copyright Portland State University, 2023
 * 
 * @brief
 * This is the source file for the UART IRQ handler
 * 
 * <pre>
 * MODIFICATION HISTORY:
 * ---------------------
 * Ver  Who Date    Changes
 * -----------------------------------
 * 1.00a SW 23-Feb-2023 First release
 * </pre>
************************************************************/

#include "uart.h"
#include "nexys4io.h"
#include "xuartlite_l.h"

// local variables
static bool dprx_on = true;

void init_buffers(void)
{
    uart_rx = false;
    uart_buff_len = false;
    uart_processing = false;
    for(int i = 0; i < UART_BUFF_SIZE; i++) 
    {
        uart_buffer[i] = 0;
    }
}

// UART RX IRQ
// idk if we need the call back here since we already use the base address
void uart_rx_irq(void *CallBackRef)
{
    NX4IO_SSEG_setDecPt(SSEGLO, DIGIT0, dprx_on);
    if (!uart_processing)
    {
        // use low level library to read tell buffer is clear
        while(!XUartLite_IsReceiveEmpty(UARLITE_BASE_ADDR)) {
            // receiving the byte hear clears the buffer
            uart_buffer[uart_buff_len] = XUartLite_RecvByte(UARLITE_BASE_ADDR);
            uart_buff_len += 1;
            uart_rx = true;
        }
    }
    dprx_on = (dprx_on) ? false : true;
}