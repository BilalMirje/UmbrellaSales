package com.umbrellaevent.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.umbrellaevent.entity.dtos.material.MaterialRequest;
import com.umbrellaevent.entity.dtos.material.MaterialResponse;

public interface MaterialService {
    MaterialResponse createMaterial(MaterialRequest request, String username);
    MaterialResponse getMaterialById(UUID id);
    List<MaterialResponse> getAllMaterials();
    Page<MaterialResponse> getAllMaterials(Pageable pageable);
    MaterialResponse updateMaterial(UUID id, MaterialRequest request, String username);
    void deleteMaterial(UUID id);
}
