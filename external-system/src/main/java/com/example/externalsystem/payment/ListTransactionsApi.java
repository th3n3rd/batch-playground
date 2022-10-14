package com.example.externalsystem.payment;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
class ListTransactionsApi {

    private static final int MaxResultSetSize = 10_000;
    private static final int MaxDateRangeInDays = 31;

    private final Transactions transactions;

    @GetMapping("/v1/payment/accounts/{accountId}/transactions")
    Response listTransactions(
        @PathVariable UUID accountId,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime startDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime endDate,
        @RequestParam(defaultValue = "1", required = false) int page,
        @RequestParam(defaultValue = "20", required = false) int pageSize
    ) {
        validateDateRange(startDate, endDate);
        var pagedTransactions = transactions.findAllByMerchantAccountIdAndTransactionUpdateDateBetween(
            accountId,
            startDate,
            endDate,
            PageRequest.of(page, pageSize)
        );
        validateResultSetSize(pagedTransactions);
        return new Response(
            accountId,
            pagedTransactions.getNumber(),
            pagedTransactions.getTotalElements(),
            pagedTransactions.getTotalPages(),
            pagedTransactions
                .getContent()
                .stream()
                .map(Response.Detail::new)
                .collect(Collectors.toList())
        );
    }

    private static void validateResultSetSize(Page<Transaction> pagedTransactions) {
        if (pagedTransactions.getTotalElements() > MaxResultSetSize) {
            throw new ResultSetTooLargeException();
        }
    }

    private static void validateDateRange(OffsetDateTime startDate, OffsetDateTime endDate) {
        if (Duration.between(startDate, endDate).toDays() > MaxDateRangeInDays) {
            throw new DateRangeTooFarApartException();
        }
    }

    @ExceptionHandler(ResultSetTooLargeException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Result set too large")
    void handleResultSetTooLarge() {}

    @ExceptionHandler(DateRangeTooFarApartException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Date range too far apart")
    void handleDateRangeTooFarApart() {}

    @AllArgsConstructor
    static class Response {
        public UUID accountId;
        public int page;
        public long totalItems;
        public int totalPages;
        public List<Detail> transactionDetails;

        @AllArgsConstructor
        static class Detail {
            public Transaction transactionInfo;
        }
    }
}
