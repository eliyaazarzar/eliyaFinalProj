package projeliya.ver2.projeliya.Pages;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

import projeliya.ver2.projeliya.AppMainLayout;
import projeliya.ver2.projeliya.Services.QuestionService;
import projeliya.ver2.projeliya.Services.SolutionService;
import projeliya.ver2.projeliya.Services.UserService;

@Route(value = "/profile", layout = AppMainLayout.class)
@PageTitle("Profile")
public class ProfilePage extends VerticalLayout {
    private static final String USER_SESSION_KEY = "user";
    private final UserService userService;
    private final QuestionService questionService;
    private final SolutionService solutionService;

    public ProfilePage(SolutionService solutionService, UserService userService, QuestionService questionService) {
        this.userService = userService;
        this.questionService = questionService;
        this.solutionService = solutionService;

        String username = (String) VaadinSession.getCurrent().getAttribute(USER_SESSION_KEY);
        if (username == null) {
            UI.getCurrent().getPage().setLocation("/");
            return;
        }

        H1 title = new H1("Welcome to your profile, " + username + "!");
        title.getStyle().set("color", "blue"); // Setting title color

        Button changePasswordButton = new Button("Change Password", new Icon(VaadinIcon.KEY));
        changePasswordButton.addClickListener(event -> openPasswordChangeDialog());
        changePasswordButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY); // Adding primary theme variant

        setAlignItems(FlexComponent.Alignment.CENTER); // Center aligning components
        setSpacing(true); // Adding space between components
        add(title, changePasswordButton);
    }

    private void openPasswordChangeDialog() {
        Dialog passwordChangeDialog = new Dialog();
        passwordChangeDialog.setModal(true);

        H1 title = new H1("Change Password");
        title.getStyle().set("color", "blue"); // Setting title color

        // Your password change form components (e.g., old password, new password,
        // confirm password fields)
        TextField oldPasswordField = new TextField("Old Password");
        PasswordField newPasswordField = new PasswordField("New Password");
        PasswordField confirmPasswordField = new PasswordField("Confirm New Password");

        Button changeButton = new Button("Change", new Icon(VaadinIcon.CHECK));
        changeButton.addClickListener(event -> {
            // Add your password change logic here
            String oldPassword = oldPasswordField.getValue();
            String newPassword = newPasswordField.getValue();
            String confirmPassword = confirmPasswordField.getValue();



            if(newPassword.isEmpty()== true || oldPassword.isEmpty()== true || confirmPassword.isEmpty()== true)
            {
                Notification.show("one of the fields is empty.")
                .setPosition(Notification.Position.MIDDLE);
                return;
            }
            // Validate the fields and perform the password change operation
            if (!newPassword.equals(confirmPassword)) {
                // Show an error message if passwords don't match
                Notification.show("New password and confirm password do not match.")
                        .setPosition(Notification.Position.MIDDLE);
                return;
            }
            String username = (String) VaadinSession.getCurrent().getAttribute(USER_SESSION_KEY);
            // Perform the password change operation
            boolean success = userService.updateUser(userService.getUserByID(username), oldPassword,
                    newPassword);

            if (success) {
                // Show a success message
                Notification.show("Password changed successfully.").setPosition(Notification.Position.MIDDLE);
                passwordChangeDialog.close();
            } else {
                // Show an error message if password change failed
                Notification.show("your old password is incorrect.")
                        .setPosition(Notification.Position.MIDDLE);
            }
        });
        changeButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS); // Adding success theme variant

        Button cancelButton = new Button("Cancel", new Icon(VaadinIcon.CLOSE));
        cancelButton.addClickListener(event -> passwordChangeDialog.close());
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY); // Adding tertiary theme variant

        VerticalLayout layout = new VerticalLayout(title, oldPasswordField, newPasswordField, confirmPasswordField,
                changeButton, cancelButton);
        passwordChangeDialog.add(layout);
        passwordChangeDialog.open();
    }

 
}
