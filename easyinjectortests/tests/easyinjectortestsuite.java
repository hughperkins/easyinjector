package tests;

import org.junit.runner.RunWith;
import org.junit.runners.*;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	TestConstructorInjection.class,
	TestCyclicConstructorInjection.class,
	TestCyclicInjection.class,
	TestInstanceAdding.class,
	TestOnlyInstantiateRegistered.class,
	TestSetterInjectionFields.class,
	TestSubclasses.class,
	TestUnregisteredCallChainDiagnosis.class
})
public class easyinjectortestsuite {

}
