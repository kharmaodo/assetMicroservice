package sn.free.selfcare.service.dto;

import sn.free.selfcare.config.Constants;
import sn.free.selfcare.domain.enumeration.TypeAuthentification;

import javax.validation.constraints.*;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
//TODO : MD : Audit : Exploiter Lombok pour dit clean code
public class AdminClientDTO {

    private Long id;

    @NotBlank
    @Pattern(regexp = Constants.LOGIN_REGEX)
    @Size(min = 1, max = 50)
    private String login;

    @NotBlank
    @Size(max = 50)
    private String firstName;

    @NotBlank
    @Size(max = 50)
    private String lastName;

    @NotNull
    @Size(min = 5, max = 10)
    private String telephone;

    @Email
    @NotNull
    @Size(min = 5, max = 254)
    private String email;

    @NotNull
    private boolean activated = false;

    private Long clientId;

    private TypeAuthentification typeAuthentification;

    @Size(min = 1)
    private Set<String> authorities;

    public AdminClientDTO() {
        // Empty constructor needed for Jackson.
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<String> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Set<String> authorities) {
        this.authorities = authorities;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public TypeAuthentification getTypeAuthentification() {
        return typeAuthentification;
    }

    public void setTypeAuthentification(TypeAuthentification typeAuthentification) {
        this.typeAuthentification = typeAuthentification;
    }

    @Override
    public String toString() {
        final int maxLen = 5;
        return "UserDTO [id=" + id + ", login=" + login + ", firstName=" + firstName + ", lastName=" + lastName
            + ", telephone=" + telephone + ", email=" + email + ", activated="
            + activated + ", clientId=" + clientId + ", typeAuthentification=" + typeAuthentification + ", authorities=" + (authorities != null ? toString(authorities, maxLen) : null) + "]";
    }

    private String toString(Collection<?> collection, int maxLen) {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        int i = 0;
        for (Iterator<?> iterator = collection.iterator(); iterator.hasNext() && i < maxLen; i++) {
            if (i > 0) builder.append(", ");
            builder.append(iterator.next());
        }
        builder.append("]");
        return builder.toString();
    }
}
