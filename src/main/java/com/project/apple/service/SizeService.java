package com.project.apple.service;

import com.project.apple.model.Size;
import com.project.apple.responsitory.SizeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SizeService {

    @Autowired
    private SizeRepository sizeRepository;

    public List<Size> getAllSizes() {
        return sizeRepository.findAll();
    }

    public Optional<Size> getSizeById(Long id) {
        return sizeRepository.findById(id);
    }

    public Size createSize(Size size) {
        return sizeRepository.save(size);
    }

    public Size updateSize(Long id, Size sizeDetails) {
        Size size = sizeRepository.findById(id).orElseThrow();
        size.setName(sizeDetails.getName());
        return sizeRepository.save(size);
    }

    public void deleteSize(Long id) {
        sizeRepository.deleteById(id);
    }
}