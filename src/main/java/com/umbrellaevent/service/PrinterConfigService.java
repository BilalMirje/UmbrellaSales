package com.umbrellaevent.service;

import java.util.List;
import java.util.UUID;

import com.umbrellaevent.entity.dtos.printer.PrinterConfigRequest;
import com.umbrellaevent.entity.dtos.printer.PrinterConfigResponse;

public interface PrinterConfigService {
    PrinterConfigResponse createPrinter(PrinterConfigRequest request);
    PrinterConfigResponse getPrinterById(UUID id);
    List<PrinterConfigResponse> getAllPrinters();
    PrinterConfigResponse updatePrinter(UUID id, PrinterConfigRequest request);

    void deletePrinter(UUID id);
    public PrinterConfigResponse getActivePrinter();
}
