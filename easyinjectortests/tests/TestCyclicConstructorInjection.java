package tests;

import junit.framework.TestCase;
import easyinjector.*;
import easyinjector.EasyInjector.InjectField;

public class TestCyclicConstructorInjection extends TestCase {
	public static class A {
		B b;
		
		public A( B b ) {
			this.b =  b;
		}
	}
	public static class B {
		@InjectField A a;
		
		public B() {
		}
	}
	
	public void testCyclickConstructorInjection() throws Exception {
		EasyInjector injector = new EasyInjector();
		try{
			A a = injector.instanceOf(A.class);
			B b= injector.instanceOf(B.class);
			System.out.println(a);
			System.out.println(b.a);
			assertEquals(a, b.a); // either the instances should be equal, or should throw an exception
			                      // but either way should never arrive here with unequal instances
		} catch( Exception e ) {
			
		}
	}
}
