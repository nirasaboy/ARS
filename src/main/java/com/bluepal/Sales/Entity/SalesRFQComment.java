package com.bluepal.Sales.Entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "sales_rfq_comments")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SalesRFQComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Comment text is mandatory")
    private String commentText;

    @NotNull
    private LocalDateTime commentedAt;

    @ManyToOne
    @JoinColumn(name = "rfq_id", nullable = false)
    private SalesRFQ salesRFQ;
    
    @PrePersist
     protected void onCreate() {
        this.commentedAt = LocalDateTime.now();
    }

	public void setSalesRFQLineItem(SalesRFQLineItem salesRFQ2) {
		// TODO Auto-generated method stub
		
	}

	public void setSalesQuote(SalesQuote salesQuote) {
		// TODO Auto-generated method stub
		
	}
}
