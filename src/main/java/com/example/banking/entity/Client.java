package com.example.banking.entity;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "Client_details")
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "date_of_birth", nullable = false)
    private Date dateOfBirth;

    @ElementCollection
    @CollectionTable(name = "client_emails", joinColumns = @JoinColumn(name = "client_id"))
    @Column(name = "email")
    @Valid
    private List<@Email(message = "Invalid email address") String> emails = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "client_phones", joinColumns = @JoinColumn(name = "client_id"))
    @Column(name = "phone")
    @Valid
    private List<@Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be 10 digits") String> phones = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "account_id", referencedColumnName = "id")
    private Account account;

    @Column(name = "username", nullable = false, unique = true)
    @NotBlank(message = "Username is mandatory")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    @Column(name = "password", nullable = false)
    @NotBlank(message = "Password is mandatory")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;

    // Constructors, getters, and setters

    public Client() {
    }

    public Client(String name, Date dateOfBirth, List<String> emails, List<String> phones, Account account, String username, String password) {
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.emails = emails;
        this.phones = phones;
        this.account = account;
        this.username = username;
        this.password = password;
    }

    // Getter and Setter methods
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public List<String> getEmails() {
        return emails;
    }

    public void setEmails(List<String> emails) {
        this.emails = emails;
    }

    public List<String> getPhones() {
        return phones;
    }

    public void setPhones(List<String> phones) {
        this.phones = phones;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
