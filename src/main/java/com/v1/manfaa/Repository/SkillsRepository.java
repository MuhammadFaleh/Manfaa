package com.v1.manfaa.Repository;

import com.v1.manfaa.Model.Skills;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SkillsRepository extends JpaRepository<Skills , Integer> {
    Skills findSkillsById(Integer id);
}
