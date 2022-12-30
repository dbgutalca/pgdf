# Format conversion tools
This repository has two tools for the conversion of property graph formats. 

# CSV to PGDF Converter

## Usage

ImportCSVtoPGDF Converter is a Java library for converting a property graph stored in CSV files to PGDF.  It expects an input directory containing a file configuration schema in JSON format to parse CSV files and an output directory will be created pgdf.

The input files need to be UTF-8 encoded. The library is available as executable Jar file and can be run from the command line by `java -jar CSVtoPGDF.jar <JSON schema directory> <output directory>.


The schema file in JSON format is to represent the CSV files of the LDBC benchmark data sets (https://github.com/ldbc/data-sets-surf-repository) in particular the SNB Interactive v1 files: CsvComposite serializer using LongDateFormatter. 

Inside the JSON schema file the PATH_CHANGE must be changed to the directory where the CSV files are stored.

To download the files from the LDBC repository, download the .zst files and then use the tool (https://github.com/facebook/zstd) to unzip the files and generate the CSV files.


## JSON schema description

The JSON schema has an array of Node objects and an array of edge objects. This schema allows us to understand the information in the CSV file properties graph in a better way. 

    Node 
        {
			"id": 1,
			"file": "PATH_CHANGE/dynamic/person_0_0.csv",
			"labels": [
				"Person"	
			],
			"properties": [
				"@id",
				"firstName",
				"lastName",
				"gender"
			]
		}

    node object parameters:

        - id: corresponds to the node identifier.
        - file: is the directory where the CSV file is located.
        - labels: corresponds to the tags that the node has (it can contain more than one tag).
        - properties: are the columns that the CSV file has with the properties that a node may have.
    
    Edge
        {
			"file": "PATH_CHANGE/dynamic/comment_replyOf_post_0_0.csv",
			"label": "replyOf",
			"source": 4,
			"target": 6,
			"properties": [
				"@source",
				"@target"
			]
		}

    edge object parameters:

        -  file: is the directory where the CSV file is located.
        -  label: corresponds to the tag that the edge has (this can only contain one tag)
        - source: corresponds to the identifier of the node where the edge is coming out
        - target: corresponds to the identifier of the node where the edge is coming in.
        - properties: are the columns that the CSV file has with the properties that a edge may have.


# PGDF to (GRAPHSON, XML, JSON and YARS-PG Converter)

## Usage

PGConverter is other Java library for converting a property graph stored in PGDF file to different data format (GRAPHSON, XML, JSON and YARS-PG). It expects the conversion method of the property graph (object-based, disk-based or direct conversion), the data format to be converted, an input directory containing a PGDF file and the output directory will be created data format converted.

The library is available as executable Jar file and can be run from the command line by `java -jar PGConverter --<disk> --<json> <PGDF directory> <output directory>.
