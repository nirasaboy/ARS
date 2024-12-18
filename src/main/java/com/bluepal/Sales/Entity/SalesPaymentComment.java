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
@Table(name = "sales_payment_comments")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SalesPaymentComment {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sales_payment_id", nullable = false)
    private SalesPayment salesPayment;

    @Column(nullable = false)
    private String commentText;

    @Column(nullable = false)
    private LocalDateTime commentedAt;
}
