package com.v1.manfaa.testing;

import com.v1.manfaa.Api.ApiException;
import com.v1.manfaa.DTO.In.CompanyProfileDTOIn;
import com.v1.manfaa.DTO.In.RegisterDTOIn;
import com.v1.manfaa.DTO.Out.CompanyFullInfoDTOOut;
import com.v1.manfaa.DTO.Out.CompanyProfileDTOOut;
import com.v1.manfaa.Model.*;
import com.v1.manfaa.Repository.CompanyCreditRepository;
import com.v1.manfaa.Repository.CompanyProfileRepository;
import com.v1.manfaa.Repository.UserRepository;
import com.v1.manfaa.Service.CompanyProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompanyProfileServiceTest {

    @Mock
    private CompanyProfileRepository companyProfileRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CompanyCreditRepository companyCreditRepository;

    @InjectMocks
    private CompanyProfileService companyProfileService;

    private CompanyProfile companyProfile;
    private User user;
    private CompanyCredit companyCredit;
    private RegisterDTOIn registerDTOIn;
    private CompanyProfileDTOIn companyProfileDTOIn;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setRole("COMPANY");

        companyProfile = new CompanyProfile();
        companyProfile.setId(1);
        companyProfile.setName("Test Company");
        companyProfile.setIndustry("Technology");
        companyProfile.setTeamSize(10);
        companyProfile.setDescription("A test company");
        companyProfile.setCreatedAt(LocalDateTime.now());
        companyProfile.setIsSubscriber(false);
        companyProfile.setUser(user);
        companyProfile.setSkills(new HashSet<>());
        companyProfile.setReceivedReviews(new HashSet<>());

        companyCredit = new CompanyCredit();
        companyCredit.setId(1);
        companyCredit.setBalance(0.0);
        companyCredit.setTotalEarned(0.0);
        companyCredit.setTotalSpent(0.0);
        companyCredit.setCompanyProfile(companyProfile);

        companyProfile.setCompanyCredit(companyCredit);

        registerDTOIn = new RegisterDTOIn();
        registerDTOIn.setUsername("newuser");
        registerDTOIn.setPassword("password123");
        registerDTOIn.setEmail("new@example.com");
        registerDTOIn.setFullName("New User");
        registerDTOIn.setPhoneNumber("1234567890");
        registerDTOIn.setRecordNumber("REC123");
        registerDTOIn.setCompanyName("New Company");
        registerDTOIn.setIndustry("Finance");
        registerDTOIn.setTeamSize(50);
        registerDTOIn.setDescription("New company description");

        companyProfileDTOIn = new CompanyProfileDTOIn();
        companyProfileDTOIn.setName("Updated Company");
        companyProfileDTOIn.setIndustry("Healthcare");
        companyProfileDTOIn.setTeamSize(100);
        companyProfileDTOIn.setDescription("Updated description");
    }

    @Test
    void getAllCompanyProfiles_ShouldReturnAllProfiles() {
        // Arrange
        when(companyProfileRepository.findAll()).thenReturn(Arrays.asList(companyProfile));

        // Act
        List<CompanyProfileDTOOut> result = companyProfileService.getAllCompanyProfiles();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Company", result.get(0).getName());
        verify(companyProfileRepository, times(1)).findAll();
    }

    @Test
    void registerCompany_ShouldCreateCompanyAndUserAndCredit() {
        // Arrange
        when(userRepository.existsByEmail(registerDTOIn.getEmail())).thenReturn(false);
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        ArgumentCaptor<CompanyProfile> companyCaptor = ArgumentCaptor.forClass(CompanyProfile.class);
        ArgumentCaptor<CompanyCredit> creditCaptor = ArgumentCaptor.forClass(CompanyCredit.class);

        // Act
        companyProfileService.registerCompany(registerDTOIn);

        // Assert
        verify(userRepository).save(userCaptor.capture());
        verify(companyProfileRepository).save(companyCaptor.capture());
        verify(companyCreditRepository).save(creditCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertEquals("newuser", savedUser.getUsername());
        assertEquals("new@example.com", savedUser.getEmail());
        assertEquals("COMPANY", savedUser.getRole());

        CompanyProfile savedCompany = companyCaptor.getValue();
        assertEquals("New Company", savedCompany.getName());
        assertEquals("Finance", savedCompany.getIndustry());
        assertFalse(savedCompany.getIsSubscriber());

        CompanyCredit savedCredit = creditCaptor.getValue();
        assertEquals(0.0, savedCredit.getBalance());
        assertEquals(0.0, savedCredit.getTotalEarned());
        assertEquals(0.0, savedCredit.getTotalSpent());
    }

    @Test
    void registerCompany_ShouldThrowException_WhenEmailAlreadyExists() {
        // Arrange
        when(userRepository.existsByEmail(registerDTOIn.getEmail())).thenReturn(true);

        // Act & Assert
        ApiException exception = assertThrows(ApiException.class,
                () -> companyProfileService.registerCompany(registerDTOIn));

        assertEquals("Email already exists", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateCompanyProfile_ShouldUpdateProfile_WhenAuthorized() {
        // Arrange
        when(companyProfileRepository.findCompanyProfileById(1)).thenReturn(companyProfile);
        when(userRepository.findUserById(1)).thenReturn(user);

        // Act
        companyProfileService.updateCompanyProfile(1, companyProfileDTOIn, 1);

        // Assert
        verify(companyProfileRepository, times(1)).save(companyProfile);
        assertEquals("Updated Company", companyProfile.getName());
        assertEquals("Healthcare", companyProfile.getIndustry());
        assertEquals(100, companyProfile.getTeamSize());
        assertEquals("Updated description", companyProfile.getDescription());
    }

    @Test
    void updateCompanyProfile_ShouldThrowException_WhenCompanyNotFound() {
        // Arrange
        when(companyProfileRepository.findCompanyProfileById(999)).thenReturn(null);

        // Act & Assert
        ApiException exception = assertThrows(ApiException.class,
                () -> companyProfileService.updateCompanyProfile(1, companyProfileDTOIn, 999));

        assertEquals("Company Profile was not Found", exception.getMessage());
    }

    @Test
    void updateCompanyProfile_ShouldThrowException_WhenUserNotFound() {
        // Arrange
        when(companyProfileRepository.findCompanyProfileById(1)).thenReturn(companyProfile);
        when(userRepository.findUserById(999)).thenReturn(null);

        // Act & Assert
        ApiException exception = assertThrows(ApiException.class,
                () -> companyProfileService.updateCompanyProfile(999, companyProfileDTOIn, 1));

        assertEquals("user not found", exception.getMessage());
    }

    @Test
    void updateCompanyProfile_ShouldThrowException_WhenUnauthorized() {
        // Arrange
        User unauthorizedUser = new User();
        unauthorizedUser.setId(2);
        unauthorizedUser.setRole("COMPANY");

        when(companyProfileRepository.findCompanyProfileById(1)).thenReturn(companyProfile);
        when(userRepository.findUserById(2)).thenReturn(unauthorizedUser);

        // Act & Assert
        ApiException exception = assertThrows(ApiException.class,
                () -> companyProfileService.updateCompanyProfile(2, companyProfileDTOIn, 1));

        assertEquals("unauthorized to make changes", exception.getMessage());
    }

    @Test
    void updateCompanyProfile_ShouldAllowAdmin_ToUpdateAnyProfile() {
        // Arrange
        User admin = new User();
        admin.setId(2);
        admin.setRole("ADMIN");

        when(companyProfileRepository.findCompanyProfileById(1)).thenReturn(companyProfile);
        when(userRepository.findUserById(2)).thenReturn(admin);

        // Act
        companyProfileService.updateCompanyProfile(2, companyProfileDTOIn, 1);

        // Assert
        verify(companyProfileRepository, times(1)).save(companyProfile);
    }

    @Test
    void deleteCompanyProfile_ShouldDeleteCompanyAndRelatedEntities() {
        // Arrange
        when(companyProfileRepository.findById(1)).thenReturn(Optional.of(companyProfile));

        // Act
        companyProfileService.deleteCompanyProfile(1);

        // Assert
        verify(companyProfileRepository, times(1)).delete(companyProfile);
        verify(companyCreditRepository, times(1)).delete(companyCredit);
        verify(userRepository, times(1)).delete(user);
    }

    @Test
    void deleteCompanyProfile_ShouldThrowException_WhenCompanyNotFound() {
        // Arrange
        when(companyProfileRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        ApiException exception = assertThrows(ApiException.class,
                () -> companyProfileService.deleteCompanyProfile(999));

        assertEquals("Company Profile not found", exception.getMessage());
    }

    @Test
    void getCompanyDetails_ShouldReturnFullInfo_WhenCompanyExists() {
        // Arrange
        when(companyProfileRepository.findCompanyProfileById(1)).thenReturn(companyProfile);

        // Act
        CompanyFullInfoDTOOut result = companyProfileService.getCompanyDetails(1);

        // Assert
        assertNotNull(result);
        assertEquals("Test Company", result.getName());
        assertEquals("Technology", result.getIndustry());
    }

    @Test
    void getCompanyDetails_ShouldReturnEmptyDTO_WhenCompanyNotFound() {
        // Arrange
        when(companyProfileRepository.findCompanyProfileById(999)).thenReturn(null);

        // Act
        CompanyFullInfoDTOOut result = companyProfileService.getCompanyDetails(999);

        // Assert
        assertNotNull(result);
        assertNull(result.getName());
    }

    @Test
    void getAvgReviews_ShouldCalculateAverageCorrectly() {
        // Arrange
        Review review1 = new Review();
        review1.setRating(4.0);
        Review review2 = new Review();
        review2.setRating(5.0);
        Review review3 = new Review();
        review3.setRating(3.0);

        Set<Review> reviews = new HashSet<>(Arrays.asList(review1, review2, review3));
        companyProfile.setReceivedReviews(reviews);

        // Act
        Double avg = companyProfileService.getAvgReviews(companyProfile);

        // Assert
        assertEquals(4.0, avg, 0.01);
    }

    @Test
    void getAvgReviews_ShouldReturnZero_WhenNoReviews() {
        // Arrange
        companyProfile.setReceivedReviews(new HashSet<>());

        // Act
        Double avg = companyProfileService.getAvgReviews(companyProfile);

        // Assert
        assertEquals(0.0, avg);
    }

    @Test
    void getAllCompaniesFullDetails_ShouldReturnAllCompaniesWithFullInfo() {
        // Arrange
        when(companyProfileRepository.findAll()).thenReturn(Arrays.asList(companyProfile));

        // Act
        List<CompanyFullInfoDTOOut> result = companyProfileService.getAllCompaniesFullDetails();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Company", result.get(0).getName());
    }
}
