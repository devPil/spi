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

public class CustomUserStorageProvider implements UserStorageProvider,
    UserLookupProvider,
    CredentialInputValidator,
    CredentialInputUpdater,
    UserRegistrationProvider,
    UserQueryProvider {

//    private static final Logger logger = Logger.getLogger(CustomUserStorageProvider.class);
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
        try (Connection c = DbUtil.getConnection(this.model)) {
            PreparedStatement st = c.prepareStatement(DatabaseQueryConstants.getUserListByUserName);
            st.setString(1, username);
            st.execute();
            ResultSet rs = st.getResultSet();
            if (rs.next()) {
                return mapUser(realm, rs);
            } else {
                return null;
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Database error:" + ex.getMessage(), ex);
        }
    }


//    protected UserModel createAdapter(RealmModel realm, String username) {
//        System.out.println("========== createAdapter Call");
//        return new AbstractUserAdapterFederatedStorage(session, realm, model) {
//            @Override
//            public String getUsername() {
//                System.out.println(session);
//                System.out.println("getUsername");
//                return username;
//            }
//
//            @Override
//            public void setUsername(String username) {
//                System.out.println("setUsername :: " + username);
////                String pw = (String)properties.remove(username);
////                if (pw != null) {
////                    properties.put(username, pw);
////                    save();
////                }
//            }
//        };
//    }

    @Override
    public UserModel getUserById(RealmModel realm, String id) {
        System.out.println("========== getUserById Call id :: " + id + "==========");

        StorageId storageId = new StorageId(id);
        String username = storageId.getExternalId();
        return getUserByUsername(realm, username);
    }

    @Override
    public UserModel getUserByEmail(RealmModel realm, String email) {
        System.out.println("========== getUserByEmail Call email :: " + email);

        try (Connection c = DbUtil.getConnection(this.model)) {
            String query = DatabaseQueryConstants.getUserListByEmail;
            PreparedStatement st = c.prepareStatement(query);
            st.setString(1, email);
            st.execute();
            ResultSet rs = st.getResultSet();
            while (rs.next()) {
                return mapUser(realm, rs);
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Database error:" + ex.getMessage(), ex);
        }

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
    public Stream<UserModel> searchForUserStream(RealmModel realm, String search,
        Integer firstResult, Integer maxResults) {
        System.out.println("========== searchForUserStream Call - 1");
        List<UserModel> users = new LinkedList<>();
        System.out.println("search :: " + search);

        try (Connection c = DbUtil.getConnection(this.model)) {
            String query = DatabaseQueryConstants.getUserListByUserName;
            if (search.equals("*")) {
                query = DatabaseQueryConstants.getUserList;
            }
            PreparedStatement st = c.prepareStatement(query);
            st.setString(1, search);
            st.execute();
            ResultSet rs = st.getResultSet();
            while (rs.next()) {
                users.add(mapUser(realm, rs));
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Database error:" + ex.getMessage(), ex);
        }

        return users.stream();
    }

    @Override
    public Stream<UserModel> searchForUserStream(RealmModel realm, Map<String, String> params,
        Integer firstResult,
        Integer maxResults) {
        System.out.println("========== searchForUserStream Call - 2");
        // only support searching by username
        String usernameSearchString = params.get("username");
        if (usernameSearchString == null) {
            return Stream.empty();
        }
        return searchForUserStream(realm, usernameSearchString, firstResult, maxResults);
    }

    @Override
    public Stream<UserModel> getGroupMembersStream(RealmModel realm, GroupModel group,
        Integer firstResult, Integer maxResults) {
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
    public Stream<UserModel> searchForUserByUserAttributeStream(RealmModel realm, String attrName,
        String attrValue) {
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

    // 키클락디비에 추가됨.
    @Override
    public UserModel addUser(RealmModel realm, String username) {
        return null;
//        return createAdapter(realm, username);
    }

    // 회원삭제 및 탈퇴
    @Override
    public boolean removeUser(RealmModel realm, UserModel user) {
        System.out.println("========== removeUser Call");

        String userName = user.getUsername();
        try (Connection c = DbUtil.getConnection(this.model)) {
            // Select
            PreparedStatement st = c.prepareStatement(DatabaseQueryConstants.getUserListByUserName);
            st.setString(1, userName);
            st.execute();
            ResultSet rs = st.getResultSet();
            if (!rs.next()) {
                return false;
            }
            // 삭제
            PreparedStatement strm = c.prepareStatement(DatabaseQueryConstants.getUserListByUserName);
            strm.setString(1, rs.getString("username"));
            return strm.execute();
        } catch (SQLException ex) {
            throw new RuntimeException("Database error:" + ex.getMessage(), ex);
        }
    }

    // CredentialInputValidator methods

    @Override
    public boolean isConfiguredFor(RealmModel realm, UserModel user, String credentialType) {
        System.out.println("========== isConfiguredFor Call");
        return supportsCredentialType(credentialType);
    }

    @Override
    public boolean supportsCredentialType(String credentialType) {
        System.out.println("========== supportsCredentialType Call" + credentialType);
        return PasswordCredentialModel.TYPE.endsWith(credentialType);
    }

    // 인증확인
    @Override
    public boolean isValid(RealmModel realm, UserModel user, CredentialInput credentialInput) {
        System.out.println("====== isValid ======");
        if (!this.supportsCredentialType(credentialInput.getType())) {
            return false;
        }

        StorageId sid = new StorageId(user.getId());
        String username = sid.getExternalId();

        try (Connection c = DbUtil.getConnection(this.model)) {
            PreparedStatement st = c.prepareStatement(DatabaseQueryConstants.getPasswordByUserName);
            st.setString(1, username);
            st.execute();
            ResultSet rs = st.getResultSet();
            if (rs.next()) {
                String pwd = rs.getString(1);
                return pwd.equals(credentialInput.getChallengeResponse());
            } else {
                return false;
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Database error:" + ex.getMessage(), ex);
        }

    }

    // CredentialInputUpdater methods

    @Override
    public boolean updateCredential(RealmModel realm, UserModel user, CredentialInput input) {
        System.out.println("========== updateCredential Call :: {}");
        System.out.println(user);
        System.out.println(input);
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
        System.out.println("========== disableCredentialType Call :: {}");
        System.out.println(user);
        System.out.println(credentialType);
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
        System.out.println("========== disableCredentialType Call :: {}");
        System.out.println(user);
        return disableableTypes.stream();
    }

    @Override
    public void close() {
        System.out.println("========== close Call :: {}");
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