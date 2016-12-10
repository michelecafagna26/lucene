package pack;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

public class CranTest {
	
	private String text;
	@Before
	public void setUp() throws Exception {
		
		text=".I 1 .T experimental investigation of the aerodynamics of a wing in a slipstream . .A brenckman,m. .B j. ae. scs. 25, 1958, 324. .W bla bla bla. "+
		".I 2 .T simple shear flow past a flat plate in an incompressible fluid of small viscosity . .A ting-yili .B department of aeronautical engineering, rensselaer polytechnic institute troy, n.y. .W  bla bla bla. ";
		
		
	}
	
	
	@Test
	public void testGetElement(){
		
		String row=".I 1 .T experimental investigation of the aerodynamics of a wing in a slipstream . .A brenckman,m. .B j. ae. scs. 25, 1958, 324. .W bla bla bla. ";
		
		assertEquals("1",Cran.getElement(row)[0]);
		assertEquals("experimental investigation of the aerodynamics of a wing in a slipstream .",Cran.getElement(row)[1]);
		assertEquals("brenckman,m.",Cran.getElement(row)[2]);
		assertEquals("j. ae. scs. 25, 1958, 324.",Cran.getElement(row)[3]);
		assertEquals("bla bla bla.",Cran.getElement(row)[4]);
	}
	
	@Test
	public void testGetContent(){
		
		String row=".I 1 .T experimental investigation of the aerodynamics of a wing in a slipstream . .A brenckman,m. .B j. ae. scs. 25, 1958, 324. .W bla bla bla. ";
		
		assertEquals("1",Cran.getContent(row).get(0)[0]);
		assertEquals("experimental investigation of the aerodynamics of a wing in a slipstream .",Cran.getContent(row).get(0)[1]);
		assertEquals("brenckman,m.",Cran.getContent(row).get(0)[2]);
		assertEquals("j. ae. scs. 25, 1958, 324.",Cran.getContent(row).get(0)[3]);
		assertEquals("bla bla bla.",Cran.getContent(row).get(0)[4]);
	}
	
	@Test
	public void testGetQueryList(){
		
		String row=".I 001 .W what similarity laws must be obeyed when constructing aeroelastic models of heated high speed aircraft .";
		
		assertEquals("001",Cran.getQueryList(row).get(0)[0]);
		assertEquals("what similarity laws must be obeyed when constructing aeroelastic models of heated high speed aircraft .",Cran.getQueryList(row).get(0)[1]);
		
	}
}
