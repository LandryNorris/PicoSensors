
#ifndef VOLTAGE_H
#define VOLTAGE_H

#include <stdint.h>

void initializeVoltage();

/**
 *
 * @param index offset from ADC_BASE_PIN to sleect. Between 0 and 2
 * @return whether the selection was applied
 */
bool selectPin(const uint8_t index);

uint8_t* getSampleBuffer();

uint16_t readVoltageImmediately();

float sampleToVolts(const uint16_t sample);

#endif //VOLTAGE_H
