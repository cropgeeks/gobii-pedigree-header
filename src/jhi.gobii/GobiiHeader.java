package jhi.gobii;

public class GobiiHeader
{
	private final int colDnaRunName = 0;

	private int colGermplasmName = -1;
	private int colGermplasmPar1 = -1;
	private int colGermplasmPar1Type = -1;
	private int colGermplasmPar2 = -1;
	private int colGermplasmPar2Type = -1;
	private int colGermplasmPar3 = -1;
	private int colGermplasmPar3Type = -1;
	private int colGermplasmPar4 = -1;
	private int colGermplasmPar4Type = -1;

	public GobiiHeader(String[] tokens)
	{
		for (int i=0; i < tokens.length; i++)
		{
			switch (tokens[i])
			{
				case "germplasm_name":
					colGermplasmName = i;
				case "germplasm_par1":
					colGermplasmPar1 = i;
				case "germplasm_par1_type":
					colGermplasmPar1Type = i;
				case "germplasm_par2":
					colGermplasmPar2 = i;
				case "germplasm_par2_type":
					colGermplasmPar2Type = i;
				case "germplasm_par3":
					colGermplasmPar3 = i;
				case "germplasm_par3_type":
					colGermplasmPar3Type = i;
				case "germplasm_par4":
					colGermplasmPar4 = i;
				case "germplasm_par4_type":
					colGermplasmPar4Type = i;
			}
		}
	}

	void validate()
		throws Exception
	{
		if (colGermplasmName == -1)
			throw new Exception("germplasm_name column was not found.");

		if (colGermplasmPar1 == -1)
			throw new Exception("germplasm_par1 column was not found.");

		if (colGermplasmPar1Type == -1)
			throw new Exception("germplasm_par1_type column was not found.");

		if (colGermplasmPar2 == -1)
			throw new Exception("germplasm_par2 column was not found.");

		if (colGermplasmPar2Type == -1)
			throw new Exception("germplasm_par2_type column was not found.");

		if (colGermplasmPar3 == -1)
			throw new Exception("germplasm_par3 column was not found.");

		if (colGermplasmPar3Type == -1)
			throw new Exception("germplasm_par3_type column was not found.");

		if (colGermplasmPar4 == -1)
			throw new Exception("germplasm_par4 column was not found.");

		if (colGermplasmPar4Type == -1)
			throw new Exception("germplasm_par4_type column was not found.");
	}

	public int getColDnaRunName()
		{ return colDnaRunName; }

	public int getColGermplasmName()
		{ return colGermplasmName; }

	public void setColGermplasmName(int colGermplasmName)
		{ this.colGermplasmName = colGermplasmName; }

	public int getColGermplasmPar1()
		{ return colGermplasmPar1; }

	public void setColGermplasmPar1(int colGermplasmPar1)
		{ this.colGermplasmPar1 = colGermplasmPar1; }

	public int getColGermplasmPar1Type()
		{ return colGermplasmPar1Type; }

	public void setColGermplasmPar1Type(int colGermplasmPar1Type)
		{ this.colGermplasmPar1Type = colGermplasmPar1Type; }

	public int getColGermplasmPar2()
		{ return colGermplasmPar2; }

	public void setColGermplasmPar2(int colGermplasmPar2)
		{ this.colGermplasmPar2 = colGermplasmPar2; }

	public int getColGermplasmPar2Type()
		{ return colGermplasmPar2Type; }

	public void setColGermplasmPar2Type(int colGermplasmPar2Type)
		{ this.colGermplasmPar2Type = colGermplasmPar2Type; }

	public int getColGermplasmPar3()
		{ return colGermplasmPar3; }

	public void setColGermplasmPar3(int colGermplasmPar3)
		{ this.colGermplasmPar3 = colGermplasmPar3; }

	public int getColGermplasmPar3Type()
		{ return colGermplasmPar3Type; }

	public void setColGermplasmPar3Type(int colGermplasmPar3Type)
		{ this.colGermplasmPar3Type = colGermplasmPar3Type; }

	public int getColGermplasmPar4()
		{ return colGermplasmPar4; }

	public void setColGermplasmPar4(int colGermplasmPar4)
		{ this.colGermplasmPar4 = colGermplasmPar4; }

	public int getColGermplasmPar4Type()
		{ return colGermplasmPar4Type; }

	public void setColGermplasmPar4Type(int colGermplasmPar4Type)
		{ this.colGermplasmPar4Type = colGermplasmPar4Type; }
}