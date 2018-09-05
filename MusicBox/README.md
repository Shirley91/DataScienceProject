# Music Box 
The objective of this project is to build classification models to predict churn and provide more business insights for MusicBox.
## Data Sources
There are three types of log data, including user's play, download, and search records in MusicBox app through 3/30/2017 to 5/12/2017.
- Play.log (17G)
  - user_id (numeric) :5 digits-11digits
  - device (categoric) :ar/ip
  - song_id (numeric): 1 digit - 15 digits
  - song_type (categoric) : 0/1/2
  - song_name(object) : Chinese/Japanese character
  - singer(object) : Chinese/Japanese character
  - play_time(numeric): records as single play records(not cumulative)most dirty data and need lots of effort to clean up
  - song_length(numeric): records in seconds
  - paid_flag (categoric): 0/1
  - file_name(object): mannually extracted from file_name, added as a new column when integrating file, as the record day
- Download.log (7G)
  - user_id
  - song_id
  - song_name
  - song_type
  - singer
- Search.log (11G)
  - user_id
  - song_id
  - device
  - search time
  - url
## Data Processing
- Download raw data
- Unpack and clean files into one dataframe, column 'event' present play (p), download (d), and search (s) types
- Remove outliers (extrmemly large play_time and frequency) by percentile
- Extract variables mainly useful for churn prediction
- Define churn users: 0 acticities from 4/29 to 5/12 if the user is active(play more than 3 times) from 3/30 to 4/28
- Downsampling: shrink dataset size and balance churn/active rate
## Feature Generation
- time-window =[1,3,7,14,21,30], snapshot date = 4/28
- frequency: count of play/search/download records within each time-window
- recency: last day of play/search/download action
## Train Models and Predict Churn
- Applied multiplyApplied multiple binary classification models in feature_data, tried default model first, then applied GridSearchCV to find the best parameter. Models including:
  - Logistic Regression
  - Random Forest
  - Gradient Boosted Trees
  - Neural Network
- Train models using spark.ml
  - Logistic Regression
  - Random Forest
  - Gradient Boosted Trees
