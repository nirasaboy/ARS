package com.bluepal.Sales.Entity;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "sales_po_line_item")
@Data
public class SalesPOLineItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "po_id")
    private SalesPO po;

    private String name;
    private String unitOfMeasure;
    private Double quantity;
    private Double rate;
    private Double amount;
    private LocalDate promiseDate;
    private String status;
}