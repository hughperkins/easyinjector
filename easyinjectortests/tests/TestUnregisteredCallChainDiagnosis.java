package tests;

import junit.framework.*;
import easyinjector.*;
import easyinjector.EasyInjector.InjectField;

public class TestUnregisteredCallChainDiagnosis extends TestCase {
	public static class A {
		@InjectField B b;
		@InjectField C c;
	}
	
	public static class B {
		@InjectField D d;
	}
	
	public static class C {
		@InjectField E e;
	}
	
	public static class D {}
	public static class E {}
	
	public void testUnregisteredCallChainDiagnosis() throws Exception {
		EasyInjector injector = new EasyInjector();
		injector.setInstantiateUnregistered(false);
		injector.instanceOf(D.class);
		injector.instanceOf(B.class);
		try{
			injector.instanceOf(A.class);
			throw new AssertionFailedError();
		} catch( Exception e ){
			e.printStackTrace();
			assertEquals(true, e.getMessage().contains( "callchain: class tests.TestUnregisteredCallChainDiagnosis$A -> class tests.TestUnregisteredCallChainDiagnosis$C" ) );
		}
	}
}
