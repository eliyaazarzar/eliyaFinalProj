package projeliya.ver2.projeliya;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.router.Location;
import com.vaadin.flow.server.VaadinSession;

import projeliya.ver2.projeliya.Classes.User;
import projeliya.ver2.projeliya.Services.SolutionService;
import projeliya.ver2.projeliya.Services.UserService;

import java.util.ArrayList;
import java.util.List;

public class AppMainLayout extends AppLayout {
    private static final String LOGIN_VIEW = "/login";
    private static final String REGISTER_VIEW = "/register";
    private static final String PROFILE_VIEW = "/profile";
    private static final String CHECK_YOUR_CODE_VIEW = "/ChackYourCode";
    private static final String ADMIN_PAGE_VIEW = "/adminPage";
    private static final String SOLUTIONS_PAGE_VIEW = "/show";
    private static final String HOME_PAGE_VIEW = "/";

    private UserService userService;
    private Button selectedButton;
    private Span userSpan;
    private SolutionService solutionService;
    private List<Button> buttons = new ArrayList<>();

    public AppMainLayout(UserService userService,SolutionService solutionService) {
        solutionService = solutionService;
        this.userService = userService;
        createHeader();
    }

   private void createHeader() {
    userSpan = new Span();
    Button btnAdminPage = null;
    H3 logo = new H3("Rate Your Solutions");
    logo.getStyle().set("color", "#2979ff").set("font-weight", "bold");

    String userName = (String) VaadinSession.getCurrent().getAttribute("user");
    String userDisplayName = ("👤: " + (userName != null ? userName : "Guest"));

    HorizontalLayout header = new HorizontalLayout();
    header.setWidthFull();
    header.setPadding(true);
    header.setAlignItems(FlexComponent.Alignment.CENTER);

    if (userName != null) {
        // Add user-specific buttons to the left side
        Button btnProfile = createButton("Profile", PROFILE_VIEW, VaadinIcon.USER);
        Button checkYourCode = createButton("Check & Upload Solution", CHECK_YOUR_CODE_VIEW, VaadinIcon.CLOUD_UPLOAD);
        Button btnAllAnswers = createButton("Solutions Page", SOLUTIONS_PAGE_VIEW, VaadinIcon.CLIPBOARD_TEXT);
        Button btnHomePage = createButton("Home Page", HOME_PAGE_VIEW, VaadinIcon.HOME);
        checkYourCode.getStyle().set("margin-right", "5px");
        btnAllAnswers.getStyle().set("margin-right", "5px");
        logo.getStyle().set("margin-right", "5px");
        btnHomePage.getStyle().set("margin-right", "5px");

        header.add(logo, btnHomePage, checkYourCode, btnAllAnswers);

        User user = userService.getUserByID(userName);
        if (user != null && user.isFlag()) {
            userDisplayName += "(Admin)";
             btnAdminPage = createButton("Admin", ADMIN_PAGE_VIEW, VaadinIcon.COG);
        }

        // Create a spacer to push components to the right
        Div spacer = new Div();
        spacer.getStyle().set("flex-grow", "1");

        // Add user name and logout button to the right side
        Button btnLogout = createButton("Logout", LOGIN_VIEW, VaadinIcon.SIGN_OUT);
        btnLogout.addClickListener(event -> {
          
            VaadinSession.getCurrent().close();
            UI.getCurrent().navigate("");
        });

        userSpan = new Span(userDisplayName);
        userSpan.getStyle().set("font-weight", "bold").set("color", "#424242");
        HorizontalLayout userAndLogoutLayout ;
        if(user.isFlag())
        {
             userAndLogoutLayout = new HorizontalLayout(userSpan,btnAdminPage,btnProfile, btnLogout);

        }else{
             userAndLogoutLayout = new HorizontalLayout(userSpan,btnProfile, btnLogout);

        }
        userAndLogoutLayout.setAlignItems(FlexComponent.Alignment.BASELINE);

        header.add(spacer, userAndLogoutLayout);
    } else {

        userSpan.setText(userDisplayName);
        // Add login and register buttons to the right side
        Button btnLogin = createButton("Login", LOGIN_VIEW, VaadinIcon.SIGN_IN);
        Button btnRegister = createButton("Register", REGISTER_VIEW, VaadinIcon.USER);
        header.add(logo, btnLogin, btnRegister,userSpan);
    }

    // Add header to the navbar
    addToNavbar(header);
    selectButtonBasedOnPath();
}


    private Button createButton(String caption, String route, VaadinIcon icon) {
        Button button = new Button(caption, icon.create());
        button.addClickListener(e -> {
            UI.getCurrent().navigate(route);
            selectButton(button);
        });
        button.setId(route);
        if (caption.equals("Logout")) {
            button.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_PRIMARY);
        } else {
            button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        }
        buttons.add(button);
        return button;
    }

    private void selectButton(Button button) {
        if (selectedButton != null) {
            selectedButton.removeThemeVariants(ButtonVariant.LUMO_SUCCESS);
        }
        button.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        selectedButton = button;
    }

    private void selectButtonBasedOnPath() {
        Location location = UI.getCurrent().getActiveViewLocation();
        String path = "/" + location.getPath();
        for (Button button : buttons) {
            String buttonPath = button.getId().orElse("");
            if (buttonPath.equals(path)) {
                selectButton(button);
                return;
            }
        }
    }
}
