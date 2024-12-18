package com.bluepal.Sales.Entity;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Table(name = "sales_invoice")
@Data
public class SalesInvoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    @ManyToOne
    @JoinColumn(name = "po_id")
    private SalesPO po;
    
    @NotNull
    private LocalDate date;
    
    @NotBlank
    private String number;
    
    @NotBlank
    private String particulars;
    
    @NotNull
    private Double amount;
    
    @NotNull
    private LocalDate dueBy;
}