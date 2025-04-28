package main

import (
	"fmt"
	"log"
	"time"

	"github.com/d2r2/go-dht"
	"github.com/d2r2/go-logger"
	"github.com/stianeikeland/go-rpio"
)

func main() {
	// Set the logger level to suppress debug and info messages
	logger.ChangePackageLogLevel("dht", logger.ErrorLevel)

	// Open and map memory to access GPIO
	err := rpio.Open()
	if err != nil {
		log.Fatal(err)
	}
	defer rpio.Close()

	// Define GPIO pin number
	pin := rpio.Pin(4)
	pin.Input() // Set the pin to input mode

	// Retry several times to get a reliable reading
	for {
		temperature, humidity, _, err := dht.ReadDHTxxWithRetry(dht.DHT11, int(pin), false, 10)
		if err != nil {
			log.Printf("Error reading DHT11: %v\n", err)
			continue // Retry on error
		}

		// Handle possible checksum errors
		if temperature == 0 && humidity == 0 {
			log.Println("Failed to read valid data. Retrying...")
			continue
		}

		// Convert temperature to Fahrenheit
		temperatureF := (temperature * 9 / 5) + 32

		fmt.Printf("Temperature = %.1f*C (%.1f*F), Humidity = %.1f%%\n", temperature, temperatureF, humidity)
		time.Sleep(2 * time.Second) // Wait before the next reading
	}
}
