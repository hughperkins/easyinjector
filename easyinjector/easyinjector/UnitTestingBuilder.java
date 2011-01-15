package easyinjector;

import java.lang.reflect.Field;
import java.util.*;

import easyinjector.EasyInjector.InjectField;

public class  UnitTestingBuilder <T>{
	Class<T> componentClass;
	
	HashMap<Class<?>, Object> dependencies = new HashMap<Class<?>, Object>();
	
	HashSet<Class<?>> ambiguousinterfaces = new HashSet<Class<?>>();

	public UnitTestingBuilder( Class<T> componentClass ){
		this.componentClass = componentClass;
	}

	public <U> void addDependency( U dependency ) {
		dependencies.put( dependency.getClass(), dependency );
		Class<?>[] interfaces = dependency.getClass().getInterfaces();
		for(Class<?> thisinterface : interfaces ) {
			if( dependencies.containsKey(thisinterface)) {
				ambiguousinterfaces.add( thisinterface);
//				System.out.println("Warning: overwriting existing interface " + thisinterface + " with " + dependency );
			}
			dependencies.put(thisinterface, dependency);				
		}
		Class<?> superclass = dependency.getClass().getSuperclass();
		while( ! superclass.equals( Object.class ) ) {
			if( dependencies.containsKey(superclass)) {
				System.out.println("Warning: overwriting existing parent class " + superclass + " with " + dependency );
			}
			dependencies.put(superclass, dependency);
			superclass = superclass.getSuperclass();
		}
	}

	public T instance() throws RuntimeException {
		T instance = null;
		try {
			instance = componentClass.newInstance();
		} catch( Exception e ){
			throw new RuntimeException("No public default constructor available for " + componentClass);
		}
		addDependencies(instance);
		return instance;
	}
	
	public void assertDependenciesSupplied() {
		Field[] fields = componentClass.getDeclaredFields();
		for( Field field : fields ) {
			InjectField annotation = field.getAnnotation(InjectField.class);
			if( annotation == null ) {
				continue;
			}

			Class<?> dependencyType = field.getType();
			if( !dependencies.containsKey(dependencyType)) {
				throw new AssertionError("Dependency " + dependencyType + " missing.");
			}
			if( ambiguousinterfaces.contains( dependencyType ) ) {
				throw new AssertionError("Dependency " + dependencyType + " has multiple matches");
			}
		}		
	}

	void addDependencies(T instance ) {
		// now repeat for fields
		Field[] fields = componentClass.getDeclaredFields();
		for( Field field : fields ) {
			InjectField annotation = field.getAnnotation(InjectField.class);
			if( annotation == null ) {
				continue;
			}

			Class<?> dependencyType = field.getType();
			if( !dependencies.containsKey(dependencyType)) {
				continue;
//				throw new RuntimeException("Dependency " + dependencyType + " missing." );				
			}
			Object dependency = dependencies.get(dependencyType);
			if( !field.isAccessible() ) {
				field.setAccessible(true);
				try{
					field.set(instance, dependency);
				} catch( Exception e ){
					throw new RuntimeException("Unable to inject " + dependency + " into " + instance );
				}
				field.setAccessible(false);
			} else {
				try{
					field.set(instance, dependency);				
				} catch( Exception e ){
					throw new RuntimeException("Unable to inject " + dependency + " into " + instance );
				}
			}
		}		
	}
}
