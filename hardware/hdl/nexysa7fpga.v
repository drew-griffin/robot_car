////////////
// Note - modified by Drew Seidel (dseidel@pdx.edu)
// For ECE 544 Project 2  
// Top-level module for ECE 544 Project #2
// May have to be modified for your specific embedded system implementation
///////////
`timescale 1 ps / 1 ps

module nexysa7fpga
   (
    RGB2_Blue,
    RGB2_Green,
    RGB2_Red,
    an,
    btnC,
    btnD,
    btnL,
    btnR,
    btnU,
    dp,
    led,
    btnCpuReset,
    RGB1_Blue,
    RGB1_Green,
    RGB1_Red,
    seg,
    sw,
    clk,
    RX,
    TX,
    // left motor header
    JA_out,
    JA_in,
    // right motor header
    JB_out,
    JB_in,
    // encoder header
    JC);
    
  output RGB2_Blue;
  output RGB2_Green;
  output RGB2_Red;
  output [7:0]an;
  input btnC;
  input btnD;
  input btnL;
  input btnR;
  input btnU;
  output dp;
  output [15:0]led;
  input btnCpuReset;
  output RGB1_Blue;
  output RGB1_Green;
  output RGB1_Red;
  output [6:0]seg;
  input [15:0]sw;
  input clk;
  output [1:0] JA_out;
  input  [1:0] JA_in;
  output [1:0] JB_out;
  input  [1:0] JB_in;
  input  [7:4] JC;
  output TX;
  input  RX;

  wire RGB2_Blue;
  wire RGB2_Green;
  wire RGB2_Red;
  wire [7:0]an;
  wire btnC;
  wire btnD;
  wire btnL;
  wire btnR;
  wire btnU;
  wire clkPWM;
  wire dp;
  wire [15:0]led;
  wire btnCpuReset;
  wire RGB1_Blue;
  wire RGB1_Green;
  wire RGB1_Red;
  wire [6:0]seg;
  wire [15:0]sw;
  wire clk;
  wire [31:0] control_reg;
  wire [31:0] gpio_pwm;
  wire [31:0] gpio_dir;
  // motor specific variables
  wire LeftMotorDirection;
  wire LeftMotorEnable;
  wire LeftMotorEncoder_A;
  wire LeftMotorEncoder_B;
  wire RightMotorDirection;
  wire RightMotorEnable;
  wire RightMotorEncoder_A;
  wire RightMotorEncoder_B;
  // encoder specific variables
  wire EcA;
  wire EcB;
  wire EcBTN;
  wire EcSW;

  // assign signals to the JA Left Motor
  assign JA_out[0]          = LeftMotorDirection;
  assign JA_out[1]          = LeftMotorEnable;
  assign LeftMotorEncoder_A = JA_in[0];
  assign LeftMotorEncoder_B = JA_in[1];
    // assign signals to the JB Right Motor
  assign JB_out[0]          = RightMotorDirection;
  assign JB_out[1]          = RightMotorEnable;
  assign RightMotorEncoder_A = JB_in[0];
  assign RightMotorEncoder_B = JB_in[1];
  // assign signals to the JC header for encoder
  assign EcA   = JC[4]; // E7 
  assign EcB   = JC[5]; // J3
  assign EcBTN = JC[6]; // J4
  assign EcSW  = JC[7]; // E6
                 
  embsys embsys_i
       (.LeftMotorDirection(LeftMotorDirection),
        .LeftMotorEnable(LeftMotorEnable),
        .LeftMotorEncoder_A(LeftMotorEncoder_A),
        .LeftMotorEncoder_B(LeftMotorEncoder_B),
        .RGB1_Blue_0(RGB1_Blue),
        .RGB1_Green_0(RGB1_Green),
        .RGB1_Red_0(RGB1_Red),
        .RGB2_Blue_0(RGB2_Blue),
        .RGB2_Green_0(RGB2_Green),
        .RGB2_Red_0(RGB2_Red),
        .RightMotorDirection(RightMotorDirection),
        .RightMotorEnable(RightMotorEnable),
        .RightMotorEncoder_A(RightMotorEncoder_A),
        .RightMotorEncoder_B(RightMotorEncoder_B),
        .an_0(an),
        .btnC_0(btnC),
        .btnD_0(btnD),
        .btnL_0(btnL),
        .btnR_0(btnR),
        .btnU_0(btnU),
        .clk_100MHz(clk),
        .dp_0(dp),
        .encA_0(EcB), // switched A and B for clockwise increment
        .encBTN_0(EcBTN),
        .encB_0(EcA),
        .encSWT_0(EcSW),
        .led_0(led),
        .resetn(btnCpuReset),
        .seg_0(seg),
        .sw_0(sw),
        .uart_rtl_0_rxd(RX),
        .uart_rtl_0_txd(TX));
endmodule
