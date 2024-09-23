package vanii.bookingapp.config;

import org.testcontainers.containers.PostgreSQLContainer;

public class CustomPostgreSQLContainer extends PostgreSQLContainer<CustomPostgreSQLContainer> {
    private static final String IMAGE_DB = "postgresql:16";

    private static CustomPostgreSQLContainer postgreSQLContainer;

    private CustomPostgreSQLContainer() {
        super(IMAGE_DB);
    }

    public static synchronized CustomPostgreSQLContainer getInstance() {
        if (postgreSQLContainer == null) {
            postgreSQLContainer = new CustomPostgreSQLContainer();
        }
        return postgreSQLContainer;
    }
    @Override
    public void start() {
        super.start();
        System.setProperty("TEST_DB_URL", postgreSQLContainer.getJdbcUrl());
        System.setProperty("TEST_DB_USERNAME", postgreSQLContainer.getUsername());
        System.setProperty("TEST_DB_PASSWORD", postgreSQLContainer.getPassword());
    }

    @Override
    public void stop() {
        super.stop();
    }
}
