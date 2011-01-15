//Copyright (c) 2011 Hugh Perkins hughperkins@gmail.com http://hughperkins.com
//All rights reserved.
//
//Redistribution and use in source and binary forms, with or without modification, are permitted 
//provided that the following conditions are met:
//
//    * Redistributions of source code must retain the above copyright notice, this list of conditions
//    and the following disclaimer.
//    * Redistributions in binary form must reproduce the above copyright notice, this list of 
//    conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
//    * The name Hugh Perkins may not be used to endorse or promote products derived from this 
//    software without specific prior written permission.
//
//THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR 
//IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY 
//AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
//CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
//DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
//DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER 
//IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
//OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

package easyinjector;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.annotation.*;
import java.lang.reflect.*;
import java.util.*;

public class EasyInjector {
	@Target(ElementType.METHOD)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface Inject {
	}
	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface InjectField {
	}

	ArrayList<Class<?>> components = new ArrayList<Class<?>>();
	HashMap<Class<?>,Object> instanceByClass = new HashMap<Class<?>, Object>();
	HashMap<Class<?>,Class<?>  > componentByInterface = new HashMap<Class<?>, Class<?>>(); 

	public void addComponent(Class<?> componentClass) {
		if( !components.contains(componentClass)) {
			components.add(componentClass);
			Class<?>[] interfaces = componentClass.getInterfaces();
			for(Class<?> thisinterface : interfaces ) {
				if( componentByInterface.containsKey(thisinterface)) {
					System.out.println("Warning: overwriting existing interface " + thisinterface + " with " + componentClass );
				}
				componentByInterface.put(thisinterface, componentClass);				
			}
		}
	}

	public <T> void addInstance( T instance ) {
		if( instanceByClass.containsKey(instance.getClass())) {
			System.out.println("Warning: overwriting existing " + instance.getClass() + " with " + instance );
		}
		instanceByClass.put(instance.getClass(), instance);
	}

	@SuppressWarnings( "unchecked")
	public <T> T instanceOf(Class<T>interfaceClass ) throws Exception {
		Class<?>componentClass = interfaceClass;
		if( componentByInterface.containsKey(interfaceClass)) {
			componentClass = componentByInterface.get(interfaceClass);
		}
		if( instanceByClass.containsKey(componentClass)){
			return (T)instanceByClass.get(componentClass);
		}
		addComponent(componentClass);
		T instance = null;
//		try {
			Constructor[] constructors = componentClass.getConstructors();
			if( constructors.length > 1 ) {
				System.out.println("Warning: more than one constructor found for " + componentClass );
			}
			if( componentClass.isInterface() ) {
				throw new Exception("No implementing class registered for " + componentClass );
			}
			if( constructors.length > 0 ) {
//				throw new RuntimeException("Error: no constructor found for " + componentClass );
				Constructor constructor = constructors[0];
				Class<?>[] constructorParameterTypes = constructor.getParameterTypes();
				int numConstructorParams = constructorParameterTypes.length;
				Object[] constructorParameters = new Object[numConstructorParams];
				for( int i = 0; i < numConstructorParams; i++ ) {
					constructorParameters[i] = instanceOf(constructorParameterTypes[i]);
				}
				instance = (T)constructor.newInstance(constructorParameters);
			} else {
				instance = (T)componentClass.newInstance();
			}
			instanceByClass.put(componentClass, instance);
			addDependencies( instance);
//		} catch( Exception e) {
//			throw new RuntimeException("couldn't construct " + componentClass + "\n" + exceptionToStackTrace(e));
//		}

		return instance;
	}

	<T> void addDependencies( T instance ) throws Exception {
		// 1. get methods
		// 2. check for annotation of @Inject
		//    3. for each
		//       4. determine dependency
		//       5. construct dependency

		Class<?>componentClass = instance.getClass();
		Method[] methods = componentClass.getMethods();
		for( Method method : methods ) {
			Inject annotation = method.getAnnotation(Inject.class);
			if( annotation == null ) {
				continue;
			}

			Class<?>[] parameterTypes = method.getParameterTypes();
			if( parameterTypes.length != 1 ) {
				System.out.println("Warning: setter " + method.getName() + " should have one parameter, not " + parameterTypes.length);
				continue;
			}
			Class<?> dependencyType = parameterTypes[0];
			Object dependency = instanceOf(dependencyType);
			method.invoke(instance, dependency);
		}
		
		// now repeat for fields
		Field[] fields = componentClass.getDeclaredFields();
		for( Field field : fields ) {
			InjectField annotation = field.getAnnotation(InjectField.class);
			if( annotation == null ) {
				continue;
			}

			Class<?> dependencyType = field.getType();
			Object dependency = instanceOf(dependencyType);
			if( !field.isAccessible() ) {
				field.setAccessible(true);
				field.set(instance, dependency);
				field.setAccessible(false);
			} else {
				field.set(instance, dependency);				
			}
		}
	}
	
}
