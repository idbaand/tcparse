package tools.ui.components;

import java.util.HashMap;
import java.util.Map;

import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Resource;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.themes.ValoTheme;

import tools.EverythingService;

/**
 * Responsive navigation menu presenting a list of available views to the user.
 */
public class Menu extends CssLayout {

    private static final String VALO_MENUITEMS = "valo-menuitems";
    private static final String VALO_MENU_TOGGLE = "valo-menu-toggle";
    private static final String VALO_MENU_VISIBLE = "valo-menu-visible";
    private Navigator navigator;
    private Map<String, Button> viewButtons = new HashMap<String, Button>();

    private CssLayout menuPart;
    private Accordion categoryPart;
    private Map<String, CssLayout> categories = new HashMap<>();
    
    public Menu(Navigator navigator) {
        this.navigator = navigator;
        setPrimaryStyleName(ValoTheme.MENU_ROOT);
        menuPart = new CssLayout();
        menuPart.addStyleName(ValoTheme.MENU_PART);

        // header of the menu
        final HorizontalLayout top = new HorizontalLayout();
        top.setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);
        top.addStyleName(ValoTheme.MENU_TITLE);
        top.setSpacing(true);
        Label title = new Label("Regression");
        title.addStyleName(ValoTheme.LABEL_H3);
        title.setSizeUndefined();
        //Image image = new Image(null, new ThemeResource("img/regression-testing.png"));
        //image.setStyleName("logo");
        //top.addComponent(image);
        top.addComponent(title);
        menuPart.addComponent(top);

        // button for toggling the visibility of the menu when on a small screen
        final Button showMenu = new Button("Menu", new ClickListener() {
            @Override
            public void buttonClick(final ClickEvent event) {
                if (menuPart.getStyleName().contains(VALO_MENU_VISIBLE)) {
                    menuPart.removeStyleName(VALO_MENU_VISIBLE);
                } else {
                    menuPart.addStyleName(VALO_MENU_VISIBLE);
                }
            }
        });
        showMenu.addStyleName(ValoTheme.BUTTON_PRIMARY);
        showMenu.addStyleName(ValoTheme.BUTTON_SMALL);
        showMenu.addStyleName(VALO_MENU_TOGGLE);
        showMenu.setIcon(FontAwesome.NAVICON);
        menuPart.addComponent(showMenu);

        categoryPart = new Accordion();
        menuPart.addComponent(categoryPart);;
        for(String category : EverythingService.getRegressions()) {
            CssLayout cssLayout = new CssLayout();
            cssLayout.setPrimaryStyleName(VALO_MENUITEMS);
            categoryPart.addTab(cssLayout, category);
            categories.put(category, cssLayout);
        }
        
        addComponent(menuPart);
    }

    /**
     * Register a pre-created view instance in the navigation menu and in the {@link Navigator}.
     *
     * @see Navigator#addView(String, View)
     *
     * @param view view instance to register
     * @param name view name
     * @param caption view caption in the menu
     * @param icon view icon in the menu
     */
    public void addView(View view, final String category, final String name, String caption,
            Resource icon) {
        navigator.addView(name, view);
        createViewButton(category, name, caption, icon);
    }

    public void addCommand(String caption, MenuBar.Command command) {
        createCommandButton(caption, command);
    }

    /**
     * Register a view in the navigation menu and in the {@link Navigator} based on a view class.
     *
     * @see Navigator#addView(String, Class)
     *
     * @param viewClass class of the views to create
     * @param name view name
     * @param caption view caption in the menu
     * @param icon view icon in the menu
     */
    public void addView(Class<? extends View> viewClass, final String category, final String name,
            String caption, Resource icon) {
        navigator.addView(viewClass.getSimpleName(), viewClass);
        createViewButton(category, name, caption, icon);
    }

    private void createViewButton(final String category, final String name, String caption,
            Resource icon) {
        Button button = new Button(caption, new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                navigator.navigateTo(name);

            }
        });
        button.setPrimaryStyleName(ValoTheme.MENU_ITEM);
        button.setIcon(icon);
        CssLayout layout = categories.get(category);
        layout.addComponent(button);
        viewButtons.put(category+"_"+name, button);
    }

    private void createCommandButton(final String caption, MenuBar.Command command) {
        Button button = new Button(caption, new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                command.menuSelected(null);
            }
        });
        button.setPrimaryStyleName(ValoTheme.MENU_ITEM);
        menuPart.addComponent(button);
    }

    /**
     * Highlights a view navigation button as the currently active view in the menu. This method does not perform the actual navigation.
     *
     * @param viewName the name of the view to show as active
     */
    public void setActiveView(String viewName) {
        for (Button button : viewButtons.values()) {
            button.removeStyleName("selected");
        }
        Button selected = viewButtons.get(viewName);
        if (selected != null) {
            selected.addStyleName("selected");
        }
        menuPart.removeStyleName(VALO_MENU_VISIBLE);
    }
}
