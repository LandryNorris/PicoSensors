cmake_minimum_required(VERSION 3.20)

if (NOT BUILD_FOR_LOCAL)
    set(PICO_SDK_FETCH_FROM_GIT ON)

    # initialize the SDK based on PICO_SDK_PATH
    # note: this must happen before project()
    include(pico_import.cmake)
endif ()

project(pico_sensors C CXX ASM)

set(CMAKE_C_STANDARD 23)

pico_sdk_init()

set(CMAKE_EXPORT_COMPILE_COMMANDS true)

add_subdirectory(voltage_sensor)
