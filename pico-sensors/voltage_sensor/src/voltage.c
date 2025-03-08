#include "voltage.h"

#include <stdint.h>
#include <hardware/adc.h>
#include <hardware/dma.h>

#define ANALOG_1_PIN 26u
#define ANALOG_2_PIN 27u
#define ANALOG_3_PIN 28u

static constexpr uint32_t SAMPLE_BUFFER_SIZE = 1000;

uint8_t sampleBuffer[SAMPLE_BUFFER_SIZE];

uint32_t counter = 0;

void voltageDmaHandler() {
    counter++;
}

void initializeVoltage() {
    adc_gpio_init(ANALOG_1_PIN);
    adc_gpio_init(ANALOG_2_PIN);
    adc_gpio_init(ANALOG_3_PIN);

    adc_init();
    adc_set_temp_sensor_enabled(true);

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

    channel_config_set_transfer_data_size(&cfg, DMA_SIZE_8);
    channel_config_set_read_increment(&cfg, false);
    channel_config_set_write_increment(&cfg, true);

    // Pace transfers based on availability of ADC samples
    channel_config_set_dreq(&cfg, DREQ_ADC);
    dma_channel_set_irq0_enabled(channel, true);

    irq_set_exclusive_handler(DMA_IRQ_0, voltageDmaHandler);

    dma_channel_configure(channel, &cfg,
        sampleBuffer,
        &adc_hw->fifo,
        SAMPLE_BUFFER_SIZE,
        true
    );

    adc_run(true);

    dma_channel_wait_for_finish_blocking(channel);
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

uint16_t readVoltageImmediately() {
    return adc_read();
}

float sampleToVolts(const uint16_t sample) {
    return (float) sample * 3.3f / 4096.0f;
}
