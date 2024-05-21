package com.toyota.cashier.Services;

import com.toyota.cashier.DAO.RolesRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class RolesDetailsServiceImp implements UserDetailsService {
    private RolesRepository rolesRepository;

    public RolesDetailsServiceImp(RolesRepository rolesRepository) {
        this.rolesRepository = rolesRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return rolesRepository.findByUsername(username).orElseThrow(()-> new UsernameNotFoundException("Admin is not found"));
    }
}
