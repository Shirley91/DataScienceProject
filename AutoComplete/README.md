# Auto Completion
The objective of this project is to generate an N to N-Gram auto completion.
## N to 1-Gram (MapReduce using Docker-Hadoop)
We need to generate a database with three columns: 
- the input phrase 
- the following phrase 
- the frequency (probability)
Therefore, we need to build two MapReduce:
1. Build N-Gram library from input text file: parsing the input text file and passing it to the Map Reduce job.  
   - The mapper would split the words on distributed nodes.
   - The reducer would merge each key word for 2 to N-1 gram, so that we could get the frequency.
2. Build Language Model on the database. 
   - The input or starting phrase as key and following phrase with count as value (from 1st MapReduce).
   - The mapper would split the following phrase and counts.
   - The reducer would generate and output the three columns database for the top k following phrase.
## N to N-Gram (MySQL-MAMP)

   
