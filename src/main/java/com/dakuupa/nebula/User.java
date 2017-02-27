package com.dakuupa.nebula;

/**
 *
 * @author ETWilliams
 */
public class User {

    private String username;
    private String firstName;
    private String middleName;
    private String lastName;
    private String displayName;
    private boolean allowAccess;

    public User() {
    }

    public User(String username, boolean allowAccess) {
        this.username = username;
        this.allowAccess = allowAccess;
    }

    public boolean isAllowAccess() {
        return allowAccess;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
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

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    
}
