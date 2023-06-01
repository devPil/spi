package io.pilju.spi.adapter;

import io.pilju.spi.entity.CustomUserModel;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.jboss.logging.Logger;
import org.keycloak.common.util.MultivaluedHashMap;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.adapter.AbstractUserAdapterFederatedStorage;

public class CustomUserAdapter extends AbstractUserAdapterFederatedStorage {

    private static final List<String> ATTRIBUTES_LIST = Arrays.asList("birthDate");
    private static final Logger logger = Logger.getLogger(CustomUserAdapter.class);
    protected CustomUserModel customUserModel;
    protected String keycloakId;
    public CustomUserAdapter(KeycloakSession session,
        RealmModel realm,
        ComponentModel model,
        CustomUserModel customUserModel
        ) {
        super(session, realm, model);
        this.customUserModel = customUserModel;
        this.keycloakId = StorageId.keycloakId(model, customUserModel.getUsername());
    }

    @Override
    public String getUsername() {
        logger.info("[CustomUserAdapter] getUsername Call userName :: {0}", new Object[]{customUserModel.getUsername()});
        return customUserModel.getUsername();
    }

    @Override
    public void setUsername(String username) {
        logger.info("[CustomUserAdapter] setUsername :: {0}", new Object[]{username});
        customUserModel.setUsername(username);
    }

    @Override
    public String getFirstName() {
        return customUserModel.getFirstName();
    }

    @Override
    public void setFirstName(String firstName) {
        customUserModel.setFirstName(firstName);
    }

    @Override
    public String getLastName() {
        return customUserModel.getLastName();
    }

    @Override
    public void setLastName(String lastName) {
        customUserModel.setLastName(lastName);
    }

    @Override
    public String getEmail() {
        return customUserModel.getEmail();
    }

    @Override
    public void setEmail(String email) {
        customUserModel.setEmail(email);
    }


    @Override
    public Map<String, List<String>> getAttributes() {
        MultivaluedHashMap<String, String> attributes = new MultivaluedHashMap<>();
        attributes.add(UserModel.USERNAME, customUserModel.getUsername());
        attributes.add(UserModel.EMAIL, customUserModel.getEmail());
        attributes.add(UserModel.FIRST_NAME, customUserModel.getFirstName());
        attributes.add(UserModel.LAST_NAME, customUserModel.getLastName());
        attributes.add("birthDate", customUserModel.getBirthDate().toString());
        return attributes;
    }

    @Override
    public void setSingleAttribute(String name, String value) {
        logger.info("[CustomUserAdapter] setSingleAttribute name :: {0}, value :: {1}", new String[]{name, value});
        if (ATTRIBUTES_LIST.contains(name)) {
            customUserModel.setBirthDate(new Date());
        } else {
            super.setSingleAttribute(name, value);
        }
    }

    @Override
    public void removeAttribute(String name) {
        logger.info("[CustomUserAdapter] removeAttribute name :: {0}", new String[]{name});
        if (ATTRIBUTES_LIST.contains(name)) {
            customUserModel.setBirthDate(null);
        } else {
            super.removeAttribute(name);
        }
    }

    @Override
    public void setAttribute(String name, List<String> values) {
        logger.info("[CustomUserAdapter] setAttribute name :: {0}, values :: {1}", new Object[]{name, values});
        if (ATTRIBUTES_LIST.contains(name)) {
            customUserModel.setBirthDate(new Date());
        } else {
            super.setAttribute(name, values);
        }
    }

    @Override
    public String getFirstAttribute(String name) {
        logger.info("[CustomUserAdapter] getFirstAttribute name :: {0}", new Object[]{name});
        if (ATTRIBUTES_LIST.contains(name)) {
           return customUserModel.getBirthDate().toString();
        } else {
           return super.getFirstAttribute(name);
        }
    }

    @Override
    protected String mapAttribute(String attributeName) {
        logger.info("[CustomUserAdapter] mapAttribute name :: {0}", new Object[]{attributeName});
        if (ATTRIBUTES_LIST.contains(attributeName)) {
            return customUserModel.getBirthDate().toString();
        } else {
            return super.mapAttribute(attributeName);
        }
    }
}
