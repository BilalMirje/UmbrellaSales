package com.umbrellaevent.service;

import java.util.List;
import java.util.UUID;

import com.umbrellaevent.entity.dtos.purchase.ProductStockGroupResponse;
import com.umbrellaevent.entity.dtos.sell.CustomerLedgerResponse;
import com.umbrellaevent.entity.dtos.sell.CustomerLedgerSummaryResponse;
import com.umbrellaevent.entity.dtos.supplier.SupplierCreditResponse;
import com.umbrellaevent.entity.dtos.supplier.SupplierPayableLedgerResponse;
import com.umbrellaevent.entity.dtos.supplier.SupplierPurchaseHistoryListResponse;

public interface ReportsService {
    SupplierPurchaseHistoryListResponse getSupplierPurchaseHistory(UUID supplierId);
    List<ProductStockGroupResponse> getProductStocksWithPurchase();
    List<SupplierPayableLedgerResponse> getSupplierPayableLedger();
    List<SupplierCreditResponse> getSupplierCreditData();

    // List<CustomerReceivableLedgerResponse> getCustomerReceivableLedger();
    
    CustomerLedgerResponse getCustomerSellLedger(UUID customerId);
    List<CustomerLedgerResponse> getAllCustomerSellLedgers();
List<CustomerLedgerSummaryResponse> getCustomerLedgerSummary();


}
