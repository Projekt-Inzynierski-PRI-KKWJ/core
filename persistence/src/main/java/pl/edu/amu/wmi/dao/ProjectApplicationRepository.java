package pl.edu.amu.wmi.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.edu.amu.wmi.entity.ProjectApplication;

public interface ProjectApplicationRepository extends JpaRepository<ProjectApplication, Long> {
}
