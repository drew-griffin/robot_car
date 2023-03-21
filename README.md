# Version 1.0 - First Robot Car Implementation
https://github.com/drew-griffin/robot_car

Drew Seidel (dseidel@pdx.edu)\
Stephen Weeks (stweeks@pdx.edu)\
Noah Page (nopage@pdx.edu) \
Emily Devlin (emdevlin@pdx.edu)

# Repository Organization 
- bitstream 
    - contains project bitstreams. demo_flexible.bit is the latest, where the ultrasonic sensor is enabled by turning on switch 0 of the NEXYS A7, and PID control is enabled by turning on switch 15 of the NEXYS A7. 
- docs
    - proposal
    - project design report 
    - project flow charts
    - the project embedded system hardware pdf
    - project BOM 
    - project presentation
    - chassis svg file 
- hardware
    - rebuild.tcl       - rebuilds vivado project
    - hdl               - project hdl code 
    - xdc               - constraints 
    - robot_car_hardware    - vivado project (not checked in)
    - IP                - project IP 
        - ece544ip_w23     - (contains and adds to the entire ece544ip_w23 directory for the tcl scripting and revisioning purposes. The custom IP for this project, **myHB3ip_1.0**, can be found here
        - bd   - contains embedded system board desing (needed for tcl script to build project)
       - Pmod_Bridge_v1_0 - not utilized in our project
- src 
    - contains all C source files for the application (project developed using the Xiling Vitis Software Platform)
- android_app 
    - contains the Android Studio App
- pi_firmware
    - contains the Raspberry Pi Firmware
- demo
    - contains a demo video of the working system 

# Instructions for Building Project in Vivado 

This repository assumes that you are running Vivado 2019.2

``` sh
git clone https://github.com/drew-griffin/robot_car
```
``` sh
cd hardware
```
``` sh
rm -rf robot_car_hardware #run after first time
```
``` sh 
vivado -source rebuild.tcl
```
Upon making modifications in Vivado, run: 
``` sh
write_project_tcl -force rebuild
```
in the Vivado tcl console, and push the new rebuild.tcl file to GitHub

# Instructions for Editing Android App

1. Open Android Studio
2. Go to file->open
3. Navigate to: 
``` sh
<your_path_to_repository>/android_app
```
4. Open project. RobotCarApp should appear upon open. 

Note:  *The .gitignore is set up such that you can make modifications, and commit all files, and this will run seemlessly for team members*.
