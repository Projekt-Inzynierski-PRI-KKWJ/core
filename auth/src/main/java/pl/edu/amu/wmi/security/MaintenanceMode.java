package pl.edu.amu.wmi.security;

import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class MaintenanceMode
{
    private final AtomicBoolean isInMaintenanceMode = new AtomicBoolean(false);

    public boolean getIsInMaintenanceMode()
    {
        return isInMaintenanceMode.get();
    }

    public void enableMaintenanceMode()
    {
        isInMaintenanceMode.set(true);
    }

    public void disableMaintenanceMode()
    {
        isInMaintenanceMode.set(false);
    }

}
