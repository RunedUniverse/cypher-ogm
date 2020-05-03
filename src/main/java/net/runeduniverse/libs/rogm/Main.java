package net.runeduniverse.libs.rogm;

import net.runeduniverse.libs.rogm.annotations.AnnotationProcessor;

public class Main {

	public static void main(String[] args) {
		AnnotationProcessor ap = new AnnotationProcessor();
		Student student = new Student("Am Baum 3");
		AnsaStudent astudent = new AnsaStudent("Am Baum 3");
		
		ap.analyze(student);
		System.out.println("-----------------");
		ap.analyze(astudent);
		
	}

}
