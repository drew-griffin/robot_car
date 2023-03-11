#include <stdlib.h>
#include <stdio.h>
#include "microblaze_sleep.h"
#include "xparameters.h"
#include "xstatus.h"
#include "nexys4io.h"
#include "xintc.h"
#include "xuartlite.h"
#include "platform.h"
#include "xil_printf.h"
#include "xuartlite_l.h"

// forward call functions
int system_init(void);
void fit_handler(void);
void uart_rx_irq(void *CallBackRef);

/**************** Macros ****************/
// INTC
#define INTC_DEVICE_ID          XPAR_INTC_0_DEVICE_ID
#define INTC_BASEADDR           XPAR_INTC_0_BASEADDR
// FIT
#define FIT_IN_CLOCK_FREQ_HZ    XPAR_CPU_CORE_CLOCK_FREQ_HZ
#define FIT_INTR_NUM            XPAR_MICROBLAZE_0_AXI_INTC_FIT_TIMER_0_INTERRUPT_INTR
// Nexys4IO
#define N4IO_DEVICE_ID          XPAR_NEXYS4IO_0_DEVICE_ID
#define N4IO_BASEADDR           XPAR_NEXYS4IO_0_S00_AXI_BASEADDR
// UARTLITE
#define UARTLITE_DEVICE_ID      XPAR_UARTLITE_0_DEVICE_ID
#define UARTLITE_INTR_NUM       XPAR_INTC_0_UARTLITE_0_VEC_ID
#define UARLITE_BASE_ADDR       XPAR_UARTLITE_0_BASEADDR
#define UART_BUFF_SIZE          1024 // kilobyte of data
#define CHARACTER_MASK          48

// variable instances
XIntc       INTC_Inst; // INTC instance
XUartLite   UART_Inst; // UART instance

// global variables
static volatile uint8_t uart_buffer[UART_BUFF_SIZE] = {0};
static volatile bool uart_rx = false;
static volatile uint32_t uart_buff_len = 0;
static bool dprx_on = false;
static volatile bool uart_processing = false;

int main()
{
    xil_printf("Stephen's UART prototype workshop!!!!!\r\n");

    uint32_t sts = system_init();
    if (sts != XST_SUCCESS)
    {
        xil_printf("FATAL(main): System failed to initialize!!!!\r\n");
    }

    microblaze_enable_interrupts();
    NX4IO_setLEDs(0xFFFF);
    usleep(1000 * 500);
    NX4IO_setLEDs(0x0000);

    xil_printf("Main processing starting\r\n");

    for(;;)
    {
        if(uart_rx)
        {
            uart_processing = true;
            for(int i = 0; i < uart_buff_len; i++)
            {
                NX4IO_SSEG_setDigit(SSEGLO, DIGIT0, (uart_buffer[i] % 48));
            }
            uart_buff_len = 0;
            uart_rx = false;
            uart_processing = false;
        }
    }
}

// initialize test system
int system_init(void)
{
    uint32_t status;

    init_platform();

    status = XUartLite_Initialize(&UART_Inst, UARTLITE_DEVICE_ID);
    if (status != XST_SUCCESS)
    {
        return XST_FAILURE;
    }

    status = XUartLite_SelfTest(&UART_Inst);
    if (status != XST_SUCCESS)
    {
        return XST_FAILURE;
    }

    status = NX4IO_initialize(N4IO_BASEADDR);
    if (status != XST_SUCCESS)
    {
        return XST_FAILURE;
    }

    status = XIntc_Initialize(&INTC_Inst, INTC_DEVICE_ID);
    if (status != XST_SUCCESS)
    {
        return XST_FAILURE;
    }

    status = XIntc_Connect(&INTC_Inst, FIT_INTR_NUM,
                            (XInterruptHandler)fit_handler,
                            (void*)0);
    if (status != XST_SUCCESS)
    {
        return XST_FAILURE;
    }
    // can connect the IRQ as normal
    status = XIntc_Connect(&INTC_Inst, UARTLITE_INTR_NUM,
                            (XInterruptHandler)uart_rx_irq,
                            (void *)&UART_Inst);
    if (status != XST_SUCCESS)
    {
        return XST_FAILURE;
    }

    status = XIntc_Start(&INTC_Inst, XIN_REAL_MODE);
    if (status != XST_SUCCESS)
    {
        return XST_FAILURE;
    }

    XIntc_Enable(&INTC_Inst, FIT_INTR_NUM);
    XIntc_Enable(&INTC_Inst, UARTLITE_INTR_NUM);

    // need to separatley enable the interrupt because that isn't confusing or anything
    XUartLite_EnableInterrupt(&UART_Inst);

    return XST_SUCCESS;
}

// FIT interrupt
void fit_handler(void)
{
    static bool isInitialized = false; // starts up led heartbeat
    static bool dpOn;

    if (!isInitialized) {
        dpOn = true;
        isInitialized = true; 
    }

    dpOn = (dpOn) ? false : true;
    NX4IO_SSEG_setDecPt(SSEGHI, DIGIT7, dpOn);
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
