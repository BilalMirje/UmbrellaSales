package com.umbrellaevent.service.impl;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.umbrellaevent.entity.Material;
import com.umbrellaevent.entity.dtos.material.MaterialRequest;
import com.umbrellaevent.entity.dtos.material.MaterialResponse;
import com.umbrellaevent.repository.MaterialRepository;
import com.umbrellaevent.service.MaterialService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@SuppressWarnings("null")
public class MaterialServiceImpl implements MaterialService {

    private final MaterialRepository materialRepository;

    @Override
    @Transactional
    public MaterialResponse createMaterial(MaterialRequest request, String username) {
        if (materialRepository.findByName(request.getName()).isPresent()) {
            throw new RuntimeException("Material name already exists");
        }

        Material material = new Material();
        material.setName(request.getName());

        Material saved = materialRepository.save(material);
        return mapToResponse(saved);
    }

    @Override
    public MaterialResponse getMaterialById(UUID id) {
        Material material = materialRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Material not found"));
        return mapToResponse(material);
    }

    @Override
    public List<MaterialResponse> getAllMaterials() {
        return materialRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Page<MaterialResponse> getAllMaterials(Pageable pageable) {
        return materialRepository.findAll(pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional
    public MaterialResponse updateMaterial(UUID id, MaterialRequest request, String username) {
        Material material = materialRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Material not found"));

        if (!material.getName().equals(request.getName()) &&
            materialRepository.findByName(request.getName()).isPresent()) {
            throw new RuntimeException("Material name already exists");
        }

        material.setName(request.getName());

        Material updated = materialRepository.save(material);
        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public void deleteMaterial(UUID id) {
        if (!materialRepository.existsById(id)) {
            throw new RuntimeException("Material not found");
        }
        materialRepository.deleteById(id);
    }

    private MaterialResponse mapToResponse(Material material) {
        MaterialResponse response = new MaterialResponse();
        response.setId(material.getId());
        response.setName(material.getName());
        response.setCreatedAt(material.getCreatedAt());
        response.setUpdatedAt(material.getUpdatedAt());
        response.setCreatedBy(material.getCreatedBy());
        response.setUpdatedBy(material.getUpdatedBy());
        return response;
    }
}
