package javaparser_bugs;

public enum AnyTypeGenericParameterResolutionInEnum
{
	A(),
	B(Integer.class);

	AnyTypeGenericParameterResolutionInEnum(Class<?> clazz)
	{
	}

	AnyTypeGenericParameterResolutionInEnum()
	{
		this(String.class);
	}
}
