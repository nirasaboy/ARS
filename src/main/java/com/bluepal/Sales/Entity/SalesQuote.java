package com.bluepal.Sales.Entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "sales_quotes")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SalesQuote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private int number;
    
    @Column(nullable = false)
    private double amount;
    
    @NotNull
    private LocalDateTime approvedAt;
    
    @Column(nullable = false)
    private String status;

    @OneToMany(mappedBy = "salesQuote", cascade = CascadeType.ALL)
    private List<SalesQuoteLineItem> lineItems;

    @PrePersist
    protected void onCreate() {
        this.date = LocalDate.now();
    }
}