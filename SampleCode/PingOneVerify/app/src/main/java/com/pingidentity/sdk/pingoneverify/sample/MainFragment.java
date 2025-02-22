package com.pingidentity.sdk.pingoneverify.sample;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.pingidentity.sdk.pingoneverify.PingOneVerifyClient;
import com.pingidentity.sdk.pingoneverify.errors.DocumentSubmissionError;
import com.pingidentity.sdk.pingoneverify.listeners.DocumentSubmissionListener;
import com.pingidentity.sdk.pingoneverify.models.DocumentSubmissionResponse;
import com.pingidentity.sdk.pingoneverify.models.DocumentSubmissionStatus;

public class MainFragment extends Fragment implements DocumentSubmissionListener {

    public static final String TAG = MainFragment.class.getCanonicalName();

    private Button mBtnVerify;
    private ProgressBar mProgress;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBtnVerify = view.findViewById(R.id.btn_verify);
        mProgress = view.findViewById(R.id.progress);

        mBtnVerify.setOnClickListener(mView -> initPingOneClient());
    }

    @Override
    public void onDocumentSubmitted(DocumentSubmissionResponse response) {
        Log.i("onDocumentSubmitted", response.toString());
    }

    @Override
    public void onSubmissionComplete(DocumentSubmissionStatus status) {
        FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
        CompletedFragment completedFragment = new CompletedFragment();
        fragmentTransaction.replace(R.id.frame_layout, completedFragment)
                .addToBackStack(null)
                .commit();
        setInProgress(false);
    }

    @Override
    public void onSubmissionError(DocumentSubmissionError error) {
        Log.e("onSubmissionError", error.getMessage());
        showAlert("Document Submission Error", error.getLocalizedMessage());
        setInProgress(false);
    }

    private void initPingOneClient() {
        new PingOneVerifyClient.Builder()
                .setRootActivity(getActivity())
                .setListener(this)
                .startVerification(new PingOneVerifyClient.Builder.BuilderCallback() {
                    @Override
                    public void onSuccess(PingOneVerifyClient client) {
                        Log.d("initPingOneClient", "success");
                        setInProgress(true);
                    }

                    @Override
                    public void onError(String errorMessage) {
                        showAlert("Client Builder Error", errorMessage);
                        setInProgress(false);
                    }
                });
    }

    private void showAlert(String title, String message) {
        new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setMessage(message)
                .create()
                .show();
    }

    private void setInProgress(boolean inProgress) {
        mBtnVerify.setVisibility(inProgress ? View.INVISIBLE : View.VISIBLE);
        mProgress.setVisibility(inProgress ? View.VISIBLE : View.GONE);
    }

}