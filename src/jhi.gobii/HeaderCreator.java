package jhi.gobii;

import java.io.*;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.*;

public class HeaderCreator
{
	private static final String PEDIGREE_HEADER = "# fjPedigree";
	private static final String WILDCARD = "*";
	private static final String SAMPLE_FILE_HEADER = "\tgermplasm_name";

	private File sample;
	private File output;
	private File genotype;

	// The allowable types of parent (RP, DP, N/A)
	private ArrayList<String> parentTypes = new ArrayList<>();

	private List<SampleEntry> sampleEntries = new ArrayList<>();

	public static void main(String[] args)
	{
		if (args.length == 3)
		{
			HeaderCreator creator = new HeaderCreator(new File(args[0]), new File(args[1]), new File(args[2]));
			try
			{
				creator.processSampleFile();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			System.out.println("Usage: jhi.gobii.HeaderCreator input_file genotype_file output_file");
		}
	}

	public HeaderCreator(File sample, File genotype, File output)
	{
		this.sample = sample;
		this.genotype = genotype;
		this.output = output;

		initParentTypes();
	}

	private void initParentTypes()
	{
		parentTypes.add(Parent.RP);
		parentTypes.add(Parent.DP);
		parentTypes.add(Parent.NA);
	}

	public void processSampleFile()
		throws Exception
	{
		// Sanity check our inputs
		// TODO: Should we check that the output file doesn't exist?
		if (sample == null || !sample.exists())
			throw new FileNotFoundException("Sample file not found");
		if (genotype == null || !genotype.exists())
			throw new FileNotFoundException("Gentoype file not found");

		parseSampleFile();
		writeOutputFile();
	}

	// Read the sample file and build up an understanding of the the parental
	// information about the lines
	private void parseSampleFile()
		throws Exception
	{
		try (BufferedReader reader = new BufferedReader(new FileReader(sample)))
		{
			GobiiHeader gobiiHeader = null;

			String fileLine;
			while ((fileLine = reader.readLine()) != null)
			{
				if (fileLine.startsWith("# fj"))
					continue;

				else if (fileLine.startsWith(SAMPLE_FILE_HEADER))
				{
					gobiiHeader = new GobiiHeader(fileLine.split("\t", -1));
					gobiiHeader.validate();
				}

				else
				{
					String[] tokens = fileLine.split("\t", -1);

					// Simple information about each line, its name, the germplasm name and whether or not it's a parent
					String lineName = tokens[gobiiHeader.getColDnaRunName()];
					String germplasmName = tokens[gobiiHeader.getColGermplasmName()];

					String[] parentInfo = Arrays.copyOfRange(tokens, gobiiHeader.getColGermplasmPar1(), gobiiHeader.getColGermplasmPar4Type() + 1);

					HashSet<Parent> parents = parseParents(parentInfo);

					SampleEntry entry = new SampleEntry(lineName, germplasmName, parents);
					sampleEntries.add(entry);
				}
			}

			// Tweak to remove parents which have been included in a parent field
			// in the sample file for an entry, but aren't part of the population
			for (SampleEntry entry : sampleEntries)
				entry.getParents().removeIf(p -> getLinesForGermplasm(p.getGermplasmName()).isEmpty());
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	private HashSet<Parent> parseParents(String[] parentInfo)
		throws Exception
	{
		HashSet<Parent> parsed = new HashSet<>();

		for (int i=0; i < parentInfo.length; i+=2)
		{
			String parentName = parentInfo[i];

			if (!parentName.isEmpty())
			{
				Parent parent = new Parent(parentName, parentInfo[i+1]);
				parsed.add(parent);
			}
		}

		return parsed;
	}

	private void writeOutputFile()
	{
		// Read the genotype file line by line and output a new genotype file with
		// the pedigree header added to it
		try (BufferedReader reader = new BufferedReader(new FileReader(genotype));
			PrintWriter writer = new PrintWriter(new FileWriter(output)))
		{
			// Output any existing Flapjack genotype file header lines first
			String genotypeLine = reader.readLine();
			while (genotypeLine.length() == 0 || genotypeLine.startsWith("#"))
			{
				writer.println(genotypeLine);
				genotypeLine = reader.readLine();
			}

			// Write our pedigree header
			outputPedigreeHeader(writer);

			// Finally output the rest of the original genotype file
			while (genotypeLine != null)
			{
				writer.println(genotypeLine);
				genotypeLine = reader.readLine();
			}
		}
		catch (Exception e) { e.printStackTrace(); }
	}

	private void outputPedigreeHeader(PrintWriter writer)
		throws Exception
	{
		if (sampleEntries.isEmpty())
			return;

		// Check if we need to output the full header information for each line
		// for each type of parent
		for (String parentType : parentTypes)
		{
			if (isWildcardForType(parentType))
			{
				String wildcardString = wildcardString(parentType);

				StringJoiner joiner = new StringJoiner("\t", PEDIGREE_HEADER + "\t", "");
				joiner.add(WILDCARD)
					.add(parentType)
					.add(wildcardString);

				if (!wildcardString.isEmpty())
					writer.println(joiner.toString());
			}
			// Otherwise we need to output a header for every line that requires one
			else
			{
				for (SampleEntry entry : sampleEntries)
				{
					// We don't output parents or when an entry doesn't have the parent type
					if (!entry.hasParentType(parentType))
						continue;

					String parentString = linesForParentSet(entry.getParentsByType(parentType));

					StringJoiner joiner = new StringJoiner("\t", PEDIGREE_HEADER + "\t", "");
					joiner.add(entry.getDnaRunName())
						.add(parentType)
						.add(parentString);

					if (!parentString.isEmpty())
						writer.println(joiner.toString());
				}
			}
		}
	}

	private boolean isWildcardForType(String parentType)
	{
		List<Set<Parent>> parents = sampleEntries.stream()
			.filter(s -> s.hasParentType(parentType))
			.map(s -> s.getParentsByType(parentType))
			.collect(Collectors.toCollection(ArrayList::new));

		// If we've found no parents this isn't a wildcard case
		if (parents.size() == 0)
			return false;

		// We have a single set of parents so we can just return
		else if (parents.size() == 1)
			return true;

		// If we have multiple sets of parents (e.g. one per SampleEntry) we
		// want to double check they're all the same, otherwise this isn't
		// really the wildcard case
		else
		{
			Set<Parent> comparisonSet = parents.get(0);

			return parents.stream().allMatch(Predicate.isEqual(comparisonSet));
		}
	}

	private String wildcardString(String parentType)
		throws Exception
	{
		Optional<Set<Parent>> parents = sampleEntries.stream()
			.filter(s -> s.hasParentType(parentType))
			.map(s -> s.getParentsByType(parentType))
			.findFirst();

		if (parents.isPresent())
		{
			Set<Parent> found = parents.get();
			return found.stream()
				.flatMap(p -> getLinesForGermplasm(p.getGermplasmName()).stream())
				.collect(Collectors.joining("\t"));
		}

		throw new Exception("An unexpected error occured while trying to generate a pedigree wildcard string.");
	}

	private String linesForParentSet(Set<Parent> parents)
	{
		return parents.stream()
			.flatMap(p -> getLinesForGermplasm(p.getGermplasmName()).stream())
			.collect(Collectors.joining("\t"));
	}

	private List<String> getLinesForGermplasm(String germplasmName)
	{
		return sampleEntries.stream()
			.filter(l -> l.getGermplasmName().equals(germplasmName))
			.map(SampleEntry::getDnaRunName)
			.collect(Collectors.toCollection(ArrayList::new));
	}
}