package com.umbrellaevent.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import com.umbrellaevent.entity.dtos.sell.CustomerCreditResponse;

import com.umbrellaevent.entity.dtos.sell.SellRequest;
import com.umbrellaevent.entity.dtos.sell.SellResponse;

public interface SellService {

    SellResponse createSell(SellRequest request, String username);

    SellResponse getSellById(UUID id);

    SellResponse getSellByInvoiceNo(String invoiceNo);

    List<SellResponse> getAllSells();

    Page<SellResponse> getAllSells(Pageable pageable);

    CustomerCreditResponse getCustomerCredit(UUID customerId);

    String getNextSellInvoiceNumber();

    void deleteSell(UUID id);
    Optional<SellResponse> findByInvoiceNo(String invoiceNo);



}

