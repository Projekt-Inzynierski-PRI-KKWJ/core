package pl.edu.amu.wmi.controller;


import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import pl.edu.amu.wmi.security.UserDetailsImpl;
import pl.edu.amu.wmi.service.ImpersonationService;



@RestController
@RequestMapping("/auth/impersonation")
public class ImpersonationController {

    private final ImpersonationService impersonationService;

    public ImpersonationController(ImpersonationService impersonationService) {
        this.impersonationService = impersonationService;
    }

    @PostMapping("/{indexNumber}")
    public ResponseEntity<?> impersonate(@PathVariable String indexNumber) {

        UserDetailsImpl currentUser =
                (UserDetailsImpl) SecurityContextHolder.getContext()
                        .getAuthentication()
                        .getPrincipal();

        ResponseCookie cookie =
                impersonationService.impersonate(indexNumber, currentUser);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
    }

    @PostMapping("/exit")
    public ResponseEntity<?> exit() {

        UserDetailsImpl currentUser =
                (UserDetailsImpl) SecurityContextHolder.getContext()
                        .getAuthentication()
                        .getPrincipal();

        ResponseCookie cookie =
                impersonationService.exitImpersonation(currentUser);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
    }
}
