package tests;

import junit.framework.TestCase;
import easyinjector.*;
import easyinjector.EasyInjector.InjectField;

public class TestSubclasses extends TestCase {
	public static class Parent {
		public String getValue(){
			return "parent";
		}
	}
	
	public static class Child extends Parent {
		@Override
		public String getValue(){
			return "child";
		}		
	}
	
	public static class UsesParent {
		@InjectField final Parent parent = null;
		
		public String getValue(){
			return parent.getValue();
		}
	}
	
	public void testInstanceOfAddingSubclasses() throws Exception {
		EasyInjector injector = new EasyInjector();
		injector.setInstantiateUnregistered(false);
		injector.instanceOf(Child.class);
		UsesParent usesParent = injector.instanceOf(UsesParent.class);
		assertEquals(true, usesParent.parent instanceof Child );
		assertEquals("child", usesParent.getValue());
	}
}
