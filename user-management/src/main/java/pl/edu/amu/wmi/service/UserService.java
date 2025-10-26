package pl.edu.amu.wmi.service;

import pl.edu.amu.wmi.model.user.CoordinatorDTO;
import pl.edu.amu.wmi.model.user.UserDTO;

public interface UserService {

    UserDTO getUser(String indexNumber, String studyYear);

    CoordinatorDTO initializeCoordinator(CoordinatorDTO coordinatorDTO);

}
