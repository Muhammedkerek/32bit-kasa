package com.toyota.cashier.Services;

import com.toyota.cashier.DAO.AdminRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AdminDetailsServiceImp implements UserDetailsService {
    private AdminRepository adminRepository;

    public AdminDetailsServiceImp(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return adminRepository.findByUsername(username).orElseThrow(()-> new UsernameNotFoundException("Admin is not found"));
    }
}
