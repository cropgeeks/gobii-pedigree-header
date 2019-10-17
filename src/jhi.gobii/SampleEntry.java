package jhi.gobii;

import java.util.*;
import java.util.stream.*;

public class SampleEntry
{
	// The sample name (line name in Flapjack parlance)
	private final String dnaRunName;
	// The germplasm name (may relate to many samples)
	private final String germplasmName;

	// List of parents
	private final HashSet<Parent> parents;

	public SampleEntry(String dnaRunName, String germplasmName, HashSet<Parent> parents)
	{
		this.dnaRunName = dnaRunName;
		this.germplasmName = germplasmName;
		this.parents = parents;
	}

	boolean hasParentType(String type)
	{
		return parents.stream().anyMatch(p -> p.getType().equals(type));
	}

	Set<Parent> getParentsByType(String type)
	{
		if (hasParentType(type))
			return parents.stream().collect(Collectors.groupingBy(Parent::getType, Collectors.toSet())).get(type);
		else
			return new HashSet<Parent>();
	}

	public String getDnaRunName()
		{ return dnaRunName; }

	public String getGermplasmName()
		{ return germplasmName; }

	public HashSet<Parent> getParents()
		{ return parents; }
}