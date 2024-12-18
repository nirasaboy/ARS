package com.bluepal.Sales.Entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "sales_rfq_line_items")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SalesRFQLineItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is mandatory")
    private String name;

    @NotNull(message = " quanity is mandatory")
    private Integer quantity;
    
    @NotBlank(message = "required Unit Of Measure")
    private String unitOfMeasure;
    
    @NotNull
    private LocalDateTime deliveryBy;

    @ManyToOne
    @JoinColumn(name = "rfq_id", nullable = false)
    private SalesRFQ salesRFQ;
}
