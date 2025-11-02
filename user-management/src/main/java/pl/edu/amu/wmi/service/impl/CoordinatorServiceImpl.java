package pl.edu.amu.wmi.service.impl;

import org.springframework.stereotype.Service;
import pl.edu.amu.wmi.dao.*;
import pl.edu.amu.wmi.entity.Role;
import pl.edu.amu.wmi.entity.UserData;
import pl.edu.amu.wmi.enumerations.UserRole;
import pl.edu.amu.wmi.model.user.CoordinatorDTO;
import pl.edu.amu.wmi.service.CoordinatorService;


import java.util.List;
@Service
public class CoordinatorServiceImpl implements CoordinatorService {



    private final UserDataDAO userDataRepository;
    private final RoleDAO roleRepository;


    public CoordinatorServiceImpl(UserDataDAO userDataRepository,RoleDAO roleRepository) {
        this.userDataRepository = userDataRepository;
        this.roleRepository = roleRepository;
    }




    @Override
    public CoordinatorDTO initializeCoordinator(CoordinatorDTO coordinatorDTO) {

        UserData user = new UserData();
        user.setFirstName(coordinatorDTO.getName());
        user.setLastName(coordinatorDTO.getLast_name());
        user.setEmail(coordinatorDTO.getEmail());
        user.setIndexNumber(coordinatorDTO.getIndexNumber());

        Role coordinatorRole = roleRepository.findByName(UserRole.COORDINATOR);
        if (coordinatorRole == null) {
            throw new IllegalStateException("Coordinator role not found in database.");
        }
        user.getRoles().add(coordinatorRole);

        userDataRepository.save(user);

        return coordinatorDTO;
    }

    //Fetches all coordinators form database
    @Override
    public List<CoordinatorDTO> getAllCoordinators() {
        Role coordinatorRole = roleRepository.findByName(UserRole.COORDINATOR);
        if (coordinatorRole == null) {
            throw new IllegalStateException("Coordinator role not found in database.");
        }

        List<UserData> coordinators = userDataRepository.findAll().stream()
                .filter(user -> user.getRoles().contains(coordinatorRole))
                .toList();

        return coordinators.stream().map(user -> {
            CoordinatorDTO dto = new CoordinatorDTO();
            dto.setIndexNumber(user.getIndexNumber());
            dto.setName(user.getFirstName());
            dto.setLast_name(user.getLastName());
            dto.setEmail(user.getEmail());
            return dto;
        }).toList();
    }

}

