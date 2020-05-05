package net.runeduniverse.libs.rogm;

import org.junit.Test;

import net.runeduniverse.libs.rogm.data.AnnotationProcessor;
import net.runeduniverse.libs.rogm.model.AnsaStudent;
import net.runeduniverse.libs.rogm.model.Student;

public class AnnotationProcessorTests {

	@Test
	public void classParsing() {
		AnnotationProcessor ap = new AnnotationProcessor();
		Student student = new Student("Am Baum 3");
		AnsaStudent astudent = new AnsaStudent("Am Baum 3");
		
		ap.analyze(student);
		System.out.println("-----------------");
		ap.analyze(astudent);
	}
	
	
}
