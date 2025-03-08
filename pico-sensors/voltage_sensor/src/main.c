#include <stdio.h>
#include <voltage.h>
#include <hardware/gpio.h>
#include <pico/stdio.h>
#include <pico/stdio_usb.h>

void initLed() {
    gpio_init(PICO_DEFAULT_LED_PIN);
    gpio_set_dir(PICO_DEFAULT_LED_PIN, GPIO_OUT);
}

int main(void) {
    stdio_init_all();

    initLed();

    initializeVoltage();

    gpio_put(PICO_DEFAULT_LED_PIN, 1);

    while (1) {

    }
}
