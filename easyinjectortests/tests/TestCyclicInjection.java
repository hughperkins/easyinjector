package tests;

import java.util.*;

import easyinjector.EasyInjector;
import easyinjector.EasyInjector.Inject;
import easyinjector.EasyInjector.InjectField;
import junit.framework.*;

public class TestCyclicInjection extends TestCase {
	public static class A{
		B b;
		
		public A(){
			System.out.println("a constructed");
		}
		
		public int go(){
			return b.dosomething();
		}
		
		public int dosomething(){
			return 41;
		}
		
		@Inject
		public void setB( B b ) {
			this.b = b;
		}
	}

	public class B {
		A a;
		
		public B(){
			System.out.println("b constructed");
		}
		
		public int go(){
			return a.dosomething();
		}
		
		public int dosomething(){
			return 51;
		}
		
		@Inject
		public void setA( A a ) {
			this.a = a;
		}
	}
	
	public void testSetterInjectionFields(){
		EasyInjector injector = new EasyInjector();
		assertEquals( 51, injector.instanceOf(A.class).go() );
		assertEquals( 41, injector.instanceOf(B.class).go() );
	}
}
