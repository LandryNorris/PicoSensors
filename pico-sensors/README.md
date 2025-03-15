Pico Sensors
============

A collection of sensors using the Raspberry Pi Pico.
Each sensor outputs a stream of data via USB as a
collection of lines, using a known character specific 
to the sensor, followed by a colon, then the relevant 
data.

For example, the voltage sensor would output the 
following:

```
V: 0.012
V: 0.512
V: 1.853
V: 2.3872
```

All sensors should show up as serial devices.

Build and run
-------------

This is a CMake project, so it can be built by running

```shell
mkdir build && cd build
cmake ..
cmake --build .
```

The binary will live in `<buildDir>/<sensorName>/<sensorName>.elf`

It can then be flashed via a picoprobe, or other debug device.

Support for RP2350
------------------

Building for the RP2350 is possible by adding the
following to your CMake options:

`-DPICO_BOARD=pico2`
