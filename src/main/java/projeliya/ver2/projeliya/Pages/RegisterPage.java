package projeliya.ver2.projeliya.Pages;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import projeliya.ver2.projeliya.AppMainLayouGuess;
import projeliya.ver2.projeliya.Classes.User;
import projeliya.ver2.projeliya.Services.UserService;

@PageTitle("RegisterPage")
@Route(value = "/register" ,layout = AppMainLayouGuess.class)
public class RegisterPage extends VerticalLayout {

    private UserService userService;

    public RegisterPage(UserService userService) {
        this.userService = userService;

        // Set up the main layout
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        setPadding(false);
        setSpacing(false);
        getStyle().set("background", "linear-gradient(to bottom right, #007bff, #00bfff)"); // Gradient background

        // Title with icon
        H1 title = new H1();
        Icon icon = new Icon(VaadinIcon.USER_CHECK);
        icon.setColor("white"); // Set the icon color to white
        title.add(icon, new com.vaadin.flow.component.Text("Register"));
        title.getStyle().set("color", "blue"); // Text color
        title.getStyle().set("font-size", "2.5em");
        title.getStyle().set("margin-bottom", "20px");

        // Form layout for centered content
        VerticalLayout formLayout = new VerticalLayout();
        formLayout.setWidth("300px");
        formLayout.getStyle().set("background-color", "rgba(255, 255, 255, 0.8)"); // Semi-transparent white background
        formLayout.getStyle().set("padding", "20px");
        formLayout.getStyle().set("border-radius", "10px");
        formLayout.getStyle().set("box-shadow", "0 4px 8px rgba(0,0,0,0.1)");

        // Input fields
        TextField usernameField = new TextField();
        usernameField.setPlaceholder("Username");
        usernameField.getStyle().set("width", "100%");
        usernameField.getStyle().set("margin-bottom", "15px");
        usernameField.setPrefixComponent(new Icon(VaadinIcon.USER));

        PasswordField passwordField = new PasswordField();
        passwordField.setPlaceholder("Password");
        passwordField.getStyle().set("width", "100%");
        passwordField.getStyle().set("margin-bottom", "15px");
        passwordField.setPrefixComponent(new Icon(VaadinIcon.PASSWORD));

        // Register button with icon
        Button registerButton = new Button("Register", new Icon(VaadinIcon.USER_CHECK));
        registerButton.getStyle().set("width", "100%");
        registerButton.getStyle().set("background-color", "#28a745"); // Green color
        registerButton.getStyle().set("color", "white");
        registerButton.getStyle().set("border-radius", "5px");
        registerButton.getStyle().set("cursor", "pointer");
        registerButton.getStyle().set("transition", "background-color 0.3s ease");
        registerButton.addClickListener(event -> handleRegister(usernameField, passwordField));

        // Adding components to the form layout
        formLayout.add(title, usernameField, passwordField, registerButton);

        // Adding formLayout to the main layout
        add(formLayout);
    }

    private void handleRegister(TextField usernameField, PasswordField passwordField) {
        String username = usernameField.getValue();
        String password = passwordField.getValue();

        if (username.isEmpty()) {
            Notification.show("Username cannot be empty");
            return;
        }

        if (password.isEmpty()) {
            Notification.show("Password cannot be empty");
            return;
        }

        if (userService.existsByName(username)) {
            Notification.show("User already exists");
            return;
        }

        User user = new User(username, password);
        userService.addUser(user);
        Notification.show("User registered successfully");
        UI.getCurrent().getSession().setAttribute("user", username);
        UI.getCurrent().navigate("/");
    }
}
