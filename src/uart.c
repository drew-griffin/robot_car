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
    uart_rx_pi = false;
    uart_rx_pi_buff_len = false;
    for(int i = 0; i < UART_BUFF_SIZE; i++) 
    {
        uart_rx_pi_buffer[i] = 0;
    }

    uart_rx_ultra = false;
    uart_rx_ultra_buff_len = false;
    for(int i = 0; i < UART_BUFF_SIZE; i++) 
    {
        uart_rx_ultra_buffer[i] = 0;
    }

    uart_tx_pi = false;
    uart_tx_pi_buff_len = false;
    uart_tx_pi_processing = false;
    for(int i = 0; i < SEND_BUFF_SIZE; i++) 
    {
        uart_tx_pi_buffer[i] = 0;
    }

    uart_tx_ultra = false;
    uart_tx_ultra_buff_len = false;
    uart_tx_ultra_processing = false;
    for(int i = 0; i < SEND_BUFF_SIZE; i++) 
    {
        uart_tx_ultra_buffer[i] = 0;
    }
}

// UART RX IRQ for Raspberry Pi 
// idk if we need the call back here since we already use the base address
void uart_rx_pi_irq(void *CallBackRef)
{
    NX4IO_SSEG_setDecPt(SSEGLO, DIGIT0, dprx_on);
    // use low level library to read tell buffer is clear
    while(!XUartLite_IsReceiveEmpty(UARLITE_BASE_ADDR_PI)) {
        // receiving the byte hear clears the buffer
        uart_rx_pi_buffer[uart_rx_pi_buff_len] = XUartLite_RecvByte(UARLITE_BASE_ADDR_PI);
        uart_rx_pi_buff_len += 1;
        uart_rx_pi = true;
    }
    dprx_on = (dprx_on) ? false : true;
}

// UART RX IRQ for UltraSonic sensor 
// idk if we need the call back here since we already use the base address
void uart_rx_ultra_irq(void *CallBackRef)
{
    // To do 
    xil_printf("Hooray. We got a response\n\r"); 
}

//UART TX IRQ
void uart_tx_pi_irq(void *CallBackRef, unsigned int EventData)
{
    TotalSentCount = EventData; 
}