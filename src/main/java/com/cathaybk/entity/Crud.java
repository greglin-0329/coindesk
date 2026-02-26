package com.cathaybk.entity;

import java.util.Objects;
import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * CRUD Entity - 幣別中文對應資料表
 */
@Entity
@Table(name = "crud")
public class Crud {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 3)
    private String code;

    @Column(nullable = false, length = 100)
    private String chineseName;

    @Column(nullable = false)
    private LocalDateTime updateTime;

    // Constructors
    public Crud() {
    }

    public Crud(String code, String chineseName) {
        this.code = code;
        this.chineseName = chineseName;
        this.updateTime = LocalDateTime.now();
    }

    public Crud(Long id, String code, String chineseName, LocalDateTime updateTime) {
        this.id = id;
        this.code = code;
        this.chineseName = chineseName;
        this.updateTime = updateTime;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getChineseName() {
        return chineseName;
    }

    public void setChineseName(String chineseName) {
        this.chineseName = chineseName;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    // toString
    @Override
    public String toString() {
        return "Currency{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", chineseName='" + chineseName + '\'' +
                ", updateTime=" + updateTime +
                '}';
    }

    // equals and hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Crud currency = (Crud) o;
        return Objects.equals(code, currency.code);
    }

    @Override
    public int hashCode() {
        return code != null ? code.hashCode() : 0;
    }
}

