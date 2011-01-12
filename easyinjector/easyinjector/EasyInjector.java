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

import java.lang.annotation.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.*;

public class EasyInjector {
	@Target(ElementType.METHOD)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface Inject {
	}
	
	ArrayList<Class<?>> components = new ArrayList<Class<?>>();
	HashMap<Class<?>,Object> instanceByClass = new HashMap<Class<?>, Object>();

	public void addComponent(Class<?> componentClass) {
		components.add(componentClass);
	}
	
	@SuppressWarnings( "unchecked")
	public <T> T instanceOf(Class<T>componentClass ) {
		if( instanceByClass.containsKey(componentClass)){
			return (T)instanceByClass.get(componentClass);
		}
		T instance = null;
		try {
			Constructor[] constructors = componentClass.getConstructors();
			if( constructors.length < 1 ) {
				System.out.println("Error: no constructor found for " + componentClass );
				return null;
			}
			if( constructors.length > 1 ) {
				System.out.println("Warning: more than one constructor found for " + componentClass );
				return null;
			}
			Constructor constructor = constructors[0];
			Class<?>[] constructorParameterTypes = constructor.getParameterTypes();
			int numConstructorParams = constructorParameterTypes.length;
			Object[] constructorParameters = new Object[numConstructorParams];
			for( int i = 0; i < numConstructorParams; i++ ) {
				constructorParameters[i] = instanceOf(constructorParameterTypes[i]);
			}
			instance = (T)constructor.newInstance(constructorParameters);
//			instance = componentClass.newInstance();
			instanceByClass.put(componentClass, instance);
			addDependencies(componentClass, instance);
		} catch( Exception e) {
			System.out.println("couldn't construct " + componentClass);
			e.printStackTrace();
		}
		
		return instance;
	}
	
	<T> void addDependencies( Class<T> componentClass, T instance ) throws Exception {
		// 1. get methods
		// 2. check for annotation of @Inject
		//    3. for each
		//       4. determine dependency
		//       5. construct dependency
		
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
	}
}
