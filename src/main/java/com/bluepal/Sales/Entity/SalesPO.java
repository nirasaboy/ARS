package com.bluepal.Sales.Entity;

import java.time.LocalDate;
import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Entity
@Table(name = "sales_po")
@Data
public class SalesPO {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "client_id")
	private Client client;

	@NotNull
	private LocalDate date;
	
	@NotBlank
	private String number;
	
	@NotNull
	private Double amount;

	@NotBlank
	private String approvedBy;

	@NotBlank
	private String deliveryBy;
	
	@NotBlank
	@Pattern(regexp = "active|inactive", message = "Status must be either 'active' or 'inactive'")
	private String status;

	@OneToMany(mappedBy = "po")
	private Set<SalesPOLineItem> lineItems;
}