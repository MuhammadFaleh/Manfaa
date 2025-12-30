package com.v1.manfaa.testing;

import com.v1.manfaa.Api.ApiException;
import com.v1.manfaa.DTO.In.SkillsDTOIn;
import com.v1.manfaa.DTO.Out.SkillsDTOOut;
import com.v1.manfaa.Model.CompanyProfile;
import com.v1.manfaa.Model.Skills;
import com.v1.manfaa.Repository.CompanyProfileRepository;
import com.v1.manfaa.Repository.SkillsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SkillsServiceTest {

    @Mock
    private SkillsRepository skillsRepository;

    @Mock
    private CompanyProfileRepository companyProfileRepository;

    @InjectMocks
    private SkillsService skillsService;

    private Skills skill1;
    private Skills skill2;
    private CompanyProfile companyProfile;
    private SkillsDTOIn skillsDTOIn;

    @BeforeEach
    void setUp() {
        skill1 = new Skills(1, "Java", "Java programming", new HashSet<>());
        skill2 = new Skills(2, "Python", "Python programming", new HashSet<>());

        companyProfile = new CompanyProfile();
        companyProfile.setId(1);
        companyProfile.setName("Tech Company");
        companyProfile.setSkills(new HashSet<>());

        skillsDTOIn = new SkillsDTOIn();
        skillsDTOIn.setName("JavaScript");
        skillsDTOIn.setDescription("JavaScript programming");
    }

    @Test
    void getAllSkills_ShouldReturnAllSkills() {
        // Arrange
        when(skillsRepository.findAll()).thenReturn(Arrays.asList(skill1, skill2));

        // Act
        List<SkillsDTOOut> result = skillsService.getAllSkills();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Java", result.get(0).getName());
        assertEquals("Python", result.get(1).getName());
        verify(skillsRepository, times(1)).findAll();
    }

    @Test
    void addSkills_ShouldSaveSkill_WhenSkillDoesNotExist() {
        // Arrange
        when(skillsRepository.findSkillsByName("JavaScript")).thenReturn(null);

        // Act
        skillsService.addSkills(skillsDTOIn);

        // Assert
        verify(skillsRepository, times(1)).findSkillsByName("JavaScript");
        verify(skillsRepository, times(1)).save(any(Skills.class));
    }

    @Test
    void addSkills_ShouldThrowException_WhenSkillAlreadyExists() {
        // Arrange
        when(skillsRepository.findSkillsByName("JavaScript")).thenReturn(skill1);

        // Act & Assert
        ApiException exception = assertThrows(ApiException.class,
                () -> skillsService.addSkills(skillsDTOIn));

        assertEquals("skill already exist", exception.getMessage());
        verify(skillsRepository, times(1)).findSkillsByName("JavaScript");
        verify(skillsRepository, never()).save(any(Skills.class));
    }

    @Test
    void updateSkills_ShouldUpdateSkill_WhenSkillExists() {
        // Arrange
        when(skillsRepository.findSkillsById(1)).thenReturn(skill1);

        // Act
        skillsService.updateSkills(1, skillsDTOIn);

        // Assert
        verify(skillsRepository, times(1)).findSkillsById(1);
        verify(skillsRepository, times(1)).save(skill1);
        assertEquals("JavaScript", skill1.getName());
        assertEquals("JavaScript programming", skill1.getDescription());
    }

    @Test
    void updateSkills_ShouldThrowException_WhenSkillNotFound() {
        // Arrange
        when(skillsRepository.findSkillsById(999)).thenReturn(null);

        // Act & Assert
        ApiException exception = assertThrows(ApiException.class,
                () -> skillsService.updateSkills(999, skillsDTOIn));

        assertEquals("Skills not found", exception.getMessage());
        verify(skillsRepository, never()).save(any(Skills.class));
    }

    @Test
    void deleteSkills_ShouldDeleteSkill_WhenSkillExists() {
        // Arrange
        when(skillsRepository.findSkillsById(1)).thenReturn(skill1);

        // Act
        skillsService.deleteSkills(1);

        // Assert
        verify(skillsRepository, times(1)).findSkillsById(1);
        verify(skillsRepository, times(1)).delete(skill1);
    }

    @Test
    void deleteSkills_ShouldThrowException_WhenSkillNotFound() {
        // Arrange
        when(skillsRepository.findSkillsById(999)).thenReturn(null);

        // Act & Assert
        ApiException exception = assertThrows(ApiException.class,
                () -> skillsService.deleteSkills(999));

        assertEquals("Skills not found", exception.getMessage());
        verify(skillsRepository, never()).delete(any(Skills.class));
    }

    @Test
    void assignSkill_ShouldAssignSkillToCompany_WhenBothExist() {
        // Arrange
        when(skillsRepository.findSkillsById(1)).thenReturn(skill1);
        when(companyProfileRepository.findCompanyProfileById(1)).thenReturn(companyProfile);

        // Act
        skillsService.assignSkill(1, 1);

        // Assert
        verify(skillsRepository, times(1)).findSkillsById(1);
        verify(companyProfileRepository, times(1)).findCompanyProfileById(1);
        verify(companyProfileRepository, times(1)).save(companyProfile);
        assertTrue(companyProfile.getSkills().contains(skill1));
    }

    @Test
    void assignSkill_ShouldThrowException_WhenSkillNotFound() {
        // Arrange
        when(skillsRepository.findSkillsById(999)).thenReturn(null);

        // Act & Assert
        ApiException exception = assertThrows(ApiException.class,
                () -> skillsService.assignSkill(1, 999));

        assertEquals("Skill or company not found", exception.getMessage());
        verify(companyProfileRepository, never()).save(any(CompanyProfile.class));
    }

    @Test
    void assignSkill_ShouldThrowException_WhenCompanyNotFound() {
        // Arrange
        when(skillsRepository.findSkillsById(1)).thenReturn(skill1);
        when(companyProfileRepository.findCompanyProfileById(999)).thenReturn(null);

        // Act & Assert
        ApiException exception = assertThrows(ApiException.class,
                () -> skillsService.assignSkill(999, 1));

        assertEquals("Skill or company not found", exception.getMessage());
        verify(companyProfileRepository, never()).save(any(CompanyProfile.class));
    }

    @Test
    void assignSkill_ShouldThrowException_WhenSkillAlreadyAssigned() {
        // Arrange
        companyProfile.getSkills().add(skill1);
        when(skillsRepository.findSkillsById(1)).thenReturn(skill1);
        when(companyProfileRepository.findCompanyProfileById(1)).thenReturn(companyProfile);

        // Act & Assert
        ApiException exception = assertThrows(ApiException.class,
                () -> skillsService.assignSkill(1, 1));

        assertEquals("skill already added", exception.getMessage());
        verify(companyProfileRepository, never()).save(any(CompanyProfile.class));
    }

    @Test
    void removeSkill_ShouldRemoveSkillFromCompany_WhenSkillIsAssigned() {
        // Arrange
        companyProfile.getSkills().add(skill1);
        when(skillsRepository.findSkillsById(1)).thenReturn(skill1);
        when(companyProfileRepository.findCompanyProfileById(1)).thenReturn(companyProfile);

        // Act
        skillsService.removeSkill(1, 1);

        // Assert
        verify(companyProfileRepository, times(1)).save(companyProfile);
        assertFalse(companyProfile.getSkills().contains(skill1));
    }

    @Test
    void removeSkill_ShouldThrowException_WhenSkillNotAssigned() {
        // Arrange
        when(skillsRepository.findSkillsById(1)).thenReturn(skill1);
        when(companyProfileRepository.findCompanyProfileById(1)).thenReturn(companyProfile);

        // Act & Assert
        ApiException exception = assertThrows(ApiException.class,
                () -> skillsService.removeSkill(1, 1));

        assertEquals("skill not present", exception.getMessage());
    }

    @Test
    void getSkillsByCompany_ShouldReturnSkills_WhenCompanyExists() {
        // Arrange
        when(companyProfileRepository.findCompanyProfileById(1)).thenReturn(companyProfile);
        when(skillsRepository.findAllByCompanyProfile(companyProfile))
                .thenReturn(Arrays.asList(skill1, skill2));

        // Act
        List<SkillsDTOOut> result = skillsService.getSkillsByCompany(1);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(companyProfileRepository, times(1)).findCompanyProfileById(1);
    }

    @Test
    void getSkillsByCompany_ShouldThrowException_WhenCompanyNotFound() {
        // Arrange
        when(companyProfileRepository.findCompanyProfileById(999)).thenReturn(null);

        // Act & Assert
        ApiException exception = assertThrows(ApiException.class,
                () -> skillsService.getSkillsByCompany(999));

        assertEquals("Company not found", exception.getMessage());
    }

    @Test
    void searchSkillsByKeyword_ShouldReturnMatchingSkills() {
        // Arrange
        when(skillsRepository.findAllByNameContainingIgnoreCase("java"))
                .thenReturn(Arrays.asList(skill1));
        when(skillsRepository.findAllByDescriptionContainingIgnoreCase("java"))
                .thenReturn(Arrays.asList());

        // Act
        List<SkillsDTOOut> result = skillsService.searchSkillsByKeyword("java");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Java", result.get(0).getName());
    }

    @Test
    void searchSkillsByKeyword_ShouldCombineNameAndDescriptionMatches() {
        // Arrange
        when(skillsRepository.findAllByNameContainingIgnoreCase("programming"))
                .thenReturn(Arrays.asList(skill1));
        when(skillsRepository.findAllByDescriptionContainingIgnoreCase("programming"))
                .thenReturn(Arrays.asList(skill2));

        // Act
        List<SkillsDTOOut> result = skillsService.searchSkillsByKeyword("programming");

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void convertToDtoOut_ShouldConvertSkillsToDTO() {
        // Arrange
        List<Skills> skills = Arrays.asList(skill1, skill2);

        // Act
        List<SkillsDTOOut> result = skillsService.convertToDtoOut(skills);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Java", result.get(0).getName());
        assertEquals("Java programming", result.get(0).getDescription());
    }
}
