package com.bluepal.Sales.Entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "comments")
@Data
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;

    
//    private String author;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    // Associating the comment with a SalesClient
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sales_client_id", nullable = false)
    private Client salesClient;

    // Associating the comment with a SalesRFQ
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sales_rfq_id", nullable = true) 
    private SalesRFQ rfq;

	public void setSalesQuote(SalesQuote salesQuote) {
		// TODO Auto-generated method stub
		
	}

	public void setSalesQuoteLineItem(SalesQuoteLineItem salesQuote) {
		// TODO Auto-generated method stub
		
	}

	public void setSalesPO(SalesPO salesPo) {
		// TODO Auto-generated method stub
		
	}

	public void setSalesPOLineItem(SalesPOLineItem salesPo) {
		// TODO Auto-generated method stub
		
	}

	public void setSalesInvoice(SalesInvoice salesInvoice) {
		// TODO Auto-generated method stub
		
	}

	public void setSalesPayment(SalesPayment salesPayment) {
		// TODO Auto-generated method stub
		
	}


}