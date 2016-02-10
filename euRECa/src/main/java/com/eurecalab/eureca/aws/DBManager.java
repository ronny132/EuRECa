package com.eurecalab.eureca.aws;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.content.Context;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBQueryExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedQueryList;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedScanList;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsyncClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.eurecalab.eureca.R;
import com.eurecalab.eureca.constants.GenericConstants;
import com.eurecalab.eureca.core.Category;
import com.eurecalab.eureca.core.Recording;
import com.eurecalab.eureca.core.Share;
import com.eurecalab.eureca.core.ShareClassification;
import com.eurecalab.eureca.core.User;

public class DBManager {
    private AmazonDynamoDBAsyncClient db;
    private Context context;
    private DynamoDBMapper mapper;

    public DBManager(Context context) {
        this.context = context;

    }

    public void connect() {
        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                context,
                context.getString(R.string.aws_pool_id), // Identity Pool ID
                Regions.EU_WEST_1 // Region
        );
        db = new AmazonDynamoDBAsyncClient(credentialsProvider);
        db.setRegion(Region.getRegion(Regions.EU_WEST_1));
        mapper = new DynamoDBMapper(db);
    }

    public void storeRecording(Recording recording) {
        mapper.save(recording);
    }

    public void storeCategory(Category category) {
        mapper.save(category);
    }

    public void storeRecordingAndCategory(Recording recording, Category category) {
        mapper.save(recording);
        mapper.save(category);
    }

    public void shareRecording(Share share) {
        mapper.save(share);
    }

    public List<Recording> getUserFavorites(String username, Date lowerBound, int limit) {
        List<ShareClassification> shareClassifications = new LinkedList<>();
        Map<Recording, Integer> shares = new HashMap<>();

        Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
        eav.put(":user", new AttributeValue().withS(username));

        if(lowerBound != null){
            eav.put(":date", new AttributeValue().withS(GenericConstants.DATE_FORMATTER.format(lowerBound)));
        }

        String filterExpression = "Username = :user";
        if(lowerBound != null){
            filterExpression += " and ShareDate > :date";
        }

        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                .withFilterExpression(filterExpression)
                .withExpressionAttributeValues(eav);

        PaginatedScanList<Share> res = mapper.scan(Share.class, scanExpression);

        for (Share sh : res) {
            Recording rec = sh.getRecording();
            int currentCount;
            if (shares.containsKey(rec)) {
                currentCount = shares.get(rec);
            } else {
                currentCount = 0;
            }
            shares.put(sh.getRecording(), currentCount + 1);
        }

        for (Recording rec : shares.keySet()) {
            int shareCount = shares.get(rec);
            ShareClassification sc = new ShareClassification();
            sc.setRecording(rec);
            sc.setShareCount(shareCount);
            shareClassifications.add(sc);
        }

        Collections.sort(shareClassifications);
        shareClassifications = shareClassifications.subList(0, limit);

        List<Recording> result = new LinkedList<>();
        for (ShareClassification sc : shareClassifications) {
            result.add(sc.getRecording());
        }

        return result;
    }

    public void downloadCategories(Collection<Category> categories, List<Category> categoriesFiltered, User user) {
        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
        PaginatedScanList<Category> result = mapper.scan(Category.class, scanExpression);
        categories.clear();
        categoriesFiltered.clear();
        for (Category category : result) {
            List<Recording> recordings = category.getRecordings();
            for (Recording recording : recordings) {
                recording.setContext(context);
                recording.setCategory(category);
            }

            if(category.getName().equals(GenericConstants.FAVORITES_CATEGORY)){
                List<Recording> favorites = getUserFavorites(user.getEmail(), null, GenericConstants.DEFAULT_USER_SEARCH_LIMIT);
                category.setRecordings(favorites);
            }

            categories.add(category);
            categoriesFiltered.add(category);
        }

        Collections.sort(categoriesFiltered, new Comparator<Category>() {
            @Override
            public int compare(Category c1, Category c2) {
                return c1.getSortIndex() - c2.getSortIndex();
            }
        });

    }

    public void downloadCategories(Collection<Category> categories) {
        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
        PaginatedScanList<Category> result = mapper.scan(Category.class, scanExpression);
        categories.clear();
        for (Category category : result) {
            List<Recording> recordings = category.getRecordings();
            for (Recording recording : recordings) {
                recording.setContext(context);
            }
            categories.add(category);
        }
    }

    public User loadUser(String email) {

        User toFind = new User();
        toFind.setEmail(email);

        User user = findUser(toFind);
        if (user == null) {
            toFind.setProVersionExpireDate(GenericConstants.DATE_INFINITE);
            storeUser(toFind);
            return toFind;
        }

        return user;
    }

    public void storeUser(User user) {
        mapper.save(user);
    }

    private User findUser(User user) {
        DynamoDBQueryExpression<User> queryExpression = new DynamoDBQueryExpression<>();
        queryExpression = queryExpression
                .withHashKeyValues(user)
                .withConsistentRead(false);

        PaginatedQueryList<User> result = mapper.query(User.class, queryExpression);

        if (result.size() == 1) {
            return result.get(0);
        } else {
            return null;
        }
    }

    public void deleteRecording(Recording recording, Category category) {
        mapper.delete(recording);
        category.removeRecording(recording);
        mapper.save(category);
    }

    public Recording findRecording(Recording recording) {
        DynamoDBQueryExpression<Recording> queryExpression = new DynamoDBQueryExpression<>();
        queryExpression = queryExpression
                .withHashKeyValues(recording)
                .withConsistentRead(false);

        PaginatedQueryList<Recording> result = mapper.query(Recording.class, queryExpression);

        if (result.size() == 1) {
            return result.get(0);
        } else {
            return null;
        }
    }

}
