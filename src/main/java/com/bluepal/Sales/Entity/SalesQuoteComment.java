package com.bluepal.Sales.Entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "quote_comments")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SalesQuoteComment {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sales_po_id", nullable = false)
    private SalesQuote salesQuote;

    @Column(nullable = false)
    private String commentText;

    @Column(nullable = false)
    private LocalDateTime commentedAt;

	public void setSalesQuoteLineItem(SalesQuoteLineItem salesQuote2) {
		// TODO Auto-generated method stub
		
	}
}
