package tests;

import junit.framework.*;
import easyinjector.*;
import easyinjector.EasyInjector.InjectField;

public class TestOnlyInstantiateRegistered extends TestCase {
	public static class A {
		@InjectField final B b = null;
		@InjectField final IB ib = null;

		public int go(){
			return b.getNum();
		}
	}
	
	interface IB{}

	public static class B implements IB {
		@InjectField final A a = null;

		public int getNum(){
			return 52;
		}
	}

	public void testOnlyInstantiateRegistered() throws Exception {
		EasyInjector injector = new EasyInjector();
		injector.setInstantiateUnregistered(false);
		try {
			injector.instanceOf(A.class);
			throw new AssertionFailedError();
		} catch( Exception e ) {

		}
		injector.addComponent(A.class);
		try {
			injector.instanceOf(A.class);
			throw new AssertionFailedError();
		} catch( Exception e ) {
			e.printStackTrace();
		}
		injector = new EasyInjector();
		injector.setInstantiateUnregistered(false);
		injector.addComponent(A.class);
//		try {
			injector.instanceOf(B.class);
//			throw new AssertionFailedError();
//		} catch( Exception e ) {
//
//		}
		injector = new EasyInjector();
		injector.setInstantiateUnregistered(false);
		injector.addComponent(A.class);
		injector.addComponent(B.class);
		assertNotNull(injector.instanceOf(A.class));
		assertNotNull(injector.instanceOf(B.class));
		assertNotNull(injector.instanceOf(IB.class));
		A a = injector.instanceOf(A.class);
		assertEquals(52, a.go());
	}
}
