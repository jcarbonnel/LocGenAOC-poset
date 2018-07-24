## Local generation of AOC-poset

# Datasets

###### Repo

Table of variants taken from [fork-insight](http://forks-insight.com/), in `.CSV` format.

###### Cleaning 

[data/fork-insight/clean.sh](./data/fork-insight/clean.sh) is a tiny script to transform variant tables in `.CSV` to variant lists in a correct format saved in a text file.

For instance, to clean the table with the fork information of the project linux:
```
./clean.sh linux/linux.csv linux/output.txt
```

###### Files.txt

This file contains the list of the paths to the variant lists, in order to be processed  as a whole.

# Implementation

For each variant list depicted in `files.txt`, the java program takes 100 random variants and computes their conceptual neighbourhoods in the AOC-poset.

Are displayed the name of the corresponding project, its number of variants and characteristics, the number of generated concepts for the 100 steps, and the average time to compute a conceptual neighbourhood.