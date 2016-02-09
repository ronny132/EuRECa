package com.eurecalab.eureca.aws;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

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
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.eurecalab.eureca.R;
import com.eurecalab.eureca.constants.GenericConstants;
import com.eurecalab.eureca.core.Category;
import com.eurecalab.eureca.core.Recording;
import com.eurecalab.eureca.core.Share;
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

    public void storeRecordingAndCategory(Recording recording, Category category){
        mapper.save(recording);
        mapper.save(category);
    }

    public void shareRecording(Share share) {
        mapper.save(share);
    }

    public List<Recording> getUserFavorites(String username){
        List<Recording> result = new LinkedList<>();

//        Share share = new Share();
//        share.setUsername(username);

        Condition rangeKeyCondition = new Condition()
                .withComparisonOperator(ComparisonOperator.EQ.toString())
                .withAttributeValueList(new AttributeValue().withS(username.toString()));

        DynamoDBQueryExpression queryExpression = new DynamoDBQueryExpression()
//                .withHashKeyValues(share)
                .withRangeKeyCondition("Username", rangeKeyCondition)
                .withConsistentRead(false);

        PaginatedQueryList<Share> res = mapper.query(Share.class, queryExpression);
        res.size();

        for (Share share : res) {
            result.add(share.getRecording());
        }

        return result;
    }

    public void downloadCategories(Collection<Category> categories, Collection<Category> categoriesFiltered) {
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
            categories.add(category);
            categoriesFiltered.add(category);
        }
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
        if(user == null){
            toFind.setProVersionExpireDate(GenericConstants.DATE_INFINITE);
            storeUser(toFind);
            return toFind;
        }

        return user;
    }

    public void storeUser(User user) {
        mapper.save(user);
    }

    private User findUser(User user){
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

    public void deleteRecording(Recording recording, Category category){
        mapper.delete(recording);
        category.removeRecording(recording);
        mapper.save(category);
    }

    public Recording findRecording(Recording recording){
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
