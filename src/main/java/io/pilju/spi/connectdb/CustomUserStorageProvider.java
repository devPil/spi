package io.pilju.spi.connectdb;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.jboss.logging.Logger;
import org.keycloak.component.ComponentModel;
import org.keycloak.credential.CredentialInput;
import org.keycloak.credential.CredentialInputUpdater;
import org.keycloak.credential.CredentialInputValidator;
import org.keycloak.models.GroupModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.UserStorageProvider;
import org.keycloak.storage.user.UserLookupProvider;
import org.keycloak.storage.user.UserQueryProvider;
import org.keycloak.storage.user.UserRegistrationProvider;

public class CustomUserStorageProvider implements UserStorageProvider,
    UserLookupProvider,
    CredentialInputValidator,
    CredentialInputUpdater,
    UserRegistrationProvider,
    UserQueryProvider {

    private static final Logger logger = Logger.getLogger(CustomUserStorageProvider.class);
    protected KeycloakSession session;
    protected ComponentModel model;
    protected CustomUserMapper mapper;


    public CustomUserStorageProvider(KeycloakSession session, ComponentModel model) {
        this.session = session;
        this.model = model;
        this.mapper = new CustomUserMapper(model);
    }

    @Override
    public boolean updateCredential(RealmModel realm, UserModel user, CredentialInput input) {
        logger.info("updateCredential user :: {0}, input :: {1}", new Object[]{user, input});
        return false;
    }

    @Override
    public void disableCredentialType(RealmModel realm, UserModel user, String credentialType) {
        logger.info("disableCredentialType user :: {0}, credentialType :: {1}", new Object[]{user, credentialType});
    }

    @Override
    public Stream<String> getDisableableCredentialTypesStream(RealmModel realm, UserModel user) {
        logger.info("getDisableableCredentialTypesStream userId :: {0}, userEmail :: {1}, userName :: {2}",
            new Object[]{user.getId(), user.getEmail(), user.getUsername()}
        );
        return Stream.empty();
    }

    @Override
    public boolean supportsCredentialType(String credentialType) {
        logger.info("supportsCredentialType credentialType :: {0}", new Object[]{credentialType});
        return true;
    }

    @Override
    public boolean isConfiguredFor(RealmModel realm, UserModel user, String credentialType) {
        logger.info("isConfiguredFor user :: {0}, credentialType :: {1}", new Object[]{user, credentialType});
        return supportsCredentialType(credentialType);
    }

    @Override
    public boolean isValid(RealmModel realm, UserModel user, CredentialInput credentialInput) {
        logger.info("isValid :: {0}", new Object[]{user});
        return true;
    }

    @Override
    public void close() {

    }

    @Override
    public UserModel getUserById(RealmModel realm, String id) {
        logger.info("getUserById id :: {0}", new String[]{id});
        String persistenceId = StorageId.externalId(id);
        try {
            List<CustomUserModel> customUserModels = mapper.getUsers(persistenceId);
            if (customUserModels.isEmpty()) {
                return null;
            }
            return new CustomUserAdapter(session, realm, model, customUserModels.get(0));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public UserModel getUserByUsername(RealmModel realm, String username) {
        logger.info("getUserByUsername username :: {0}", new String[]{username});
        try {
            List<CustomUserModel> customUserModels = mapper.getUsers(username);
            if (customUserModels.isEmpty()) {
                return null;
            }
            return new CustomUserAdapter(session, realm, model, customUserModels.get(0));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public UserModel getUserByEmail(RealmModel realm, String email) {
        logger.info("getUserByEmail email :: {0}", new String[]{email});
        return null;
    }

    @Override
    public int getUsersCount(RealmModel realm) {
        logger.info("getUsersCount Call");
        try {
            List<CustomUserModel> customUserModels = mapper.getUsers();
            int result = customUserModels.size();
            logger.info("getUsersCount Call", new Integer[]{result});
            return result;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Stream<UserModel> searchForUserStream(RealmModel realm, String search, Integer firstResult, Integer maxResults) {
        logger.info("searchForUserStream search :: {0}, firstResult :: {1}, maxResults :: {2}"
            , new Object[]{search, firstResult, maxResults}
        );
        List<UserModel> userModels = new ArrayList<>();
        try {
            List<CustomUserModel> customUserModels = mapper.getUsers();
            if (!"*".equals(search)) {
                customUserModels = mapper.getUsers(search);
            }
            logger.info("searchForUserStream customUserModels :: {0}", new Object[]{customUserModels});
            return customUserModels.stream().map(v -> new CustomUserAdapter(session, realm, model, v));
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public Stream<UserModel> searchForUserStream(RealmModel realm, Map<String, String> params, Integer firstResult, Integer maxResults) {
        logger.info("searchForUserStream map :: {0}, firstResult :: {1}, maxResults :: {2}", new Object[]{params, firstResult, maxResults});
        String username = params.get("username");
        if (username == null || username.equals("")) return Stream.empty();
        return searchForUserStream(realm, username, firstResult, maxResults);
    }

    @Override
    public Stream<UserModel> getGroupMembersStream(RealmModel realm, GroupModel group, Integer firstResult, Integer maxResults) {
        logger.info("getGroupMembersStream group :: {0}, firstResult :: {1}, maxResults :: {2}", new Object[]{group, firstResult, maxResults});
        return Stream.empty();
    }

    @Override
    public Stream<UserModel> getGroupMembersStream(RealmModel realm, GroupModel group) {
        return getGroupMembersStream(realm, group, null, null);
    }

    @Override
    public Stream<UserModel> searchForUserByUserAttributeStream(RealmModel realm, String attrName,
        String attrValue) {
        return null;
    }

    @Override
    public UserModel addUser(RealmModel realm, String username) {
        logger.info("addUser username :: {0}", new Object[]{username});
        return null;
    }

    @Override
    public boolean removeUser(RealmModel realm, UserModel user) {
        logger.info("removeUser user :: {0}", new Object[]{user});
        return false;
    }

}