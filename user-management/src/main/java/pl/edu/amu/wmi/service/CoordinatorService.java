package pl.edu.amu.wmi.service;

import pl.edu.amu.wmi.model.user.CoordinatorDTO;

import java.util.List;

public interface CoordinatorService {
    CoordinatorDTO initializeCoordinator(CoordinatorDTO coordinatorDTO);


    List<CoordinatorDTO> getAllCoordinators();
}
