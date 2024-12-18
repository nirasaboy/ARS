package com.bluepal.Sales.Service;

import org.springframework.stereotype.Service;

import com.bluepal.Sales.Entity.SalesRFQ;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import jakarta.servlet.ServletOutputStream;

import java.io.IOException;
import java.util.List;

@Service
public class SalesRFQPdfService {

    public void generatePDF(List<SalesRFQ> salesRFQs, ServletOutputStream servletOutputStream) throws Exception {

    	try {
			Document document = new Document();
			PdfWriter.getInstance(document, servletOutputStream);
			document.open();
			
			for(SalesRFQ salesRFQ : salesRFQs) {
				document.add(new Paragraph("Sales RFQ ID: " + salesRFQ.getId()));
		        document.add(new Paragraph("Client Name: " + salesRFQ.getClientName()));
		        document.add(new Paragraph("Date: " + salesRFQ.getDate()));
		        document.add(new Paragraph("Status: " + salesRFQ.getStatus()));
		        document.add(new Paragraph("Delivery By: " + salesRFQ.getDeliveryBy()));

			}
			 document.close();
		} catch (Exception e) {
			throw new IOException("Error expoting to PDF", e);
		}

    }
}
