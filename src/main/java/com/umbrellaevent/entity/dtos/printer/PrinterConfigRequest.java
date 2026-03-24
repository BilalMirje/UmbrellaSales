package com.umbrellaevent.entity.dtos.printer;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PrinterConfigRequest {
    private String name;
    private Boolean isActive;
}
