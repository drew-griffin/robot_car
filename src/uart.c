/**
 * @file uart.c
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
    uart_rx_buff_len = false;
    uart_rx_processing = false;
    for(int i = 0; i < UART_BUFF_SIZE; i++) 
    {
        uart_rx_buffer[i] = 0;
    }

    
    uart_tx = false;
    uart_tx_buff_len = false;
    uart_tx_processing = false;
    for(int i = 0; i < SEND_BUFF_SIZE; i++) 
    {
        uart_tx_buffer[i] = 0;
    }
}

// UART RX IRQ
// idk if we need the call back here since we already use the base address
void uart_rx_irq(void *CallBackRef)
{
    NX4IO_SSEG_setDecPt(SSEGLO, DIGIT0, dprx_on);
    if (!uart_rx_processing)
    {
        // use low level library to read tell buffer is clear
        while(!XUartLite_IsReceiveEmpty(UARLITE_BASE_ADDR)) {
            // receiving the byte hear clears the buffer
            uart_rx_buffer[uart_rx_buff_len] = XUartLite_RecvByte(UARLITE_BASE_ADDR);
            uart_rx_buff_len += 1;
            uart_rx = true;
        }
    }
    dprx_on = (dprx_on) ? false : true;
}

//UART TX IRQ
void uart_tx_irq(void *CallBackRef, unsigned int EventData)
{
    TotalSentCount = EventData; 
}