package com.example.assignment;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MatchPairGridFragment extends Fragment {

    // Interface for communication with Activity
    public interface MatchPairGridListener {
        void onMoveUpdated(int moves);
        void onGameFinished();
    }

    private MatchPairGridListener listener;
    private ImageButton[] btns;
    private int[] buttonAns;
    private int questionCount = 4;
    private int moves = 0;
    private int correctCount = 0;
    private int btnIndex = -1;
    private int yourAnswer, tmpAnswer;
    private boolean memStart = false;
    private MediaPlayer mp, mp2;
    private final int[] animals = {R.drawable.rabbit, R.drawable.elephant, R.drawable.lion, R.drawable.cat,
            R.drawable.bee, R.drawable.shark, R.drawable.penguin, R.drawable.otter};

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MatchPairGridListener) {
            listener = (MatchPairGridListener) context;
        } else {
            throw new RuntimeException(context + " must implement MatchPairGridListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_matchpair_grid, container, false);

        // Initialize ImageButtons
        btns = new ImageButton[]{
                view.findViewById(R.id.A1), view.findViewById(R.id.A2), view.findViewById(R.id.A3),
                view.findViewById(R.id.A4), view.findViewById(R.id.A5), view.findViewById(R.id.A6),
                view.findViewById(R.id.A7), view.findViewById(R.id.A8), view.findViewById(R.id.A9),
                view.findViewById(R.id.A10), view.findViewById(R.id.A11), view.findViewById(R.id.A12),
                view.findViewById(R.id.A13), view.findViewById(R.id.A14), view.findViewById(R.id.A15),
                view.findViewById(R.id.A16)
        };

        // Get difficulty from arguments
        int diff = getArguments().getInt("difficulty", 1);
        switch (diff) {
            case 1:
                for (int i = 8; i < btns.length; i++) {
                    btns[i].setVisibility(View.GONE);
                }
                break;
            case 2:
                questionCount = 6;
                for (int i = 12; i < btns.length; i++) {
                    btns[i].setVisibility(View.GONE);
                }
                break;
            case 3:
                questionCount = 8;
                break;
        }

        // Initialize MediaPlayers
        mp = MediaPlayer.create(getContext(), R.raw.filpsound3);
        mp2 = MediaPlayer.create(getContext(), R.raw.success);

        // Set click listeners
        View.OnClickListener clickListener = v -> ansClick(v);
        for (ImageButton btn : btns) {
            btn.setOnClickListener(clickListener);
        }

        // Generate random pairs
        buttonAns = genRandom();

        return view;
    }

    private void ansClick(View view) {
        try {
            if (mp != null) {
                mp.start();
            }
        } catch (IllegalStateException e) {
            // Release and recreate the media player if it is in an illegal state
            mp.release();
            mp = MediaPlayer.create(getContext(), R.raw.filpsound3);
            mp.start();
        }

        for (int i = 0; i < buttonAns.length; i++) {
            if (view.getId() == btns[i].getId()) {
                if (btnIndex == i) return;
                btnIndex = i;
                yourAnswer = buttonAns[i];
                break;
            }
        }

        BtnClearTask btnTask = new BtnClearTask();
        btnTask.setOnFinishShowDigitListener(() -> {
            for (int i = 0; i < buttonAns.length; i++) {
                if ("done".equals(btns[i].getTag(R.id.tag_status))) {
                    try {
                        if (mp2 != null) {
                            mp2.start();
                        }
                    } catch (IllegalStateException ex) {
                        mp2.release();
                        mp2 = MediaPlayer.create(getContext(), R.raw.success);
                        mp2.start();
                    }
                    btns[i].setBackground(null);
                    btns[i].setEnabled(false);
                    btns[i].setTag(R.id.tag_status, "");
                }
            }
            for (int i = 0; i < btns.length; i++) {
                if ("showing".equals(btns[i].getTag(R.id.tag_status))) {
                    try {
                        if (mp != null) {
                            mp.start();
                        }
                    } catch (IllegalStateException ex) {
                        mp.release();
                        mp = MediaPlayer.create(getContext(), R.raw.filpsound3);
                        mp.start();
                    }
                    btns[i].setTag(R.id.tag_status, "");
                    btns[i].animate().rotationYBy(-180);
                }
                btns[i].setImageResource(android.R.color.transparent);
                if (btns[i].getBackground() != null) {
                    btns[i].setEnabled(true);
                }
            }
        });

        ImageButton card = (ImageButton) view;
        if (!memStart) {
            tmpAnswer = yourAnswer;
            view.setTag(R.id.tag_status, "showing");
            card.setImageResource(0);
            card.animate().rotationYBy(180).withEndAction(() -> btns[btnIndex].setImageResource(animals[yourAnswer - 1]));
        } else {
            for (int i = 0; i < btns.length; i++) {
                btns[i].setEnabled(false);
            }
            if (yourAnswer == tmpAnswer) {
                for (int i = 0; i < buttonAns.length; i++) {
                    if (buttonAns[i] == yourAnswer) {
                        btns[i].setTag(R.id.tag_status, "done");
                    }
                }
                correctCount++;
            } else {
                view.setTag(R.id.tag_status, "showing");
            }
            card.setImageResource(0);
            card.animate().rotationYBy(180).withEndAction(() -> {
                btns[btnIndex].setImageResource(animals[yourAnswer - 1]);
                btnTask.execute();
                tmpAnswer = 0;
                yourAnswer = 0;
                btnIndex = -1;
                moves++;
                if (listener != null) listener.onMoveUpdated(moves);
                if (correctCount >= questionCount && listener != null) listener.onGameFinished();
            });
        }
        memStart = !memStart;
    }

    private int[] genRandom() {
        List<Integer> list = new ArrayList<>();
        for (int i = 1; i <= questionCount; i++) {
            list.add(i);
            list.add(i);
        }
        Collections.shuffle(list);
        int[] intArr = new int[questionCount * 2];
        for (int i = 0; i < questionCount * 2; i++) {
            intArr[i] = list.get(i);
        }
        return intArr;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mp != null) mp.release();
        if (mp2 != null) mp2.release();
    }
}