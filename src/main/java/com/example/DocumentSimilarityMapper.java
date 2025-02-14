package com.example;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import org.apache.hadoop.mapreduce.lib.input.FileSplit; 
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class DocumentSimilarityMapper extends Mapper<Object, Text, Text, Text> {

    private StringBuilder docContent = new StringBuilder();
    private String fileName = "";
    private final static Text constantKey = new Text("doc");
    private final static Text outValue = new Text();

    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        // Get the file name (document ID) from the input split.
        FileSplit fileSplit = (FileSplit) context.getInputSplit();
        fileName = fileSplit.getPath().getName();
    }

    @Override
    protected void map(Object key, Text value, Context context) throws IOException, InterruptedException {
        // Accumulate all lines for this file.
        docContent.append(value.toString()).append(" ");
    }

    @Override
    protected void cleanup(Context context) throws IOException, InterruptedException {
        // Process the complete document content.
        String content = docContent.toString().trim();
        if (content.isEmpty()) {
            return;
        }
        String[] tokens = content.split("\\s+");
        Set<String> words = new HashSet<>();
        for (String token : tokens) {
            // Converting to lowercase and removing non-alphanumeric characters.
            String word = token.toLowerCase().replaceAll("[^a-zA-Z0-9]", "");
            if (!word.isEmpty()) {
                words.add(word);
            }
        }
        // Create a comma-separated list of unique words.
        StringBuilder sb = new StringBuilder();
        for (String w : words) {
            sb.append(w).append(",");
        }
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1); // Remove trailing comma.
        }
        // Emit one record per document: "fileName \t word1,word2,..."
        outValue.set(fileName + "\t" + sb.toString());
        context.write(constantKey, outValue);
    }
}
