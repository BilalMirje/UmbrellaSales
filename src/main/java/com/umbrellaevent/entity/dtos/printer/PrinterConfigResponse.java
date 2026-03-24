package com.umbrellaevent.entity.dtos.printer;

import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PrinterConfigResponse {
    private UUID id;
    private String name;
    private Boolean isActive;
}
