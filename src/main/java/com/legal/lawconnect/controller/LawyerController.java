package com.legal.lawconnect.controller;

import com.legal.lawconnect.dto.LawyerDto;
import com.legal.lawconnect.dto.RatingDto;
import com.legal.lawconnect.exceptions.ResourceNotFoundException;
import com.legal.lawconnect.model.Lawyer;
import com.legal.lawconnect.requests.*;
import com.legal.lawconnect.response.ApiResponse;
import com.legal.lawconnect.services.auth.AuthService;
import com.legal.lawconnect.services.lawyer.ILawyerService;
import com.legal.lawconnect.services.rating.RatingService;
import com.legal.lawconnect.util.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/lawyers")
public class LawyerController {
    private final ILawyerService lawyerService;
    private final RatingService ratingService;
    private final JwtUtil jwtUtil;
    private final AuthService authService;

    @PostMapping("/add")
    public ResponseEntity<ApiResponse> addLawyer(@RequestBody AddLawyerRequest lawyer) {
        try{
            Lawyer lawyerSaved = lawyerService.save(lawyer);
            LawyerDto convertedLawyer = lawyerService.convertLawyerToDto(lawyerSaved);
            return ResponseEntity.ok(new ApiResponse("success", convertedLawyer));
        }catch(RuntimeException e){
            return ResponseEntity.status(500).body(new ApiResponse(e.getMessage(),null));
        }
    }


    @GetMapping("/all")
    public ResponseEntity<ApiResponse> getAllLawyers() {
        try {
            List<Lawyer> lawyers = lawyerService.findAll();
            List<LawyerDto> convertedLawyers = lawyerService.getConvertedLawyers(lawyers);
            return ResponseEntity.ok(new ApiResponse("success", convertedLawyers));
        }catch (Exception e){
            return ResponseEntity.status(500).body(new ApiResponse(e.getMessage(),null));
        }
    }

    @GetMapping("/get-by-id/{id}")
    public ResponseEntity<ApiResponse> findLawyerById(@PathVariable UUID id) {
        try {
            Lawyer lawyer = lawyerService.findById(id);
            LawyerDto convertedLawyer = lawyerService.convertLawyerToDto(lawyer);
            return ResponseEntity.ok(new ApiResponse("success", convertedLawyer));
        }catch (RuntimeException e){
            return ResponseEntity.status(404).body(new ApiResponse(e.getMessage(),null));
        }
    }

    @GetMapping("/find-by-email")
    public ResponseEntity<ApiResponse> findLawyerByEmail(@RequestParam("email") String email) {
        try {
            Lawyer lawyer = lawyerService.findByEmail((email));
            LawyerDto convertedLawyer = lawyerService.convertLawyerToDto(lawyer);
            return ResponseEntity.ok(new ApiResponse("success", convertedLawyer));
        }catch (Exception e){
            return ResponseEntity.status(500).body(new ApiResponse("error", e.getMessage()));
        }
    }

    @GetMapping("/find-by-phone")
    public ResponseEntity<ApiResponse> findLawyerByPhone(@RequestParam("phone") String phone) {
        try {
            Lawyer lawyer = lawyerService.findByPhone(phone);
            LawyerDto convertedLawyer = lawyerService.convertLawyerToDto(lawyer);
            return ResponseEntity.ok(new ApiResponse("success", convertedLawyer));
        }catch (Exception e){
            return ResponseEntity.status(500).body(new ApiResponse("error", e.getMessage()));
        }
    }

    @GetMapping("/find-lawyer-phone-by-lawyerId/{lawyerId}")
    public ResponseEntity<ApiResponse> findLawyerPhone(@PathVariable UUID lawyerId) {
        try {
            String lawyerPhoneNumber = lawyerService.getLawyerPhoneNumber(lawyerId);
            return ResponseEntity.ok(new ApiResponse("success", lawyerPhoneNumber));
        }catch (RuntimeException e){
            return ResponseEntity.status(500).body(new ApiResponse("error", e.getMessage()));
        }
    }

    @PatchMapping("/lawy/update")
    public ResponseEntity<ApiResponse> updateLawyer(@RequestBody UpdateLawyerRequest request){
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if(authentication == null || !authentication.isAuthenticated()){
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse("You are not logged in", null));
            }
            String email = authentication.getName();
            Lawyer lawyer = lawyerService.updateLawyer(request,email);
            LawyerDto convertedLawyer = lawyerService.convertLawyerToDto(lawyer);
            return ResponseEntity.ok(new ApiResponse("Lawyer Updated successfully", convertedLawyer));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @DeleteMapping("/lawy-adm/delete/{id}")
    public ResponseEntity<ApiResponse> deleteLawyerById(@PathVariable("id") String id) {
        try {
            UUID uuid = UUID.fromString(id);
            lawyerService.deleteLawyer((uuid));
            return ResponseEntity.ok(new ApiResponse("Lawyer deletion successful", null));
        }catch (Exception e){
            return ResponseEntity.status(500).body(new ApiResponse("error", e.getMessage()));
        }
    }


    @PutMapping("/lawy/changeAvailability")
    public ResponseEntity<ApiResponse> changeAvailabilityForWork(@RequestParam boolean availability, @RequestParam UUID lawyerId) {
        try{
            lawyerService.setAvailabilityForWork(lawyerId, availability);
            return ResponseEntity.ok(new ApiResponse("Availability changed!", null));
        }catch(ResourceNotFoundException e){
            return ResponseEntity.status(404).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PostMapping("/login-by-phone")
    public ResponseEntity<ApiResponse> phoneLogin (@RequestBody PhoneLoginRequest phoneLoginRequest) {
        try{
            Lawyer lawyer = lawyerService.findLawyerByPhoneAndPassword(phoneLoginRequest);
            LawyerDto convertedLawyer = lawyerService.convertLawyerToDto(lawyer);
            return ResponseEntity.ok(new ApiResponse("Success", convertedLawyer));
        }catch (RuntimeException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage(),null));
        }
    }


    @PostMapping("/login-by-email")
    public ResponseEntity<?> emailLogin(@RequestBody EmailLoginRequest emailLoginRequest, HttpServletResponse response) {
        try{
            Lawyer lawyer = lawyerService.findLawyerByEmailAndPassword(emailLoginRequest);
            LawyerDto convertedLawyer = lawyerService.convertLawyerToDto(lawyer);
            String accessToken = jwtUtil.generateToken(convertedLawyer.getEmail());
            String refreshToken = authService.createRefreshTokenByLawyer(lawyer);
            Cookie accessCookie = new Cookie("access_token",accessToken);
            accessCookie.setHttpOnly(true);
            accessCookie.setPath("/");
            accessCookie.setMaxAge(15 * 60);

            Cookie refreshCookie = new Cookie("refresh_token",refreshToken);
            refreshCookie.setHttpOnly(true);
            refreshCookie.setPath("/");
            refreshCookie.setMaxAge(7 * 24 * 60 * 60); // 7 days

            response.addCookie(accessCookie);
            response.addCookie(refreshCookie);
            return ResponseEntity.ok(Map.of(
                    "accessToken", accessToken,
                    "refreshToken", refreshToken,
                    "citizen", convertedLawyer
            ));
        }catch (RuntimeException e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(e.getMessage(),null));
        }
    }

    @GetMapping("/find-rating-below/{ratingValue}")
    public ResponseEntity<ApiResponse> findRatingBelow(@PathVariable int ratingValue){
        try{
            List<Lawyer> lawyers = lawyerService.findLawyersByRatingScoresBelow(ratingValue);
            List<LawyerDto> lawyerDtos = lawyerService.getConvertedLawyers(lawyers);
            return ResponseEntity.ok(new ApiResponse("Success", lawyerDtos));
        }
        catch (RuntimeException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse(e.getMessage(),null));
        }
    }

    @GetMapping("/find-rating-above/{ratingValue}")
    public ResponseEntity<ApiResponse> findRatingAbove(@PathVariable int ratingValue){
        try{
            List<Lawyer> lawyers = lawyerService.findLawyersByRatingScoresAbove(ratingValue);
            List<LawyerDto> lawyerDtos = lawyerService.getConvertedLawyers(lawyers);
            return ResponseEntity.ok(new ApiResponse("Success", lawyerDtos));
        }
        catch (RuntimeException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse(e.getMessage(),null));
        }
    }

    @GetMapping("/get-all-rating")
    public ResponseEntity<ApiResponse> getAllRating(){
    try{
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null || !authentication.isAuthenticated()){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse("You are not logged in", null));
        }
        String email = authentication.getName();
        List<RatingDto> ratingDtos = ratingService.getRatingsOfLawyer(email);
        return ResponseEntity.ok(new ApiResponse("Success", ratingDtos));
    }catch (RuntimeException e){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage(),null));
    }
    }



    @GetMapping("/find-rating-equals-to/{ratingValue}")
    public ResponseEntity<ApiResponse> findRatingEqualsTo(@PathVariable int ratingValue){
        try{
            List<Lawyer> lawyers = lawyerService.findLawyersByRatingScoresEqualsTo(ratingValue);
            List<LawyerDto> lawyerDtos = lawyerService.getConvertedLawyers(lawyers);
            return ResponseEntity.ok(new ApiResponse("Success", lawyerDtos));
        }
        catch (RuntimeException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse(e.getMessage(),null));
        }
    }

    @PutMapping("/lawy/changeLanguage/{lawyerId}")
    public ResponseEntity<ApiResponse> changeLanguagePreference(@RequestParam String newLanguage, @PathVariable UUID lawyerId){
        try{
            lawyerService.changeLanguagePreference(newLanguage, lawyerId);
            return ResponseEntity.ok(new ApiResponse("Language preference changed Successfully", null));
        }catch(ResourceNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage(),null));
        }

    }

    @PutMapping("/lawy/change-password")
    public ResponseEntity<ApiResponse> updatePassword(@RequestBody ChangePasswordRequest request){
        try {
            lawyerService.changePassword(request);
            return ResponseEntity.ok(new ApiResponse("Password changed Successfully", null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse(e.getMessage(),null));
        }
    }

    @GetMapping("/lawy/getCurrent")
    public ResponseEntity<ApiResponse> getCurrentLawyer(){
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if(authentication == null || !authentication.isAuthenticated()){
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse("You are not logged in", null));
            }
            String email = authentication.getName();
            Lawyer lawyer = lawyerService.findByEmail(email);
            LawyerDto convertedLawyer = lawyerService.convertLawyerToDto(lawyer);
            return ResponseEntity.ok(new ApiResponse("Success", convertedLawyer));
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(),null));
        }
    }
}
