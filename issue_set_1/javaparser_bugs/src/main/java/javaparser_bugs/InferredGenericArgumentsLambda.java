package javaparser_bugs;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class InferredGenericArgumentsLambda
{
	Map<String, Set<Integer>> set = new HashMap<>();

	void x()
	{
		set.computeIfAbsent("abc", (k) -> new HashSet<>()).add(1);
	}
}
