package com.iot.aforo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fazecast.jSerialComm.SerialPort;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

@Service
public class ArduinoListenerService {

    private static final Logger log = LoggerFactory.getLogger(ArduinoListenerService.class);
    private static final int BAUD_RATE = 9600;

    private final BusinessService businessService;
    private final String arduinoPortName;
    private final Long businessId;
    private final boolean arduinoEnabled;
    private volatile boolean running;
    private SerialPort serialPort;
    private Thread listenerThread;

    public ArduinoListenerService(
            BusinessService businessService,
            @Value("${arduino.port:COM3}") String arduinoPortName,
            @Value("${arduino.business-id:1}") Long businessId,
            @Value("${arduino.enabled:false}") boolean arduinoEnabled
    ) {
        this.businessService = businessService;
        this.arduinoPortName = arduinoPortName;
        this.businessId = businessId;
        this.arduinoEnabled = arduinoEnabled;
    }

    @PostConstruct
    public void start() {
        if (!arduinoEnabled) {
            log.info("Listener serial Arduino deshabilitado por configuración (arduino.enabled=false). El simulador web seguirá funcionando.");
            return;
        }

        try {
            listenerThread = new Thread(this::listenSerialPort, "arduino-listener-thread");
            listenerThread.setDaemon(true);
            listenerThread.start();
        } catch (Throwable ex) {
            log.warn("No se pudo iniciar el listener serial del Arduino. El servidor continuará sin el sensor físico.", ex);
        }
    }

    @PreDestroy
    public void stop() {
        running = false;
        if (serialPort != null && serialPort.isOpen()) {
            serialPort.closePort();
        }
    }

    private void listenSerialPort() {
        try {
            if (arduinoPortName == null || arduinoPortName.isBlank()) {
                log.warn("No hay puerto Arduino configurado; se omite el listener serial.");
                return;
            }

            serialPort = SerialPort.getCommPort(arduinoPortName);
            serialPort.setBaudRate(BAUD_RATE);
            serialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 1000, 0);

            if (!serialPort.openPort()) {
                log.warn("Arduino no conectado o puerto ocupado: {}. El simulador web seguira disponible.", arduinoPortName);
                return;
            }

            running = true;
            log.info("Arduino conectado en {} a {} baudios para el negocio {}.", arduinoPortName, BAUD_RATE, businessId);

            while (running && serialPort.isOpen()) {
                if (serialPort.bytesAvailable() <= 0) {
                    sleepBriefly();
                    continue;
                }

                byte[] buffer = new byte[serialPort.bytesAvailable()];
                int readBytes = serialPort.readBytes(buffer, buffer.length);
                for (int i = 0; i < readBytes; i++) {
                    processSignal((char) buffer[i]);
                }
            }
        } catch (Throwable ex) {
            log.warn("No se pudo iniciar o mantener la lectura serial del Arduino en {}. El servidor continua activo.", arduinoPortName, ex);
        } finally {
            if (serialPort != null && serialPort.isOpen()) {
                serialPort.closePort();
            }
        }
    }

    private void processSignal(char signal) {
        try {
            if (signal == 'E') {
                businessService.registerEntry(businessId);
                log.info("Arduino registro ENTRADA para el negocio {}.", businessId);
            } else if (signal == 'S') {
                businessService.registerExit(businessId);
                log.info("Arduino registro SALIDA para el negocio {}.", businessId);
            }
        } catch (IllegalArgumentException ex) {
            log.warn("La senal '{}' del Arduino no pudo aplicarse: {}", signal, ex.getMessage());
        }
    }

    private void sleepBriefly() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            running = false;
        }
    }
}
