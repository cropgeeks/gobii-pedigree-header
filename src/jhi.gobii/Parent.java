package jhi.gobii;

public class Parent
{
	public static final String RP = "RP";
	public static final String DP = "DP";
	public static final String NA = "N/A";

	private final String germplasmName;
	private final String type;

	public Parent(String germplasmName, String type)
		throws Exception
	{
		this.germplasmName = germplasmName;
		this.type = parseParentType(type);
	}

	private String parseParentType(String type)
		throws Exception
	{
		if (type.isEmpty())
			type = NA;

		switch (type.toUpperCase())
		{
			case RP:
				return RP;
			case DP:
				return DP;
			case NA:
				return NA;

			default:
				throw new Exception("Unknown parent type found");
		}
	}

	public String getGermplasmName()
		{ return germplasmName; }

	public String getType()
		{ return type; }

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		Parent parent = (Parent) o;

		if (!germplasmName.equals(parent.germplasmName))
			return false;

		return type.equals(parent.type);
	}

	@Override
	public int hashCode()
	{
		int result = germplasmName.hashCode();
		result = 31 * result + type.hashCode();

		return result;
	}

	@Override
	public String toString()
	{
		return "Parent{" +
			"germplasmName='" + germplasmName + '\'' +
			", type='" + type + '\'' +
			'}';
	}
}
