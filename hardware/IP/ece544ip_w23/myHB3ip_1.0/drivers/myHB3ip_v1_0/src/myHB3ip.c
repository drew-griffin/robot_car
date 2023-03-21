

/***************************** Include Files *******************************/
#include "myHB3ip.h"

/***************************** Global variables ****************************/
//static uint32_t baseAddress = 0L;
//static bool isInitialized = false;

/************************** Function Definitions ***************************/

/**
 * Sets the PWM output of the HB3
 *
 * @param   baseAddr    the base address of the HB3
 *          enable      enable signal to turn the PWM signal on or off
 *          DC          u16 value truncated to 10 bits to set duty cycle
 *
 * @return  void
 *
 */
void HB3_setPWM(uint32_t baseAddr, bool enable, uint16_t DC, bool direction)
{
	uint32_t cntlreg;
    // initialize the value depending on whether PWM is enabled
	// enable is Control register[31]
	cntlreg = (enable) ? 0x80000000 : 0x00000000;
    // add the duty cycles
	cntlreg |= ((DC & 0x03FF) << 20);
    cntlreg |= (direction) ? 0x00000001 : 0x00000000;
	
    MYHB3IP_mWriteReg(baseAddr, HB3_PWM_OFFSET, cntlreg);
}


/**
 * Returns the number of ticks per second, value updated every 0.25s
 *
 * @param   baseAddr    the base address of the HB3
 *
 * @return  returns count if initialized it returns the ticks/second
 *
 */
uint32_t HB3_getTicks(uint32_t baseAddr)
{
    uint32_t count;
    count = MYHB3IP_mReadReg(baseAddr, HB3_TICKS_OFFSET);
    return count;
}


/**
 * Returns the RPM of the motor, value updated every 0.25s
 *
 * @param   baseAddr    the base address of the HB3
 *
 * @return  returns rpm if initialized returns the rpm
 *
 */
uint32_t HB3_getRPM(uint32_t baseAddr)
{
    uint32_t rpm;
    rpm = MYHB3IP_mReadReg(baseAddr, HB3_TICKS_OFFSET);
    rpm *= 60; //60 seconds per minute
    rpm /= 823.13; // 11 ticks * 74.83 gear ratio
    return rpm;
}