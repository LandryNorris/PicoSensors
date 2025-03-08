#include <stdio.h>
#include <voltage.h>
#include <hardware/gpio.h>
#include <pico/stdio.h>
#include <pico/stdio_usb.h>

float currentVoltage = 0.0;

void initLed() {
    gpio_init(PICO_DEFAULT_LED_PIN);
    gpio_set_dir(PICO_DEFAULT_LED_PIN, GPIO_OUT);
}

int main(void) {
    stdio_init_all();

    initLed();

    initializeVoltageSingleShot();

    gpio_put(PICO_DEFAULT_LED_PIN, 1);

    while (1) {
        const uint16_t counts = readVoltageSingleShot();
        currentVoltage = sampleToVolts(counts);
    }
}
