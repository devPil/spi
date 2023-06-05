package io.pilju.spi.provider;


import static io.pilju.spi.constants.DatabaseConstants.*;

import io.pilju.spi.utils.DbUtil;
import java.sql.Connection;
import java.util.List;
import java.util.Properties;
import org.jboss.logging.Logger;
import org.keycloak.Config;
import org.keycloak.component.ComponentModel;
import org.keycloak.component.ComponentValidationException;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.provider.ProviderConfigurationBuilder;
import org.keycloak.storage.UserStorageProviderFactory;

public class CustomUserStorageProviderFactory implements UserStorageProviderFactory<CustomUserStorageProvider> {

    private static final String USER_STORAGE_NAME = "CUSTOM_USER_PROVIDER";
    private static final Logger logger = Logger.getLogger(CustomUserStorageProviderFactory.class);
    protected static final List<ProviderConfigProperty> configMetadata;
    protected Properties properties = new Properties();

    static {
        // Create config metadata
        configMetadata = ProviderConfigurationBuilder.create()
            .property().name(CONFIG_KEY_JDBC_DRIVER)
                .label("JDBC Driver Class")
                .type(ProviderConfigProperty.STRING_TYPE)
                .defaultValue("org.h2.Driver")
                .helpText("Fully qualified class name of the JDBC driver")
            .add()
            .property().name(CONFIG_KEY_JDBC_URL)
                .label("JDBC URL")
                .type(ProviderConfigProperty.STRING_TYPE)
                .defaultValue(CONFIG_KEY_JDBC_URL)
                .helpText("JDBC URL used to connect to the user database")
            .add()
            .property().name(CONFIG_KEY_DB_USERNAME)
                .label("Database User")
                .type(ProviderConfigProperty.STRING_TYPE)
                .defaultValue("root")
                .helpText("Username used to connect to the database")
            .add()
            .property().name(CONFIG_KEY_DB_PASSWORD)
                .label("Database Password")
                .type(ProviderConfigProperty.STRING_TYPE)
                .defaultValue(CONFIG_KEY_DB_PASSWORD)
                .helpText("Password used to connect to the database")
                .secret(true)
            .add()
            .property().name(CONFIG_KEY_VALIDATION_QUERY)
                .label("SQL Validation Query")
                .type(ProviderConfigProperty.STRING_TYPE)
                .helpText("SQL query used to validate a connection")
                .defaultValue("select 1")
            .add()
            .build();
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return configMetadata;
    }

    @Override
    public void validateConfiguration(KeycloakSession session, RealmModel realm, ComponentModel config) throws ComponentValidationException {
        try (Connection c = DbUtil.getConnection(config)) {
            logger.info("[I84] Testing connection...");

            c.createStatement().execute(config.get(CONFIG_KEY_VALIDATION_QUERY));

            logger.info("[I92] Connection OK !" );
        }
        catch(Exception ex) {
            logger.info("[W94] Unable to validate connection: ex="+ ex.getMessage());
            throw new ComponentValidationException("Unable to validate database connection",ex);
        }
    }

    @Override
    public String getId() {
        return USER_STORAGE_NAME;
    }

    @Override
    public CustomUserStorageProvider create(KeycloakSession session, ComponentModel model) {
        logger.info("CustomUserStorageProviderFactory create");
        return new CustomUserStorageProvider(session, model);
    }

    @Override
    public void init(Config.Scope config) {
        logger.info("CustomUserStorageProviderFactory init PROVIDER_NAME :: {0}", new String[]{USER_STORAGE_NAME});
    }
}
