package com.generation_p.hotel_demo.views;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Button;
import org.openqa.selenium.TimeoutException;
import org.vaadin.viritin.util.HtmlElementPropertySetter;

import com.generation_p.hotel_demo.entity.Hotel;
import com.generation_p.hotel_demo.services.HotelDataProvider;
import com.generation_p.hotel_demo.services.ServiceProvider;
import com.generation_p.hotel_demo.views.form.HotelForm;
import com.vaadin.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.SerializablePredicate;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.renderers.HtmlRenderer;
import com.vaadin.ui.themes.ValoTheme;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

@SuppressWarnings("serial")
@SpringUI
public class HotelView extends AbstractEntityView {

	public static String VIEW_NAME = "hotels";
    private static String TAKE_FACILITIES_BUTTON = "Take facilities";

	private HotelForm form;
	private Grid<Hotel> grid = new Grid<>(Hotel.class);
	private TextField filterText = new TextField();
	private ConfigurableFilterDataProvider<Hotel, Void, String> dataProvider;

    private Button takeFacilitiesBtn = new Button(TAKE_FACILITIES_BUTTON, VaadinIcons.PLUS);
    private WebDriver driver;
    private static final String BASE_URL = "http://localhost:8080";

	/**
	 * Method that is called each time on view enter
	 */
	@Override
	public void enter(ViewChangeEvent event) {
		form = new HotelForm(this);
		HorizontalLayout controlPannel = new HorizontalLayout();
		initControls(controlPannel);

		initGridComponent();
		// hide form by default
		form.setVisible(false);

		HorizontalLayout main = new HorizontalLayout(grid, form);
		main.setSizeFull();
		main.setExpandRatio(grid, 1);

		addComponents(controlPannel, main);
	}

	private void initControls(HorizontalLayout controlPannel) {
		filterText.setPlaceholder("filter by name...");
		filterText.addValueChangeListener(e -> updateList());
		filterText.setValueChangeMode(ValueChangeMode.LAZY);
		filterText.setStyleName(ValoTheme.TEXTFIELD_TINY);
        filterText.setId("filterByNameHotelView_id");                 // set Id for testing
		// make the field look like HTML5 search
		HtmlElementPropertySetter s1 = new HtmlElementPropertySetter(filterText);
		s1.setProperty("type", "search");

		HorizontalLayout buttons = getButtons();
		buttons.removeComponent(editButton);

//        controlPannel.addComponents(filterText, buttons);
        controlPannel.addComponents(filterText, buttons, takeFacilitiesBtn);

        // take facility script
        takeFacilitiesBtn.addClickListener(p -> takeFacilities());


		// set Id for testing
		super.newButton.setId("newBtnHotelView_id");
	}

	private void initGridComponent() {
		grid.setColumns("name", "rating", "address");
		grid.addColumn(hotel -> hotel.getCategory() == null ? "Not defined" : hotel.getCategory().getCategoryName(),
				new HtmlRenderer()).setCaption("Category");
		grid.addColumn(hotel -> "<a href='" + hotel.getUrl() + "' target='_blank'>More info</a>", new HtmlRenderer())
				.setCaption("Info link").setId("url");

		grid.asSingleSelect();
		// author: Sergey Nikonov
		grid.addItemClickListener(e -> {
			enableButtons(false, e.getItem() != null);
			if (e.getItem() == null) {
				form.setVisible(false);
			} else if (!e.getColumn().equals(grid.getColumn("url"))) {
				form.setVisible(true);
				form.setHotel(e.getItem());
			}
		});
		grid.setSizeFull();
		grid.setHeight(31, Unit.EM);
		
		dataProvider = new HotelDataProvider().withConfigurableFilter();
		grid.setDataProvider(dataProvider);

//		// set id for testing
//        grid.setId("filterByNameTextFieldCategoryView_id");

		updateList();
	}

	public void updateList() {
		String filter = filterText.getValue();
		dataProvider.setFilter(filter);
		// grid.getDataProvider().refreshAll();
		// List<Hotel> hotels =
		// EntityService.getHotelService().findAll(filterText.getValue());
		// grid.setItems(hotels);
	}

	public SerializablePredicate<Hotel> getFilter() {
		String filter = filterText.getValue();
		SerializablePredicate<Hotel> predicate = hotel -> hotel.getName().toLowerCase()
				.contains(filter == null ? "" : filter.toLowerCase());
		return predicate;
	}

	@Override
	protected void delete() {
		Set<Hotel> selected = grid.getSelectedItems();
		selected.forEach(item -> ServiceProvider.getHotelService().delete(item));
		form.closeForm();
		updateList();
	}

	@Override
	protected void edit() {}

	@Override
	protected void add() {
		Hotel h = new Hotel();
		form.setVisible(true);
		form.setHotel(h);

	}

	private void takeFacilities() {
//        System.setProperty("webdriver.chrome.driver", "/usr/bin/chromedriver");
        System.setProperty("webdriver.chrome.driver", "D:\\java-libraries\\chromedriver_win32\\chromedriver.exe");
        driver = new ChromeDriver();
        // Maximizes the current window if it is not already maximized
        driver.manage().window().maximize();

        driver.get(BASE_URL);
//        Thread.sleep(1000); // Let the user actually see something!

        // go to Hotel page
        WebElement hotelMenuItem = null;
        try {
            hotelMenuItem = (new WebDriverWait(driver, 100))
                    .until(ExpectedConditions.elementToBeClickable(By.xpath(
                            "//span[@class='v-menubar-menuitem'][contains(./span/text(), 'Hotel')]"
                    )));
        } catch (org.openqa.selenium.TimeoutException exp) {
            System.out.println("Cant load base page for 100 seconds");
            throw new TimeoutException(exp);
        }
        if (hotelMenuItem != null && hotelMenuItem.isDisplayed() && hotelMenuItem.isEnabled()) {
            hotelMenuItem.click();
//            Thread.sleep(1000); // Let the user actually see something!
        }

        // find Hotels if exist
        List<WebElement> finedHotels = getAllFilteredTableRows("",
                "filterByNameHotelView_id");
        // if any Hotel exist
        if ( ! finedHotels.isEmpty()) {
            for (WebElement finedHotel : finedHotels) {
                // take Grid Cells
                List<WebElement> gridCells = finedHotel.findElements(By.xpath(
                        ".//td[contains(@class, v-grid-cell)]"));
                // take 5-s cell - booking link
                WebElement bookingLinkCell = gridCells.get(4);
                String linkFromGrid = null;
                if (bookingLinkCell.isEnabled()) {
                    WebElement bookingLink = bookingLinkCell.findElement(By.xpath(
                            ".//a"));
                    bookingLink.click();

                    linkFromGrid = bookingLinkCell.getText();
                    System.out.println("linkFromGrid: " + linkFromGrid);
                }
            }
        }

        driver.quit();
    }

    private List<WebElement> getAllFilteredTableRows(String filterByName, String filterByNameTextFieldId)
            {
        // find All Table Rows where name contains filterByName
        WebElement filterByNameTextField = driver.findElement(By.id(filterByNameTextFieldId));
        if (filterByNameTextField.isEnabled()) {
            filterByNameTextField.clear();
            filterByNameTextField.sendKeys(filterByName);
        }
//        Thread.sleep(1000); // Let the user actually see something!

        WebElement anyFilteredTableRow = null;
        try {
            anyFilteredTableRow = (new WebDriverWait(driver, 5))
                    .until(ExpectedConditions.presenceOfElementLocated(By.xpath(
                            "//tbody[@class='v-grid-body']/tr[contains(@class, 'v-grid-row')]")));

        } catch (org.openqa.selenium.TimeoutException exp) {
            System.out.println("No any rows were found in table body for 5 seconds!");
        }

        List<WebElement> filteredTableRows = new LinkedList<>();
        if (anyFilteredTableRow != null) {
            // if any row present - take all table rows
            filteredTableRows = driver.findElements(By.xpath(
                    "//tbody[@class='v-grid-body']/tr[contains(@class, 'v-grid-row')]"));
        }
        return filteredTableRows;
    }
}
