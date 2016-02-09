package com.eurecalab.eureca.fragments;

import java.io.File;
import java.util.Collection;

import com.eurecalab.eureca.MainActivity;
import com.eurecalab.eureca.R;
import com.eurecalab.eureca.UploadActivity;
import com.eurecalab.eureca.common.ActionCommon;
import com.eurecalab.eureca.common.FileCommon;
import com.eurecalab.eureca.constants.GenericConstants;
import com.eurecalab.eureca.constants.DynamoDBAction;
import com.eurecalab.eureca.constants.S3Action;
import com.eurecalab.eureca.constants.ViewPagerConstants;
import com.eurecalab.eureca.core.Callable;
import com.eurecalab.eureca.core.Category;
import com.eurecalab.eureca.core.GlobalState;
import com.eurecalab.eureca.core.Recording;
import com.eurecalab.eureca.io.FileChooser;
import com.eurecalab.eureca.net.CategoriesAsyncTask;
import com.eurecalab.eureca.net.DynamoDBTask;
import com.eurecalab.eureca.net.S3Task;
import com.eurecalab.eureca.ui.CategoryNamesAdapter;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

public class UploadFragment extends Fragment implements View.OnClickListener, Callable, View.OnTouchListener {
    private EditText soundName;
    private EditText soundTags;
    private Button save;
    private Button chooseFile;
    private Spinner soundCategory;
    private SpinnerAdapter categoryAdapter;
    private File file;
    private TextView pathTV;
    private GlobalState gs;
    private TextView result;
    private Category oldCategory;
    private Recording oldRecording;

    private final static int FILE_CHOOSER_ACTIVITY = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_upload, container, false);

        soundName = (EditText) rootView.findViewById(R.id.soundName);
        soundTags = (EditText) rootView.findViewById(R.id.soundTags);
        chooseFile = (Button) rootView.findViewById(R.id.chooseFile);
        save = (Button) rootView.findViewById(R.id.save);
        soundCategory = (Spinner) rootView.findViewById(R.id.soundCategory);
        pathTV = (TextView) rootView.findViewById(R.id.pathTV);
        result = (TextView) rootView.findViewById(R.id.result);

        gs = (GlobalState) getActivity().getApplication();
        if (gs.getCategories() == null || gs.getCategories().isEmpty()) {
            CategoriesAsyncTask task = new CategoriesAsyncTask(getActivity(), this);
            task.execute();
        } else {
            categoryAdapter = new CategoryNamesAdapter(getActivity(), gs.getCategories());
            soundCategory.setAdapter(categoryAdapter);
        }

        soundCategory.setOnTouchListener(this);

        save.setOnClickListener(this);
        chooseFile.setOnClickListener(this);

        Intent intent = getActivity().getIntent();
        if (Intent.ACTION_SEND.equals(intent.getAction())) {
            Uri uri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
            if (uri != null) {
                String path = FileCommon.getPathfromUri(uri);
                file = new File(path);
                pathTV.setText(file.getName());
            }
        }
        if (intent.hasExtra(GenericConstants.SELECTED_RECORDING)) {
            oldRecording = (Recording) intent.getSerializableExtra(GenericConstants.SELECTED_RECORDING);
            file = FileCommon.getPath(getActivity(), oldRecording.getFileName());
            pathTV.setText(oldRecording.getFileName());
            soundName.setText(oldRecording.getName());
            soundName.setEnabled(false);

            soundCategory.setSelection(oldRecording.getCategory().getSortIndex());
            soundTags.setText(oldRecording.getTags());
            oldCategory = oldRecording.getCategory();
        }

        return rootView;
    }

    @Override
    public void onClick(View v) {
        if (v.equals(save)) {
            ActionCommon.hideKeyboard(getActivity());
            String name = soundName.getText().toString();

            if (file == null || pathTV.getText().length() == 0) {
                result.setText(getString(R.string.select_a_file));
            } else if (!checkFile(file)) {
                result.setText(getString(R.string.check_file));
            } else {
                checkName(name);
            }
        } else if (v.equals(chooseFile)) {
            Intent intent = new Intent(getActivity(), FileChooser.class);
            intent.putExtra(FileChooser.SELECT_FILES, true);
            String extensions = "mp3 aif aifc aiff au funk gsd gsm it jam kar la lam lma m2a mid midi mjf mod mp2 mpa mpg mpga my pfunk qcp ra "
                    + "ram rm rmi rmm rmp rpm s3m sid snd tsi voc vox wav xm aac ogg oga spx flac axa";
            intent.putExtra(FileChooser.FILE_EXTENSIONS, extensions);
            startActivityForResult(intent, FILE_CHOOSER_ACTIVITY);
        }
    }

    private void upload() {
        Recording recording = new Recording();
        String name = soundName.getText().toString();
        String filename = file.getName();

        recording.setFileName(filename);
        recording.setName(name);
        String tags = soundTags.getText().toString();
        if (tags == null || tags.isEmpty()) {
            tags = name;
        }
        recording.setTags(tags);
        recording.setPath(FileCommon.getPath(getActivity(), filename).getAbsolutePath());
        recording.setOwner(gs.getAuthenticatedUser().getEmail());
        recording.setContext(getActivity());
        Category category = (Category) soundCategory.getSelectedItem();
        category.addRecording(recording);
        recording.setCategory(category);
        if (oldCategory != null && !oldCategory.equals(category)) {
            oldCategory.removeRecording(recording);

            Collection<Category> categories = gs.getCategories();
            for (Category cat : categories) {
                if (cat.equals(oldCategory)) {
                    cat.removeRecording(recording);
                }
            }

            DynamoDBTask persister2 = new DynamoDBTask(getActivity(), null, oldCategory, null, null, DynamoDBAction.CATEGORY);
            persister2.execute();
        }
        S3Task s3AsyncTask = new S3Task(getActivity(), recording,
                file, null, S3Action.UPLOAD);
        s3AsyncTask.execute();
        DynamoDBTask persister = new DynamoDBTask(getActivity(), recording, category, null, new Callable() {
            @Override
            public void callback(Object... args) {
                Activity activity = getActivity();
                if (activity instanceof UploadActivity) {
                    if (oldRecording == null) {
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        getActivity().startActivity(intent);
                    }
                    getActivity().finish();
                } else if (activity instanceof MainActivity) {
                    MainActivity mainActivity = (MainActivity) activity;
                    mainActivity.viewPager.setCurrentItem(ViewPagerConstants.HOME, true);
                }
            }
        }, DynamoDBAction.CATEGORY_AND_RECORDING);
        persister.execute();

        clear();
        result.setText(getString(R.string.upload_ok));
    }

    private boolean checkFile(File file) {
        long bytes = file.length();
        if (bytes < GenericConstants.MAX_ACCEPTED_FILESIZE) {
            return true;
        }
        return false;
    }

    private void checkName(String name) {
        if (name.trim().length() == 0) {
            result.setText(getString(R.string.empty_name));
        } else {
            checkNameAlreadyExisting();
        }
    }

    private void checkNameAlreadyExisting() {
        Recording recording = new Recording();
        recording.setName(soundName.getText().toString());
        DynamoDBTask task = new DynamoDBTask(getActivity(), recording, null, null, new Callable() {
            @Override
            public void callback(Object... args) {
                if (args.length == 1 && args[0] instanceof Boolean) {
                    boolean ok = (Boolean) args[0];
                    if (ok) {
                        upload();
                    } else {
                        result.setText(getString(R.string.check_name));
                    }
                } else {
                    result.setText(getString(R.string.check_name));
                }
            }
        }, DynamoDBAction.FIND_RECORDING);
        task.execute();
    }


    private void clear() {
        soundName.setText("");
        soundTags.setText("");
        soundCategory.setSelection(0);
        pathTV.setText("");
        file = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FILE_CHOOSER_ACTIVITY) {
            if (resultCode == Activity.RESULT_OK) {
                file = new File(data.getStringExtra("file_path"));
                pathTV.setText(file.getName());
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void callback(Object... args) {
        categoryAdapter = new CategoryNamesAdapter(getActivity(), gs.getCategories());
        soundCategory.setAdapter(categoryAdapter);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v.equals(soundCategory)) {
            ActionCommon.hideKeyboard(getActivity());
        }
        return false;
    }
}
