package com.umbrellaevent.service.impl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.umbrellaevent.entity.SequenceGenerator;
import com.umbrellaevent.repository.SequenceGeneratorRepository;

import jakarta.transaction.Transactional;

@Service
@SuppressWarnings("null")
public class SequenceGeneratorService {

    @Autowired
    private SequenceGeneratorRepository repository;

    @Transactional
    public String getNextNumber(String key) {
        SequenceGenerator sequence = repository.findById(key)
                .orElse(new SequenceGenerator());

        if (sequence.getCurrentValue() == null) {
            sequence.setCurrentValue(0L);
        }

        Long nextValue = sequence.getCurrentValue() + 1;
        sequence.setName(key);
        sequence.setCurrentValue(nextValue);

        repository.save(sequence);

        // For purchase_invoice, format as PA followed by incremental numbers with leading zeros
        if ("purchase_invoice".equals(key)) {
            int digits = 8; // Start with 8 digits after PA
            while (nextValue >= Math.pow(10, digits)) {
                digits++;
            }
            return "PA" + String.format("%0" + digits + "d", nextValue);
        }

        // For sell_invoice, format as SE followed by incremental numbers with leading zeros
        if ("sell_invoice".equals(key)) {
            int digits = 8; // Start with 8 digits after SE (7 zeros + 1)
            while (nextValue >= Math.pow(10, digits)) {
                digits++;
            }
            return "SE" + String.format("%0" + digits + "d", nextValue);
        }

        // Default format to 6 digits with leading zeros for other keys
        return String.format("%06d", nextValue);
    }

    public String getNextNumberWithoutSave(String key) {
        SequenceGenerator sequence = repository.findById(key)
                .orElse(new SequenceGenerator());

        if (sequence.getCurrentValue() == null) {
            sequence.setCurrentValue(0L);
        }

        Long nextValue = sequence.getCurrentValue() + 1;

        // For purchase_invoice, format as PA followed by incremental numbers with leading zeros
        if ("purchase_invoice".equals(key)) {
            int digits = 8; // Start with 8 digits after PA
            while (nextValue >= Math.pow(10, digits)) {
                digits++;
            }
            return "PA" + String.format("%0" + digits + "d", nextValue);
        }

        // For sell_invoice, format as SE followed by incremental numbers with leading zeros
        if ("sell_invoice".equals(key)) {
            int digits = 8; // Start with 8 digits after SE (7 zeros + 1)
            while (nextValue >= Math.pow(10, digits)) {
                digits++;
            }
            return "SE" + String.format("%0" + digits + "d", nextValue);
        }

        // Default format to 6 digits with leading zeros for other keys
        return String.format("%06d", nextValue);
    }

    @Transactional
    public String getNextSellInvoiceNumber() {
        return getNextNumber("sell_invoice");
    }

    public String getNextSellInvoiceNumberWithoutSave() {
        return getNextNumberWithoutSave("sell_invoice");
    }
}

