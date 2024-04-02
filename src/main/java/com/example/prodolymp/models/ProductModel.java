package com.example.prodolymp.models;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "product")
public class ProductModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "title")
    @Schema(description = "Заголовок товара")
    private String title;

    @Column(name = "description")
    @Schema(description = "Описание товара")
    private String description;

    @Column(name = "category")
    @Schema(description = "Категория")
    private String category;

    @Column(name = "price")
    @Schema(description = "Цена товара")
    private Integer price;

    @Column(name = "image")
    @Schema(description = "Картинка товара")
    private String image;

    @Column(name = "is_bought")
    @Schema(description = "Куплен или не куплен")
    private Boolean isBought;
}
