package com.toyota.cashier.Services;

import com.toyota.cashier.DAO.CouponsRepository;
import com.toyota.cashier.DAO.ProductsRepository;
import com.toyota.cashier.DAO.SalesRepository;
import com.toyota.cashier.Domain.Coupons;
import com.toyota.cashier.Domain.Products;
import com.toyota.cashier.Domain.Sales;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SaleService {
    private final ProductsRepository productsRepository;
    private final SalesRepository salesRepository;
    private final CouponsRepository couponsRepository;

    public SaleService(ProductsRepository productsRepository, SalesRepository salesRepository, CouponsRepository couponsrepository) {
        this.productsRepository = productsRepository;
        this.salesRepository = salesRepository;
        this.couponsRepository = couponsrepository;
    }

    public List<Sales> getAllSales() {
        return salesRepository.findAllActiveSales();
    }

    public ResponseEntity<?> findSaleById(Long saleId) {
        Optional<Sales> optionalSale = salesRepository.findById(saleId);
        if (optionalSale.isPresent()) {
            return ResponseEntity.ok(optionalSale.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Sale not found with ID: " + saleId);
        }
    }

    public ResponseEntity<String> createSale(List<Long> productIds) {
        if (productIds.isEmpty()) {
            return ResponseEntity.badRequest().body("Product IDs list cannot be empty.");
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String cashierName = authentication.getName();

        try {
            Map<Long, Long> productCountMap = productIds.stream()
                    .collect(Collectors.groupingBy(productId -> productId, Collectors.counting()));

            List<Products> products = productCountMap.keySet().stream()
                    .map(productId -> {
                        Products product = productsRepository.findById(productId).orElse(null);
                        if (product == null || product.getId() == 0) {
                            throw new IllegalArgumentException("Invalid product ID: " + productId);
                        }
                        if (product.isDeleted() == true) {
                            throw new IllegalArgumentException("Product ID " + productId + " is deleted.");
                        }
                        return product;
                    })
                    .filter(Objects::nonNull)

                    .collect(Collectors.toList());

            double totalAmount = 0;

            for (Products product : products) {
                Long inputQuantity = productCountMap.get(product.getId());
                if (product.getQuantity() < inputQuantity) {
                    throw new IllegalArgumentException("Insufficient quantity for product ID: " + product.getId());
                }

                if (product.getQuantity() > 0 && inputQuantity <= product.getQuantity()) {
                    totalAmount += product.getPrice() * inputQuantity;
                    product.setQuantity(product.getQuantity() - inputQuantity);
                    productsRepository.save(product);
                }
            }

            Sales sale = new Sales();
            sale.setCashierName(cashierName);
            sale.setSaleDate(LocalDateTime.now());
            sale.setProducts(products);
            sale.setTotalAmount(totalAmount);
            int salesDayValue = sale.getDayOfWeekNumber();


            if (salesDayValue == 6 || salesDayValue == 7 && totalAmount > 100) {
                Coupons coupon = new Coupons();
                coupon.setDiscountValue(40.0);
                coupon.setDiscountType(Coupons.DiscountType.FIXED);
                coupon.setExpirationDate(LocalDateTime.now().plusDays(2));
                couponsRepository.save(coupon);
                totalAmount -= 40.0;
                sale.setTotalAmount(totalAmount);
                sale.setCoupons(coupon);
            } else if (totalAmount >= 100) {
                Coupons coupons = new Coupons();
                double discountValue = totalAmount * 0.10;
                coupons.setDiscountValue(discountValue);
                coupons.setExpirationDate(LocalDateTime.now().plusDays(2));
                coupons.setDiscountType(Coupons.DiscountType.PERCENTAGE);
                couponsRepository.save(coupons);
                totalAmount = totalAmount - discountValue;
                sale.setTotalAmount(totalAmount);
                sale.setCoupons(coupons);
            }


            salesRepository.save(sale);
            return ResponseEntity.ok().body("Sale created successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error creating sale: " + e.getMessage());
        }

    }

    public ResponseEntity<String> updateSale(Long saleId, List<Long> newProductIds) {
        // first checking if the Product's list in the request body is not empty

        if (newProductIds.isEmpty()) {
            return ResponseEntity.badRequest().body("Product IDs list cannot be empty.");
        }


        // getting the cashier name from the authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String cashierName = authentication.getName();

        try {
            Sales existingSale = salesRepository.findById(saleId).orElse(null);
            if (existingSale == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Sale not found.");
            }

            // Revert stock for old products
            for (Products oldProduct : existingSale.getProducts()) {
                oldProduct.setQuantity(oldProduct.getQuantity() + 1);
                productsRepository.save(oldProduct);
            }

            // Fetch and validate new products
            List<Products> newProducts = newProductIds.stream()
                    .map(productId -> {
                        Products product = productsRepository.findById(productId).orElse(null);
                        if (product == null || product.getId() == 0) {
                            throw new IllegalArgumentException("Invalid product ID: " + productId);
                        }
                        if (product.isDeleted() == true) {
                            throw new IllegalArgumentException("Product ID " + productId + " is deleted.");
                        }
                        return product;
                    })
                    .filter(Objects::nonNull)

                    .collect(Collectors.toList());

            double newTotalAmount = 0;
            for (Products newProduct : newProducts) {
                if (newProduct.getQuantity() <= 0) {
                    throw new IllegalArgumentException("Quantity is zero for product ID: " + newProduct.getId());
                }
                newTotalAmount += newProduct.getPrice();
                newProduct.setQuantity(newProduct.getQuantity() - 1);
                productsRepository.save(newProduct);
            }

            // Update the sale
            existingSale.setCashierName(cashierName);
            existingSale.setSaleDate(LocalDateTime.now());
            existingSale.setProducts(newProducts);
            existingSale.setTotalAmount(newTotalAmount);

            Coupons existingCoupon = existingSale.getCoupons(); // the id of coupon in sale
            if (existingCoupon != null) {
                salesRepository.delete(existingSale);
                couponsRepository.delete(existingCoupon);

            }

            int salesDayValue = existingSale.getDayOfWeekNumber();

            if ((salesDayValue == 6 || salesDayValue == 7) && newTotalAmount > 100) {
                Coupons coupon = new Coupons();
                coupon.setDiscountValue(40.0);
                coupon.setDiscountType(Coupons.DiscountType.FIXED);
                coupon.setExpirationDate(LocalDateTime.now().plusDays(2));
                couponsRepository.save(coupon);
                newTotalAmount -= 40.0;
                existingSale.setTotalAmount(newTotalAmount);
                existingSale.setCoupons(coupon);
            } else if (newTotalAmount >= 100) {
                Coupons coupon = new Coupons();
                double discountValue = newTotalAmount * 0.10;
                coupon.setDiscountValue(discountValue);
                coupon.setExpirationDate(LocalDateTime.now().plusDays(2));
                coupon.setDiscountType(Coupons.DiscountType.PERCENTAGE);
                couponsRepository.save(coupon);
                newTotalAmount -= discountValue;
                existingSale.setTotalAmount(newTotalAmount);
                existingSale.setCoupons(coupon);
            } else {
                existingSale.setCoupons(null);
            }

            salesRepository.save(existingSale);
            return ResponseEntity.ok().body("Sale updated successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error updating sale: " + e.getMessage());
        }

    }

    public ResponseEntity<String> deleteSale(Long id) {
        Optional<Sales> sale = salesRepository.findById(id);
        if (sale.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Sale not found with ID: " + id);
        }

        Sales deletedSale = sale.get();
        deletedSale.setDeleted(true);
        salesRepository.save(deletedSale);

        return ResponseEntity.ok().body("Sale Soft Deleted successfully.");

    }


}
