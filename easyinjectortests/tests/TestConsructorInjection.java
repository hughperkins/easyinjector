package tests;

import java.util.*;

import junit.framework.TestCase;

import easyinjector.EasyInjector;
import easyinjector.EasyInjector.Inject;

public class TestConsructorInjection extends TestCase {
	public static class A{
		B b;
		
		public A(B b){
			this.b = b;
		}
		
		public int go(){
			return b.dosomething();
		}
		
		public int dosomething(){
			return 1;
		}	
	}
	
	public static class B {
		A a;
		
		public B(){
		}
		
		public int go(){
			return a.dosomething();
		}
		
		public int dosomething(){
			return 2;
		}
		
		@Inject
		public void setA( A a ) {
			this.a = a;
		}
	}

	public void testConstructorInjection(){
		EasyInjector injector = new EasyInjector();
		assertEquals( 2, injector.instanceOf(A.class).go() );
		assertEquals( 1, injector.instanceOf(B.class).go() );
	}
}
