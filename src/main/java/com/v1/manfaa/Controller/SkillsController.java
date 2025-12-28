package com.v1.manfaa.Controller;


import com.v1.manfaa.Api.ApiResponse;
import com.v1.manfaa.DTO.In.SkillsDTOIn;
import com.v1.manfaa.DTO.Out.SkillsDTOOut;
import com.v1.manfaa.Service.SkillsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/skills")
@RequiredArgsConstructor
public class SkillsController {

    private final SkillsService skillsService;

    @GetMapping("/get")
    public ResponseEntity<List<SkillsDTOOut>>getAllSkills(){
        return ResponseEntity.status(200).body(skillsService.getAllSkills());
    }

    @PostMapping("/add/{companyProfileId}")
    public ResponseEntity<?> addSkills(@PathVariable Integer companyProfileId, @RequestBody @Valid SkillsDTOIn skillsDTOIn){
        skillsService.addSkills(companyProfileId, skillsDTOIn);
        return ResponseEntity.status(200).body(new ApiResponse("Skills added"));
    }

    @PutMapping("/update/{skillsId}/{companyProfileId}")
    public ResponseEntity<?> updateSkills(@PathVariable Integer skillsId, @PathVariable Integer companyProfileId, @RequestBody @Valid SkillsDTOIn skillsDTOIn){
        skillsService.updateSkills(skillsId, companyProfileId, skillsDTOIn);
        return ResponseEntity.status(200).body(new ApiResponse("Skills updated"));
    }

    @DeleteMapping("/delete/{companyProfileId}/{skillsId}")
    public  ResponseEntity<?> deleteSkills(@PathVariable Integer companyProfileId, @PathVariable Integer skillsId){
        skillsService.deleteSkills(companyProfileId, skillsId);
        return ResponseEntity.status(200).body(new ApiResponse("Skills deleted"));
    }
}




