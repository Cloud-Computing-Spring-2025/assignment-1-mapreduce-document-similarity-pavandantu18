package com.example;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class DocumentSimilarityReducer extends Reducer<Text, Text, Text, Text> {

    // class to hold document info.
    private static class Document {
        String id;
        Set<String> words;
        Document(String id, Set<String> words) {
            this.id = id;
            this.words = words;
        }
    }

    private final static Text outKey = new Text();
    private final static Text outValue = new Text();

    @Override
    protected void reduce(Text key, Iterable<Text> values, Context context)
            throws IOException, InterruptedException {
        List<Document> docs = new ArrayList<>();
        // Each value is: "fileName \t word1,word2,..."
        for (Text val : values) {
            String[] parts = val.toString().split("\\t");
            if (parts.length < 2) continue;
            String id = parts[0];
            Set<String> wordSet = new HashSet<>(Arrays.asList(parts[1].split(",")));
            docs.add(new Document(id, wordSet));
        }
        // Compute pairwise Jaccard similarity.
        for (int i = 0; i < docs.size(); i++) {
            for (int j = i + 1; j < docs.size(); j++) {
                Set<String> intersection = new HashSet<>(docs.get(i).words);
                intersection.retainAll(docs.get(j).words);
                Set<String> union = new HashSet<>(docs.get(i).words);
                union.addAll(docs.get(j).words);
                double similarity = union.size() == 0 ? 0.0 : (double) intersection.size() / union.size();

                if(similarity > 0.50) {
                String simStr = String.format("Similarity: %.2f", similarity);
                outKey.set(docs.get(i).id + ", " + docs.get(j).id);
                outValue.set(simStr);
                context.write(outKey, outValue);
            }
        }
        }
    }
}
