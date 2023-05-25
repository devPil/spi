package io.pilju.spi.connectdb;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import org.keycloak.component.ComponentModel;
import org.keycloak.credential.CredentialInput;
import org.keycloak.credential.CredentialInputUpdater;
import org.keycloak.credential.CredentialInputValidator;
import org.keycloak.models.GroupModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.credential.PasswordCredentialModel;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.UserStorageProvider;
import org.keycloak.storage.adapter.AbstractUserAdapterFederatedStorage;
import org.keycloak.storage.user.UserLookupProvider;
import org.keycloak.storage.user.UserQueryProvider;
import org.keycloak.storage.user.UserRegistrationProvider;

public class CustomUserStorageProvider implements
        UserStorageProvider,
        UserLookupProvider,
        CredentialInputValidator,
        CredentialInputUpdater,
        UserRegistrationProvider,
        UserQueryProvider {
    protected KeycloakSession session;
    protected ComponentModel model;


    public CustomUserStorageProvider(KeycloakSession session, ComponentModel model) {
        this.session = session;
        this.model = model;
    }

    // UserLookupProvider methods

    @Override
    public UserModel getUserByUsername(RealmModel realm, String username) {
        System.out.println("========== getUserByUsername Call");
        try ( Connection c = DbUtil.getConnection(this.model)) {
            PreparedStatement st = c.prepareStatement("select username, firstName,lastName, email, birthDate from users where username = ?");
            st.setString(1, username);
            st.execute();
            ResultSet rs = st.getResultSet();
            if ( rs.next()) {
                return mapUser(realm,rs);
            }
            else {
                return null;
            }
        }
        catch(SQLException ex) {
            throw new RuntimeException("Database error:" + ex.getMessage(),ex);
        }
    }
    

    protected UserModel createAdapter(RealmModel realm, String username) {
        System.out.println("createAdapter");
        return new AbstractUserAdapterFederatedStorage(session, realm, model) {
            @Override
            public String getUsername() {
                return username;
            }

            @Override
            public void setUsername(String username) {
//                String pw = (String)properties.remove(username);
//                if (pw != null) {
//                    properties.put(username, pw);
//                    save();
//                }
            }
        };
    }

    @Override
    public UserModel getUserById(RealmModel realm, String id) {
        System.out.println("========== getUserById Call");
        StorageId storageId = new StorageId(id);
        String username = storageId.getExternalId();
        return getUserByUsername(realm, username);
    }

    @Override
    public UserModel getUserByEmail(RealmModel realm, String email) {
        return null;
    }

    // UserQueryProvider methods

    @Override
    public int getUsersCount(RealmModel realm) {
        System.out.println("========== getUsersCount Call");
//        return properties.size();
        return 1;
    }

    // UserQueryProvider method implementations

    @Override
    public Stream<UserModel> searchForUserStream(RealmModel realm, String search, Integer firstResult, Integer maxResults) {
        System.out.println("========== searchForUserStream Call - 1");
        List<UserModel> users = new LinkedList<>();
        System.out.println("search :: " + search);
        System.out.println("getAttributes :: " + realm.getAttributes());

        try ( Connection c = DbUtil.getConnection(this.model)) {
            PreparedStatement st = c.prepareStatement(DatabaseQueryConstants.getUserList);
            st.setString(1, search);
            st.execute();
            ResultSet rs = st.getResultSet();
            if (rs.next()) {
                users.add(mapUser(realm,rs));
            }
        }
        catch(SQLException ex) {
            throw new RuntimeException("Database error:" + ex.getMessage(),ex);
        }

        return users.stream();
    }

    @Override
    public Stream<UserModel> searchForUserStream(RealmModel realm, Map<String, String> params, Integer firstResult,
            Integer maxResults) {
        System.out.println("========== searchForUserStream Call - 2");
        // only support searching by username
        String usernameSearchString = params.get("username");
        if (usernameSearchString == null) return Stream.empty();
        return searchForUserStream(realm, usernameSearchString, firstResult, maxResults);
    }

    @Override
    public Stream<UserModel> getGroupMembersStream(RealmModel realm, GroupModel group, Integer firstResult, Integer maxResults) {
        System.out.println("========== getGroupMembersStream Call - 1");
        // runtime automatically handles querying UserFederatedStorage
        return Stream.empty();
    }

    @Override
    public Stream<UserModel> getGroupMembersStream(RealmModel realm, GroupModel group) {
        System.out.println("========== getGroupMembersStream Call - 2");
        // runtime automatically handles querying UserFederatedStorage
        return Stream.empty();
    }

    @Override
    public Stream<UserModel> searchForUserByUserAttributeStream(RealmModel realm, String attrName, String attrValue) {
        System.out.println("========== searchForUserByUserAttributeStream Call - 1");
        // runtime automatically handles querying UserFederatedStorage
        return Stream.empty();
    }


    // UserRegistrationProvider method implementations

    public void save() {
//        String path = model.getConfig().getFirst("path");
//        path = EnvUtil.replace(path);
//        try {
//            FileOutputStream fos = new FileOutputStream(path);
//            properties.store(fos, "");
//            fos.close();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
    }

    @Override
    public UserModel addUser(RealmModel realm, String username) {
        System.out.println("========== addUser Call");
        System.out.println(realm.getAttributes());
        System.out.println(username);
        System.out.println("========== addUser End");
        return createAdapter(realm, username);
    }

    @Override
    public boolean removeUser(RealmModel realm, UserModel user) {
        System.out.println("========== removeUser Call");
//        synchronized (properties) {
//            if (properties.remove(user.getUsername()) == null) return false;
//            save();
//            return true;
//        }
        return false;
    }





    // CredentialInputValidator methods

    @Override
    public boolean isConfiguredFor(RealmModel realm, UserModel user, String credentialType) {
//        String password = properties.getProperty(user.getUsername());
        String password = "";
        return credentialType.equals(PasswordCredentialModel.TYPE) && password != null;
    }

    @Override
    public boolean supportsCredentialType(String credentialType) {
        return credentialType.equals(PasswordCredentialModel.TYPE);
    }

    // 인증확인
    @Override
    public boolean isValid(RealmModel realm, UserModel user, CredentialInput input) {
//        if (!supportsCredentialType(input.getType()) || !(input instanceof UserCredentialModel)) return false;
//
//        UserCredentialModel cred = (UserCredentialModel)input;
//        String password = properties.getProperty(user.getUsername());
//        if (password == null || UNSET_PASSWORD.equals(password)) return false;
//        return password.equals(cred.getValue());
        StorageId sid = new StorageId(user.getId());
        String username = sid.getExternalId();

        System.out.println("====== isValid ======");
        System.out.println(input.getCredentialId());
        System.out.println(input.getType());
        System.out.println(input.getChallengeResponse());
        try ( Connection c = DbUtil.getConnection(this.model)) {
            PreparedStatement st = c.prepareStatement("select password from users where username = ?");
            st.setString(1, username);
            st.execute();
            ResultSet rs = st.getResultSet();
            if ( rs.next()) {
                String pwd = rs.getString(1);
//                return pwd.equals(credentialInput.getChallengeResponse());
                return false;
            }
            else {
                return false;
            }
        }
        catch(SQLException ex) {
            throw new RuntimeException("Database error:" + ex.getMessage(),ex);
        }

    }

    // CredentialInputUpdater methods

    @Override
    public boolean updateCredential(RealmModel realm, UserModel user, CredentialInput input) {
//        if (!(input instanceof UserCredentialModel)) return false;
//        if (!input.getType().equals(PasswordCredentialModel.TYPE)) return false;
//        UserCredentialModel cred = (UserCredentialModel)input;
//        synchronized (properties) {
//            properties.setProperty(user.getUsername(), cred.getValue());
//            save();
//        }
        return true;
    }

    @Override
    public void disableCredentialType(RealmModel realm, UserModel user, String credentialType) {
//        if (!credentialType.equals(PasswordCredentialModel.TYPE)) return;
//        synchronized (properties) {
//            properties.setProperty(user.getUsername(), UNSET_PASSWORD);
//            save();
//        }
    }

    private static final Set<String> disableableTypes = new HashSet<>();

    static {
        disableableTypes.add(PasswordCredentialModel.TYPE);
    }

    @Override
    public Stream<String> getDisableableCredentialTypesStream(RealmModel realm, UserModel user) {
        return disableableTypes.stream();
    }

    @Override
    public void close() {

    }

    private UserModel mapUser(RealmModel realm, ResultSet rs) throws SQLException {
        System.out.println("========== mapUser ==========");
        CustomUser user = new CustomUser.Builder(session, realm, model, rs.getString("username"))
            .email(rs.getString("email"))
            .firstName(rs.getString("firstName"))
            .lastName(rs.getString("lastName"))
            .birthDate(rs.getDate("birthDate"))
            .build();
        System.out.println(user);

        return user;
    }
}