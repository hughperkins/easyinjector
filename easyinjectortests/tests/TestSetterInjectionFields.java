package tests;

import java.util.*;

import easyinjector.EasyInjector;
import easyinjector.EasyInjector.Inject;
import easyinjector.EasyInjector.InjectField;
import junit.framework.*;

public class TestSetterInjectionFields extends TestCase {
	public static class A {
		@InjectField
		final B b = null;
		
		public int go(){
			return b.getNum();
		}
	}
	
	public static class B {
		public int getNum(){
			return 52;
		}
	}
	
	public void testSetterInjectionFields(){
		EasyInjector injector = new EasyInjector();
		assertEquals( 52, injector.instanceOf(A.class).go() );
	}
}
