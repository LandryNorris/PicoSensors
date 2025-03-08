#include "voltage.h"

#include <stdint.h>
#include <hardware/adc.h>
#include <hardware/dma.h>

#define ANALOG_1_PIN 26u
#define ANALOG_2_PIN 27u
#define ANALOG_3_PIN 28u

static constexpr uint32_t SAMPLE_BUFFER_SIZE = 1000;

uint8_t sampleBuffer[SAMPLE_BUFFER_SIZE];

void initializeVoltage() {
    adc_init();
    adc_set_temp_sensor_enabled(true);

    adc_gpio_init(ANALOG_1_PIN);
    adc_gpio_init(ANALOG_2_PIN);
    adc_gpio_init(ANALOG_3_PIN);

    adc_set_clkdiv(0); // run at full speed
    adc_select_input(ANALOG_1_PIN);
    adc_fifo_setup(
        true,    // Write each completed conversion to the sample FIFO
        true,    // Enable DMA data request (DREQ)
        1,       // DREQ (and IRQ) asserted when at least 1 sample present
        false,   // We won't see the ERR bit because of 8 bit reads; disable.
        true     // Shift each sample to 8 bits when pushing to FIFO
    );

    // DMA setup
    const uint channel = dma_claim_unused_channel(true);
    dma_channel_config cfg = dma_channel_get_default_config(channel);

    // Pace transfers based on availability of ADC samples
    channel_config_set_dreq(&cfg, DREQ_ADC);

    dma_channel_configure(channel, &cfg,
        sampleBuffer,
        &adc_hw->fifo,
        SAMPLE_BUFFER_SIZE,
        true
    );

    dma_channel_set_irq0_enabled(channel, true);

    adc_run(true);

    adc_fifo_setup(true, true, 1, false, true);
}

bool selectPin(const uint8_t index) {
    if (index >= 3) {
        return false;
    }
    adc_select_input(ADC_BASE_PIN + index);
    return true;
}

uint8_t* getSampleBuffer() {
    return sampleBuffer;
}
