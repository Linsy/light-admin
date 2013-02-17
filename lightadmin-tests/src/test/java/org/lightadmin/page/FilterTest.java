package org.lightadmin.page;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.lightadmin.SeleniumIntegrationTest;
import org.lightadmin.config.FilterTestEntityConfiguration;
import org.lightadmin.data.Domain;
import org.lightadmin.data.User;
import org.springframework.beans.factory.annotation.Autowired;

import static org.lightadmin.util.DomainAsserts.assertTableData;

public class FilterTest extends SeleniumIntegrationTest {

	@Autowired
	private LoginPage loginPage;

	private ListViewPage productListViewPage;
	@Before
	public void setup() {
		repopulateDatabase();

		removeAllDomainTypeAdministrationConfigurations();

		registerDomainTypeAdministrationConfiguration( FilterTestEntityConfiguration.class );

		productListViewPage = loginPage.get().loginAs( User.ADMINISTRATOR ).navigateToDomain( Domain.FILTER_TEST_DOMAIN );
	}

	//Covers LA-6: https://github.com/max-dev/light-admin/issues/6
	//TODO: max: LA-25: Filtering sucks when primitive data types are used in configured Entity!
	@Test
	public void canFilterByIntegerField() {
		productListViewPage.openAdvancedSearch();
		productListViewPage.filter( "integerField", "1234567" );

		assertTableData( expectedResult1, productListViewPage.getDataTable(), webDriver(), webDriverTimeout() );
	}

	@Test
	public void canFilterByIdField() {
		productListViewPage.openAdvancedSearch();
		productListViewPage.filter( "id", "5" );

		assertTableData( expectedResult6, productListViewPage.getDataTable(), webDriver(), webDriverTimeout() );
	}

	@Test
	public void canFilterByDecimalField() {
		productListViewPage.openAdvancedSearch();
		productListViewPage.filter( "decimalField", "1499.99" );

		assertTableData( expectedResult2, productListViewPage.getDataTable(), webDriver(), webDriverTimeout() );
	}

	@Test
	public void textFilterIsCaseSensitive() {
		productListViewPage.openAdvancedSearch();
		productListViewPage.filter( "textField", "Case Sensitivity Test" );

		assertTableData( expectedResult4, productListViewPage.getDataTable(), webDriver(), webDriverTimeout() );
	}

	@Test
	public void canFilterByPartialTextQuery() {
		productListViewPage.openAdvancedSearch();
		productListViewPage.filter( "textField", "query" );

		assertTableData( expectedResult5, productListViewPage.getDataTable(), webDriver(), webDriverTimeout() );
	}

	@Test
	@Ignore // TODO: max: Will be fixed later
	public void canFilterByTextWithSpecialCharacters() {
		productListViewPage.openAdvancedSearch();
		productListViewPage.filter( "textField", "#<,&«$'(*@×¢¤₤€¥ª ™®© ØøÅåÆæĈę ¦_{~>½" );

		assertTableData( expectedResult3, productListViewPage.getDataTable(), webDriver(), webDriverTimeout() );
	}

	private static final String[][] expectedResult1 = {{"1", "integer search test", "1234567", "521", "22.2"}};
	private static final String[][] expectedResult2 = {{"2", "decimal search test", "456", "31264", "1499.99"}};
	private static final String[][] expectedResult3 = {{"3", "#<,&«$'(*@×¢¤₤€¥ª ™®© ØøÅåÆæĈę ¦_{~>½", "789", "62342", "22.2"}};
	private static final String[][] expectedResult4 = {{"4", "Case Sensitivity Test", "901", "823", "22.2"}};
	private static final String[][] expectedResult5 = {
		{"6", "query partial search test", "234", "9164", "22.2"},
		{"7", "partial querysearch test", "345", "612325", "22.2"},
		{"8", "search test by partial query", "567", "623412", "22.2"}
	};
	private String[][] expectedResult6 = {{"5", "Case sensitivity test", "901", "9521", "22.2"}};
}