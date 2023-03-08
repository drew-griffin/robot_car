

/***************************** Include Files *******************************/
#include "myHB3ip.h"

/***************************** Global variables ****************************/
static uint32_t baseAddress = 0L;
static bool isInitialized = false;

/************************** Function Definitions ***************************/
/**
 * Initializes the PmodENC544 peripheral and runs the self-test
 *
 * @param   baseaddr_p  base address of the PmodENC544 peripheral
 *
 * @return  returns XST_SUCCESS if the PmodENC544 is intialized, false otherwise
 *
 */
XStatus HB3_initialize(uint32_t baseaddr_p)
{
    XStatus sts;
    
    if (baseaddr_p == NULL) {
        isInitialized = false;
        return XST_FAILURE;  
    }
    
    if (isInitialized) {
        return XST_SUCCESS;
    }
    else {
        baseAddress = baseaddr_p;
        sts = MYHB3IP_Reg_SelfTest(baseAddress);
        if (sts != XST_SUCCESS)
            return XST_FAILURE;
        isInitialized = true;
    }
    return XST_SUCCESS; 
}

/**
 * Sets the PWM output of the HB3
 *
 * @param   enable enable signal to turn the PWM signal on or off
 *          DC     u16 value truncated to 10 bits to set duty cycle
 *
 * @return  void
 *
 */
void HB3_setPWM(bool enable, u16 DC)
{
	u32 cntlreg;

	// initialize the value depending on whether PWM is enabled
	// enable is Control register[31]
	cntlreg = (enable) ? 0x80000000 : 0x0000000;

	// add the duty cycles
	cntlreg |= ((DC & 0x03FF) << 20);

	if (isInitialized){
		MYHB3IP_mWriteReg(baseAddress, HB3_PWM_OFFSET, cntlreg);
	}
}


/**
 * Returns the number of ticks per second, value updated every 0.25s
 *
 * @param   void
 *
 * @return  returns count if initialized it returns the ticks/second
 *
 */
uint32_t HB3_getTicks(void)
{
    uint32_t count;
    if (isInitialized) {
        count = MYHB3IP_mReadReg(baseAddress, HB3_TICKS_OFFSET);
    }
    else{
        count = 0xDEADBEEF;
    }
    return count;
}


/**
 * Returns the RPM of the motor, value updated every 0.25s
 *
 * @param   void
 *
 * @return  returns rpm if initialized returns the rpm
 *
 */
uint32_t HB3_getRPM(void)
{
    uint32_t rpm;
    if(isInitialized){
        rpm = MYHB3IP_mReadReg(baseAddress, HB3_TICKS_OFFSET);
        rpm *= 60; //60 seconds per minute
        rpm /= 823.13; // 11 ticks * 74.83 gear ratio
    }
    else{
        rpm = 0xDEADBEEF;
    }
    return rpm;
}