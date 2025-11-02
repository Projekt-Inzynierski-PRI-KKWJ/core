package pl.edu.amu.wmi.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceContext;
import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Service;
import pl.edu.amu.wmi.model.user.CoordinatorDTO;
import pl.edu.amu.wmi.security.MaintenanceMode;


import javax.sql.DataSource;
import java.sql.Connection;
import java.util.List;

@Service
public class DataFeedReset {

    private final DataSource dataSource;
    private final String changeLogPath;


    private final CoordinatorService coordinatorService;

    private final MaintenanceMode maintenanceMode;

    private final EntityManager entityManager;

    private final EntityManagerFactory entityManagerFactory;

    public DataFeedReset(DataSource dataSource,
                        @Value("${spring.liquibase.change-log}") String changeLogPath,CoordinatorService coordinatorService,EntityManager entityManager, EntityManagerFactory entityManagerFactory, MaintenanceMode maintenanceMode) {
        this.dataSource = dataSource;

        this.changeLogPath = changeLogPath;

        this.coordinatorService=coordinatorService;

        this.maintenanceMode=maintenanceMode;

        this.entityManager = entityManager;

        this.entityManagerFactory=entityManagerFactory;

    }

    public void resetDatabase()
    {
        List<CoordinatorDTO> coordinators = coordinatorService.getAllCoordinators();

        Connection connection = DataSourceUtils.getConnection(dataSource);

        try
        {

            maintenanceMode.enableMaintenanceMode();
            Database database = DatabaseFactory.getInstance()
                    .findCorrectDatabaseImplementation(new liquibase.database.jvm.JdbcConnection(connection));



            String normalizedPath = changeLogPath.replace("classpath:", "");
            Liquibase liquibase = new Liquibase(normalizedPath, new ClassLoaderResourceAccessor(), database);



            liquibase.dropAll();
            liquibase.clearCheckSums();
            liquibase.update(new Contexts(), new LabelExpression());

            entityManager.clear();
            entityManagerFactory.getCache().evictAll();


        }
        catch (LiquibaseException e)
        {
            throw new RuntimeException("Failed to reset DB with Liquibase", e);
        }
        finally
        {
            DataSourceUtils.releaseConnection(connection, dataSource);
            maintenanceMode.disableMaintenanceMode();
        }
        coordinators.forEach(coordinatorService::initializeCoordinator);
    }
}
