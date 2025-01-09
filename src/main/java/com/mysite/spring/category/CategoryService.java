package com.mysite.spring.category;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    // 카테고리 목록 조회
    public List<Category> getList() {
        return categoryRepository.findAll();
    }

    // 특정 카테고리 조회 (필요하면 추가)
    public Category getCategory(String name) {
        return categoryRepository.findByName(name)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "카테고리를 찾을 수 없습니다."));
    }


}
