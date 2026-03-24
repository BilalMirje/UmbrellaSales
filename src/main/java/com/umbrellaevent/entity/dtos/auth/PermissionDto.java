package com.umbrellaevent.entity.dtos.auth;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PermissionDto {

    private String userPermission;

    private List<String> privileges=new ArrayList<>();
}
