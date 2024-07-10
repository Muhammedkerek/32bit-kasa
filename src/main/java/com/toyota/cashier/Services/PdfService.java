package com.toyota.cashier.Services;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import com.toyota.cashier.Domain.Products;
import com.toyota.cashier.Domain.Sales;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class PdfService {
    private final ProductsService productsService;

    public PdfService(ProductsService productsService) {
        this.productsService = productsService;
    }

    public byte[] generatePdf(Sales sale) throws IOException, DocumentException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = new Document(new Rectangle(316, 1100)); // Approximate receipt size
        PdfWriter.getInstance(document, outputStream);
        document.open();

        // Set fonts
        Font titleFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
        Font subFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);
        Font textFont = new Font(Font.FontFamily.HELVETICA, 10);

        // Header
        Paragraph title = new Paragraph("My Business\n123 Main Street, Suite 101\nCity, State, Zip Code\n(535) 725-1920", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        document.add(new Paragraph("\n"));

        // Format date and time
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        // Sale info
        Paragraph saleInfo = new Paragraph(String.format("Date: %s    Time: %s\nSale ID: %d\nCashier: %s\n",
                sale.getSaleDate().toLocalDate().format(dateFormatter),
                sale.getSaleDate().toLocalTime().format(timeFormatter),
                sale.getId(),
                sale.getCashierName()), textFont);
        document.add(saleInfo);
        document.add(new Paragraph("-----------------------------------", textFont));

        // Product details
        List<Products> products = sale.getProducts();
        for (Products product : products) {
            // Get the initial quantity before the sale
            Long initialQuantity = productsService.findInitialQuantityById(product.getId());

            // Calculate sold quantity for this product
            Long soldQuantity = initialQuantity - product.getQuantity();

            double price = product.getPrice();

            // Print the sold quantity and calculate the total price
            document.add(new Paragraph(String.format("(%d X %s)     $%.2f",
                    soldQuantity,
                    product.getName(),
                    price * soldQuantity), textFont));
        }

        document.add(new Paragraph("-----------------------------------", textFont));

        // Total calculation
        double totalAmount = sale.getTotalAmount();
        double discount = (sale.getCoupons() != null) ? sale.getCoupons().getDiscountValue() : 0.0;

        if (discount > 0) {
            document.add(new Paragraph(String.format("Discount:                  $%.2f", discount), textFont));
        }
        document.add(new Paragraph(String.format("Total Amount:              $%.2f", totalAmount), textFont));

        document.add(new Paragraph("-----------------------------------", textFont));
        document.add(new Paragraph("Not a Tax Receipt", subFont));
        document.add(new Paragraph("-----------------------------------", textFont));

        document.close();
        return outputStream.toByteArray();
    }
}
