package com.legal.lawconnect.services;

import com.legal.lawconnect.model.Citizen;
import com.legal.lawconnect.model.Lawyer;
import com.legal.lawconnect.repository.CitizenRepository;
import com.legal.lawconnect.repository.LawyerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final CitizenRepository citizenRepository;
    private final LawyerRepository lawyerRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Try to find citizen by email
        Citizen citizen = citizenRepository.findByEmail(email);
        if (citizen != null) {
            return User.withUsername(citizen.getEmail())
                    .password(citizen.getPassword())
                    .roles("CITIZEN")
                    .build();
        }

        // Try to find lawyer by email
        Lawyer lawyer = lawyerRepository.findByEmail(email);
        if (lawyer != null) {
            return User.withUsername(lawyer.getEmail())
                    .password(lawyer.getPassword())
                    .roles("LAWYER")
                    .build();
        }

        // If not found in either
        throw new UsernameNotFoundException("User not found with email: " + email);
    }
}
