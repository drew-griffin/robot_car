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
// UARTLITE PI (uartlite_0)
#define UARTLITE_DEVICE_ID_PI      XPAR_UARTLITE_0_DEVICE_ID
#define UARTLITE_INTR_NUM_PI       XPAR_INTC_0_UARTLITE_0_VEC_ID
#define UARLITE_BASE_ADDR_PI       XPAR_UARTLITE_0_BASEADDR

// UARTLITE ULTRASONICE SENSOR (uartlite_1)
#define UARTLITE_DEVICE_ID_ULTRA      XPAR_UARTLITE_2_DEVICE_ID
#define UARTLITE_INTR_NUM_ULTRA       XPAR_INTC_0_UARTLITE_2_VEC_ID
#define UARLITE_BASE_ADDR_ULTRA       XPAR_UARTLITE_2_BASEADDR

#define UART_BUFF_SIZE          1024 // kilobyte of data
#define SEND_BUFF_SIZE          5 // bytes of data
#define CHARACTER_MASK          48

// UART instance
XUartLite   UART_Inst_Pi; // UART instance for the Raspberry Pi connection 
XUartLite   UART_Inst_Ultra; //UART instance for the ultrasonic sensor

// Global variable setup
uint8_t uart_rx_pi_buffer[UART_BUFF_SIZE];
bool uart_rx_pi;
uint32_t uart_rx_pi_buff_len;

uint8_t uart_rx_ultra_buffer[UART_BUFF_SIZE];
bool uart_rx_ultra;
uint32_t uart_rx_ultra_buff_len;

uint8_t uart_tx_pi_buffer[SEND_BUFF_SIZE];
bool uart_tx_pi;
uint32_t uart_tx_pi_buff_len;
bool uart_tx_pi_processing;

uint8_t uart_tx_ultra_buffer[SEND_BUFF_SIZE];
bool uart_tx_ultra;
uint32_t uart_tx_ultra_buff_len;
bool uart_tx_ultra_processing;

volatile uint32_t TotalSentCount; 

void init_buffers(void);

/**
 * @brief IRQ handler for the UART rx pin coming in from Raspberry pi
*/
void uart_rx_pi_irq(void *CallBackRef);

/**
 * @brief IRQ handler for the UART rx pin coming in from ultrasonic
*/
void uart_rx_ultra_irq(void *CallBackRef);

/**
 * @brief IRQ handler for the UART tx pin
 * 
 */
void uart_tx_pi_irq(void *CallBackRef, unsigned int EventData);
#endif  // UART_H
