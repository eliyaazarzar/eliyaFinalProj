package projeliya.ver2.projeliya.Pages;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.VaadinSession;

import projeliya.ver2.projeliya.AppMainLayouGuess;
import projeliya.ver2.projeliya.AppMainLayout;
import projeliya.ver2.projeliya.Classes.User;
import projeliya.ver2.projeliya.Services.UserService;

@PageTitle("Login")
@Route(value = "/login" ,layout = AppMainLayouGuess.class)

public class LoginPage extends VerticalLayout {
    private static final String USER_SESSION_KEY = "user"; // Session attribute key for the user
    private UserService userService;

    public LoginPage(UserService userService) {
        this.userService = userService;
        String loggedInUsername = (String) VaadinSession.getCurrent().getAttribute(USER_SESSION_KEY);
        if (loggedInUsername != null) {
            System.out.println("-------- User NOT Authorized - can't use chat! --------");
            UI.getCurrent().getPage().setLocation("/"); // Redirect to home page
            return;
        }

        // Create a layout for login form
        VerticalLayout loginFormLayout = new VerticalLayout();
        loginFormLayout.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);

        // Create a big title text with icon
        Icon titleIcon = new Icon(VaadinIcon.KEY);
        titleIcon.setSize("4em");

        // Add icon and text together
        // Add the icon
        // Add the text

        HorizontalLayout titleLayout = new HorizontalLayout();

        // Add the text "Welcome to LoginPage" to the layout
        titleLayout.add(new Span("Welcome to LoginPage"));

        // Add the icon to the layout after the text
        titleIcon.setSize("40px");
        titleLayout.add(titleIcon);

        // Add the title layout to the login form layout
        loginFormLayout.add(titleLayout);
        loginFormLayout.getStyle().set("font-size", "2em"); // Increase font size

        // Create text fields for username and password with icons and placeholders
        TextField usernameField = new TextField();
        usernameField.setPlaceholder("Username");
        usernameField.setPrefixComponent(new Icon(VaadinIcon.USER));

        PasswordField passwordField = new PasswordField();
        passwordField.setPlaceholder("Password");
        passwordField.setPrefixComponent(new Icon(VaadinIcon.LOCK));

        // Create a login button with an icon and text
        Button loginButton = new Button("Login");
        loginButton.setIcon(new Icon(VaadinIcon.SIGN_IN));
        loginButton.addClickListener(event -> {
            String username = usernameField.getValue();
            String password = passwordField.getValue();
            if(username.equals("") || password.equals(""))
            {
                Notification.show("name or password empty!");
                return;

            }
            // Now you can compare the username and password with the database
            User userExists = userService.getUserByID(username);

            if (userExists != null && userExists.getPassword().equals(password)) {
                Notification.show("Login Successful!");
                UI.getCurrent().getSession().setAttribute("user", username);
                UI.getCurrent().navigate("/"); // Redirect to home page
            } else if (userExists != null) {
                Notification.show("Invalid username or password. Please try again.");
            } else  {
                Notification.show("Please register first.");
            }
        });

        // Create a button to navigate to the register page with an icon and text
        RouterLink registerLink = new RouterLink("To Register", RegisterPage.class);
        registerLink.add(new Icon(VaadinIcon.USER_CHECK));

        // Add components to the login form layout
        loginFormLayout.add(usernameField, passwordField, loginButton, registerLink);

        // Set alignment and background for the login page
        setAlignItems(FlexComponent.Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        setWidth("100%");
        setHeight("100vh");
        getStyle().set("background", "linear-gradient(to right, #E0EAFC, #CFDEF3)"); // Regular color scheme

        // Add the login form layout to the login page
        add(loginFormLayout);
    }
}
