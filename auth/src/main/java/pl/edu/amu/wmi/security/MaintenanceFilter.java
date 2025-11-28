package pl.edu.amu.wmi.security;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.io.IOException;
import jakarta.servlet.*;



@Component
@Slf4j
public class MaintenanceFilter implements Filter
{


    private MaintenanceMode maintenanceMode;

    public MaintenanceFilter(MaintenanceMode maintenanceMode)
    {
        this.maintenanceMode=maintenanceMode;
    }


    // To block operations on database when it's being reset or something
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        if (maintenanceMode.getIsInMaintenanceMode()) {
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            httpResponse.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            httpResponse.getWriter().write("System is in maintenance, please try again in a moment.");
            log.info("System is in maintenance, please try again in a moment. Database is probably being reset");
            return;
        }


        chain.doFilter(request, response);
    }



}





