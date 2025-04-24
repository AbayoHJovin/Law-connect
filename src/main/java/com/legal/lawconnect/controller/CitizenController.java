package com.legal.lawconnect.controller;


import com.legal.lawconnect.dto.CitizenDto;
import com.legal.lawconnect.exceptions.AlreadyExistsException;
import com.legal.lawconnect.exceptions.ResourceNotFoundException;
import com.legal.lawconnect.model.Citizen;
import com.legal.lawconnect.model.RefreshToken;
import com.legal.lawconnect.requests.*;
import com.legal.lawconnect.response.ApiResponse;
import com.legal.lawconnect.services.auth.AuthService;
import com.legal.lawconnect.services.citizen.ICitizenService;
import com.legal.lawconnect.util.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/citizens")
public class CitizenController {
    private final ICitizenService citizenService;
    private final JwtUtil jwtUtil;
    private final AuthService authService;

    @PostMapping("/add")
    public ResponseEntity<ApiResponse> addCitizen(@RequestBody AddCitizenRequest citizen) {
        try{
            Citizen theCitizen = citizenService.addCitizen(citizen);
            CitizenDto convertedCitizen = citizenService.convertCitizenToDto(theCitizen);
            return ResponseEntity.ok(new ApiResponse("Success", convertedCitizen));
        }catch(RuntimeException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse(e.getMessage(), null));
        }
    }
    @GetMapping("/all")
    public ResponseEntity<ApiResponse> getAllCitizens(){
        try{
            List<Citizen> citizens = citizenService.getAllCitizens();
            List<CitizenDto> convertedCitizens = citizenService.getConvertedCitizens(citizens);
            return ResponseEntity.ok(new ApiResponse("Success", convertedCitizens));
        }catch(Exception e){
            return ResponseEntity.ok(new ApiResponse(e.getMessage(),null));
        }
    }

    @GetMapping("/get-by-id/{citizenId}")
    public ResponseEntity<ApiResponse> getCitizenById(@PathVariable("citizenId") UUID citizenId) {
        try{
            Citizen citizen = citizenService.getCitizenById(citizenId);
            CitizenDto convertedCitizen = citizenService.convertCitizenToDto(citizen);
            return ResponseEntity.ok(new ApiResponse("Success", convertedCitizen));
        }catch(RuntimeException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage(),null));
        }
    }

    @GetMapping("/find-by-location")
    public ResponseEntity<ApiResponse> getCitizensByLocation(@RequestParam("location") String location) {
        try{
            List<Citizen> citizen = citizenService.getCitizensByLocation(location);
            List<CitizenDto> convertedCitizens = citizenService.getConvertedCitizens(citizen);
            return ResponseEntity.ok(new ApiResponse("Success", convertedCitizens));
        }catch(ResourceNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage(),null));
        }
    }

    @GetMapping("/find-by-phone")
    public ResponseEntity<ApiResponse> getCitizensByPhone(@RequestParam("phone") String phone) {
        try{
            Citizen citizen = citizenService.getCitizenByPhoneNumber(phone);
            CitizenDto convertedCitizen = citizenService.convertCitizenToDto(citizen);
            return ResponseEntity.ok(new ApiResponse("Success", convertedCitizen));
        }catch (ResourceNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage(),null));
        }
    }

    @GetMapping("/find-by-email")
    public ResponseEntity<ApiResponse> getCitizenByEmail(@RequestParam("email") String email) {
        try{
            Citizen citizen = citizenService.getCitizenByEmail(email);
            CitizenDto convertedCitizen = citizenService.convertCitizenToDto(citizen);
            return ResponseEntity.ok(new ApiResponse("Success", convertedCitizen));
        }catch(ResourceNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage(),null));
        }
    }

    @PostMapping("/cit/rate-lawyer")
    public ResponseEntity<ApiResponse> rateLawyer(@RequestBody AddRatingRequest request){
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if(authentication == null || !authentication.isAuthenticated()){
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse("You are not logged in", null));
            }
            System.out.println("The authentication is " + authentication + " and the request is " + request);
            String email = authentication.getName();
            citizenService.rateLawyer(request,email);
            return ResponseEntity.ok(new ApiResponse("You've rated the lawyer successfully!", null));
        }catch(ResourceNotFoundException | AlreadyExistsException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PutMapping("/cit/change-password")
    public ResponseEntity<ApiResponse> updatePassword(@RequestBody ChangePasswordRequest request){
        try {
            citizenService.changePassword(request);
            return ResponseEntity.ok(new ApiResponse("Password changed Successfully", null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(),null));
        }
    }

    @PutMapping("/cit/changeLanguage")
    public ResponseEntity<ApiResponse> changeLanguage(@RequestParam String newLanguage) {
        try {
            // Get the currently authenticated user's email
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse("You are not logged in", null));
            }
            String email = authentication.getName();

            // Call the service method with the email
            citizenService.changeLanguagePreference(newLanguage, email);

            return ResponseEntity.ok(new ApiResponse("Language preference changed successfully", null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PatchMapping("/cit/update-citizen")
    public ResponseEntity<ApiResponse> updateCitizen(@RequestBody UpdateCitizenRequest request){
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse("You are not logged in", null));
            }
            String email = authentication.getName();
            Citizen cit = citizenService.updateCitizen(request, email);
            CitizenDto convertedCitizen = citizenService.convertCitizenToDto(cit);
            return ResponseEntity.ok(new ApiResponse("Success", convertedCitizen));
        }catch(ResourceNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage(),null));
        }
    }

    @DeleteMapping("cit-adm/deleteCitizen/{citizenId}")
    public ResponseEntity<ApiResponse> deleteCitizen(@PathVariable UUID citizenId){
        try {
            citizenService.deleteCitizen(citizenId);
            return ResponseEntity.ok(new ApiResponse("Success", null));
        }catch(ResourceNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage(),null));
        }
    }

    @PostMapping("/login-by-phone")
    public ResponseEntity<ApiResponse> loginByPhone(@RequestBody PhoneLoginRequest phoneLoginRequest){
        try{
            Citizen citizen = citizenService.findCitizenByPhoneNumberAndPassword(phoneLoginRequest);
            CitizenDto convertedCitizen = citizenService.convertCitizenToDto(citizen);
            return ResponseEntity.ok(new ApiResponse("Success", convertedCitizen));
        }catch (ResourceNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage(),null));
        }
    }

    @PostMapping("/login-by-email")
    public ResponseEntity<?> loginByEmail(@RequestBody EmailLoginRequest request, HttpServletResponse response){
        try{
            Citizen citizen = citizenService.findCitizenByEmailAndPassword(request);
            CitizenDto convertedCitizen = citizenService.convertCitizenToDto(citizen);
            String accessToken = jwtUtil.generateToken(convertedCitizen.getEmail());
            String refreshToken = authService.createRefreshTokenByCitizen(citizen);
            Cookie accessCookie = new Cookie("access_token", accessToken);
            accessCookie.setHttpOnly(true);
            accessCookie.setPath("/");
            accessCookie.setMaxAge(15 * 60); // 15 min

            Cookie refreshCookie = new Cookie("refresh_token", refreshToken);
            refreshCookie.setHttpOnly(true);
            refreshCookie.setPath("/");
            refreshCookie.setMaxAge(7 * 24 * 60 * 60); // 7 days

            response.addCookie(accessCookie);
            response.addCookie(refreshCookie);
            return ResponseEntity.ok(Map.of(
                    "accessToken", accessToken,
                    "refreshToken", refreshToken,
                    "citizen", convertedCitizen
            ));
        }catch (ResourceNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage(),null));
        }
    }


}
