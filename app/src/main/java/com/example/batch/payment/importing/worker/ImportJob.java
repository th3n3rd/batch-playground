package com.example.batch.payment.importing.worker;

import java.io.Serial;
import java.io.Serializable;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ImportJob implements Serializable {
    @Serial
    private static final long serialVersionUID = 8991600012363837545L;
    public String accountId;
}
