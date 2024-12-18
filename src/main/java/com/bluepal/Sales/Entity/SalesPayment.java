package com.bluepal.Sales.Entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "sales_payments")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SalesPayment {

	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	    @NotNull
	    private LocalDate date; // Date of payment

	    @Column(nullable = false)
	    private double amount; // Amount of payment

	    @Column(nullable = false)
	    private String modeOfPayment; // Mode of payment (e.g., Credit Card, Bank Transfer, etc.)

}
