# Yelp Recommendation System
The objective of this project is to build up a recommendation system for yelp users.
## Data Sources
The data is from [Yelp Dataset Challenge](https://www.yelp.com/dataset/challenge).
- Files:
  - yelp_academic_dataset_business.json
  - yelp_academic_dataset_checkin.json
  - yelp_academic_dataset_review.json
  - yelp_academic_dataset_tip.json
  - yelp_academic_dataset_user.json
- Dataset:
  - 4.1M reviews and 947K tips by 1M users for 144K businesses
  - 1.1M business attributes, e.g., hours, parking availability, ambience.
  - Aggregated check-ins over time for each of the 125K businesses
  - 200,000 pictures from the included businesses
## Extract Features
(Natural Language Processing, NLP)
  
- Tokenization: split words by space, lower case, remove stop words and punctuation.
- Stemming and Lemmatization
- Convert words into vectors using TF-IDF
## Classify Reviews
- Naïve Bayes
- Logistic Regression
- Random Forest 
## Cluster Users
- K-mean cluster
## Recommmendation System
(Item-Item Similarity)
  
- Collaborative filtering
- Matrix factorization: NMF, SVD, GraphLab
