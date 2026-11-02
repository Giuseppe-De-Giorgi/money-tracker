package com.moneytracker.transaction_service.exception;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public record ApiError(
        LocalDateTime timestamp,
        int status,
        String message,
        Map<String, List<String>> errors
) {
}
