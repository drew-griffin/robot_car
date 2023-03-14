/**
 * @file uart.h
 * 
 * @authors Stephen, Drew, Noah
 * @copyright Portland State University, 2023
 * 
 * @brief
 * This is the header file handles the UART data
 * 
 * <pre>
 * MODIFICATION HISTORY:
 * ---------------------
 * Ver  Who Date    Changes
 * -----------------------------------
 * 1.00a SW 11-Mar-2023 First release
 * </pre>
************************************************************/

#ifndef UART_H
#define UART_H

#include <stdlib.h>
#include<stdio.h>
#include "xparameters.h"
#include "xuartlite.h"
#include <stdbool.h>

/**************** Macros ****************/
// UARTLITE
#define UARTLITE_DEVICE_ID      XPAR_UARTLITE_0_DEVICE_ID
#define UARTLITE_INTR_NUM       XPAR_INTC_0_UARTLITE_0_VEC_ID
#define UARLITE_BASE_ADDR       XPAR_UARTLITE_0_BASEADDR
#define UART_BUFF_SIZE          1024 // kilobyte of data
#define SEND_BUFF_SIZE          3 // byte of data
#define CHARACTER_MASK          48

// UART instance
XUartLite   UART_Inst; // UART instance

// Global variable setup
uint8_t uart_rx_buffer[UART_BUFF_SIZE];
bool uart_rx;
uint32_t uart_rx_buff_len;

uint8_t uart_tx_buffer[SEND_BUFF_SIZE];
bool uart_tx;
uint32_t uart_tx_buff_len;
bool uart_tx_processing;
volatile uint32_t TotalSentCount; 

void init_buffers(void);

/**
 * @brief IRQ handler for the UART rx pin
*/
void uart_rx_irq(void *CallBackRef);

/**
 * @brief IRQ handler for the UART tx pin
 * 
 */
void uart_tx_irq(void *CallBackRef, unsigned int EventData);
#endif  // UART_H
