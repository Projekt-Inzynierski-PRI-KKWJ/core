package pl.edu.amu.projectmarket.web.mapper;

import java.util.List;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import pl.edu.amu.wmi.entity.ProjectMarket;
import pl.edu.amu.wmi.entity.UserData;
import pl.edu.amu.projectmarket.web.model.ProjectMemberDTO;
import pl.edu.amu.projectmarket.web.model.ProjectMembersDTO;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class ProjectMemberMapper {

    @Mapping(target = "availableSlots", expression = "java(getAvailableSlots(projectMarket))")
    @Mapping(target = "totalSlots", source = "projectMarket.maxMembers")
    @Mapping(target = "members", expression = "java(getMembers(projectMarket))")
    public abstract ProjectMembersDTO fromProjectMarket(ProjectMarket projectMarket);

    @Mapping(target = "firstName", source = "student.userData.firstName")
    @Mapping(target = "lastName", source = "student.userData.lastName")
    @Mapping(target = "indexNumber", source = "student.indexNumber")
    @Mapping(target = "isAdmin", source = "projectAdmin")
    protected abstract ProjectMemberDTO fromStudentProject(pl.edu.amu.wmi.entity.StudentProject studentProject);

    protected List<ProjectMemberDTO> getMembers(ProjectMarket projectMarket) {
        var project = projectMarket.getProject();
        if (project == null || project.getAssignedStudents() == null) {
            return List.of();
        }
        return project.getAssignedStudents().stream()
            .map(this::fromStudentProject)
            .toList();
    }

    protected static Integer getAvailableSlots(ProjectMarket projectMarket) {
        return projectMarket.getMaxMembers() - projectMarket.getCurrentMembersCount();
    }
}
