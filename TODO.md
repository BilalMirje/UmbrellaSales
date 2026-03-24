# Supplier Payment Implementation TODO

## Step 1: Create PaymentType Enum
- Create PaymentType.java enum with CREDIT and DEBIT values.

## Step 2: Create SupplierPaymentHistory Entity
- Create SupplierPaymentHistory.java extending Audit with fields: id (UUID), supplierId (UUID), paidAmount (Double), type (PaymentType).

## Step 3: Create SupplierPaymentHistoryRepository
- Create SupplierPaymentHistoryRepository.java interface extending JpaRepository.

## Step 4: Create DTOs
- Create SupplierPaymentRequest.java (supplierId, paidAmount, type).
- Create SupplierPaymentHistoryResponse.java and related DTOs.

## Step 5: Create SupplierPaymentService Interface
- Create SupplierPaymentService.java with methods: payToSupplier, payFromSupplier, getSupplierPaymentHistory.

## Step 6: Create SupplierPaymentServiceImpl
- Implement logic to update SupplierCredit (reduce creditAmount for credit, debitAmount for debit) and save history.

## Step 7: Create PaymentController
- Create PaymentController.java with endpoints: /api/payment/to-supplier, /api/payment/from-supplier, /api/payment/supplier-payment-history.
