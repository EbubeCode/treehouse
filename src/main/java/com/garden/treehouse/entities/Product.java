package com.garden.treehouse.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="id", nullable = false, updatable = false)
    private Long id;

    private double price;
    private String name;

    @Column(columnDefinition="text")
    private String description;
    private int inStockNumber;

    @Transient
    private MultipartFile bookImage;
}
