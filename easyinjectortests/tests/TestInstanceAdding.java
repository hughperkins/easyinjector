package tests;

import static org.mockito.Mockito.*;
import junit.framework.TestCase;
import easyinjector.*;
import easyinjector.EasyInjector.InjectField;

public class TestInstanceAdding extends TestCase {
	public static class A {
		@InjectField final B b = null;
		@InjectField final IB ib = null;

		public int go(){
			return b.getNum();
		}
	}
	
	interface IB{}
	
	class C {
		
	}

	public static class B implements IB {
		@InjectField final A a = null;
		@InjectField final C c = null;

		public int getNum(){
			return 52;
		}
	}

	public void testInstanceAdding() throws Exception {
		B b = mock(B.class);
		System.out.println( b.getClass().getSuperclass() );
		for( Class<?> thisinterface : b.getClass().getSuperclass().getInterfaces() ) {
			System.out.println("interface of parent: " + thisinterface);
		}
		for( Class<?> thisinterface : b.getClass().getInterfaces() ) {
			System.out.println("interface direct: " + thisinterface);
		}
		EasyInjector injector = new EasyInjector();
		injector.setInstantiateUnregistered(false);
		injector.addInstance(b);
		injector.addComponent(A.class);
		assertNotNull(injector.instanceOf(B.class));
		assertNotNull(injector.instanceOf(IB.class));
		assertNotNull(injector.instanceOf(A.class));
		A a = injector.instanceOf(A.class);
		when(b.getNum()).thenReturn(52);
		assertEquals(52, a.go());
	}
}
