package com.cathaybk.controller;

import com.cathaybk.entity.Crud;
import com.cathaybk.service.CrudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Crud API Controller - 幣別管理 API
 */
@RestController
@RequestMapping("/api/crud")
public class CrudController {

    @Autowired
    private CrudService crudService;

    /**
     * 查詢所有幣別
     * GET /api/crud/query/getAll
     */
    @GetMapping("/query/getAll")
    public ResponseEntity<List<Crud>> getAll() {
        List<Crud> crubs = crudService.findAll();
        return ResponseEntity.ok(crubs);
    }

    /**
     * 根據幣別代碼查詢
     * GET /api/crud/query/{code}
     */
    @GetMapping("/query/{code}")
    public ResponseEntity<Crud> getCrudByCode(@PathVariable String code) {
        Optional<Crud> crud = crudService.findByCode(code.toUpperCase());
        return crud.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 新增幣別
     * POST /api/crud/insert
     */
    @PostMapping("/insert")
    public ResponseEntity<Crud> createCrud(@RequestBody Crud crud) {
        try {
            if (crudService.existsByCode(crud.getCode())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
            Crud savedCrud = crudService.save(crud);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedCrud);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 更新幣別
     * PUT /api/crud/{code}
     */
    @PostMapping("/update")
    public ResponseEntity<Crud> updateCrud(@RequestBody Crud crud) {
        try {
            Crud updatedCrud = crudService.update(
                    crud.getCode().toUpperCase(),
                    crud.getChineseName()
            );
            return ResponseEntity.ok(updatedCrud);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 刪除幣別
     * DELETE /api/crud/{code}
     */
    @PostMapping("/delete")
    public ResponseEntity<Crud> deleteCrud(@RequestBody Crud crud) {
        try {
            crudService.deleteByCode(crud.getCode().toUpperCase());
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}

