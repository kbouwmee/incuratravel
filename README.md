# IncuraTravel Tool

This tool takes as input 2 export files from Incura:
1. Client list with addresses
2. All visits made

With this data the tool produces 1 output file:
1. List all trips chronologically with distance in kilometers

## Usage
This is how to call the tool:
```
java IncuraTravel <year> <therapist> <configfile> <importdir> <exportdir>
```
For example:
```
java IncuraTravel 2017 maria config.properties ./input ./output
```

## Config File
The config file should have the following attributes:
```
practice.street=Hoofdstraat 1
practice.postalcode=1234AA
practice.city=Amsterdam

maria.match=Janssen, M. (Maria): Ergotherapeuten
maria.street=Hoofdstraat 1
maria.postalcode =1234AA
maria.city=Utrecht

john.match=Doe, J. (John): Ergotherapeuten
john.street=Hoofdstraat 1
john.postalcode=1234AA
john.city=Rotterdam
```
There can be multiple therapists defined. The prefix for the therapist refers to the command line <therapist> parameter. The practice address is used for vists done at the practive office location.

## Input
The tool expects the following files to be present in the import directory:
- `clients_<year>.csv` for example: `clients_2017.csv`
- `visits_<year>_<therapist>.csv` for example: `visits_2017_maria.csv`

## Output
The tool output a file to the output directory
- `trips_<year>_<therapist>.csv` for example: `trips_2017_maria.csv`

The output csv file contains the following columns:
- Date (formatted as dd-mm-yyy)
- Client id
- From address (street, postal code and city)
- To address (street, postal code and city)
- Distance in kilometers
