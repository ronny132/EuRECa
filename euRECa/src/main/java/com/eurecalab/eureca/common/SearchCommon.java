package com.eurecalab.eureca.common;

import android.widget.Adapter;

import com.eurecalab.eureca.core.Category;
import com.eurecalab.eureca.core.GlobalState;
import com.eurecalab.eureca.core.Recording;
import com.eurecalab.eureca.ui.CategoryAdapter;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Created by MeringoloRo on 29/01/2016.
 */
public class SearchCommon {

    public static void searchForRecordings(String query, GlobalState gs, CategoryAdapter adapter) {
        if (query.length() == 0) {
            resetRecordings(gs, adapter);
        } else {
            query = query.toLowerCase(Locale.getDefault());

            String [] queryTokens = query.split(" ");

            gs.getFilteredCategories().clear();
//            int index = 0;
            for (Category category : gs.getCategories()) {
                Category categoryClone = new Category();
                categoryClone.setColorHex(category.getColorHex());
                categoryClone.setIconFileName(category.getIconFileName());
                categoryClone.setName(category.getName());
                List<Recording> recordings = category.getRecordings();
                for (Recording recording : recordings) {
                    String tagString = recording.getTags();
                    if (tagString != null) {
                        tagString = tagString.toLowerCase(Locale.getDefault());

                        String [] tags = tagString.split(" ");

                        boolean found = findMatches(queryTokens, tags);

                        if (found) {
                            categoryClone.addRecording(recording);
                        } else {
                            String name = recording.getName().toLowerCase(Locale.getDefault());
                            if (name.contains(query)) {
                                categoryClone.addRecording(recording);
                            }
                        }
                    }
                }
                if (categoryClone.size() > 0) {
                    gs.getFilteredCategories().add(categoryClone);
//                    adapter.notifyItemChanged(index);
                }
//                else{
//                    adapter.notifyItemRemoved(index);
//                }
//                index++;
//                adapter.notifyDataSetChanged();
            }
        }
    }

    private static boolean findMatches(String [] queryTokens, String [] tags) {
        boolean match = false;
        for (int i = 0; i<queryTokens.length; i++){
            String queryToken = queryTokens[i];
            match = findStringInArray(queryToken, tags);
            if(!match){
                break;
            }
        }
        return match;
    }

    private static boolean findStringInArray(String queryToken, String[] tags) {
        boolean match = false;
        for (int j=0; j<tags.length; j++){
            String tag = tags[j];
            if(tag.contains(queryToken)){
                match = true;
                break;
            }
        }
        return match;
    }

    public static void resetRecordings(GlobalState gs, CategoryAdapter adapter) {
        gs.getFilteredCategories().clear();
        gs.getFilteredCategories().addAll(gs.getCategories());
//        adapter.notifyDataSetChanged();
    }
}
