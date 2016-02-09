package com.eurecalab.eureca.aws;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.eurecalab.eureca.R;
import com.eurecalab.eureca.core.Recording;

import android.content.Context;

public class S3Manager {
    private AmazonS3Client s3;
    private Context context;
    private TransferUtility transferUtility;
    private String bucketName;

    public S3Manager(Context context) {
        this.context = context;
        bucketName = context.getString(R.string.s3_bucket_name);
    }

    public void connect() {
        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                context,
                context.getString(R.string.aws_pool_id), // Identity Pool ID
                Regions.EU_WEST_1 // Region
        );
        s3 = new AmazonS3Client(credentialsProvider);
        s3.setRegion(Region.getRegion(Regions.EU_WEST_1));
        transferUtility = new TransferUtility(s3, context);
    }

    public TransferObserver download(Recording recording) {
        TransferObserver observer = transferUtility.download(bucketName, recording.getFileName(), new File(recording.getPath()));
        return observer;
    }

    public TransferObserver upload(Recording recording, File toUpload) {
        TransferObserver observer = transferUtility.upload(bucketName, recording.getFileName(), toUpload);
        return observer;
    }

    public TransferObserver downloadImage(String imageName, File path) {
        TransferObserver observer = transferUtility.download(bucketName, "img/" + imageName, path);
        return observer;
    }

    public void delete(Recording recording){
        s3.deleteObject(bucketName, recording.getFileName());
    }
    
    public List<String> list(){
        ObjectListing listing = s3.listObjects(bucketName);
        List<S3ObjectSummary> summaries = listing.getObjectSummaries();
        while (listing.isTruncated()) {
            listing = s3.listNextBatchOfObjects (listing);
            summaries.addAll (listing.getObjectSummaries());
        }

        List<String> result = new LinkedList<>();
        for (S3ObjectSummary summary : summaries) {
            result.add(summary.getKey());
        }

        return result;
    }
}
