package multiset;

import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class HashMultiSetTest {

	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	@Test
	public void testbuildFromCollection() {
	    //exception.expect(IllegalArgumentException.class);
	    //exception.expectMessage("Method should be invoked with a non null file path");
	    HashMultiSet<String, Integer> hmSet = new HashMultiSet<>();

        List<String> listaStringhe = new ArrayList<>();
        listaStringhe.add("aa");
        listaStringhe.add("ab");
        listaStringhe.add("ac");
        listaStringhe.add("ab");
        hmSet.buildFromCollection(listaStringhe);

        assertEquals("Equal", true, hmSet.getElementFrequency("ab") == 2);

        List<String> linearizzo = hmSet.linearize();
        System.out.println(linearizzo);
	}
	
	@Test
	public void testElementFrequency() {
	    HashMultiSet<Integer, Integer> hmSet = new HashMultiSet<>();
	    hmSet.addElement(1);
	    hmSet.addElement(1);
	    //System.out.println(hmSet.getElementFrequency(1));
	    assertEquals("Equal", true, hmSet.getElementFrequency(1) == 2);
	}

	@Test
	public void testBuildFromFile() throws IOException {


	    HashMultiSet<String, Integer> hmSet = new HashMultiSet<>();
		Path test = Path.of("test.txt");
		hmSet.buildFromFile(test);
		assertEquals("Equal", true, hmSet.getElementFrequency("ab") == 2);


		/* This test exposes the problem that we have with keys of class Integer.
		 * getElementFrequency method would require an Integer, but keys in hashtable cannot be casted
		 * to Integer from String without a parseInteger so keys are stored in table as String.


        HashMultiSet<Integer, Integer> hmSet2 = new HashMultiSet<>();
        Path test2 = Path.of("test2.txt");
        hmSet2.buildFromFile(test2);
        //System.out.println(hmSet2.getElementFrequency(1));
        //assertEquals("Equal", true, hmSet2.getElementFrequency(1) == 2);
		System.out.println(hmSet2.getElementFrequency(1));
		System.out.println(hmSet2.getElementFrequency(5));
		*/

	}
}
