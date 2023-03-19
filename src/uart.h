/**
 * @file uart.h
 * 
 * @authors Stephen, Drew, Noah
 * @copyright Portland State University, 2023
 * 
 * @brief
 * This is the header file handles the UART data
 * 
 * MODIFICATION HISTORY:
 * ---------------------
 * Ver  Who Date    Changes
 * -----------------------------------
 * 0.01  SW 11-Mar-2023 First release
 * 1.00  TEAM 19-Mar-2023 Version 1 full functionality relase
************************************************************/

#ifndef UART_H
#define UART_H

#include <stdlib.h>
#include<stdio.h>
#include "xparameters.h"
#include "xuartlite.h"
#include "task.h"
#include "debug.h"
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
#define NUM_UARTS               2


// UART instance
XUartLite   UART_Inst_Pi; // UART instance for the Raspberry Pi connection 
XUartLite   UART_Inst_Ultra; //UART instance for the ultrasonic sensor

//enum for UART ports. To add ports, 
//add a UartLite instance, increase NUM_UARTS, 
//and add a descriptive name below. 
enum uart_port_t {
    PI, 
    ULTRA
}; 

/* structure to map out UART control. 
   Uses single struct, array format 
   */
typedef struct{
    uint8_t rx_buffer[NUM_UARTS][UART_BUFF_SIZE]; //rx buffers for the UART instances
    uint8_t tx_buffer[NUM_UARTS][SEND_BUFF_SIZE]; //tx buffers for the UART instances
    uint32_t rx_buff_len[NUM_UARTS];              //rx buffer length counter for the UART instance
    uint32_t tx_buff_len[NUM_UARTS];              //tx buffer length counter 
    bool tx[NUM_UARTS];                           //tx interrupt received 
    bool rx[NUM_UARTS];                           //rx received boolean flag 
    bool tx_processing[NUM_UARTS];                //tx is processing 
} uart_t; 


void init_buffers(void);

/**
 * @brief IRQ handler for the UART rx pin coming in from Raspberry pi
*/
void rx_pi_irq(void *CallBackRef);

/**
 * @brief IRQ handler for the UART rx pin coming in from ultrasonic
*/
void rx_ultra_irq(void *CallBackRef);

#endif  // UART_H
