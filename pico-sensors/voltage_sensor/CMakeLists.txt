project(VoltageSensor)

file(GLOB VOLTAGE_SENSOR_SRCS ${CMAKE_CURRENT_LIST_DIR}/src/*.c)

add_executable(voltage_sensor ${VOLTAGE_SENSOR_SRCS})
target_link_libraries(voltage_sensor PUBLIC pico_stdlib)

target_include_directories(voltage_sensor PUBLIC ${CMAKE_CURRENT_LIST_DIR}/include)

target_link_libraries(voltage_sensor PUBLIC hardware_adc hardware_dma)

pico_enable_stdio_usb(voltage_sensor ON)
