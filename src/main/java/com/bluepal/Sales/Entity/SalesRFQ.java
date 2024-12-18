package com.bluepal.Sales.Entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "sales_rfqs")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SalesRFQ {

	    @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	    @NotBlank(message = "Client name is mandatory")
	    private String clientName;

	    @NotBlank
		@Pattern(regexp = "active|inactive", message = "Status must be either 'active' or 'inactive'")
		private String status;

	    @NotNull
	    private LocalDateTime date;

	    @NotNull
	    private LocalDateTime deliveryBy;

	    @OneToMany(mappedBy = "salesRFQ", cascade = CascadeType.ALL, orphanRemoval = true)
	    private List<SalesRFQLineItem> lineItems = new ArrayList<>();

	    @PrePersist
	    protected void onCreate() {
	        this.date = LocalDateTime.now();
	    }

	    @PreUpdate
	    protected void onUpdate() {
	        this.deliveryBy = LocalDateTime.now();
	    }
	}

