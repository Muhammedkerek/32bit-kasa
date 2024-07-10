package com.toyota.cashier.Resources;


import com.itextpdf.text.DocumentException;
import com.toyota.cashier.Domain.Sales;
import com.toyota.cashier.Services.PdfService;
import com.toyota.cashier.Services.SaleService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@RestController
public class PdfResource {
    private final PdfService pdfService;
    private final SaleService saleService;

    public PdfResource(PdfService pdfService, SaleService saleService) {
        this.pdfService = pdfService;
        this.saleService = saleService;
    }

    @PreAuthorize("hasAnyAuthority('STORE_MANAGER', 'ADMIN')")
    @GetMapping("/sales/{saleId}/pdf")

        public ResponseEntity<String> generateSalePdf(@PathVariable Long saleId) {
            try {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

                if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
                    UserDetails userDetails = (UserDetails) authentication.getPrincipal();
                    // Check if user is authorized
                    if (userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("STORE_MANAGER"))) {


                        ResponseEntity<?> responseEntity = saleService.findSaleById(saleId);
                        if (responseEntity.getStatusCode() == HttpStatus.OK && responseEntity.getBody() instanceof Sales) {
                            Sales sale = (Sales) responseEntity.getBody();

                            byte[] pdfBytes = pdfService.generatePdf(sale);

                            // Save the PDF file
                            String directoryPath = "Receipts";
                            File receiptsDir = new File(directoryPath);
                            if (!receiptsDir.exists()) {
                                receiptsDir.mkdirs();
                            }
                            String pdfFilePath = directoryPath + "/salesReceipt.pdf";
                            Files.write(Paths.get(pdfFilePath), pdfBytes);

                            return ResponseEntity.ok("Receipt has been created and saved successfully.");
                        } else if (responseEntity.getStatusCode() == HttpStatus.NOT_FOUND) {
                            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Sale not found with ID: " + saleId);
                        } else {
                            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error retrieving sale details.");
                        }
                    }
                    else{
                        throw new AccessDeniedException("You are not allowed to ");
                    }
                } else {
                    throw new AccessDeniedException("Authentication failed or user not found.");
                }



            } catch (DocumentException | IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating or saving receipt.");
            }

        
    }

}
