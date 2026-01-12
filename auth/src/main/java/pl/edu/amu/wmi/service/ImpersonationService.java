package pl.edu.amu.wmi.service;


import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import pl.edu.amu.wmi.dao.UserDataDAO;
import pl.edu.amu.wmi.entity.UserData;
import pl.edu.amu.wmi.security.JwtUtils;
import pl.edu.amu.wmi.security.UserDetailsImpl;


@Service
public class ImpersonationService {

    private final UserDataDAO userDataDAO;
    private final JwtUtils jwtUtils;
    private final ProjectMemberService projectMemberService;

    public ImpersonationService(
            UserDataDAO userDataDAO,
            JwtUtils jwtUtils,
            ProjectMemberService projectMemberService
    ) {
        this.userDataDAO = userDataDAO;
        this.jwtUtils = jwtUtils;
        this.projectMemberService = projectMemberService;
    }

    public ResponseCookie impersonate(String targetIndex, UserDetailsImpl currentUser) {


        String originalIndex = currentUser.getOriginalUsername() != null
                ? currentUser.getOriginalUsername()
                : currentUser.getUsername();


        if (!projectMemberService.isUserRoleCoordinator(originalIndex)) {
            throw new SecurityException("Only coordinator can impersonate users");
        }

        UserData targetUser = userDataDAO.findByIndexNumber(targetIndex)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + targetIndex));

        return jwtUtils.generateImpersonationJwtCookie(
                targetUser.getIndexNumber(),
                originalIndex
        );
    }

    public ResponseCookie exitImpersonation(UserDetailsImpl currentUser) {

        String originalIndex = currentUser.getOriginalUsername();
        if (originalIndex == null) {
            throw new IllegalStateException("Not in impersonation mode");
        }

        UserData originalUser = userDataDAO.findByIndexNumber(originalIndex)
                .orElseThrow(() -> new IllegalStateException("Original user not found"));

        return jwtUtils.generateJwtCookie(originalUser);
    }




}


