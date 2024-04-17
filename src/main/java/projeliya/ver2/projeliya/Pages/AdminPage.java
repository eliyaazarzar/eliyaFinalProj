package projeliya.ver2.projeliya.Pages;

import java.util.ArrayList;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

import projeliya.ver2.projeliya.AppMainLayout;
import projeliya.ver2.projeliya.Classes.User;
import projeliya.ver2.projeliya.Services.UserService;

@Route(value = "/adminPage" ,layout = AppMainLayout.class)
public class AdminPage extends VerticalLayout 
{

    private UserService userService;
    private Button updateToAdmin;
    private Grid<User> userGrid;
    private Button deleteUserButton;

    public AdminPage(UserService userService) {
        this.userService = userService;
      // Assuming you have an icon resource named "deleteIcon" and "updateIcon"

        String user = (String) VaadinSession.getCurrent().getAttribute("user");
        if(user != null) 
        {
            User userBeAdmin = userService.getUserByID(user);
            if(userBeAdmin.isFlag() == false)
            {
                System.out.println("-------- User NOT Authorized - can't use chat! --------");
                UI.getCurrent().getPage().setLocation("/"); // Redirect to login page (HomePage).
                return;  
            }
            
        }else{
            System.out.println("-------- User NOT Authorized - can't use chat! --------");
            UI.getCurrent().getPage().setLocation("/"); // Redirect to login page (HomePage).
            return;        
        }


        userGrid = new Grid<>(User.class);
        // Set up grid configuration
        userGrid.setColumns("id", "flag"); // Change according to your User class
        userGrid.getColumnByKey("id").setHeader("Name Of User");
        userGrid.getColumnByKey("flag").setHeader("Admin");
        userGrid.setItems(getUsers());
        deleteUserButton = new Button("Delete User", event -> {
            User selectedUser = userGrid.asSingleSelect().getValue();
            if (selectedUser != null && !selectedUser.isFlag()) { // Check if the selected user is not an admin
                deleteUser(selectedUser.getId());
            }
        });
        updateToAdmin = new Button("Update to Admin", event -> {
            User selectedUser = userGrid.asSingleSelect().getValue();
            if (selectedUser != null && !selectedUser.isFlag()) { // Check if the selected user is not already an admin
                updateToAdmin(selectedUser);
            }
        });
        HorizontalLayout horizontalLayout = new HorizontalLayout(deleteUserButton, updateToAdmin);

        add(userGrid, horizontalLayout);

        // Add selection listener to the userGrid
        userGrid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null && event.getValue().isFlag()==false) { // If selected user is an admin
                // Enable both buttons
                deleteUserButton.setEnabled(true);
                updateToAdmin.setEnabled(true);
            } else {
                // If selected user is not an admin, disable update to admin button
                deleteUserButton.setEnabled(false);
                updateToAdmin.setEnabled(false);
            }
        });
        buttonsfunc();

    }
    public void buttonsfunc() {
        deleteUserButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY); // Adding primary theme variant
        deleteUserButton.setIcon(new Icon(VaadinIcon.TRASH));
        deleteUserButton.getStyle().set("background", "linear-gradient(to right, #ff416c, #ff4b2b)");
        deleteUserButton.getStyle().set("color", "white");
        deleteUserButton.getStyle().set("border-radius", "10px");
        
        updateToAdmin.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        updateToAdmin.setIcon(new Icon(VaadinIcon.USER_CHECK));
        updateToAdmin.getStyle().set("background", "linear-gradient(to right, #2b32b2, #1488cc)");
        updateToAdmin.getStyle().set("color", "white");
        updateToAdmin.getStyle().set("border-radius", "10px");
    }

    private ArrayList<User> getUsers() {
        return new ArrayList<>(userService.getAllUsers());
    }

    private void updateToAdmin(User user) {
        userService.updateToAdmin(user);
        userGrid.setItems(getUsers());
    }

    private void deleteUser(String id) {
        userService.deleteUserByID(id);
        userGrid.setItems(getUsers());
    }
}
