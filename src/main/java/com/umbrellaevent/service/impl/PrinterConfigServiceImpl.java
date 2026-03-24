package com.umbrellaevent.service.impl;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.umbrellaevent.entity.PrinterConfig;
import com.umbrellaevent.entity.dtos.printer.PrinterConfigRequest;
import com.umbrellaevent.entity.dtos.printer.PrinterConfigResponse;
import com.umbrellaevent.repository.PrinterConfigRepository;
import com.umbrellaevent.service.PrinterConfigService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PrinterConfigServiceImpl implements PrinterConfigService {

    private final PrinterConfigRepository printerConfigRepository;

    @Override
    public PrinterConfigResponse createPrinter(PrinterConfigRequest request) {
        PrinterConfig printerConfig = new PrinterConfig();
        printerConfig.setName(request.getName());
        printerConfig.setIsActive(request.getIsActive());

        // If setting to active, deactivate all others
        if (Boolean.TRUE.equals(request.getIsActive())) {
            List<PrinterConfig> activePrinters = printerConfigRepository.findByIsActiveTrue();
            for (PrinterConfig activePrinter : activePrinters) {
                activePrinter.setIsActive(false);
                printerConfigRepository.save(activePrinter);
            }
        }

        PrinterConfig saved = printerConfigRepository.save(printerConfig);
        return mapToResponse(saved);
    }

    @SuppressWarnings("null")
    @Override
    public PrinterConfigResponse getPrinterById(UUID id) {
        PrinterConfig printerConfig = printerConfigRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Printer config not found"));
        return mapToResponse(printerConfig);
    }

    @Override
    public List<PrinterConfigResponse> getAllPrinters() {
        List<PrinterConfig> printers = printerConfigRepository.findAll();
        return printers.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @SuppressWarnings("null")
    @Override
    public PrinterConfigResponse updatePrinter(UUID id, PrinterConfigRequest request) {
        PrinterConfig printerConfig = printerConfigRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Printer config not found"));
        printerConfig.setName(request.getName());
        printerConfig.setIsActive(request.getIsActive());

        // If setting to active, deactivate all others
        if (Boolean.TRUE.equals(request.getIsActive())) {
            List<PrinterConfig> activePrinters = printerConfigRepository.findByIsActiveTrue();
            for (PrinterConfig activePrinter : activePrinters) {
                if (!activePrinter.getId().equals(id)) {
                    activePrinter.setIsActive(false);
                    printerConfigRepository.save(activePrinter);
                }
            }
        }

        PrinterConfig saved = printerConfigRepository.save(printerConfig);
        return mapToResponse(saved);
    }

    @SuppressWarnings("null")
    @Override
    public void deletePrinter(UUID id) {
        if (!printerConfigRepository.existsById(id)) {
            throw new RuntimeException("Printer config not found");
        }
        printerConfigRepository.deleteById(id);
    }

    private PrinterConfigResponse mapToResponse(PrinterConfig printerConfig) {
        PrinterConfigResponse response = new PrinterConfigResponse();
        response.setId(printerConfig.getId());
        response.setName(printerConfig.getName());
        response.setIsActive(printerConfig.getIsActive());
        return response;
    }

    @Override
    public PrinterConfigResponse getActivePrinter() {
        PrinterConfig printerConfig = printerConfigRepository.findActivePrinter()
                .orElseThrow(() -> new RuntimeException("No active printer config found"));
        return mapToResponse(printerConfig);
    }
}
