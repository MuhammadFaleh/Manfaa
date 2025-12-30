package com.v1.manfaa.Controller;


import com.v1.manfaa.Api.ApiResponse;
import com.v1.manfaa.DTO.In.SkillsDTOIn;
import com.v1.manfaa.DTO.Out.SkillsDTOOut;
import com.v1.manfaa.DTO.Out.UserDTOOut;
import com.v1.manfaa.Model.Skills;
import com.v1.manfaa.Model.User;
import com.v1.manfaa.Service.SkillsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/skills")
@RequiredArgsConstructor
public class SkillsController {

    private final SkillsService skillsService;

    @GetMapping("/get")
    public ResponseEntity<?>getAllSkills(){
        return ResponseEntity.status(200).body(skillsService.getAllSkills());
    }

    @PostMapping("/add")
    public ResponseEntity<?> addSkills(@RequestBody @Valid SkillsDTOIn skillsDTOIn, @AuthenticationPrincipal User user){
        skillsService.addSkills(skillsDTOIn);
        return ResponseEntity.status(200).body(new ApiResponse("Skills added"));
    }

    @PutMapping("/update/{skillsId}")
    public ResponseEntity<?> updateSkills(@PathVariable Integer skillsId, @RequestBody @Valid SkillsDTOIn skillsDTOIn, @AuthenticationPrincipal User user){
        skillsService.updateSkills(skillsId, skillsDTOIn);
        return ResponseEntity.status(200).body(new ApiResponse("Skills updated"));
    }

    @DeleteMapping("/delete/{skillsId}")
    public  ResponseEntity<?> deleteSkills(@PathVariable Integer skillsId, @AuthenticationPrincipal User user){
        skillsService.deleteSkills(skillsId);
        return ResponseEntity.status(200).body(new ApiResponse("Skills deleted"));
    }

    @PostMapping("/assign-skill/{skillId}")
    public ResponseEntity<?> assignSkill(@PathVariable Integer skillId, @AuthenticationPrincipal User user){
        skillsService.assignSkill(user.getId(),skillId);
        return ResponseEntity.status(200).body(new ApiResponse("skill added successfully"));
    }

    @PostMapping("/remove-skill/{skillId}")
    public ResponseEntity<?> removeSkill(@PathVariable Integer skillId, @AuthenticationPrincipal User user){
        skillsService.removeSkill(user.getId(),skillId);
        return ResponseEntity.status(200).body(new ApiResponse("skill removed successfully"));
    }


    @GetMapping("/get-skills")
    public ResponseEntity<List<SkillsDTOOut>> getSkillsByCompany(@AuthenticationPrincipal User user) {
        return ResponseEntity.status(200).body(skillsService.getSkillsByCompany(user.getId()));
    }


    @GetMapping("/search/{keyword}")
    public ResponseEntity<List<SkillsDTOOut>> searchSkillsByKeyword(@PathVariable String keyword, @AuthenticationPrincipal User user) {
        return ResponseEntity.status(200).body(skillsService.searchSkillsByKeyword(keyword));
    }
}





