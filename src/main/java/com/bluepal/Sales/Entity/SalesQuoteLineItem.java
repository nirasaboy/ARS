package com.bluepal.Sales.Entity;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "sales_quote_line_items")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SalesQuoteLineItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Double rate;

    @NotNull(message = " quanity is mandatory")
    private int quantity;
    
    @NotBlank(message = "required Unit Of Measure")
    private String unitOfMeasure;
    
    @Column(nullable = false)
    private double amount;
    
    @NotNull
    private LocalDate promiseDate;
    
    @NotBlank
	@Pattern(regexp = "active|inactive", message = "Status must be either 'active' or 'inactive'")
	private String status;
    
    @ManyToOne
    @JoinColumn(name = "quote_id")
    @JsonIgnore
    private SalesQuote salesQuote;
}
