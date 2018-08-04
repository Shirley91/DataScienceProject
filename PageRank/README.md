# Page Rank
The objective of this project is to rank websites in the search engine results.
## Assumptions:
- Quantity assumption: more important websites are likely to receive more links for other websites.
- Quality assumption: website with higher Page Rank will pass higher weight.

Therefore, we need to build two matrixes and then do matrix cell multiplication using Map Reduce.
## Matrixes:
- Transition matrix
- Weight matrix (value of page rank)
## MapReduce:
1. 1st MapReduce:
   - Two Mappers are used to transit and initialize page rank matrixes into cell, respectively.
     - The transition cell with from webpage as key and to webpage and relation (probability) as value
     - The page rank cell with webpage as key and page rank (weight) as value 
   - The reducer merge the values with the same key, so that we get a list of value for each key (webpage).
2. 2nd MapReduce:
   - The mapper reads file generated form the prior Map Reduce job with to webpage as key and sub-page rank value as value (probability * initial weight).
   - The reducer would sum up the sub-page rank value to get the total page rank for each webpage.
